# Changelog

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
- Unlimited dependencies support (Pro)
- All build systems: npm/yarn/pnpm, pip/poetry, Go modules (Pro)
- Full SPDX license database (Pro)
- Compliance dashboard with filtering by license type, risk, and module (Pro)
- SBOM export in SPDX and CycloneDX formats (Pro)
- License policy definitions: allowed/denied/review-required (Pro)
- CI integration with Gradle task (Pro)
- SARIF output for GitHub Code Scanning (Pro)
- HTML report for legal review (Pro)
