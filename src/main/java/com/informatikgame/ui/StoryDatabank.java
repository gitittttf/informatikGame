package com.informatikgame.ui;

public enum StoryDatabank {

    INTRO_ROOM("""
        Du wachst benommen in einem zerschlagenen Labor auf. Blutspuren ziehen sich über den Boden, und flackernde Lichter tauchen alles in grelles Rot.
        Eine Tür quietscht offen - dein einziger Ausweg. Ein einsamer Zombie schlurft durch die Trümmer. Du greifst nach einer Eisenstange ...
    """),
    FLOOR_ROOM("""
        Der Boden ist übersät mit herumliegenden Dokumenten und zerbrochenem Glas. Zwei schlurfende Zombies bemerken dich sofort.
        Es ist klar: Du bist nicht mehr in einem normalen Forschungskomplex - irgendetwas ist hier entsetzlich schiefgelaufen.
    """),
    ZOMBIE_ROOM("""
        In diesem verlassenen Aufenthaltsraum liegt alles durcheinander. Essensreste, zerstörte Möbel - als wäre hier gekämpft worden.
        Zwei Mini-Zombies brechen aus einem Lüftungsschacht hervor. Du musst schnell reagieren!
    """),
    PANTRY_1("""
        Du betrittst eine Vorratskammer. Die Regale sind geplündert. Zwischen leeren Dosen und Konservengläsern lauern zwei Zombies.
        Sie scheinen hier eingeschlossen gewesen zu sein - ausgehungert und wütend.
    """),
    LIBRARY_ROOM("""
        Eine alte Forschungsbibliothek. Bücher liegen verstreut, Monitore sind zerschlagen. Drei Gestalten bewegen sich im Dunkeln.
        Es sind zwei Mini-Zombies - und ein ehemals menschlicher Wissenschaftler, mutiert und feindselig.
    """),
    DINING_HALL("""
        Ein riesiger Speisesaal. Überall zerstörtes Mobiliar und Blutflecken. Du hörst das Kratzen von Nägeln auf Metall.
        Gleich fünf Gegner nähern sich aus verschiedenen Richtungen - Mini-Zombies und mutierte Wissenschaftler. Es gibt keinen Rückzug.
    """),
    LABORATORY("""
        Du erreichst das zentrale Forschungslabor. Hier wurde alles dokumentiert - und alles ist fehlgeschlagen.
        Vier Wissenschaftler-Zombies greifen dich gleichzeitig an. Ihre Bewegungen sind seltsam koordiniert. Als wären sie noch bei Bewusstsein ...
    """),
    CORRIDOR("""
        Ein langer, dunkler Verbindungsgang. Die Wände sind mit Kratzspuren überzogen. Du hörst ein tiefes Grollen.
        Drei gigantische Zombies stellen sich dir in den Weg. Diese Kreaturen sind langsamer, aber unglaublich stark.
    """),
    PANTRY_2("""
        Noch eine Vorratskammer? Nein ... hier stimmt etwas nicht. Die Luft ist dick, das Licht flackert nervös.
        Zwei Mini-Zombies stürmen auf dich zu. Es scheint fast, als würden sie dich in Richtung der nächsten Tür treiben ...
    """),
    FINAL_ROOM("""
        Du trittst durch die letzte Sicherheitstür. Ein gigantisches Labor öffnet sich vor dir. Maschinen pfeifen, Flüssigkeiten blubbern in Tanks.
        In der Mitte steht es: das Ergebnis aller Experimente. Der Endboss - ein mutierter Superzombie, aus Dutzenden Leichen zusammengesetzt.
        Du bist allein. Es gibt kein Zurück. Nur den Kampf.
    """);

    public String story;

    StoryDatabank(String story) {
        this.story = story;
    }

    public static String getStory(StoryDatabank storyKey) {
        return storyKey.story;
    }
}
