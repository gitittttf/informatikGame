package com.informatikgame.world;

public enum StoryDatabank {

    // ===================== INTRO =====================
    INTRO_ROOM("""
        Du wachst benommen im Eingangsbereich eines zerschlagenen Labors auf. Blutspuren ziehen sich über den Boden, und flackernde Lichter tauchen alles in grelles Rot.
        Eine Tür steht einen Spalt offen - dein einziger Ausweg. Ein einsamer Zombie schlurft durch die Trümmer. Du greifst nach deiner Waffe ...
    """),
    INTRO_ROOM_END("""
        Du hast den Zombie erfolgreich besiegt! Zwischen den Trümmern entdeckst du eine zerfetzte Bandage, die deine Wunden heilt.
        Vorsichtig bewegst du dich weiter und nimmst einen beißenden Gestank wahr. Die nächste Tür quietscht, als du sie aufstößt, und ein kalter Windhauch schlägt dir entgegen...
    """),
    // ===================== FLOOR =====================
    FLOOR_ROOM("""
        Du trittst in den nächsten Raum ein und spürst die stickige Luft. Blutige Spuren ziehen sich über den Boden, und irgendwo klirren zerbrochene Glasflaschen.
        Zwei Mini-Zombies tauchen aus der Dunkelheit auf - du musst reagieren!
    """),
    FLOOR_ROOM_END("""
        Du konntest die Monster besiegen. Deine Lebensenergie wird durch eine gefundene Bandage wiederhergestellt.
    """),
    // ===================== PANTRY 1 =====================
    PANTRY_1("""
        Die Vorratskammer ist geplündert. Zwischen den leeren Regalen lauern zwei hungrige Zombies. Du bereitest dich auf den Kampf vor ...
    """),
    PANTRY_1_END("""
        Nach einem harten Kampf bist du erschöpft, aber deine Angriffe wirken nun stärker.
        Außerdem findest du auf dem Boden einen Heiltrank, der deine Lebensenergie auffüllt.
    """),
    // ===================== LIBRARY =====================
    LIBRARY_ROOM("""
        Vor dir erhebt sich eine massive Holztür. Als du sie öffnest, wird dir die Größe der alten Bibliothek bewusst. Überall liegen verstreute Bücher, Monitore sind zerstört.
        Im schwachen Licht erkennst du drei Gestalten: zwei Mini-Zombies und ein mutierter Wissenschaftler. Bereite dich auf den Kampf vor!
    """),
    LIBRARY_ROOM_END("""
        Nachdem du die Gegner besiegt hast, entdeckst du Hinweise in einem zerfallenen Buch, die dir neue Kampftechniken lehren.
    """),
    // ===================== DINING HALL =====================
    DINING_HALL("""
        Die Mensa liegt vor dir, zerstörtes Mobiliar und Blutflecken überall. Mini-Zombies und mutierte Wissenschaftler stürmen auf dich zu.
        Kein Rückzug möglich, bereite dich auf den Kampf vor!
    """),
    DINING_HALL_END("""
        Nach dem erbitterten Kampf spürst du, wie deine Angriffe präziser und stärker werden.
    """),
    // ===================== LABORATORY =====================
    LABORATORY("""
        Du betrittst das zentrale Forschungslabor. Alles wirkt dokumentiert, doch alles ist fehlgeschlagen.
        Wissenschaftler-Zombies greifen dich gleichzeitig an. Ihre Bewegungen wirken seltsam koordiniert ...
    """),
    LABORATORY_END("""
        Du hast die Gegner besiegt, aber der Weg zum nächsten Bereich ist noch gefährlich.
    """),
    // ===================== CORRIDOR =====================
    CORRIDOR("""
        Ein langer, dunkler Verbindungstrakt breitet sich vor dir aus. Ein rotes Warnlicht blinkt schwach. Die Wände sind von Kratzspuren gezeichnet.
        Gigantische Zombies stellen sich dir in den Weg. Bereite dich auf den Kampf vor!
    """),
    CORRIDOR_END("""
        Nach dem Kampf findest du ein altes Schutzschild, das deine Rüstung verstärkt.
    """),
    // ===================== FINAL ROOM =====================
    FINAL_ROOM("""
        Die letzte Sicherheitstür öffnet sich und enthüllt ein riesiges Labor. Maschinen pfeifen, Flüssigkeiten blubbern in Tanks.
        In der Mitte steht der mutierte Superzombie, zusammengesetzt aus Dutzenden Leichen. Nur der Kampf zählt jetzt.
    """),
    FINAL_ROOM_END("""
        Du hast es geschafft! Du besiegst den mutierten Mega-Zombie und sicherst das Serum.
        Dank deines Einsatzes konnte die Menschheit gerettet werden.
    """);

    public final String story;

    StoryDatabank(String story) {
        this.story = story.strip();
    }
}
