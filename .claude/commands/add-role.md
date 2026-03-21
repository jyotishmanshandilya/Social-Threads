---
description: Add a new job role (with synonyms) to the search config in application.yml
---

The user wants to add a new role to `search.roles` in `application.yml`. The argument is the role name and optionally its synonyms separated by commas (e.g. `data engineer, data analyst, analytics engineer`).

1. Read `src/main/resources/application.yml`.
2. Find the `search.roles` section.
3. Add the new role group with the provided synonyms. Use the first term as the group key (lowercase, spaces preserved).
4. Confirm the change was made and show the updated `search.roles` block.
5. Remind the user that adding N synonyms across 2 sites will generate `2 × N × total_locations` new queries, increasing Google CSE usage proportionally.
