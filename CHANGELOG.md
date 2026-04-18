# Changelog

## [2026.3.6] - 2026-04-18

### Changed
- Enabled freemium licensing via `<product-descriptor optional="true">`. Plugin remains fully functional without a license; premium features may be introduced in future minor releases.


## [2026.2.5] - 2026-02-13

### Added
- Added ProGuard obfuscation for code protection


## [2026.2.4] - 2026-02-13

### Changed
- Version bump for JetBrains Marketplace publication


## [2026.2.3] - 2026-02-13

### Changed
- Expanded compatibility range: sinceBuild 232.0+ (IntelliJ 2023.2+)
- Removed upper version bound for future IDE compatibility


## [2026.2.2] - 2026-02-13

### Added
- Initial release
- Automatic license scanning for project dependencies
- Inline license hints in Gradle (Kotlin DSL + Groovy) and Maven build files
- Risk indicators: 🟢 Permissive (MIT, Apache), 🟡 Weak copyleft (LGPL, MPL), 🔴 Strong copyleft (GPL, AGPL)
- Gutter icons for dependencies with license issues
- License conflict detection (e.g., GPL dependency in Apache-licensed project)
- Project license auto-detection from root LICENSE file
- License resolution from Maven Central POM metadata
- Local license cache with 24-hour TTL
- Fallback license detection from JAR LICENSE files and GitHub API
- Configurable commercial project flag for copyleft risk assessment
- License policy enforcement with blocked license list
- Per-dependency suppression with `// license-lens:ignore` comment
- `.license-lens.yaml` support for team-shared configuration
- Scan Dependency Licenses action (Tools menu)
- License Lens tool window with compliance overview
- Unlimited dependencies support
- All build systems: npm/yarn/pnpm, pip/poetry, Go modules
- Full SPDX license database
- Compliance dashboard with filtering by license type, risk, and module
- SBOM export in SPDX and CycloneDX formats
- License policy definitions: allowed/denied/review-required
- CI integration with Gradle task
- SARIF output for GitHub Code Scanning
- HTML report for legal review
