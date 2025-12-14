-- Czech Full-Text Search Configuration (Optional)
-- This file configures PostgreSQL for Czech language full-text search
-- Prerequisites: Czech Hunspell dictionary files must be in PostgreSQL tsearch_data directory
-- Find dictionary files at: https://github.com/wooorm/dictionaries or https://github.com/LibreOffice/dictionaries

-- Step 1: Download Czech dictionary files (cs_CZ.dic, cs_CZ.aff) and Czech stopwords
-- Place them in PostgreSQL's tsearch_data directory (usually /usr/share/postgresql/*/tsearch_data/)
-- Rename files to: czech.dict, czech.affix, czech.stop

-- Step 2: Create Czech Hunspell dictionary
-- Uncomment the following if you have Czech dictionary files installed:
/*
CREATE TEXT SEARCH DICTIONARY czech_hunspell (
    TEMPLATE = ispell,
    DictFile = czech,
    AffFile = czech,
    StopWords = czech
);

-- Step 3: Create Czech text search configuration
CREATE TEXT SEARCH CONFIGURATION czech (COPY = simple);

-- Step 4: Configure mappings to use Czech dictionary
ALTER TEXT SEARCH CONFIGURATION czech
    ALTER MAPPING FOR word, asciiword WITH czech_hunspell, simple;

ALTER TEXT SEARCH CONFIGURATION czech
    ALTER MAPPING FOR hword, hword_part, hword_asciipart WITH czech_hunspell, simple;
*/

-- Step 5: If you don't have Czech dictionaries, you can use simple configuration
-- This provides basic search without stemming but still works well with pg_trgm
-- The application will use 'simple' configuration by default and can be switched to 'czech' later

-- Test the configuration (uncomment after setup):
-- SELECT to_tsvector('czech', 'Zdraví je nejdůležitější věcí v životě');
-- SELECT plainto_tsquery('czech', 'zdraví život');
