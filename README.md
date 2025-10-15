Installation
Requirements

Java JDK 17 or higher

Gradle (comes with the project via Gradle Wrapper)

Internet access to resolve BGW dependencies

Setup Steps

Clone or download the repository:

git clone https://github.com/YourUsername/Kombispiel.git
cd Kombispiel


Build the project using Gradle:

./gradlew build


Run the application:

./gradlew run


The main class (MainKt) will start the application and open the Game Scene.

How to Play
Objective

Each player competes to achieve the highest score by making optimal combinations during their turn.
You can play against other players or against AI-controlled bots.

Basic Rules

The game starts once all players have joined (either local or networked).

Players take turns according to the order shown in the UI.

On your turn, you can:

Make a move according to the combination rules.

Skip or pass if no valid move exists.

The round ends when no further combinations are possible.

Scores are calculated based on the combinations achieved.

The player with the highest total score wins.

Each game can be configured with different difficulty levels for bots or network play options.

Testing

All entity and service classes are covered by JUnit 5 tests.
To run the test suite:

./gradlew test


Tests verify core logic, service interactions, and edge cases to ensure consistent gameplay behavior.

Code Quality and Documentation

The code adheres to the SoPra Code Review Criteria:

All classes and public methods are documented using KDoc.

No unused imports, legacy code, or warnings.

Each function is tested and readable.

Design decisions follow a clear separation between GUI, Service, and Entity layers.
