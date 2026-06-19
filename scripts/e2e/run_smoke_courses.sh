#!/usr/bin/env bash
set -euo pipefail

################################################################################
# Run E2E smoke test for Courses feature
#
# Example:
#   ./scripts/e2e/run_smoke_courses.sh
#
# Requirements:
# - psql available and configured to access the local dev DB as described in sprint-memory.md
#   (example: psql -U artbeams_user -d artbeams -f scripts/seed_courses_e2e.sql)
# - Java 21 / Gradle toolchain to run the app locally (start.sh or ./gradlew bootRun)
# - Node.js and dependencies for the MCP script (see scripts/e2e/README.md)
#
################################################################################

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Prefer git to find repository root; fall back to two levels up from this script
if ROOT_DIR_GIT=$(git -C "$SCRIPT_DIR" rev-parse --show-toplevel 2>/dev/null); then
  ROOT_DIR="$ROOT_DIR_GIT"
else
  ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
fi
# Keep artifacts beside this script for predictable paths regardless of ROOT_DIR
ARTIFACTS_DIR="$SCRIPT_DIR/artifacts"

mkdir -p "$ARTIFACTS_DIR"

echo "[1/8] Seeding test data using psql"
SEED_FILE="$ROOT_DIR/scripts/seed_courses_e2e.sql"
if [ ! -f "$SEED_FILE" ]; then
  echo "Seed file not found: $SEED_FILE" >&2
  exit 3
fi

echo "Running seed SQL: $SEED_FILE"
# Determine psql command: allow overriding via PSQL_CMD env var, otherwise require psql in PATH
if [ -n "${PSQL_CMD:-}" ]; then
  PSQL_CMD="$PSQL_CMD"
elif command -v psql >/dev/null 2>&1; then
  PSQL_CMD="psql"
else
  echo "psql not found in PATH and PSQL_CMD not set. Please install PostgreSQL client or set PSQL_CMD to a psql-compatible command." >&2
  echo "See sprint-memory.md for example connection settings." >&2
  exit 3
fi

${PSQL_CMD} -v ON_ERROR_STOP=1 -f "$SEED_FILE"

# 2) Start application
echo "[2/8] Starting application (background)"
# Use start.sh if available; fall back to gradle bootRun
if [ -x "$ROOT_DIR/start.sh" ]; then
  pushd "$ROOT_DIR" >/dev/null
  nohup "$ROOT_DIR/start.sh" > "$ROOT_DIR/.e2e_app.log" 2>&1 &
  WRAPPER_PID=$!
  popd >/dev/null
  echo "$WRAPPER_PID" > "$ROOT_DIR/.app.pid"
else
  pushd "$ROOT_DIR" >/dev/null
  nohup ./gradlew bootRun --args='--spring.profiles.active=local' > "$ROOT_DIR/.e2e_app.log" 2>&1 &
  WRAPPER_PID=$!
  popd >/dev/null
  echo "$WRAPPER_PID" > "$ROOT_DIR/.app.pid"
fi

echo "Started app wrapper PID: $WRAPPER_PID"

# Try to detect the actual JVM process started by the app (may vary based on gradle/bootRun semantics)
sleep 2
JAVA_PID="$(pgrep -f "org.xbery.artbeams.ApplicationKt" || true)"
if [ -n "$JAVA_PID" ]; then
  echo "Detected Java PID: $JAVA_PID"
  echo "$JAVA_PID" > "$ROOT_DIR/.e2e_app.java.pid"
fi

echo "[3/6] Waiting for http://localhost:8080/ to respond with 200"
RETRIES=60
SLEEP=2
OK=0
for i in $(seq 1 $RETRIES); do
  if curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/ | grep -q '^2'; then
    OK=1
    break
  fi
  sleep $SLEEP
done
if [ "$OK" -ne 1 ]; then
  echo "Application did not start in time. Check logs." >&2
  # best-effort stop
  kill "$WRAPPER_PID" >/dev/null 2>&1 || true
  exit 4
fi

echo "[4/8] Preparing browser-driven MCP/puppeteer script"
if ! command -v node >/dev/null 2>&1; then
  echo "node not found. Please install Node.js to run the browser script." >&2
  # best-effort stop
  kill "$WRAPPER_PID" >/dev/null 2>&1 || true
  exit 5
fi

# If package.json exists, install dependencies locally to scripts/e2e
if [ -f "$SCRIPT_DIR/package.json" ]; then
  if ! command -v npm >/dev/null 2>&1; then
    echo "npm not found but package.json is present. Please install npm." >&2
    kill "$WRAPPER_PID" >/dev/null 2>&1 || true
    exit 5
  fi
  echo "Installing Node.js dependencies in $SCRIPT_DIR (npm ci)"
  (cd "$SCRIPT_DIR" && npm ci) || {
    echo "npm ci failed" >&2
    kill "$WRAPPER_PID" >/dev/null 2>&1 || true
    exit 5
  }
fi

echo "Running browser script"
node "$ROOT_DIR/scripts/e2e/smoke_courses_mcp.js" --baseUrl=http://localhost:8080 --artifacts="$ARTIFACTS_DIR"
RC=$?

echo "[5/8] Verifying idempotence via SQL checks"
USER_COUNT=$(${PSQL_CMD} -t -A -c "SELECT COUNT(*) FROM users WHERE login = 'testmember';" 2>/dev/null || echo "0")
PRODUCT_COUNT=$(${PSQL_CMD} -t -A -c "SELECT COUNT(*) FROM products WHERE slug = 'kurz-zdraveho-stravovani';" 2>/dev/null || echo "0")

echo "user.testmember count: $USER_COUNT"
echo "product.kurz-zdraveho-stravovani count: $PRODUCT_COUNT"

if [ "$USER_COUNT" -ne 1 ]; then
  echo "Idempotence check failed: expected 1 testmember user but found $USER_COUNT" >&2
  RC=6
fi

if [ "$PRODUCT_COUNT" -lt 1 ]; then
  echo "Idempotence check failed: expected >=1 product rows but found $PRODUCT_COUNT" >&2
  RC=7
fi

echo "[6/8] Stopping application"
# Prefer killing the detected Java PID, then wrapper PID, then fallback to pkill
if [ -f "$ROOT_DIR/.e2e_app.java.pid" ]; then
  JPID=$(cat "$ROOT_DIR/.e2e_app.java.pid" 2>/dev/null || true)
  if [ -n "$JPID" ]; then
    echo "Killing Java PID $JPID"
    kill "$JPID" || true
    sleep 1
    kill -0 "$JPID" >/dev/null 2>&1 && kill -9 "$JPID" >/dev/null 2>&1 || true
    rm -f "$ROOT_DIR/.e2e_app.java.pid"
  fi
fi

if [ -f "$ROOT_DIR/.app.pid" ]; then
  PID=$(cat "$ROOT_DIR/.app.pid" 2>/dev/null || true)
  if [ -n "$PID" ]; then
    echo "Killing wrapper PID $PID"
    kill "$PID" || true
    rm -f "$ROOT_DIR/.app.pid"
  fi
fi

# Best-effort: kill any remaining processes matching the app main class
pkill -f "org.xbery.artbeams.ApplicationKt" >/dev/null 2>&1 || true

if [ "$RC" -ne 0 ]; then
  echo "E2E run failed with code $RC" >&2
  exit $RC
fi

echo "E2E run succeeded. Artifacts in: $ARTIFACTS_DIR"
exit 0
