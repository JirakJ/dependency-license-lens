# Copilot Instructions — Dependency License Lens

## Projekt
JetBrains IDE plugin — zobrazuje licence závislostí inline v build souborech a detekuje licenční konflikty.

## Technický stack
- **Jazyk:** Kotlin (JVM 17+)
- **Build:** Gradle (Kotlin DSL) s `org.jetbrains.intellij.platform` plugin 2.x
- **Platform:** IntelliJ Platform SDK 2024.3+
- **Testování:** JUnit 5 + `BasePlatformTestCase`

## Build a testy
```bash
./gradlew build
./gradlew test
./gradlew test --tests "*.LicenseResolverTest.testMavenLicense"
./gradlew runIde
./gradlew verifyPlugin
```

## Architektura
- **Resolution:** `LicenseResolver` — resolvuje licence z Maven Central, npm, PyPI (HTTP klient + cache)
- **Cache:** Lokální SQLite/JSON cache resolvovaných licencí (TTL 24h)
- **Conflict engine:** `ConflictDetector` — porovnává dependency licence s projekt licencí dle kompatibility matice
- **UI:** `InlayHintProvider` (inline licence za dependency), `LineMarkerProvider` (gutter ikony), `ToolWindowFactory` (dashboard)
- **Build system support:** Abstrakce `BuildSystemAdapter` — implementace pro Gradle, Maven, npm, pip

## Konvence
- Balíčky: `com.jakubjirak.licenselens.*`
- Licence identifikátory vždy SPDX format (MIT, Apache-2.0, GPL-3.0-only)
- Risk levels: PERMISSIVE, WEAK_COPYLEFT, STRONG_COPYLEFT, UNKNOWN
- HTTP requesty přes `HttpClient` s rate limiting (max 10 req/s)
- Texty v `messages/LicenseLensBundle.properties`
- Verzování: `2026.2.X`

## Production-grade požadavky
- Všechny I/O operace na background threadech (`Task.Backgroundable`, coroutines)
- Read/Write actions správně (`ReadAction.run {}`, `WriteAction.run {}`)
- `Disposable` pattern pro listeners a services
- Exception handling: žádné unhandled exceptions, graceful degradation
- Logging: `Logger.getInstance(MyClass::class.java)` v každé třídě
- EDT nesmí být blokován — žádné síťové/git/file operace na EDT
- Smart mode check (`DumbService.isDumb()`) kde potřeba

## Freemium architektura
- `FreemiumGate` centrální guard: `FreemiumGate.isPaidFeatureAvailable("feature.id")`
- JetBrains Marketplace Licensing API přes `<product-descriptor>` v `plugin.xml`
- Paid UI elementy: skryté nebo disabled ve free tieru (ne crash/error)
- Trial: 30 dní plný přístup, pak downgrade na free tier
- Upgrade prompt: max 1× denně, non-intrusive balloon

## CI/CD
- GitHub Actions: `./gradlew check`, `verifyPlugin`, `runPluginVerifier`
- Detekt pro statickou analýzu Kotlin kódu
- Tag-based release → `publishPlugin` na Marketplace
- Kotlin coding conventions (JetBrains official style)

## Ikona
- `src/main/resources/META-INF/pluginIcon.svg` (40×40, light)
- `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark)
- Action ikony: 16×16 + 13×13 SVG (light + dark varianty)

## Freemium hranice
- Free: inline hints, max 50 deps, Gradle/Maven, 3 licence typy
- Paid: neomezené, všechny build systémy, dashboard, SBOM, CI, policy
