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
- Balíčky: `com.jirak.licenselens.*`
- Licence identifikátory vždy SPDX format (MIT, Apache-2.0, GPL-3.0-only)
- Risk levels: PERMISSIVE, WEAK_COPYLEFT, STRONG_COPYLEFT, UNKNOWN
- HTTP requesty přes `HttpClient` s rate limiting (max 10 req/s)
- Texty v `messages/LicenseLensBundle.properties`
- Verzování: `2026.2.X`

## Freemium hranice
- Free: inline hints, max 50 deps, Gradle/Maven, 3 licence typy
- Paid: neomezené, všechny build systémy, dashboard, SBOM, CI, policy
