# Dependency License Lens — Business Requirements Specification (BRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Executive Summary

Dependency License Lens přináší licenční transparentnost přímo do editoru. Vývojář vidí licenci každé závislosti inline v build souboru a je okamžitě varován před konflikty. Compliance bez opuštění IDE.

---

## 2. Obchodní cíle

### 2.1 KPI
| Metrika | Cíl (6 měsíců) | Cíl (12 měsíců) |
|---------|:---:|:---:|
| Celkové instalace | 7 000 | 28 000 |
| MAU | 3 000 | 11 000 |
| Paid konverze | 5 % | 7 % |
| Marketplace rating | ≥ 4.0 | ≥ 4.3 |

---

## 3. Cílový trh

- **Enterprise vývojáři** (40 %) — compliance požadavky
- **Open source maintaineři** (30 %) — licenční kompatibilita
- **Startupy** (30 %) — prevence licenčních problémů před funding

---

## 4. Konkurenční prostředí

| Řešení | Omezení |
|--------|---------|
| FOSSA / Snyk / WhiteSource | SaaS, drahé, ne IDE-native inline |
| Gradle License Report plugin | Jen report, ne inline hints |
| npm license-checker | CLI only |

**Diferenciace:** IDE-native inline zobrazení + okamžitý conflict alert při psaní.

---

## 5. Revenue model

| Tier | Cena |
|------|------|
| **Free** | $0 — inline hints, max 50 deps, Gradle/Maven |
| **Pro** | $3.90/měsíc nebo $39/rok — neomezené, všechny build systémy, SBOM |
| **Team** | $7.90/user/měsíc — Pro + license policy, compliance reports |

---

## 6. Go-to-Market

1. Blog: „Do You Know What Licenses Your Dependencies Use? You Should."
2. Legal/compliance community outreach
3. Integration s SBOM standardy (SPDX, CycloneDX) pro enterprise credibilitu

---

## 7. Milníky

| Milestone | Popis |
|-----------|-------|
| **M1: MVP** | Gradle/Maven inline hints, conflict detection |
| **M2: Multi-build** | npm, pip, go modul podpora |
| **M3: Dashboard** | Compliance dashboard, SBOM export |
| **M4: CI** | Gradle task, SARIF, policy enforcement |
| **M5: Enterprise** | Team tier, centrální policy |

---

## 8. Success Criteria

1. 15 000+ instalací do 9 měsíců
2. ≥ 5 % paid konverze (compliance = high willingness-to-pay)
3. SBOM export jako enterprise differentiátor
4. Alespoň 1 enterprise zákazník
