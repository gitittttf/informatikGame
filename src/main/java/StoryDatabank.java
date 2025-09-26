
public enum StoryDatabank {

    INTRO_ROOM("""
        Du wachst benommen in einem zerschlagenen Labor auf. Blutspuren ziehen sich über den Boden, und flackernde Lichter tauchen alles in grelles Rot.
        Eine Tür quietscht offen - dein einziger Ausweg. Ein einsamer Zombie schlurft durch die Trümmer. Du greifst nach einer Eisenstange ...
    """),
    FLOOR_ROOM_END("""
        Du konntest das Zombie erfolgreich besiegen! Als Belohnung erhältst du eine Bandage, mit der du zusätzliche Leben generieren kannst! 
        Nun begibst machst du dich auf dem Weg zuzm nächsten Raum und du fängst an einen frürchterlichen Gestank wahrzunehmen,währenddu versuchst durch das Chaos zu kämpfen! 
        Langsam öffnest du die knartschende Tür und ein kalter Windhauch kommt dir entgegen...
    """),
    ZOMBIE_ROOM("""
        Du befindest dich in einem alten Wartezimmer, welches komplett durchwüstet ist. Essensreste, zerstörte Möbel - als wäre hier gekämpft worden.
        Zwei Mini-Zombies brechen aus einem Lüftungsschacht hervor. Du musst schnell reagieren!
    """),
    PANTRY_1("""
        Nachdem du dich weiter durch die düsteren Gänge begeben hast, betrittst nun die Vorratskammer. Die Regale sind geplündert. Zwischen leeren Dosen und Konservengläsern lauern zwei Zombies.
        Sie scheinen hier eingeschlossen gewesen zu sein - ausgehungert und wütend.
    """),
    
    PANTRY_1_END("""
                Du konntest nach einem harten Kampf die Zombies bewältigen, aber du bist sehr geschwächt. Zum Glück findest du auf dem Boden einen Erste Hilfe Koffer.
                 """),
        
    LIBRARY_ROOM("""
        Anschließend steht vor einer großen alten Holztür. Du öffnest langsam die schwere die Tür und bist überwältigt von der Größer der Bücherei. Außerdem liegen überall Bücher verstreut, Monitore sind zerschlagen. Drei Gestalten bewegen sich im Dunkeln.
        Es sind zwei Mini-Zombies - und ein ehemals menschlicher Wissenschaftler, mutiert und feindselig.
    """),
    DINING_HALL("""
        Nach dieser anstrengenden Schlacht begibst du dich zum nächstenb Raum. Die Mensa. Überall ist zerstörtes Mobiliar und Blutflecken. Du hörst das Kratzen von Nägeln auf Metall.
        Gleich nähern sich fünf Gegner aus verschiedenen Richtungen - Mini-Zombies und mutierte Wissenschaftler. Es gibt keinen Rückzug.
    """),
    LABORATORY("""
        Du erreichst das zentrale Forschungslabor. Hier wurde alles dokumentiert - und alles ist fehlgeschlagen.
        Vier Wissenschaftler-Zombies greifen dich gleichzeitig an. Ihre Bewegungen sind seltsam koordiniert. Als wären sie noch bei Bewusstsein ...
    """),
    CORRIDOR("""
        Nun befindest du dich in einem großen dunklen Verbindungstrakt. Nur ein kleines rotes Warnlicht blinkt . Die Wände sind mit Kratzspuren überzogen. Du hörst ein tiefes Grollen.
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
    """),
    FINAL_ROOM_END("""
        Du hast es geschafft! Du konntest erfolgreich das mutierte Mega-Zombie besiegen und das Serum sichern! Nur wegen dir konnte die Menschheit gerettet werden! 
    """);

    public String story;

    StoryDatabank(String story) {
        this.story = story;
    }

    public static String getStory(StoryDatabank story) {
        return story.toString();
    }
}
