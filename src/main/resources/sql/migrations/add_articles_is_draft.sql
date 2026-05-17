-- Add draft flag to articles.
-- Draft articles must never be visible on public web pages.

ALTER TABLE articles
    ADD COLUMN IF NOT EXISTS is_draft boolean NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_articles_is_draft ON articles (is_draft);

