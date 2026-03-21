---
description: Run Flyway DB migrations against the configured database
---

Run the Flyway migration using the flyway profile (which points at Supabase):

```bash
SPRING_PROFILES_ACTIVE=flyway ./gradlew flywayMigrate
```

After it completes, report:
- Which migrations were applied (parse the Gradle output for `Successfully applied`)
- The current schema version
- Any errors or skipped migrations

If the command fails, show the full error and suggest whether the issue is a connection problem, a bad migration file, or a checksum mismatch.
