# Dependency License Lens — Git Enhancement Specifikace

**Verze:** 2026.2.1
**Status:** Plánováno
**Závislost:** Git4Idea API, `com.intellij.git4idea`

---

## Přehled

Rozšíření pluginu o Git-based funkce pro detekci změn licencí per commit, pre-commit hook blokující problémové licence a automatický licence audit v PR review. Tyto funkce umožňují zachytit licenční problémy co nejdříve ve vývojovém procesu — ideálně před commitem nebo při PR review.

---

## FR-G1: License Change Detection per Commit

**Popis:** Detekce kdy se změnila licence dependency — monitoring git diff na lockfiles a build souborech pro identifikaci licenčních změn.

### Akceptační kritéria

- [ ] AC-1: Plugin sleduje git diff na souborech: `build.gradle.kts`, `build.gradle`, `pom.xml`, `package-lock.json`, `yarn.lock`, `requirements.txt`, `go.sum`
- [ ] AC-2: Při detekci nové nebo změněné dependency automaticky provede license resolution
- [ ] AC-3: Notifikace: „License changed: library X changed from MIT to GPL-3.0 in commit abc1234"
- [ ] AC-4: Timeline view: chronologický přehled licenčních změn seřazený dle commitů
- [ ] AC-5: Highlight v VCS Log: commity které změnily dependency licence (ikona ⚖️)
- [ ] AC-6: Diff view: side-by-side porovnání licence stavu před a po commitu

### Datový model — rozšíření

```kotlin
data class LicenseChangeEvent(
    val commitSha: String,
    val commitMessage: String,
    val commitAuthor: String,
    val commitDate: Instant,
    val dependency: DependencyInfo,
    val changeType: LicenseChangeType,
    val previousLicense: String?,      // SPDX ID, null pokud nově přidaná
    val newLicense: String?,           // SPDX ID, null pokud odebraná
    val previousRiskLevel: RiskLevel?,
    val newRiskLevel: RiskLevel?,
    val sourceFile: String             // build.gradle.kts, pom.xml, ...
)

enum class LicenseChangeType {
    DEPENDENCY_ADDED,       // nová dependency
    DEPENDENCY_REMOVED,     // dependency odebrána
    VERSION_CHANGED,        // verze změněna (může změnit licenci)
    LICENSE_CHANGED         // licence explicitně změněna
}
```

### Implementační poznámky

- `GitHistoryUtils.history()` pro sledování commitů měnících build/lock soubory
- `git diff <sha>~1 <sha> -- build.gradle.kts` pro file-level diff
- Parsování diff výstupu pro extrakci přidaných/odebraných dependencies
- Batch license resolution pro nově detekované dependencies
- VCS Log integrace: `VcsLogHighlighterFactory` pro highlight licenčních commitů

---

## FR-G2: Pre-Commit Hook

**Popis:** Git pre-commit hook který blokuje přidání dependency s problémovou licencí — zachycení problému před commitem.

### Akceptační kritéria

- [ ] AC-1: Action „Install Pre-Commit Hook" — nainstaluje git hook do `.git/hooks/pre-commit`
- [ ] AC-2: Hook kontroluje staged soubory (build files, lockfiles) na nové dependencies
- [ ] AC-3: Pro každou novou dependency provede license resolution a ověří proti allowed/denied listu
- [ ] AC-4: Blokuje commit pokud nová dependency má: STRONG_COPYLEFT licenci (konfigurovatelné), nebo licenci na denied listu
- [ ] AC-5: Výstup hooku: srozumitelná chybová zpráva s názvem dependency, licencí a důvodem blokace
- [ ] AC-6: Bypass: `git commit --no-verify` nebo `SKIP_LICENSE_CHECK=1` environment variable
- [ ] AC-7: Hook je generován jako shell script kompatibilní s bash/zsh (cross-platform: macOS, Linux, Windows Git Bash)

### Datový model — rozšíření

```kotlin
data class PreCommitCheckResult(
    val passed: Boolean,
    val blockedDependencies: List<BlockedDependency>,
    val warnings: List<LicenseWarning>,
    val checkedFiles: List<String>,
    val duration: Duration
)

data class BlockedDependency(
    val dependency: DependencyInfo,
    val license: String,           // SPDX ID
    val riskLevel: RiskLevel,
    val reason: String,            // "License GPL-3.0 is on denied list"
    val sourceFile: String
)

data class LicenseWarning(
    val dependency: DependencyInfo,
    val license: String,
    val message: String            // "Unknown license — review manually"
)
```

### Implementační poznámky

- Hook generátor: `PreCommitHookGenerator.kt` — generuje shell script
- Hook volá standalone CLI mode pluginu nebo Python/Node helper script
- Alternativa: integrace s `pre-commit` framework (`.pre-commit-config.yaml`)
- IDE action: „Install Hook" s dialogem pro konfigurace (allowed/denied lists)
- Hook template v resources: `src/main/resources/hooks/pre-commit.sh.template`

---

## FR-G3: PR License Audit

**Popis:** Automatický check licencí v PR review — generuje compliance report pro všechny dependency změny v PR.

### Akceptační kritéria

- [ ] AC-1: Action „Generate PR License Audit" dostupná při aktivním branch (ne main/master)
- [ ] AC-2: Porovná dependency stav aktuálního branch vs. base branch (main/master)
- [ ] AC-3: Report obsahuje: nově přidané dependencies s licencemi, odebrané dependencies, změněné verze s potenciální licenční změnou
- [ ] AC-4: Compliance status: ✅ PASS (žádné problémy), ⚠️ WARNING (unknown licence), ❌ FAIL (konflikty nebo denied licence)
- [ ] AC-5: Report formát: Markdown (pro PR description), HTML (pro tool window), JSON (pro CI integrace)
- [ ] AC-6: Automatická aktualizace reportu při novém commitu na branch
- [ ] AC-7: Copy-to-clipboard action pro snadné vložení do PR description

### Datový model — rozšíření

```kotlin
data class PrLicenseAudit(
    val sourceBranch: String,
    val targetBranch: String,
    val auditDate: Instant,
    val overallStatus: AuditStatus,
    val addedDependencies: List<AuditedDependency>,
    val removedDependencies: List<AuditedDependency>,
    val changedDependencies: List<DependencyVersionChange>,
    val conflicts: List<LicenseConflict>,
    val summary: String               // auto-generovaný textový souhrn
)

data class AuditedDependency(
    val dependency: DependencyInfo,
    val license: String?,             // SPDX ID
    val riskLevel: RiskLevel,
    val complianceStatus: AuditStatus
)

data class DependencyVersionChange(
    val dependency: DependencyInfo,
    val previousVersion: String,
    val newVersion: String,
    val previousLicense: String?,
    val newLicense: String?,
    val licenseChanged: Boolean
)

enum class AuditStatus {
    PASS,      // žádné problémy
    WARNING,   // unknown licence nebo weak copyleft
    FAIL       // denied licence nebo strong copyleft konflikt
}
```

### Implementační poznámky

- `git diff --name-only HEAD...origin/main` pro seznam změněných build souborů
- `git show origin/main:<file>` pro baseline verzi build souboru
- Parsování obou verzí build souborů přes existující `BuildSystemAdapter`
- Markdown template v resources pro report generování
- Clipboard: `CopyPasteManager.getInstance().setContents(StringSelection(markdown))`
- Auto-update: `GitRepositoryChangeListener` pro detekci nových commitů

---

## Konfigurace — rozšíření `LicenseLensSettings`

```yaml
# .license-lens.yaml — nové sekce
git-enhancement:
  license-change-detection:
    enabled: true
    monitored-files:
      - "build.gradle.kts"
      - "build.gradle"
      - "pom.xml"
      - "package-lock.json"
      - "yarn.lock"
      - "requirements.txt"
      - "go.sum"
    notify-on-change: true
    highlight-in-vcs-log: true
  pre-commit-hook:
    enabled: false                    # musí být explicitně zapnut
    block-strong-copyleft: true
    block-unknown: false              # neblokovat unknown, jen warning
    denied-licenses:
      - "GPL-3.0-only"
      - "GPL-3.0-or-later"
      - "AGPL-3.0-only"
    allowed-licenses: []              # prázdný = vše kromě denied
  pr-license-audit:
    enabled: true
    base-branch: "main"              # auto-detect pokud neuvedeno
    auto-generate: true              # automaticky generovat při push
    report-format: "markdown"        # markdown | html | json
    include-transitive: false        # zahrnout transitívní dependencies
```
