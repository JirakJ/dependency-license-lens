# Screenshot Planning — Dependency License Lens

## Screenshots

### 1. Inline License Hints in Build File
- **Title:** "See Licenses Right in Your Build File"
- **What to capture:** A Gradle build file with inline hints showing license types (MIT, Apache-2.0, GPL-3.0) next to dependency declarations.

### 2. Risk-Colored Gutter Icons
- **Title:** "Color-Coded License Risk"
- **What to capture:** Build file with green, yellow, and red gutter icons indicating different risk levels for dependencies.

### 3. License Conflict Warning
- **Title:** "Catch License Conflicts Early"
- **What to capture:** A warning highlighting an incompatible dependency (e.g., GPL in an Apache project) with the conflict explanation tooltip.

### 4. Compliance Dashboard
- **Title:** "Full License Overview"
- **What to capture:** The License Lens tool window showing all dependencies grouped by license type, with risk level columns and summary statistics.

### 5. SBOM Export Dialog (Pro)
- **Title:** "Generate SBOM Reports"
- **What to capture:** The export dialog with SPDX and CycloneDX format options and the generated output preview.

### 6. Policy Configuration
- **Title:** "Enforce License Policies"
- **What to capture:** The settings panel showing the allowed/denied/review-required license lists and the .license-lens.yaml configuration.

### 7. Scan Results Notification
- **Title:** "Quick Scan Summary"
- **What to capture:** The balloon notification after a scan completes, showing the number of dependencies scanned and any issues found.

### 8. Maven POM Support
- **Title:** "Works with Maven Too"
- **What to capture:** A Maven pom.xml file with inline license hints and gutter icons, similar to the Gradle screenshot.

---

## Video Demo Outline (45 seconds)

| Time | Scene | Description |
|------|-------|-------------|
| 0-5s | Intro | Plugin name and tagline with build file in background |
| 5-15s | Scan | Run "Scan Dependency Licenses" from Tools menu, show progress |
| 15-25s | Inline hints | Navigate build file showing license types and risk colors |
| 25-35s | Conflict | Highlight a license conflict with the warning tooltip |
| 35-40s | Dashboard | Open compliance dashboard, show grouped view |
| 40-45s | CTA | "Install from JetBrains Marketplace" with URL |
