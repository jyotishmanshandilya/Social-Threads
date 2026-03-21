---
description: Scaffold a new Flyway migration file with the next version number
---

The user wants to create a new Flyway migration. The argument is the migration description (e.g. `add index on company_jobs`).

1. List all files in `src/main/resources/db/migration/` to find the highest existing version number (format: `V000XX`).
2. Increment it by 1 to get the next version.
3. Convert the description argument to snake_case.
4. Create a new file at `src/main/resources/db/migration/V<next>__<snake_case_description>.sql` with an empty SQL template:

```sql
-- Migration: <description>
-- Created: <today's date>

```

5. Tell the user the full file path and remind them to write idempotent SQL (use `IF NOT EXISTS`, `IF EXISTS` guards where applicable).
