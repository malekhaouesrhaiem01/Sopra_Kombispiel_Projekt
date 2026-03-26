# Kombispiel – Kotlin Card Game

A two-player card game developed in Kotlin as part of the Software-Praktikum at TU Dortmund.
Built with BGW GUI, following SoPra design and code review standards.

![Gameplay](https://github.com/user-attachments/assets/8277a4c6-9bcb-4185-a6ed-3d805aca0f01)

---

## Tech Stack

`Kotlin` `BGW GUI` `Gradle` `JUnit 5` `KDoc`

---

## Installation

**Voraussetzungen / Requirements**
- Java JDK 11
- Gradle (im Projekt enthalten über den Wrapper)
- Internetverbindung zum Herunterladen der BGW-Abhängigkeiten

**Schritte / Steps**

```bash
git clone https://github.com/malekhaouesrhaiem01/Sopra_Kombispiel_Projekt.git
cd Sopra_Kombispiel_Projekt
./gradlew build
./gradlew run
```

---

## Spielregeln (Deutsch)

**Kartendeck:** Standard-Deck mit 52 Karten – 4 Farben × 13 Werte

**Spielaufbau**
- Jede Spielerin erhält 7 Handkarten
- 3 offene Karten liegen als Tauschbereich in der Mitte
- 35 Karten verdeckt als Nachziehstapel
- Jede Spielerin hat einen eigenen Ablagestapel

**Spielablauf**

Pro Zug darf ein/e Spieler\*in bis zu zwei verschiedene Aktionen ausführen:

| Aktion | Beschreibung |
|---|---|
| Karte ziehen | Vom Nachziehstapel nehmen (max. 10 Handkarten) |
| Karte austauschen | Handkarte gegen Karte aus dem Tauschbereich tauschen |
| Kombination ausspielen | Gültige Kombination auf den Ablagestapel legen |
| Passen | Eine oder beide Aktionen überspringen |

**Gültige Kombinationen**

| Kombination | Beschreibung | Punkte |
|---|---|---|
| Drilling | 3 gleiche Werte (z. B. 7♣ 7♥ 7♠) | 10 |
| Vierling | 4 gleiche Werte (z. B. K♦ K♥ K♠ K♣) | 15 |
| Sequenz | ≥3 aufeinanderfolgende Karten einer Farbe | 2 pro Karte |

**Spielende:** Wenn ein/e Spieler\*in keine Handkarten mehr hat, oder beide nacheinander passen. Die meisten Punkte gewinnen.

---

## Game Rules (English)

**Deck:** Standard 52-card deck – 4 suits × 13 ranks

**Setup**
- Each player starts with 7 cards
- 3 open cards in the center as exchange area
- 35 cards face down as draw pile
- Each player has a personal discard pile

**Gameplay**

Each turn, a player may perform up to two different actions:

| Action | Description |
|---|---|
| Draw a card | Take from draw pile (max. 10 cards in hand) |
| Exchange a card | Swap a hand card with one from the exchange area |
| Play combination | Lay a valid combination on your discard pile |
| Pass | Skip one or both actions |

**Valid Combinations**

| Combination | Description | Points |
|---|---|---|
| Three of a kind | 3 cards of same rank | 10 |
| Four of a kind | 4 cards of same rank | 15 |
| Sequence | ≥3 consecutive cards of same suit | 2 per card |

**Game End:** When a player has no cards left, or both players pass consecutively. Highest score wins.

---

## Testing

```bash
./gradlew test
```

All entity and service classes are covered by JUnit 5 tests.

---

## Code Quality

- Clean layered architecture: **Entity → Service → GUI**
- Full **KDoc** documentation on all classes and methods
- Follows SoPra Code Review Criteria
- No warnings, no empty blocks
