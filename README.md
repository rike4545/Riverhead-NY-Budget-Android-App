# Riverhead NY Budget Android App

Native Android companion to the Riverhead NY Budget App, built with Kotlin and Jetpack Compose.

An independent public fiscal intelligence app for exploring Riverhead Town budgets, taxes, payroll, campaign finance, capital projects, and civic accountability tools — built for Android using publicly available Town and New York State records.

## Status

In active development, not yet published. Split out from the iOS repo's former `AndroidFork/` folder into its own repository, then rebuilt with a real app architecture and six data-backed flagship screens.

## What is included

- Five-tab app shell matching the iOS app: Home, Budget, Civic, Tools, More — Navigation Compose with a single flat NavHost, one ViewModel per data screen.
- Real bundled data (~8.3MB, copied from the web platform's own ETL output at `web/public/data/*`, no live network calls): funds, department/line-item budgets, payroll, general fund history, tax cap/levy history, community/tax-base context, Annual Financial Report actuals.
- Six real, data-backed screens in the Budget Hub / Tools:
  - **Funds Explorer** — all 19 town funds, real 2026 appropriations, department → line-item drilldown
  - **General Fund History** — appropriations, levy, and revenues, 2005-2025
  - **Tax Cap & Overrides** — the state 2% cap and Riverhead's override history, including the 2018-2022 override-law lapse
  - **My Tax Bill** — a real assessed-value tax calculator using the Town's own published 2025/2026 rate table
  - **Fund Balance** — the real 2025 AFR unassigned General Fund balance against reserve-policy targets
  - **Payroll Explorer** — real actual employee earnings 2018-2025, headcount, top earners
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

Verified on a real emulator (Pixel device profile, API 36) — not just a compile check.

## Notes

This is a native Android port, not a line-for-line SwiftUI translation. The five-tab shell, real navigation, and six flagship data screens are in place with the same real budget/payroll/tax data as the iOS and web apps. Home, Civic, Tools, and More still carry placeholder cards for features not yet ported (2027 planning tools, Civic Command Center's full accountability suite, campaign finance, meeting-by-meeting votes, search) — see the iOS and web repos for what those look like when built out.

## Also Available

- **[Riverhead NY Budget iOS App](https://github.com/rike4545/Riverhead-NY-Budget-iOS-App)** — the native iOS app this project is ported from.
- **[Riverhead Budget Live (Web)](https://github.com/rike4545/Riverhead-NY-Budget-Web-App)** — the browser-based companion platform.
