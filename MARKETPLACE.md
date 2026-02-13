# Dependency License Lens — JetBrains Marketplace Listing

## Plugin Name

Dependency License Lens

## Tagline

See every dependency's license before it becomes a legal problem.

## Short Description

Shows license info for dependencies inline in your build files. Flags GPL/AGPL risks, detects conflicts, and exports SBOM. Gradle, Maven, npm, pip, Go.

## Full Description

```html
<h2>Dependency License Lens</h2>

<p>You're one <code>npm install</code> away from a GPL violation. Most developers don't check
dependency licenses until legal asks — and by then it's a painful audit. <b>Dependency License Lens</b>
shows license information directly in your build files, flags risky copyleft licenses, detects
conflicts with your project license, and generates compliance reports — all without leaving the IDE.</p>

<h3>Key Features</h3>
<ul>
    <li><b>Inline License Hints</b> — see license type next to every dependency in Gradle (Kotlin DSL &amp; Groovy) and Maven build files</li>
    <li><b>Risk Indicators</b> — 🟢 Permissive (MIT, Apache), 🟡 Weak Copyleft (LGPL, MPL), 🔴 Strong Copyleft (GPL, AGPL)</li>
    <li><b>Gutter Icons</b> — visual markers for dependencies with license issues</li>
    <li><b>Conflict Detection</b> — flags incompatible licenses (e.g., GPL dependency in an Apache-licensed project)</li>
    <li><b>License Resolution</b> — resolves licenses from Maven Central POM metadata with local caching (24h TTL)</li>
    <li><b>Automatic Project License Detection</b> — reads your root LICENSE file to check compatibility</li>
    <li><b>Quick-Fix: Replace with Compatible Alternative</b> — suggests swap-in dependencies with permissive licenses</li>
    <li><b>Per-Dependency Suppression</b> — <code>// license-lens:ignore</code> comments</li>
    <li><b>Unlimited Dependencies (Pro)</b> — free tier scans up to 50; Pro removes the cap</li>
    <li><b>All Build Systems (Pro)</b> — npm/yarn/pnpm, pip/poetry, and Go modules alongside Gradle and Maven</li>
    <li><b>Full SPDX Database (Pro)</b> — resolve any SPDX-listed license identifier</li>
    <li><b>Compliance Dashboard (Pro)</b> — tool window with filtering by license type, risk level, or module</li>
    <li><b>SBOM Export (Pro)</b> — generate SPDX and CycloneDX Software Bill of Materials</li>
    <li><b>License Policy Enforcement (Pro)</b> — define allowed, denied, and review-required licenses</li>
    <li><b>CI Integration (Pro)</b> — <code>./gradlew licenseLens --policy=strict --fail-on-violation</code></li>
    <li><b>SARIF Output (Pro)</b> — integrate with GitHub Code Scanning</li>
    <li><b>HTML Report (Pro)</b> — polished report ready for legal review</li>
    <li><b>Centralized Policy Management (Team)</b> — share license policies across all team members</li>
    <li><b>Team Compliance Reports (Team)</b> — aggregate compliance status across projects</li>
</ul>

<h3>Free Tier</h3>
<ul>
    <li>Inline license hints for up to 50 dependencies</li>
    <li>Gradle (Kotlin DSL + Groovy) and Maven support</li>
    <li>Risk indicators (🟢🟡🔴) and gutter icons</li>
    <li>Basic conflict detection</li>
    <li>Quick-fix: replace with compatible alternative</li>
    <li>Per-dependency suppression</li>
</ul>

<h3>Pro Tier ($3.90/month)</h3>
<ul>
    <li>Unlimited dependencies</li>
    <li>All build systems: npm, yarn, pnpm, pip, poetry, Go modules</li>
    <li>Full SPDX license database</li>
    <li>Compliance dashboard with advanced filtering</li>
    <li>SBOM export (SPDX and CycloneDX formats)</li>
    <li>License policy enforcement (allowed/denied/review-required)</li>
    <li>CI integration with SARIF output</li>
    <li>HTML report for legal review</li>
</ul>

<h3>Team Tier ($7.90/user/month)</h3>
<ul>
    <li>All Pro features</li>
    <li>Centralized license policy management</li>
    <li>Team-wide compliance reports</li>
</ul>

<h3>Getting Started</h3>
<ol>
    <li><b>Install</b> — search "Dependency License Lens" in Settings → Plugins → Marketplace</li>
    <li><b>Open a build file</b> — look for inline license hints next to your dependencies in <code>build.gradle.kts</code>, <code>pom.xml</code>, or <code>package.json</code></li>
    <li><b>Review risks</b> — check 🔴 indicators and open the License Lens tool window for the full compliance picture</li>
</ol>

<h3>Compatibility</h3>
<p>IntelliJ IDEA 2024.3+ and all compatible JetBrains IDEs. Requires Java 17+. Supports Gradle, Maven,
npm/yarn/pnpm (Pro), pip/poetry (Pro), and Go modules (Pro).</p>

<h3>Part of Developer Productivity Suite</h3>
<p>Dependency License Lens helps you ship with confidence, ensuring every dependency in your project
meets your licensing requirements — from prototype to production.</p>
```

## Tags

`license`, `compliance`, `dependencies`, `sbom`, `security`

## Category

Build & DevOps

## Pricing

| Tier | Price | Features |
|------|-------|----------|
| Free | $0 | Inline hints (50 deps), Gradle/Maven, risk indicators, basic conflict detection, quick-fix |
| Pro | $3.90/month or $39/year | Unlimited deps, all build systems, SPDX database, compliance dashboard, SBOM export, CI integration |
| Team | $7.90/user/month | All Pro + centralized policy management, team compliance reports |

## Screenshots Needed

1. **Inline License Hints** — `build.gradle.kts` showing license type and risk color next to each dependency declaration
2. **Conflict Detection** — editor warning highlighting a GPL dependency in an Apache-licensed project with quick-fix popup
3. **Compliance Dashboard** — License Lens tool window with dependencies filtered by risk level
4. **SBOM Export** — dialog showing SPDX/CycloneDX export options with generated report preview
5. **Risk Indicators in Maven** — `pom.xml` with 🟢🟡🔴 gutter icons next to dependency blocks
