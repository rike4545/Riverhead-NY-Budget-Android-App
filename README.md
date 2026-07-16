# Riverhead NY Budget Android App

Native Android companion to the Riverhead NY Budget App, built with Kotlin and Jetpack Compose.

An independent public fiscal intelligence app for exploring Riverhead Town budgets, taxes, payroll, campaign finance, capital projects, and civic accountability tools — built for Android using publicly available Town and New York State records.

## Status

Early-stage scaffold, not yet published. Split out from the iOS repo's `AndroidFork/` folder into its own repository.

## What is included

- Five-tab app shell matching the iOS app: Home, Budget, Discover, Toolkits, More.
- Riverhead-inspired color system, hero cards, link cards, and resident-facing disclaimer language.
- Budget Hub with resident/expert mode, section chips, tax estimator, fund-balance check, employee placeholder, hearing toolkit, and budget document cards.
- Official Riverhead links for town services, Channel 22, payments, departments, financial reports, and feedback.
- Local Android assets copied from the iOS project:
  - Gross earnings CSVs for 2018-2023
  - 2026 payroll CSV
  - levy and general-fund CSVs
  - selected budget PDF documents

## Build

Open this repository in Android Studio, or run:

```sh
./gradlew assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Notes

This is an Android-native fork scaffold, not a line-for-line SwiftUI translation. The major navigation, local data assets, official links, resident/expert budget framing, and core calculator flows are in place. The next porting pass should wire the copied CSV assets into searchable tables and charts.

## Also Available

- **[Riverhead NY Budget iOS App](https://github.com/rike4545/Riverhead-NY-Budget-iOS-App)** — the native iOS app this project is ported from.
- **[Riverhead Budget Live (Web)](https://github.com/rike4545/Riverhead-NY-Budget-Web-App)** — the browser-based companion platform.
