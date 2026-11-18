# Informatik Game

Ein Spiel für den Informatikunterricht von Benedikt, Paul M., Simon

## Voraussetzungen

### Notwendige Installationen
1. **Visual Studio Code** ([Download](https://code.visualstudio.com/))
2. **Java 17 oder höher** ([Download](https://adoptium.net/))
3. **Git** ([Download](https://git-scm.com/))

### VSCode Erweiterungen (obligatorisch)
Erweiterungen (Ctrl+Shift+X) in VSCode:
- **Extension Pack for Java** (von Microsoft)
- **Maven for Java** (von Microsoft)

## Spiel starten
### Zwei Optionen
1. Die .exe im /target/ Ordner starten. Falls nicht vorhanden, vorher mit `mvn clean package` erstellen.
2. `mvn exec:java` im Terminal eingeben

## Tests
`mvn test` lässt alle Tests laufen. Der `BalanceTest` spielt pro Schwierigkeit ein paar tausend Runden automatisch durch und schlägt fehl, wenn das Spiel durch geänderte Werte zu leicht oder unschaffbar wird.

## Steuerung
- **↑ ↓ / Enter**: Menüs
- **← → oder Zahlen**: Gegner, Finte- und Wuchtschlag-Level wählen, **Enter** weiter
- **ESC**: im Kampf einen Schritt zurück, sonst Spiel abbrechen (mit Nachfrage)
- **M**: Karte
- **Enter** während das Kampf-Log läuft: überspringen

## Schwierigkeit
Nach der Charakterwahl kann man die Schwierigkeit auswählen:

| | Gegner pro Raum | Gegner-Stärke | Heilung nach jedem Raum |
|---|---|---|---|
| Leicht | max. 3 | 50% HP, Angriff -3 | voll |
| Normal | max. 4 | 75% HP, Angriff -2 | voll |
| Schwer | alle | 80% HP, Angriff -2 | 75% der fehlenden HP |

Die Werte stehen in `world/Schwierigkeit.java`.

## Aufbau
- `core`: `GameManager` (Spielablauf in Phasen) und die Ereignisse die an die UI gehen
- `combat`: `FightManager` (Runden und Initiative)
- `entities`: Spieler, Gegner, Würfel
- `world`: Räume, Story, Gegner- und Spielertypen, Upgrades, Schwierigkeit
- `ui`: Screens (Menüs, Einstellungen), `ui/gameplay` ist der Spiel-Screen mit Panels, Log, Story und Karte

Ein neuer Raum braucht nur einen Eintrag in `RoomType` und die Story in `StoryDatabank`.
