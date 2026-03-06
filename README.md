# Dependency License Lens

[![Build](https://github.com/JirakJ/dependency-license-lens/actions/workflows/build.yml/badge.svg)](https://github.com/JirakJ/dependency-license-lens/actions/workflows/build.yml)
[![Version](https://img.shields.io/badge/version-2026.2.2-blue.svg)](https://github.com/JirakJ/dependency-license-lens/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

> Shows license information for project dependencies directly in the IDE. Parses build files, resolves license info, and flags potentially problematic licenses — compliance without leaving your editor.

## ✨ Features

- **License scanning** — automatically detect licenses for all dependencies
- **Inline license hints** — see license type directly in Gradle/Maven build files
- **Risk indicators** — 🟢 Permissive (MIT, Apache), 🟡 Weak copyleft (LGPL, MPL), 🔴 Strong copyleft (GPL, AGPL)
- **Gutter icons** — visual indicator for dependencies with license issues
- **Conflict detection** — detect incompatible licenses (e.g., GPL dependency in an Apache project)
- **Build file support** — Gradle (Kotlin DSL + Groovy), Maven, npm/yarn/pnpm, pip/poetry, Go modules
- **License resolution** — from Maven Central POM metadata with local caching
- **Full SPDX license database** — complete license identification
- **Compliance dashboard** — tool window with overview of all dependencies and their licenses
- **License filtering** — filter by license type, risk level, or module
- **SBOM export** — SPDX and CycloneDX formats
- **License policy enforcement** — define allowed/denied/review-required license policies
- **CI integration** — Gradle task: `./gradlew licenseLens --policy=strict --fail-on-violation`
- **SARIF output** — for GitHub Code Scanning
- **HTML report** — for legal review

## 📦 Installation

### From JetBrains Marketplace
1. Open **Settings** → **Plugins** → **Marketplace**
2. Search for **"Dependency License Lens"**
3. Click **Install** and restart the IDE

### From ZIP
1. Download the latest release from [Releases](https://github.com/JirakJ/dependency-license-lens/releases)
2. Open **Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk**
3. Select the downloaded ZIP file and restart the IDE

## 🚀 Quick Start

1. Install the plugin and restart your IDE
2. Open a project with a `build.gradle.kts`, `build.gradle`, or `pom.xml` file
3. Go to **Tools → Scan Dependency Licenses** to scan your project
4. Open any build file — you'll see inline license hints next to each dependency declaration
5. Open the **License Lens** tool window (bottom panel) for a full compliance overview
6. Check gutter icons for dependencies flagged with license issues

## ⚙️ Configuration

Settings are available at **Settings → Tools → Dependency License Lens**.

| Setting | Default | Description |
|---------|---------|-------------|
| Commercial project | `true` | Flag copyleft licenses as risky (for commercial/proprietary projects) |
| Enable license policy enforcement | `false` | Block dependencies with denied licenses |
| Blocked licenses | `GPL-2.0, GPL-3.0, AGPL-3.0` | Comma-separated list of SPDX identifiers to block |

### Team-shared Configuration

Create a `.license-lens.yaml` in your repository root for shared team settings. Use `// license-lens:ignore` comment on a dependency declaration to suppress warnings for individual dependencies.

## 🎯 Usage

### Inline License Hints
- Open any build file (`build.gradle.kts`, `pom.xml`, `package.json`)
- License information appears as inlay hints next to each dependency declaration
- Colors indicate risk level: 🟢 Permissive, 🟡 Weak copyleft, 🔴 Strong copyleft

### Scanning
- Run **Tools → Scan Dependency Licenses** to scan all project dependencies
- Results appear in the **License Lens** tool window and as inline hints

### Conflict Detection
- The plugin automatically detects your project's license from the root `LICENSE` file
- Incompatible dependency licenses are flagged with inspection warnings
- Use quick-fix actions to find compatible alternatives

### Compliance Dashboard
- Open the **License Lens** tool window from the bottom panel
- View all dependencies grouped by license type and risk level
- Export SBOM in SPDX or CycloneDX format

## 🔌 Actions

| Action | Location | Description |
|--------|----------|-------------|
| Scan Dependency Licenses | Tools menu | Scan project dependencies for license information |

## 📋 Requirements

- IntelliJ IDEA 2024.3+ (or any compatible JetBrains IDE)
- Java 17+
- A project with Gradle, Maven, or npm build files

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/dependency-license-lens.git
cd dependency-license-lens
./gradlew build
```

## 📝 License

MIT — [LICENSE](LICENSE)

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)
