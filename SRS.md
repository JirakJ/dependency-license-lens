# Dependency License Lens — Software Requirements Specification (SRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Úvod

### 1.1 Účel
Dependency License Lens zobrazuje licence všech závislostí přímo v build souborech (build.gradle.kts, pom.xml, package.json) formou inlay hintů a varuje před licenčními konflikty a rizikovými licencemi.

### 1.2 Rozsah
Plugin parsuje dependency tree, resolvuje licence z Maven Central / npm registry / PyPI a zobrazuje je inline v editoru. Detekuje konflikty (např. GPL v MIT projektu) a poskytuje compliance report.

---

## 2. Celkový popis

### 2.1 Perspektiva produktu
Existuje několik license scanning nástrojů (FOSSA, Snyk, WhiteSource), ale žádný neposkytuje IDE-native inline zobrazení licencí přímo v build souborech s okamžitou zpětnou vazbou.

### 2.2 Uživatelské skupiny
- **Vývojáři** — chtějí vidět licenci před přidáním závislosti
- **Legal / Compliance** — potřebují license audit
- **Open source maintaineři** — musí zajistit kompatibilitu

---

## 3. Funkční požadavky

### FR-1: License Resolution
- **FR-1.1:** Automaticky resolvovat licenci pro každou závislost z:
  - Maven Central (POM metadata)
  - npm registry (package.json license field)
  - PyPI (classifier metadata)
  - Go modules (LICENSE file detection)
- **FR-1.2:** Cache resolvovaných licencí lokálně (SQLite / JSON)
- **FR-1.3:** Fallback: LICENSE file v JAR/package, GitHub API

### FR-2: Inline zobrazení
- **FR-2.1:** Inlay hint za každou dependency deklarací: `implementation("com.google.guava:guava:33.0") // Apache-2.0`
- **FR-2.2:** Barva dle rizikovosti: 🟢 Permissive (MIT, Apache), 🟡 Weak copyleft (LGPL, MPL), 🔴 Strong copyleft (GPL, AGPL)
- **FR-2.3:** Ikona v gutteru pro dependency s problémem

### FR-3: Konflikt detekce
- **FR-3.1:** Detekce nekompatibilních licencí (např. GPL dependency v Apache projektu)
- **FR-3.2:** Projekt-level license detection (z LICENSE souboru v root)
- **FR-3.3:** Inspection s quick-fix: „Replace with compatible alternative"
- **FR-3.4:** Pravidla kompatibility konfigurovatelná (allow/deny listy)

### FR-4: Compliance Dashboard (Paid)
- **FR-4.1:** Tool window s přehledem všech závislostí a jejich licencí
- **FR-4.2:** Filtrování dle licence typu, rizika, modulu
- **FR-4.3:** SBOM export (SPDX, CycloneDX)
- **FR-4.4:** License policy definice (allowed/denied/review-required)

### FR-5: CI integrace (Paid)
- **FR-5.1:** Gradle task: `./gradlew licenseLens --policy=strict --fail-on-violation`
- **FR-5.2:** SARIF output
- **FR-5.3:** HTML report pro legal review

### FR-6: Konfigurace
- **FR-6.1:** Settings: projekt license, risk thresholds, allowed/denied licenses
- **FR-6.2:** `.license-lens.yaml` v repo root
- **FR-6.3:** Per-dependency suppress: `// license-lens:ignore`

---

## 4. Nefunkční požadavky

### NFR-1: Výkon
- License resolution asynchronní, neblokuje editor
- Cache s TTL 24 hodin (licence se nemění často)
- Batch resolution při otevření projektu

### NFR-2: Kompatibilita
- Build systémy: Gradle (Kotlin DSL + Groovy), Maven, npm/yarn/pnpm, pip/poetry
- IntelliJ IDEA 2024.3+

### NFR-3: Přesnost
- License resolution accuracy > 95 % (validace na top 1000 Maven artifacts)

---

## 5. Datový model

### 5.1 DependencyLicense
```
DependencyLicense {
  groupId: String
  artifactId: String
  version: String
  license: String (SPDX identifier)
  licenseUrl: String?
  riskLevel: Enum (PERMISSIVE, WEAK_COPYLEFT, STRONG_COPYLEFT, UNKNOWN)
  source: Enum (POM, NPM, PYPI, LICENSE_FILE, GITHUB, MANUAL)
  resolvedAt: Instant
}
```

### 5.2 LicenseConflict
```
LicenseConflict {
  projectLicense: String
  dependencyLicense: String
  dependency: DependencyLicense
  severity: Enum (ERROR, WARNING)
  description: String
}
```

---

## 6. Freemium model

### Free tier
- Inline license hinty pro Gradle/Maven (max 50 závislostí)
- Základní conflict detection
- 3 licence typy (MIT, Apache, GPL)

### Paid tier
- Neomezené závislosti
- Všechny build systémy (npm, pip, go)
- Compliance dashboard
- SBOM export (SPDX, CycloneDX)
- CI integrace
- License policy enforcement
- Full SPDX license database
