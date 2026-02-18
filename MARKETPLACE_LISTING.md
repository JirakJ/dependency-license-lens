# Dependency License Lens — JetBrains Marketplace Listing

---

## 📝 Plugin Information

| Field            | Value                                |
|------------------|--------------------------------------|
| **Name**         | Dependency License Lens              |
| **Plugin ID**    | com.jakubjirak.licenselens           |
| **Version**      | 2026.2.9                             |
| **Category**     | Build / Dependencies                 |
| **Tags**         | License Compliance, Dependencies, SBOM, Open Source, Legal |

---

## 🎯 Short Description

> Scans project dependencies and shows license info inline in build files. Flags risky copyleft licenses, detects conflicts, and exports SBOM reports — compliance without leaving your editor.

_(185 characters)_

---

## 📖 Full Description

### ⚖️ Know Your Licenses Before They Become a Legal Problem

**The Problem:** Modern projects pull in dozens — sometimes hundreds — of open-source dependencies. Each one carries a license with obligations. Accidentally shipping GPL code in a proprietary product can have serious legal consequences, but manually auditing licenses is painful and easily forgotten.

**The Solution:** Dependency License Lens resolves license information for every dependency directly inside your IDE. It shows inline hints in your build files, flags risky licenses with color-coded risk indicators, and can enforce license policies in CI — turning compliance from a quarterly audit into an automatic, continuous check.

---

### ✨ Key Features

#### 🆓 Free — Instant License Visibility

- 🔍 **License Scanning** — Automatically resolves licenses for up to 50 dependencies
- 📝 **Inline License Hints** — See license info right in your `build.gradle.kts`, `build.gradle`, or `pom.xml`
- 🚦 **Risk Indicators** — Color-coded at a glance:
  - 🟢 Permissive (MIT, Apache-2.0, BSD)
  - 🟡 Weak Copyleft (LGPL, MPL)
  - 🔴 Strong Copyleft (GPL, AGPL)
- 🔖 **Gutter Icons** — Instant visual indicators for license issues in build files
- ⚠️ **Basic Conflict Detection** — Flags incompatible license combinations
- 🏗️ **Build System Support** — Gradle (Kotlin DSL + Groovy) and Maven
- 📦 **Maven Central Resolution** — Reads POM metadata with local caching for speed

#### 💎 Pro — $3.90/month

- ♾️ **Unlimited Dependencies** — No 50-dependency cap
- 🌐 **All Build Systems** — npm/yarn/pnpm, pip/poetry, Go modules (in addition to Gradle/Maven)
- 📚 **Full SPDX Database** — Complete license identification coverage
- 📊 **Compliance Dashboard** — Dedicated tool window with overview of all dependencies and their licenses
- 🔎 **License Filtering** — Filter by type, risk level, or module
- 📄 **SBOM Export** — Generate reports in SPDX and CycloneDX formats
- 🛡️ **Policy Enforcement** — Define allowed/denied/review-required license lists
- 🔧 **CI Integration** — `./gradlew licenseLens --policy=strict --fail-on-violation`
- 📋 **SARIF Output** — Feed results into GitHub Code Scanning
- 📰 **HTML Reports** — Generate styled reports for legal review

---

### 💰 Pricing Model

| Tier     | Price       | Highlights                                              |
|----------|-------------|---------------------------------------------------------|
| **Free** | $0          | 50 dependencies, inline hints, risk indicators, Gradle/Maven, conflict detection |
| **Pro**  | $3.90/month | Unlimited deps, all build systems, SPDX, SBOM export, policy enforcement, CI integration |

---

### 🎯 Perfect For

- **Commercial software teams** needing continuous license compliance
- **Open-source maintainers** wanting to verify contributor dependency choices
- **Legal/compliance teams** needing audit-ready SBOM reports
- **CTOs and engineering leads** managing open-source risk across multiple projects

---

### ⚙️ Requirements

| Requirement        | Version                          |
|---------------------|----------------------------------|
| **JetBrains IDE**  | 2024.3+ (build 232+)            |
| **Java**           | 17+                             |

---

### 🚀 Quick Start

1. **Install** — Search "Dependency License Lens" in `Settings → Plugins → Marketplace`
2. **Open a build file** — Open `build.gradle.kts`, `build.gradle`, or `pom.xml`
3. **See inline hints** — License information appears as inlay hints next to each dependency
4. **Scan project** — Go to `Tools → Scan Dependency Licenses` for a full project scan
5. **Configure policies** — Set your allowed/blocked licenses in `Settings → Tools → Dependency License Lens` or via `.license-lens.yaml`

---

## 📸 Screenshots & Media

| #  | Screenshot Description                                                                 |
|----|----------------------------------------------------------------------------------------|
| 1  | **Inline license hints** — `build.gradle.kts` file showing license names and risk colors next to each dependency declaration |
| 2  | **Gutter icons in build file** — 🟢🟡🔴 risk indicators in the gutter of a Gradle file |
| 3  | **License conflict warning** — Editor highlighting an incompatible license combination with a warning tooltip |
| 4  | **Compliance dashboard** — Tool window listing all dependencies with license, risk level, and status columns (Pro) |
| 5  | **SBOM export dialog** — Export options showing SPDX and CycloneDX format selection (Pro) |
| 6  | **Settings panel** — Configuration page with blocked license list, commercial project toggle, and team config path |
| 7  | **CI integration terminal** — Output of `./gradlew licenseLens --policy=strict` with violation details (Pro) |
| 8  | **HTML report preview** — Styled license audit report suitable for legal review (Pro) |

---

## 🏷️ Marketplace Categories

| Type          | Category                |
|---------------|-------------------------|
| **Primary**   | Build / Dependencies    |
| **Secondary** | Code Quality / Compliance |

---

## 🔍 Keywords (SEO)

`license compliance`, `dependency license`, `open source license`, `SBOM`, `SPDX`, `CycloneDX`, `GPL detection`, `license audit`, `license risk`, `build dependencies`, `maven`, `gradle`, `npm`, `pip`, `copyleft`, `license policy`, `software composition analysis`, `legal compliance`

---

## 📋 Compatibility

### Compatible JetBrains IDEs

| IDE                        | Compatible |
|----------------------------|:----------:|
| IntelliJ IDEA Community    | ✅          |
| IntelliJ IDEA Ultimate     | ✅          |
| PyCharm                    | ✅          |
| WebStorm                   | ✅          |
| GoLand                     | ✅          |
| PhpStorm                   | ✅          |
| Rider                      | ✅          |
| CLion                      | ✅          |
| RubyMine                   | ✅          |
| Android Studio             | ✅          |

**Minimum IDE Version:** 2024.3 (build 232)

---

## 📞 Vendor Information

| Field        | Value                              |
|--------------|------------------------------------|
| **Vendor**   | Jakub Jirak                        |
| **Email**    | jirak@jakubjirak.com               |
| **Website**  | https://jakubjirak.com             |
| **Support**  | support@jakubjirak.com             |

---

## ✅ Pre-Launch Checklist

- [ ] Plugin icon meets JetBrains Marketplace guidelines (SVG, 40x40 & 80x80)
- [ ] Short description is under 255 characters
- [ ] Full description renders correctly in Marketplace preview
- [ ] Screenshots are high-resolution (1280×800 or higher), PNG format
- [ ] Plugin ID matches `gradle.properties` and `plugin.xml`
- [ ] `plugin.xml` vendor email and URL are correct
- [ ] Pricing tiers configured in JetBrains Marketplace seller dashboard
- [ ] Product code `PLICENSELENS` is registered
- [ ] License agreement / EULA uploaded
- [ ] Compatibility range tested on minimum supported IDE version (2024.3)
- [ ] Plugin verified with Plugin Verifier (`runPluginVerifier` Gradle task)
- [ ] CHANGELOG.md is up to date for the release version
- [ ] Support email is monitored and responsive
- [ ] Privacy policy URL is set
- [ ] Plugin signed with JetBrains Marketplace certificate
