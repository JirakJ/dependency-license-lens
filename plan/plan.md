# Dependency License Lens — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — IntelliJ Platform 2024.3+, JVM 17
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
  - [ ] Dependency: `org.snakeyaml:snakeyaml`, HTTP client (ktor-client nebo java.net.http)
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jirak.licenselens`
  - [ ] Závislosti: `com.intellij.modules.platform`, `com.intellij.gradle` (optional), `com.intellij.modules.lang`
- [ ] Balíčková struktura: `com.jirak.licenselens.*`
  - [ ] `.model` — datové třídy
  - [ ] `.resolution` — license resolution z registries
  - [ ] `.conflict` — conflict detection
  - [ ] `.cache` — lokální cache
  - [ ] `.build` — build system adaptéry
  - [ ] `.ui` — inlay hints, gutter, tool window
  - [ ] `.settings` — konfigurace
- [ ] `src/main/resources/messages/LicenseLensBundle.properties`
- [ ] `.gitignore`
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
- [ ] Extension point: `com.jirak.licenselens.buildSystemAdapter`
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

## Fáze 10: Polish & Release

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
