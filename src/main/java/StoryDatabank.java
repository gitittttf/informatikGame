public enum StoryDatabank {

    INTRO_ROOM("""
        Du wachst benommen in einem zerschlagenen Labor auf. Blutspuren ziehen sich über den Boden, und flackernde Lichter tauchen alles in grelles Rot. 
        Eine Tür steht einen Spalt offen – dein einziger Ausweg. Ein einsamer Zombie schlurft durch die Trümmer. Du greifst nach einer Eisenstange ...
    """),

    FLOOR_ROOM_END("""
        Du hast den Zombie erfolgreich besiegt! Zwischen den Trümmern entdeckst du eine zerfetzte Bandage, die deine Wunden heilt. (+5 HP)
        Vorsichtig bewegst du dich weiter und nimmst einen beißenden Gestank wahr. Die nächste Tür quietscht, als du sie aufstößt, und ein kalter Windhauch schlägt dir entgegen...
    """),

    ZOMBIE_ROOM("""
        Du befindest dich in einem alten Wartezimmer, das völlig verwüstet ist. Essensreste und zerstörte Möbel liegen verstreut. 
        Aus einem Lüftungsschacht brechen zwei Mini-Zombies hervor. Du musst schnell reagieren, um nicht überrascht zu werden!
    """),

    PANTRY_1("""
        Nachdem du dich weiter durch die düsteren Gänge bewegt hast, trittst du in die Vorratskammer ein. Die Regale sind größtenteils geplündert, zwischen den zerbrochenen Dosen lauern zwei hungrige Zombies. 
        Du setzt alles daran, sie zu besiegen. Nach dem Kampf spürst du, wie deine Angriffe kräftiger treffen. (+2 Damage)
    """),

    PANTRY_1_END("""
        Erschöpft lehnst du dich gegen ein Regal. Auf dem Boden liegt ein Erste-Hilfe-Koffer, der deine Lebensenergie wieder auffüllt. (+5 HP)
    """),

    LIBRARY_ROOM("""
        Vor dir erhebt sich eine massive Holztür. Als du sie öffnest, wird dir die Größe der alten Bibliothek bewusst. Überall liegen verstreute Bücher, Monitore sind zerstört. 
        Im schwachen Licht erkennst du drei Gestalten: zwei Mini-Zombies und ein mutierter Wissenschaftler. 
        Während du dich auf den Kampf vorbereitest, entdeckst du Hinweise in einem zerfallenen Buch, die dir neue Kampftechniken lehren. (+1 Finte-Level)
    """),

    DINING_HALL("""
        Nach der Bibliothek betrittst du die Mensa. Überall liegen zerbrochene Stühle und Blutflecken. Du hörst das Kratzen von Nägeln auf Metall. Fünf Gegner stürmen auf dich zu – Mini-Zombies und mutierte Wissenschaftler. 
        Nach dem erbitterten Kampf spürst du, wie deine Angriffe präziser und stärker werden. (+1 Attack)
    """),

    LABORATORY("""
        Du erreichst das zentrale Forschungslabor. Alles wirkt dokumentiert, doch alles ist fehlgeschlagen. 
        Vier Wissenschaftler-Zombies greifen dich gleichzeitig an, ihre Bewegungen sind seltsam koordiniert, als hätten sie noch Verstand. 
    """),

    CORRIDOR("""
        Ein langer, dunkler Verbindungstrakt breitet sich vor dir aus. Ein rotes Warnlicht blinkt schwach. Die Wände sind von Kratzspuren gezeichnet. 
        Drei gigantische Zombies stellen sich dir in den Weg. Sie sind langsamer, aber ihre Stärke ist enorm. 
        Zwischen den Trümmern findest du ein altes Schutzschild, das deine Rüstung verstärkt. (+3 Armour)
    """),

    PANTRY_2("""
        Du betrittst eine weitere Vorratskammer. Die Luft ist stickig, das Licht flackert nervös. Zwei Mini-Zombies stürmen auf dich zu. 
        Sie treiben dich förmlich durch den Raum, während du dich gegen sie behauptest ...
    """),

    FINAL_ROOM("""
        Die letzte Sicherheitstür öffnet sich und enthüllt ein riesiges Labor. Maschinen pfeifen, Flüssigkeiten blubbern in Tanks. 
        In der Mitte steht das Ergebnis aller Experimente: der mutierte Superzombie, zusammengesetzt aus Dutzenden Leichen. 
        Du bist allein. Es gibt kein Zurück – nur den Kampf.
    """),

    FINAL_ROOM_END("""
        Du hast es geschafft! Du besiegst den mutierten Mega-Zombie und sicherst das Serum. 
        Dank deines Einsatzes konnte die Menschheit gerettet werden.
    """);

    public String story;

    StoryDatabank(String story) {
        this.story = story;
    }

    public static String getStory(StoryDatabank story) {
        return story.toString();
    }
}
