# Dependency License Lens

**Know your licenses before they become legal problems.**

---

## The Problem

Modern projects depend on dozens or hundreds of third-party libraries. Each comes with its own license — MIT, Apache, GPL, LGPL, and many more. Mixing incompatible licenses can create legal liability. Most teams don't discover license conflicts until a compliance audit, by which time it's expensive to fix.

---

## Key Features

### Automatic License Detection
Scans all project dependencies and resolves their licenses from Maven Central POM metadata. No manual lookup required.

### Inline License Hints
See the license type directly in your build files next to each dependency declaration. Gradle (Kotlin + Groovy) and Maven supported out of the box.

### Color-Coded Risk Levels
Instantly see license risk: 🟢 Permissive (MIT, Apache, BSD), 🟡 Weak Copyleft (LGPL, MPL), 🔴 Strong Copyleft (GPL, AGPL).

### Conflict Detection
Automatically flags incompatible license combinations — like GPL dependencies in an Apache-licensed project.

### Compliance Dashboard
A dedicated tool window lists all dependencies grouped by license type and risk level. Sort, filter, and search to get the full picture.

### SBOM Export (Pro)
Generate industry-standard software bill of materials in SPDX and CycloneDX formats for audits and compliance.

### Policy Enforcement (Pro)
Define allowed, denied, and review-required license lists. The plugin enforces your policy on every scan.

### Transitive Dependency Analysis (Pro)
Don't just check direct dependencies. Trace license obligations through your entire dependency tree.

---

## How It Works

1. **Install the plugin** — Available from the JetBrains Marketplace.
2. **Scan dependencies** — Run "Tools → Scan Dependency Licenses" to analyze your project.
3. **Review results** — See inline hints in build files and the full compliance dashboard.
4. **Fix conflicts** — Address flagged license incompatibilities before they become legal issues.

---

## Pricing

| Feature | Free | Pro ($3.90/month) |
|---------|------|---------------------|
| License scanning (up to 50 deps) | ✅ | ✅ |
| Inline license hints | ✅ | ✅ |
| Risk indicators | ✅ | ✅ |
| Conflict detection | ✅ | ✅ |
| Gutter icons | ✅ | ✅ |
| Gradle + Maven support | ✅ | ✅ |
| Local caching | ✅ | ✅ |
| Unlimited dependencies | ❌ | ✅ |
| npm/yarn/pnpm support | ❌ | ✅ |
| pip/poetry support | ❌ | ✅ |
| Go modules support | ❌ | ✅ |
| SBOM export (SPDX/CycloneDX) | ❌ | ✅ |
| Policy enforcement | ❌ | ✅ |
| Transitive analysis | ❌ | ✅ |
| CI integration + SARIF | ❌ | ✅ |

---

## Testimonials

> *"We caught a GPL dependency in our proprietary project before release. License Lens paid for itself instantly."*
> — **CTO, SaaS Company** *(placeholder)*

> *"The inline hints make it so easy to see what you're bringing into your project."*
> — **Lead Developer** *(placeholder)*

---

## FAQ

**Q: Where does the license data come from?**
A: Licenses are resolved from Maven Central POM metadata for Java/Kotlin dependencies, with local caching for performance.

**Q: What build systems are supported?**
A: Free tier supports Gradle (Kotlin + Groovy) and Maven. Pro adds npm/yarn/pnpm, pip/poetry, and Go modules.

**Q: How does conflict detection work?**
A: The plugin detects your project's own license from the LICENSE file in the repo root and flags dependencies with incompatible licenses.

**Q: Can I enforce license policies across my team?**
A: Yes. Pro users can define policies in a `.license-lens.yaml` file that can be committed to the repository for team-wide enforcement.

**Q: Does it support transitive dependencies?**
A: Pro tier analyzes the full dependency tree including transitive dependencies and their license obligations.

---

## Get Started

Install Dependency License Lens from the JetBrains Marketplace and take control of your project's license compliance.

[Install from JetBrains Marketplace](#) | [View Documentation](#) | [Report Issues](#)
