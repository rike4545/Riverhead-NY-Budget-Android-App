# Riverhead NY Budget Android App

Native Android companion to the Riverhead NY Budget App, built with Kotlin and Jetpack Compose.

An independent public fiscal intelligence app for exploring Riverhead Town budgets, taxes, payroll, campaign finance, capital projects, and civic accountability tools — built for Android using publicly available Town and New York State records.

## Status

In active development, not yet published. Split out from the iOS repo's former `AndroidFork/` folder into its own repository, then rebuilt with a real app architecture, six data-backed budget screens, and a full civic-accountability suite.

## What is included

- Five-tab app shell matching the iOS app: Home, Budget, Civic, Tools, More — Navigation Compose with a single flat NavHost, one ViewModel per data screen.
- Real bundled data (copied from the web platform's own ETL output at `web/public/data/*`): funds, department/line-item budgets, payroll, general fund history, tax cap/levy history, community/tax-base context, Annual Financial Report actuals, and every Town Board meeting/resolution/roll-call vote since 2025.
- Six real, data-backed Budget Hub screens:
  - **Funds Explorer** — all 19 town funds, real 2026 appropriations, department → line-item drilldown
  - **General Fund History** — appropriations, levy, and revenues, 2005-2025
  - **Tax Cap & Overrides** — the state 2% cap and Riverhead's override history, including the 2018-2022 override-law lapse
  - **My Tax Bill** — a real assessed-value tax calculator using the Town's own published 2025/2026 rate table
  - **Fund Balance** — the real 2025 AFR unassigned General Fund balance against reserve-policy targets
  - **Payroll Explorer** — real actual employee earnings 2018-2025, headcount, top earners
- Two 2027 planning tools, built on the same reconciled constants as iOS/web:
  - **2027 Spending Reduction** — a real, sourced, toggleable $1.27M recurring savings package measured against the modeled payroll-pressure gap
  - **2027 Budget Simulator** — adjust levy growth, COLA, savings, and service investments against real appropriations/fund-balance data to test whether a scenario is structurally balanced
- A full civic-accountability suite in the Civic tab:
  - **Town Board Scorecard** — the app's one live network feature, fetching real campaign-contribution totals from NY State's Board of Elections Open Data API for the current five-member Town Board, cross-checked against Petrocelli and Scott's Pointe related-party watch lists
  - **Town Board Votes** — every meeting since January 2025, resolution-by-resolution, with mover/seconder and full per-member roll-call votes
  - **Procurement Watch** — sourced facts and open questions on the Town Square land-sale/procurement process
  - **Campaign Donation Ethics** — how the Town's $1,000 campaign-contribution aggregation rule actually works
- Riverhead-inspired color system, hero cards, link cards, and resident-facing disclaimer language.
- Official Riverhead links for town services, Channel 22, payments, departments, financial reports, and feedback.

## Build

Open this repository in Android Studio, or run:

```sh
./gradlew assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Verified on a real emulator (Pixel device profile, API 36) — not just a compile check. The Scorecard screen requires network access; every other screen works fully offline from bundled data.

## Notes

This is a native Android port, not a line-for-line SwiftUI translation. The 2027 Budget Simulator in particular is a single consolidated screen carrying the same real formulas as iOS's much larger Lab/Simulator views, rather than a full port of their multi-thousand-line district-by-district fiscal-stress lens. Home, Tools, and More still carry placeholder cards for features not yet ported (search, some Civic Command Center extras) — see the iOS and web repos for what those look like when built out.

## Also Available

- **[Riverhead NY Budget iOS App](https://github.com/rike4545/Riverhead-NY-Budget-iOS-App)** — the native iOS app this project is ported from.
- **[Riverhead Budget Live (Web)](https://github.com/rike4545/Riverhead-NY-Budget-Web-App)** — the browser-based companion platform.
