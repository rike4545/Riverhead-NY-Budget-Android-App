# Riverhead NY Budget App (Android): notes for Claude

A Kotlin and Jetpack Compose app about the Town of Riverhead, NY's budgets,
taxes, payroll and Town Board decisions. It is not yet published. Its readers
will be residents, and every figure in it has to trace to an official record.
The website (rike4545/Riverhead-NY-Budget-Web-App) is the source of truth: when
a figure here disagrees with the website, the website's data and `web/lib/` are
right. The iOS app (rike4545/Riverhead-NY-Budget-iOS-App) is the sibling this
app follows for screens and tab names.

## Layout

- `app/src/main/java/com/riverheadny/budget/`: `data/` (loading and models)
  and `ui/screens/` (home, budget, civic, tools, more, search). There is one
  NavHost, with one ViewModel per data screen.
- `app/src/main/assets/data/`: bundled JSON, copied from the website's
  `web/public/data/`.
- `ui/screens/more/WebParityScreen.kt`: the catalog of every website page,
  opened in a web view (88 routes as of November 11, 2026).
- `scripts/verify-web-parity.mjs`: checks that catalog against the website's
  `web/components/SiteNav.tsx`.
- `.github/workflows/`: `android.yml` (parity check, then build) and
  `sync-web-data.yml` (daily copy of shared datasets from the website).
- `.claude/agents/`: `riverhead-domain-expert` (figures and framing) and
  `android-ml-expert`.

## Commands

```bash
# Parity check (node only). Needs a checkout of the website repo.
node scripts/verify-web-parity.mjs ../Riverhead-NY-Budget-Web-App/web/components/SiteNav.tsx

# Build and unit tests (needs JDK 17 and the Android SDK)
./gradlew --no-daemon testDebugUnitTest assembleDebug
```

A cloud session has no Android SDK, so CI is the compiler there: push to a
draft PR and read the checks. There are no unit tests yet, so
`testDebugUnitTest` passes without testing anything.

## Rules

- Work on the branch the session gives you and open draft pull requests. Never
  merge and never push to `main`: the owner merges.
- CI checks out the website's `main`. A new website route therefore fails the
  parity check here until the website PR merges. Merge the website first, then
  re-run this repo's failed job.
- This repo is private, so CI bills Actions minutes. A job that ends in seconds
  with no runner means the account's minutes or spending limit ran out, not that
  the code failed.
- Don't hand-edit `assets/data/budget-supplement/{lines,outliers,reductions}.json`
  or `assets/data/payroll/{records,titles-by-year}.json`. The daily sync
  overwrites them from the website, whose `shared-data/manifest.json` lists
  them. Other bundled data is copied by hand, so check it against the website's
  current file before trusting it.
- `usesCleartextTraffic` stays false: every link is https.
- Commit messages and pull requests say what changed and why, in prose.

## Accuracy

- Quote a figure from its record and cite it in the copy. Leave it blank rather
  than guess, and say why.
- Hard-coded figures go stale. When the Town publishes a newer record, search
  the Kotlin sources for the old figure and update every copy. Example: the 2025
  audit (accepted September 1, 2026, Resolution 2026-834) gives unassigned
  General Fund balance as $28,829,513. The unaudited Annual Financial Report's
  figure was $29,671,084.
- The Town's fund-balance policy is Resolution 918 of 2011: a 15% floor and no
  ceiling. Never present a 20% target as Town policy.
- Frame findings as likely concerns for residents, not legal or audit
  determinations.
- Never put personal email addresses, keys or tokens in code or data.
