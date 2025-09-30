# Informatik Game

Ein Spiel für den Informatikuntericht von Benedikt, Paul M., Simon

## Vorraussetzungen

### Notwendige INstallationen
1. **Visual Studio Code** ([Download](https://code.visualstudio.com/))
2. **Java 17 oder höher** ([Download](https://www.oracle.com/java/))
3. **Git** ([Download](https://git-scm.com/))

### VSCode Erweiterungen (obligatorisch)
Erweiterungen (Ctrl+Shift+X) in VSCode:
- **Extension Pack for Java** (von Microsoft)
- **Maven for Java** (von Microsoft)

## Spiel starten
### Zwei optionen
1. Die .exe im /target/ Ordner starten. Falls nicht vorhanden, vorher mit `mvn clean package -DskipTests` erstellen.
2. `mvn exec:java` im Terminal eingeben
