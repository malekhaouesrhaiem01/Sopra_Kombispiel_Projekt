Installation
Voraussetzungen

Java JDK 11

Gradle (im Projekt enthalten über den Wrapper)

Internetverbindung zum Herunterladen der BGW-Abhängigkeiten

Schritte

Repository klonen oder herunterladen:

git clone https://github.com/malekhaouesrhaiem/Sopra_Kombispiel_Projekt.git
cd Sopra_Kombispiel_Projekt


Projekt bauen:

./gradlew build


Spiel starten:

./gradlew run


Die Hauptklasse MainKt startet die Anwendung und öffnet die Game Scene.

Spielregeln (Deutsch)
Kartendeck

Standard-Kartendeck mit 52 Karten
→ 4 Farben × 13 Werte:
{Karo, Herz, Pik, Kreuz} × {2, 3, 4, 5, 6, 7, 8, 9, 10, Bube, Dame, König, Ass}

Spielaufbau

Handkarten: Jeder Spielerin erhält zu Beginn 7 Karten.

Tauschbereich: Drei offene Karten liegen in der Mitte.

Nachziehstapel: Die restlichen 35 Karten werden verdeckt in die Mitte gelegt.

Ablagestapel: Jeder Spielerin hat einen eigenen Ablagestapel (anfangs leer).

Ziel

Sammle möglichst viele Punkte durch das Ausspielen gültiger Kartenkombinationen.

Spielablauf

Der/Die Startspielerin wird zufällig bestimmt. Danach spielen die Teilnehmerinnen abwechselnd.
In jedem Zug darf ein/e Spieler*in eine oder zwei verschiedene Aktionen ausführen:

Karte ziehen

Eine Karte vom Nachziehstapel nehmen (falls vorhanden).

Maximal 10 Handkarten sind erlaubt.

Karte austauschen

Eine Handkarte mit einer Karte aus dem offenen Tauschbereich tauschen.

Die getauschte Karte vom Tauschbereich wird aufgenommen, die abgelegte ersetzt sie.

Kartenkombination(en) ausspielen

Lege gültige Kombinationen aus der Hand auf den eigenen Ablagestapel.

Gültige Kombinationen:

Drilling: Drei gleiche Werte (z. B. 7♣, 7♥, 7♠)

Vierling: Vier gleiche Werte (z. B. K♦, K♥, K♠, K♣)

Sequenz: Mind. drei aufeinanderfolgende Karten derselben Farbe
(nach Ass folgt wieder die 2, z. B. D♠, K♠, A♠, 2♠, 3♠)

Passen

Auf eine oder beide Aktionen verzichten.

Nach maximal zwei Aktionen endet der Zug, und der/die nächste Spieler*in ist an der Reihe.

Punktewertung
Kombination	Punkte
Drilling (3 gleiche Werte)	10
Vierling (4 gleiche Werte)	15
Sequenz (≥3 Karten einer Farbe)	2 Punkte pro Karte

Beispiele:

3er-Sequenz → 6 Punkte

4er-Sequenz → 8 Punkte

Spielende

Das Spiel endet, wenn:

ein/e Spieler*in keine Handkarten mehr hat, oder

beide Spieler*innen nacheinander gepasst haben.

Der/die Spieler*in mit den meisten Punkten gewinnt.

Zusätzliche Anforderungen

Spielnamen können zu Beginn konfiguriert werden.

Spieler*innen wechseln sich am gleichen Bildschirm (Hotseat-Modus) ab.

Die Handkarten des/der inaktiven Spieler*in sind verdeckt.

Ein „Nächster Spielerin“-Screen schützt die Kartenansicht beim Spielerwechsel.

Nach Spielende wird der/die Gewinner*in angezeigt.

Game Rules (English)
Deck

A standard deck of 52 cards:
4 suits × 13 ranks → {Diamonds, Hearts, Spades, Clubs} × {2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King, Ace}

Setup

Hand cards: Each player starts with 7 cards.

Exchange area: Three open cards are placed in the center.

Draw pile: The remaining 35 cards are placed face down in the middle.

Discard pile: Each player has a personal discard pile (initially empty).

Objective

Earn as many points as possible by forming valid card combinations.

Gameplay

The starting player is chosen randomly.
Each turn, a player may perform one or two different actions (no action twice):

Draw a card

Draw one card from the face-down draw pile (if not empty).

A player may not hold more than 10 cards.

Exchange a card

Swap one hand card with any card from the open exchange area.

The taken card joins your hand, and your discarded card replaces it.

Play combination(s)

Lay valid combinations from your hand on your personal discard pile.

Valid combinations:

Three of a kind: Three cards of the same rank

Four of a kind: Four cards of the same rank

Sequence: At least three consecutive cards of the same suit
(sequence continues cyclically after Ace → 2)

Pass

Skip one or both actions.

After up to two actions, the turn ends and the next player continues.

Scoring
Combination	Points
Three of a kind	10
Four of a kind	15
Sequence (≥3 cards)	2 points per card

Examples:

3-card sequence → 6 points

4-card sequence → 8 points

Game End

The game ends when:

A player has no cards left, or

Both players pass consecutively.

The player with the highest score wins.

Extra Features

Players can enter their names at startup.

Local Hotseat mode lets players alternate on the same screen.

Hands are hidden during player change.

A “Next Player” screen ensures privacy.

The winner is displayed at the end.

Testing

All entity and service classes are covered by JUnit 5 tests.
To run all tests:

./gradlew test

Code Quality and Documentation

The implementation follows the SoPra Code Review Criteria:

Each class and method is documented using KDoc.

Code compiles without warnings or empty blocks.

The design cleanly separates Entity, Service, and GUI layers.

All core features are tested and readable.
