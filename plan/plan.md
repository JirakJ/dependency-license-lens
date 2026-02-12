# Dependency License Lens — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 0: Plugin Ikona & Branding

- [ ] Navrhnout plugin ikonu (SVG + PNG)
  - [ ] Koncept: 🔍 Lupa nad licenčním textem
  - [ ] Hlavní ikona: 40×40 SVG pro Marketplace listing
  - [ ] Toolbar ikona: 16×16 + 13×13 SVG (light + dark varianta)
  - [ ] Tool window ikona: 13×13 SVG
  - [ ] `src/main/resources/META-INF/pluginIcon.svg` (40×40, light theme)
  - [ ] `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark theme)
  - [ ] Action ikony pro toolbar/menu (16×16 SVG, light + dark)
- [ ] Barevná paleta konzistentní s Compliance brandem
- [ ] Marketplace banner obrázek (1280×800)
- [ ] Screenshoty pro Marketplace listing (min 3, 1280×800 PNG)

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — IntelliJ Platform 2024.3+, JVM 17
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
  - [ ] Dependency: `org.snakeyaml:snakeyaml`, HTTP client (ktor-client nebo java.net.http)
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jakubjirak.licenselens`
  - [ ] Závislosti: `com.intellij.modules.platform`, `com.intellij.gradle` (optional), `com.intellij.modules.lang`
- [ ] Balíčková struktura: `com.jakubjirak.licenselens.*`
  - [ ] `.model` — datové třídy
  - [ ] `.resolution` — license resolution z registries
  - [ ] `.conflict` — conflict detection
  - [ ] `.cache` — lokální cache
  - [ ] `.build` — build system adaptéry
  - [ ] `.ui` — inlay hints, gutter, tool window
  - [ ] `.settings` — konfigurace
- [ ] `src/main/resources/messages/LicenseLensBundle.properties`
- [ ] `.gitignore` (build/, .idea/, .gradle/, .kotlin/, out/)
- [ ] `gradle/libs.versions.toml` — version catalog
- [ ] `LICENSE` (Apache-2.0)
- [ ] `.editorconfig` (Kotlin style conventions)
- [ ] Detekt konfigurace (`detekt.yml`)
- [ ] GitHub Actions CI workflow (`.github/workflows/build.yml`)
- [ ] Ověřit `./gradlew build`

## Fáze 2: Datový model

- [ ] `DependencyInfo.kt` — `groupId`, `artifactId`, `version`, `buildSystem: BuildSystem`
- [ ] `DependencyLicense.kt` — `dependency: DependencyInfo`, `license: String` (SPDX), `licenseUrl`, `riskLevel: RiskLevel`, `source: ResolutionSource`, `resolvedAt: Instant`
- [ ] `RiskLevel.kt` — enum: PERMISSIVE, WEAK_COPYLEFT, STRONG_COPYLEFT, UNKNOWN
- [ ] `LicenseConflict.kt` — `projectLicense`, `dependencyLicense`, `dependency`, `severity`, `description`
- [ ] `BuildSystem.kt` — enum: GRADLE_KTS, GRADLE_GROOVY, MAVEN, NPM, PIP
- [ ] `ResolutionSource.kt` — enum: POM, NPM_REGISTRY, PYPI, LICENSE_FILE, GITHUB, MANUAL
- [ ] SPDX license database — `SpdxLicenseDatabase.kt` — mapování SPDX ID → metadata + kompatibilita
- [ ] Unit testy

## Fáze 3: Build System Adaptéry

- [ ] `BuildSystemAdapter.kt` — interface
  - [ ] `fun extractDependencies(file: PsiFile): List<DependencyInfo>`
  - [ ] `fun getProjectLicense(): String?`
  - [ ] `fun getDependencyPsiElement(file: PsiFile, dep: DependencyInfo): PsiElement?`
- [ ] `GradleKtsAdapter.kt` — parsuje `build.gradle.kts`
  - [ ] Rozpoznání `implementation()`, `api()`, `compileOnly()`, `runtimeOnly()`, `testImplementation()`
  - [ ] Extrakce group:artifact:version z string notace
  - [ ] Extrakce z dependency catalog (libs.x.y)
- [ ] `GradleGroovyAdapter.kt` — parsuje `build.gradle`
- [ ] `MavenAdapter.kt` — parsuje `pom.xml` (dependency element → groupId/artifactId/version)
- [ ] Extension point: `com.jakubjirak.licenselens.buildSystemAdapter`
- [ ] Unit testy pro každý adaptér (fixture soubory s build scripty)

## Fáze 4: License Resolution

- [ ] `LicenseResolver.kt` — interface
  - [ ] `suspend fun resolve(dependency: DependencyInfo): DependencyLicense?`
- [ ] `MavenCentralResolver.kt`
  - [ ] HTTP GET na `https://repo1.maven.org/maven2/{group}/{artifact}/{version}/{artifact}-{version}.pom`
  - [ ] Parse `<licenses>` element z POM XML
  - [ ] Mapování license name → SPDX ID
- [ ] `NpmRegistryResolver.kt`
  - [ ] HTTP GET na `https://registry.npmjs.org/{package}/{version}`
  - [ ] Parse `license` field z JSON
- [ ] `PyPIResolver.kt`
  - [ ] HTTP GET na `https://pypi.org/pypi/{package}/{version}/json`
  - [ ] Parse `license` z classifiers/info
- [ ] `CompositeResolver.kt` — chain resolverů s fallback
- [ ] Rate limiting: max 10 req/s (semaphore)
- [ ] Error handling: timeout, 404, network failure → UNKNOWN
- [ ] Unit testy (mock HTTP responses)

## Fáze 5: License Cache

- [ ] `LicenseCache.kt`
  - [ ] Lokální SQLite databáze nebo JSON soubor v `.idea/license-lens-cache/`
  - [ ] Key: `groupId:artifactId:version`, value: `DependencyLicense`
  - [ ] TTL: 24 hodin (licence se nemění často)
  - [ ] Cache invalidace: manuální + version change
- [ ] `CacheWarmupService.kt`
  - [ ] Při otevření projektu: batch resolve všech závislostí na pozadí
  - [ ] Progress bar: „Resolving dependency licenses..."
- [ ] Unit testy

## Fáze 6: Conflict Detection

- [ ] `ConflictDetector.kt`
  - [ ] Input: projekt licence + list `DependencyLicense`
  - [ ] Kompatibilní matice: `LicenseCompatibilityMatrix.kt`
    - [ ] MIT → kompatibilní s vším
    - [ ] Apache-2.0 → nekompatibilní s GPL-2.0-only
    - [ ] GPL-3.0 → vyžaduje GPL-3.0 projekt
    - [ ] LGPL → OK v non-LGPL pokud dynamicky linkováno
  - [ ] Output: `List<LicenseConflict>`
- [ ] `ProjectLicenseDetector.kt`
  - [ ] Detekce projekt licence z: LICENSE/LICENSE.md soubor, POM `<licenses>`, package.json `license`
- [ ] Unit testy pro každou kombinaci

## Fáze 7: Inlay Hints

- [ ] `LicenseInlayHintProvider.kt` implements `InlayHintsProvider`
  - [ ] Registrace v `plugin.xml`
  - [ ] Za každou dependency deklarací: `// Apache-2.0`
  - [ ] Barva dle risk level: 🟢 permissive, 🟡 weak copyleft, 🔴 strong copyleft, ⚪ unknown
  - [ ] Klik na hint → otevře licenci URL
- [ ] Gutter ikony pro konflikty (⚠️ / ❌)
- [ ] Hover tooltip: licence detail, zdroj, resolved timestamp

## Fáze 8: Inspection

- [ ] `LicenseConflictInspection.kt` extends `LocalInspectionTool`
  - [ ] Highlight: dependency s konfliktem
  - [ ] Quick-fix: „View license details"
  - [ ] Quick-fix: „Add to allowed licenses"
  - [ ] Quick-fix: „Suppress for this dependency"
- [ ] `UnknownLicenseInspection.kt`
  - [ ] Warning pro dependency s neresolvovanou licencí
  - [ ] Quick-fix: „Set license manually"
- [ ] Inspection testy

## Fáze 9: Konfigurace

- [ ] `LicenseLensSettings.kt` implements `PersistentStateComponent`
  - [ ] Projekt licence (override), allowed/denied licence listy
  - [ ] Risk threshold, cache TTL
  - [ ] Enabled build systems
- [ ] `LicenseLensConfigurable.kt` — Settings → Tools → License Lens
  - [ ] Project license selector, allowed/denied lists, build system toggles
- [ ] `.license-lens.yaml` team config
- [ ] Unit testy

## Fáze 10: Freemium & Licensing Infrastructure

- [ ] Integrace JetBrains Marketplace Licensing API
  - [ ] `LicenseChecker.kt` — ověření licence přes `com.intellij.ide.plugins.PluginManager` a Marketplace API
  - [ ] `LicenseStatus.kt` — enum: FREE, TRIAL, PAID, EXPIRED
  - [ ] `FreemiumGate.kt` — centrální guard pro paid features
    - [ ] `fun isPaidFeatureAvailable(featureId: String): Boolean`
    - [ ] Graceful degradation — paid UI elementy skryté/disabled ve free tieru
  - [ ] Trial period: 30 dní plný přístup
- [ ] `plugin.xml` — `<product-descriptor>` element pro Marketplace licensing
  - [ ] `code` atribut (unique product code)
  - [ ] `release-date`, `release-version`
- [ ] Nag screen / upgrade prompt
  - [ ] Non-intrusive balloon notification při pokusu o paid feature
  - [ ] „Upgrade to Pro" action → Marketplace stránka
  - [ ] Max 1× denně (ne spam)
- [ ] Feature flags
  - [ ] `FeatureRegistry.kt` — definice free vs. paid features
  - [ ] Každý paid feature má `@PaidFeature("feature.id")` anotaci nebo guard check
- [ ] Unit testy pro licensing logic (mock Marketplace API)

## Fáze 11: Production-Grade Quality

- [ ] Error Handling & Resilience
  - [ ] Globální exception handler (`ErrorReportSubmitter` implementace)
  - [ ] Graceful degradation — plugin nesmí crashnout IDE
  - [ ] Try-catch na všech EP entry pointech
  - [ ] Timeout na všech I/O operacích (network, git, file system)
  - [ ] Rate limiting na API volání
- [ ] Logging
  - [ ] `Logger` instance v každé třídě (IntelliJ `Logger.getInstance()`)
  - [ ] Structured logging: DEBUG pro vývoj, INFO pro operace, WARN/ERROR pro problémy
  - [ ] Žádné citlivé údaje v logech
- [ ] Telemetrie (opt-in, anonymní)
  - [ ] `PluginUsageTracker.kt` — FUS (Feature Usage Statistics) integrace
  - [ ] Metriky: aktivace featury, error rate, latence
  - [ ] Registrace FUS group v `plugin.xml`
- [ ] Performance
  - [ ] Profiling na velkých projektech (10k+ souborů)
  - [ ] Background thread pro všechny I/O operace (nesmí blokovat EDT)
  - [ ] Read/write action správně (ReadAction, WriteAction, smart mode check)
  - [ ] Disposal pattern: `Disposable` pro všechny listeners a services
  - [ ] Memory leak check (WeakReference kde potřeba)
- [ ] Testing — production úroveň
  - [ ] Unit testy: ≥80 % coverage core logiky
  - [ ] Integrační testy: `BasePlatformTestCase` pro PSI, inspections, EP
  - [ ] UI testy: `HeavyPlatformTestCase` pro tool windows, dialogy
  - [ ] Performance testy: benchmark na velkých souborech
- [ ] CI/CD Pipeline
  - [ ] GitHub Actions workflow:
    - [ ] `./gradlew check` — kompilace + testy
    - [ ] `./gradlew verifyPlugin` — plugin strukturální validace
    - [ ] `./gradlew verifyPluginProjectConfiguration` — metadata validace
    - [ ] `./gradlew runPluginVerifier` — binární kompatibilita s target IDE verzemi
    - [ ] Qodana scan (JetBrains code quality)
  - [ ] Release workflow:
    - [ ] Tag-based release (push tag → build → publish)
    - [ ] `./gradlew publishPlugin` — automatický upload na Marketplace
    - [ ] Changelog generace z git commitů
  - [ ] Dependabot / Renovate pro dependency updates
- [ ] Kódová kvalita
  - [ ] Kotlin coding conventions (official JetBrains style)
  - [ ] Detekt nebo ktlint pro statickou analýzu
  - [ ] Žádné deprecated API (IntelliJ Platform compatibility)
  - [ ] `@ApiStatus.Experimental` / `@ApiStatus.Internal` anotace
- [ ] Dokumentace
  - [ ] KDoc pro všechny public API
  - [ ] README.md: instalace, konfigurace, screenshoty, FAQ
  - [ ] CHANGELOG.md (Keep a Changelog format)
  - [ ] CONTRIBUTING.md (pokud open source)

## Fáze 12: Polish & Release

- [ ] README.md
- [ ] `./gradlew verifyPlugin`, `./gradlew buildPlugin`
- [ ] Manuální testování: Gradle KTS, Gradle Groovy, Maven projekty
- [ ] Cross-IDE test
- [ ] Marketplace listing
- [ ] Git tag `v2026.2.1`

---

## Budoucí fáze (Paid)

- [ ] npm, pip, go module adaptéry
- [ ] Compliance dashboard (tool window)
- [ ] SBOM export (SPDX, CycloneDX)
- [ ] License policy enforcement
- [ ] CI integrace (Gradle task, SARIF)
- [ ] Neomezené závislosti (free limit: 50)
- [ ] Full SPDX database
