---
description: Show a summary of the current seed discovery state from search_progress
---

Read `SeedAggregatorService.java` and `SearchProgressRepository.java`, then explain the current state of seed discovery to the user:

1. How many queries exist in total (count from `QueryGeneratorService.buildQueries()` logic and `application.yml` config).
2. How many are COMPLETED, PENDING, and FAILED (describe what each status means operationally).
3. What `MAX_RESULTS = 100` means for the total board ceiling per query.
4. Remind the user of the known cap: Google CSE returns at most 100 results per query, so the ceiling is `unique_queries × ~2 unique boards per query` on average.
5. If the user has run the aggregator recently, suggest checking the `search_progress` table directly:
   ```sql
   SELECT status, COUNT(*) FROM search_progress GROUP BY status;
   SELECT COUNT(*) FROM seed_list;
   ```
