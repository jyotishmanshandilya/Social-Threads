ALTER TABLE search_progress
ADD COLUMN IF NOT EXISTS seeds_discovered INT DEFAULT 0;