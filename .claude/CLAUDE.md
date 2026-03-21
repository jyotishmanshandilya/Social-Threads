# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew build          # Compile and build
./gradlew bootRun        # Run the application
./gradlew test           # Run tests
./gradlew flywayMigrate  # Run Flyway DB migrations
```

The app runs on port **4196**.

## Development Setup

1. Copy `.env` with database credentials (PostgreSQL).
2. Start the local DB:
   ```bash
   docker-compose up -d
   ```
3. Run with the local profile (set in `application-local.yml`):
   ```bash
   SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
   ```
4. To run migrations separately, activate the `flyway` profile (`application-flyway.yml` has Supabase credentials).

## Configuration Profiles

| Profile | File | Purpose |
|---|---|---|
| *(default)* | `application.yml` | Production settings, Flyway disabled |
| `local` | `application-local.yml` | Local dev overrides |
| `flyway` | `application-flyway.yml` | Enables Flyway migrations (Supabase) |

Key config values: server port, PostgreSQL DSN, JWT secret/expiry, Google Custom Search CX + API key, Greenhouse API base URL, scheduled cron expressions.

## Architecture Overview

**Domain:** Job discovery and aggregation platform targeting Greenhouse-hosted job boards, focused on India-based roles.

### Core Flows

**1. Seed Discovery (Scheduled)**
- `SeedAggregatorService` runs on a cron (every minute locally, 9 AM weekdays Mon–Fri in prod).
- Builds search queries from configured roles × locations (in `application.yml`).
- Calls `SearchEngineService` → Google Custom Search API to find `boards.greenhouse.io` URLs.
- Validates each found board by calling the Greenhouse API and checking for India jobs.
- Persists new boards to the `seed_list` table; tracks pagination state in `search_progress`.
- Handles Google rate limiting (HTTP 429) by saving a checkpoint and resuming on the next run.

**2. Job Aggregation**
- `JobAggregatorService` iterates all `SeedList` entries and calls the Greenhouse Boards API (`/v1/boards/{boardId}/jobs`).
- Fetches full job content, extracts years-of-experience via regex (`JobDetailExtractionService` + JSoup HTML parsing).
- Batch-saves 100 jobs at a time to `company_jobs`, deduplicating by Greenhouse job ID.

**3. Authentication**
- JWT-based stateless auth. `/api/auth/**` is public; all other endpoints require a Bearer token.
- `JwtAuthenticationFilter` → `JwtService` validates tokens. Passwords hashed with BCrypt.
- Token expiry: 1 hour (prod) / 24 hours (local).

### Layer Structure

```
com.JobSwipe.webApp/
├── controller/        # REST endpoints (auth, jobs, seedlist, departments, preferences, search)
├── service/           # Business logic
├── repository/        # Spring Data JDBC/JPA repositories
├── entities/          # JPA entities (UserConfig, CompanyJobs, SeedList, UserPreference, SearchProgress, …)
├── model/             # DTOs; model/enums/ (Role, SearchQueryStatus, CsvContentType)
├── configuration/     # Spring Security, bean config
├── filter/            # JwtAuthenticationFilter
├── util/              # BackoffUtils (sleep+jitter), JsonUtils
└── flyway/            # Flyway callback utilities
```

### Database

PostgreSQL 15. Migrations live in `src/main/resources/db/migration/` (V00001–V00009).

Notable tables: `user_config`, `company_jobs`, `seed_list`, `search_progress`, `user_preferences`, `job_department_mapping`.

`UserPreference` uses JSONB columns for flexible storage of preferred titles, locations, employment types.

### External APIs

- **Google Custom Search API** — discovers Greenhouse board URLs; configured via `cx` and `api-key` in `application.yml`.
- **Greenhouse Boards API** — fetches job listings from each discovered board (`/v1/boards/{boardId}/jobs`).
- **AWS S3 SDK** — included as a dependency; not yet wired into production flows.

## Key Decisions & Gotchas

- **`hasIndiaJobs()` is defined in `SeedAggregatorService` but never called** — boards are saved regardless of whether they have India jobs. India filtering is intended to move to `JobAggregatorService`.
- **`MAX_RESULTS = 100` is a hard Google CSE ceiling**, not a configurable limit. Google never returns more than 100 results (10 pages × 10) per query.
- **`ProfileService` is empty** — scaffolded but not implemented.
- **`filterJobs()` in `UserPreferenceService` is a stub** — returns all jobs unfiltered.
- **Flyway is disabled by default** in `application.yml`; enable via the `flyway` profile.
- **Cron in `application.yml` is set to `"0 * * * * *"` (every minute)** for local testing — change to `"0 0 9 * * MON-FRI"` before any production deployment.
- Migrations follow `V000XX__description.sql` naming. Next migration is `V00010`.
