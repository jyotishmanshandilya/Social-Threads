CREATE TABLE IF NOT EXISTS search_progress (
    id UUID PRIMARY KEY,
    query TEXT,
    page_start INT,
    last_run TIMESTAMP,
    status TEXT -- e.g. PENDING, COMPLETE
);