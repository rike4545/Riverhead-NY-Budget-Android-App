#!/usr/bin/env node
// Keep Android's fallback directory in exact parity with the canonical web nav.
// The web app owns the route list; this check reads SiteNav.tsx directly so a
// new, removed, or renamed destination cannot silently disappear on Android.

import { readFileSync } from 'node:fs';

const siteNavPath = process.argv[2] ?? 'web-source/web/components/SiteNav.tsx';
const androidPath = 'app/src/main/java/com/riverheadny/budget/ui/screens/more/WebParityScreen.kt';

function unique(values) {
  return [...new Set(values)];
}

const siteNav = readFileSync(siteNavPath, 'utf8');
const start = siteNav.indexOf('const PRIMARY:');
const end = siteNav.indexOf('const linkStyle');
if (start < 0 || end <= start) {
  throw new Error(`Could not locate PRIMARY and GROUPS in ${siteNavPath}`);
}
const navSource = siteNav.slice(start, end);
const webRoutes = [...navSource.matchAll(/\$\{base\}(\/[^\x60]+)/g)].map((match) => match[1]);
const androidSource = readFileSync(androidPath, 'utf8');
const androidRoutes = [...androidSource.matchAll(/WebParityRoute\(\s*"[^"]+"\s*,\s*"([^"]+)"/g)]
  .map((match) => match[1]);

const webUnique = unique(webRoutes);
const androidUnique = unique(androidRoutes);
const duplicateWeb = webRoutes.filter((route, index) => webRoutes.indexOf(route) !== index);
const duplicateAndroid = androidRoutes.filter((route, index) => androidRoutes.indexOf(route) !== index);
const missing = webUnique.filter((route) => !androidUnique.includes(route));
const extra = androidUnique.filter((route) => !webUnique.includes(route));

console.log(`Canonical web navigation: ${webRoutes.length} routes (${webUnique.length} unique)`);
console.log(`Android fallback catalog: ${androidRoutes.length} routes (${androidUnique.length} unique)`);
if (duplicateWeb.length || duplicateAndroid.length || missing.length || extra.length) {
  if (duplicateWeb.length) console.error(`Duplicate web routes: ${unique(duplicateWeb).join(', ')}`);
  if (duplicateAndroid.length) console.error(`Duplicate Android routes: ${unique(duplicateAndroid).join(', ')}`);
  if (missing.length) console.error(`Missing on Android: ${missing.join(', ')}`);
  if (extra.length) console.error(`No longer in web navigation: ${extra.join(', ')}`);
  process.exit(1);
}
console.log('Android fallback catalog exactly matches the canonical web navigation.');
