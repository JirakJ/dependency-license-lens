# Dependency License Lens

[![Plugin Verification](https://github.com/JirakJ/dependency-license-lens/actions/workflows/verify.yml/badge.svg)](https://github.com/JirakJ/dependency-license-lens/actions/workflows/verify.yml)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)

> Lists the dependencies declared in your project's root build files together with their license and a risk rating, so copyleft or policy-blocked licenses are visible before they reach a release.

## Features

- **Build file parsing** — `build.gradle.kts` (Kotlin DSL), `build.gradle` (Groovy DSL), `pom.xml` and `package.json` in the project root
- **Offline license lookup** — a built-in mapping of widely used libraries to SPDX identifiers plus a built-in SPDX database; no network access
- **Risk levels** — Safe (permissive), Caution (weak copyleft or unknown), Warning (strong copyleft in a commercial project, proprietary), Blocked (license on the policy block list)
- **License policy** — configurable list of blocked SPDX identifiers (default `GPL-2.0, GPL-3.0, AGPL-3.0`)
- **Pre-commit check** — with the policy enabled, committing a build file that declares a dependency with a blocked license asks for confirmation first
- **License Lens tool window** — table of dependencies with version, license, category and risk, plus a summary line

Dependencies that the built-in mapping does not know are shown as *Unknown* with risk *Caution*.

## Installation

1. **Settings → Plugins → Marketplace**, search for **Dependency License Lens**, install.
2. Or download a release ZIP from [Releases](https://github.com/JirakJ/dependency-license-lens/releases) and use **Install Plugin from Disk**.

## Usage

1. Open a project with one of the supported build files in its root.
2. Run **Tools → Scan Dependency Licenses** (or open the **License Lens** tool window and click **Scan Dependencies**).
3. Review the table; the summary line counts permissive, weak/strong copyleft, unknown and issue dependencies.

## Configuration

**Settings → Tools → Dependency License Lens**

| Setting | Default | Description |
|---------|---------|-------------|
| Commercial project | `true` | Strong copyleft licenses are rated *Warning* instead of *Caution* |
| Enable license policy enforcement | `false` | Rates blocked licenses as *Blocked* and enables the pre-commit check |
| Blocked licenses | `GPL-2.0, GPL-3.0, AGPL-3.0` | Comma-separated SPDX identifiers |

## Requirements

- IntelliJ-based IDE 2023.2 or newer

## Building from source

```bash
./gradlew detekt test buildPlugin
```

## License

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE). The plugin is free; issues and pull requests are welcome at https://github.com/JirakJ/dependency-license-lens.
