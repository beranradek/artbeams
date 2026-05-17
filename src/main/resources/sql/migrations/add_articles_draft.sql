-- Add/rename draft flag on articles.
-- Draft articles must never be visible on public web pages.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'articles'
          AND column_name = 'is_draft'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'articles'
          AND column_name = 'draft'
    ) THEN
        ALTER TABLE articles RENAME COLUMN is_draft TO draft;
    END IF;
END $$;

ALTER TABLE articles
    ADD COLUMN IF NOT EXISTS draft boolean NOT NULL DEFAULT FALSE;

DROP INDEX IF EXISTS idx_articles_is_draft;
CREATE INDEX IF NOT EXISTS idx_articles_draft ON articles (draft);
