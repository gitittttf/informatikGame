package com.informatikgame.world;

import static com.informatikgame.world.EnemyType.BIG_ZOMBIE;
import static com.informatikgame.world.EnemyType.ENDBOSS;
import static com.informatikgame.world.EnemyType.MINI_ZOMBIE;
import static com.informatikgame.world.EnemyType.SCIENTIST;

/**
 * Alles was zu einem Raum gehört an einer Stelle: Name, Story, Belohnung,
 * Bild und Gegner. Die Reihenfolge hier ist auch die Reihenfolge im Spiel.
 */
public enum RoomType {
    INTRO_ROOM("Eingangsbereich", "[Eingang]", "Ein verlassener Eingangsbereich.",
            StoryDatabank.INTRO_ROOM, StoryDatabank.INTRO_ROOM_END, UpgradeType.LIFE_UPGRADE,
            new String[]{
                "╔══════╪══════╗",
                "║      ▼      ║",
                "║   EINGANG   ║",
                "╚═════════════╝"
            },
            MINI_ZOMBIE),
    FLOOR_ROOM("Verlassener Flur", "[Flur]", "Ein dunkler Flur.",
            StoryDatabank.FLOOR_ROOM, StoryDatabank.FLOOR_ROOM_END, UpgradeType.LIFE_UPGRADE,
            new String[]{
                "╔═════════════╗",
                "║ ░░░░░░░░░░░ ║",
                "║─────────────║",
                "╚═════════════╝"
            },
            MINI_ZOMBIE, MINI_ZOMBIE),
    PANTRY_1("Speisekammer", "[Speisekammer]", "Eine geplünderte Speisekammer.",
            StoryDatabank.PANTRY_1, StoryDatabank.PANTRY_1_END, UpgradeType.PANTRY_COMPOUND_UPGRADE,
            new String[]{
                "╔═════════════╗",
                "║ ╤═╤ ╤═╤ ╤═╤ ║",
                "║ ╧ ╧ ╧o╧ ╧ ╧ ║",
                "╚═════════════╝"
            },
            MINI_ZOMBIE, MINI_ZOMBIE),
    LIBRARY_ROOM("Alte Bibliothek", "[Bibliothek]", "Eine alte Bibliothek.",
            StoryDatabank.LIBRARY_ROOM, StoryDatabank.LIBRARY_ROOM_END, UpgradeType.SKILL_UPGRADE,
            new String[]{
                "╔═════════════╗",
                "║ ▓▓▓ ▓▓▓ ▓▓▓ ║",
                "║ ▓▓▓ ▓▓▓ ▓▓▓ ║",
                "╚═════════════╝"
            },
            MINI_ZOMBIE, MINI_ZOMBIE, SCIENTIST),
    DINING_HALL("Speisesaal", "[Speisesaal]", "Ein großer Speisesaal.",
            StoryDatabank.DINING_HALL, StoryDatabank.DINING_HALL_END, UpgradeType.ATTACK_UPGRADE,
            new String[]{
                "╔═════════════╗",
                "║ ╥───╥ ╥───╥ ║",
                "║ ╨───╨ ╨───╨ ║",
                "╚═════════════╝"
            },
            MINI_ZOMBIE, MINI_ZOMBIE, MINI_ZOMBIE, SCIENTIST, SCIENTIST),
    LABORATORY("Laboratorium", "[Labor]", "Ein wissenschaftliches Labor.",
            StoryDatabank.LABORATORY, StoryDatabank.LABORATORY_END, null,
            new String[]{
                "╔═════════════╗",
                "║  ○   ◊   ○  ║",
                "║ ═╩═ ═╩═ ═╩═ ║",
                "╚═════════════╝"
            },
            SCIENTIST, SCIENTIST, SCIENTIST, SCIENTIST),
    CORRIDOR("Dunkler Korridor", "[Korridor]", "Ein schwach beleuchteter Korridor.",
            StoryDatabank.CORRIDOR, StoryDatabank.CORRIDOR_END, UpgradeType.ARMOUR_UPGRADE,
            new String[]{
                "╔═════════════╗",
                "║ ·  ·  ✦  ·  ║",
                "║═════════════║",
                "╚═════════════╝"
            },
            BIG_ZOMBIE, BIG_ZOMBIE, BIG_ZOMBIE),
    FINAL_ROOM("Boss-Kammer", "[Boss Raum]", "Die finale Kammer.",
            StoryDatabank.FINAL_ROOM, StoryDatabank.FINAL_ROOM_END, null,
            new String[]{
                "╔═════╦═╦═════╗",
                "║     ║☠║     ║",
                "║  ▒▒ ╚═╝ ▒▒  ║",
                "╚═════════════╝"
            },
            ENDBOSS);

    public final String anzeigeName;
    public final String kartenLabel;
    public final String beschreibung;
    public final StoryDatabank story;
    public final StoryDatabank endStory;
    private final UpgradeType upgrade;
    private final String[] bild;
    private final EnemyType[] enemiesInRoom;

    RoomType(String anzeigeName, String kartenLabel, String beschreibung, StoryDatabank story,
            StoryDatabank endStory, UpgradeType upgrade, String[] bild, EnemyType... enemiesInRoom) {
        this.anzeigeName = anzeigeName;
        this.kartenLabel = kartenLabel;
        this.beschreibung = beschreibung;
        this.story = story;
        this.endStory = endStory;
        this.upgrade = upgrade;
        this.bild = bild;
        this.enemiesInRoom = enemiesInRoom;
    }

    /**
     * Belohnung nach dem Raum, kann null sein
     */
    public UpgradeType getUpgrade() {
        return upgrade;
    }

    public String[] getBild() {
        return bild.clone();
    }

    /**
     * Alle Gegner (so wie auf Schwer)
     */
    public EnemyType[] getEnemiesInRoom() {
        return enemiesInRoom.clone();
    }
}
