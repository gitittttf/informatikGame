package com.informatikgame.core;

import java.util.ArrayList;
import java.util.List;

import com.informatikgame.combat.FightManager;
import com.informatikgame.entities.AngriffsErgebnis;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.entities.Wuerfel;
import com.informatikgame.world.EnemyType;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.RoomType;
import com.informatikgame.world.Schwierigkeit;
import com.informatikgame.world.UpgradeType;
import com.informatikgame.world.World;

/**
 * GameManager - Zentrale Spiellogik
 *
 * Ablauf pro Raum: STORY → (Enter) → KAMPF → RAUM_ENDE_STORY → (Enter) →
 * WARTE_AUF_WEITER → (Enter) → nächster Raum. Nach dem letzten Raum kommt
 * SIEG, wenn man stirbt GAME_OVER.
 *
 * Die UI ruft nur weiter() und spielerAngriff() auf und bekommt alles andere
 * als Ereignisse über den SpielListener.
 */
public class GameManager implements FightManager.CombatEventListener {

    public interface SpielListener {

        void onEreignis(Ereignis ereignis);
    }

    // pausen fürs log in ms, damit man den kampf mitlesen kann
    private static final long PAUSE_KURZ = 200;
    private static final long PAUSE_NORMAL = 400;
    private static final long PAUSE_LANG = 600;
    private static final long PAUSE_VOR_STORY = 1200;

    private final Wuerfel wuerfel;
    private SpielListener listener;
    private World world;
    private FightManager fightManager;
    private Player player;
    private Schwierigkeit schwierigkeit = Schwierigkeit.NORMAL;
    private Spielphase phase;

    public GameManager() {
        this(new Wuerfel());
    }

    public GameManager(Wuerfel wuerfel) {
        this.wuerfel = wuerfel;
    }

    public void setListener(SpielListener listener) {
        this.listener = listener;
    }

    /**
     * Neues Spiel vorbereiten, alter Zustand wird komplett verworfen
     */
    public void initialisiereSpiel(PlayerType playerType, Schwierigkeit schwierigkeit) {
        this.player = new Player(playerType);
        this.schwierigkeit = schwierigkeit;
        this.world = World.standardWelt();
        this.fightManager = new FightManager(player, wuerfel);
        this.fightManager.setCombatEventListener(this);
        this.phase = null;
    }

    public void starteSpiel() {
        if (player == null) {
            throw new IllegalStateException("Erst initialisiereSpiel() aufrufen");
        }
        sende(new Ereignis.Zustand(aktuellerZustand(), 0));
        zeigeRaumStory();
    }

    /**
     * Enter gedrückt: je nach Phase weiter zum Kampf, zum nächsten Raum oder
     * zum Sieg
     */
    public void weiter() {
        if (phase == null) {
            return;
        }
        switch (phase) {
            case STORY ->
                starteKampf();
            case RAUM_ENDE_STORY -> {
                if (world.hasNextRoom()) {
                    setzePhase(Spielphase.WARTE_AUF_WEITER, null, 0);
                    nachricht("Drücke ENTER um in den nächsten Raum zu gehen", NachrichtenTyp.INFO, PAUSE_KURZ);
                } else {
                    nachricht("SIEG! Du hast alle Räume gemeistert!", NachrichtenTyp.KAMPF_ENDE, 0);
                    setzePhase(Spielphase.SIEG, null, 0);
                }
            }
            case WARTE_AUF_WEITER -> {
                world.naechsterRaum();
                zeigeRaumStory();
            }
            default -> {
                // in KAMPF, GAME_OVER und SIEG macht enter nichts
            }
        }
    }

    /**
     * Spieler greift an
     *
     * @return false wenn gerade nicht erlaubt oder ungültig
     */
    public boolean spielerAngriff(int zielIndex, int finte, int wuchtschlag) {
        if (phase != Spielphase.KAMPF) {
            return false;
        }
        return fightManager.executePlayerAction(zielIndex, finte, wuchtschlag);
    }

    private void zeigeRaumStory() {
        setzePhase(Spielphase.STORY, getAktuellerRaum().story.story, 0);
    }

    private void starteKampf() {
        RoomType raum = getAktuellerRaum();
        setzePhase(Spielphase.KAMPF, null, 0);
        nachricht(">>> Betreten: " + raum.anzeigeName, NachrichtenTyp.RUNDE, 0);
        List<Enemy> gegner = new ArrayList<>();
        for (EnemyType typ : schwierigkeit.gegnerFuer(raum)) {
            gegner.add(new Enemy(typ, schwierigkeit.gegnerWerte(typ.werte)));
        }
        fightManager.startFight(gegner);
    }

    // === CombatEventListener ===
    @Override
    public void onRundenStart(int runde) {
        if (runde == 1) {
            nachricht("=== Der Kampf beginnt! ===", NachrichtenTyp.KAMPF_START, PAUSE_NORMAL);
            sende(new Ereignis.Zustand(aktuellerZustand(), 0));
        }
        nachricht("=== Runde " + runde + " ===", NachrichtenTyp.RUNDE, PAUSE_LANG);
    }

    @Override
    public void onAngriff(AngriffsErgebnis e) {
        NachrichtenTyp typ = e.angreiferIstSpieler() ? NachrichtenTyp.SPIELER : NachrichtenTyp.GEGNER;

        if (!e.angreiferIstSpieler()) {
            nachricht(e.angreifer() + " ist am Zug!", typ, PAUSE_NORMAL);
        }
        nachricht(e.angreifer() + " startet den Angriff!", typ, PAUSE_NORMAL);
        if (e.finte() > 0) {
            nachricht("Versucht eine Finte (Level " + e.finte() + ") auszuführen...", NachrichtenTyp.SPEZIAL, PAUSE_NORMAL);
        }
        if (e.wuchtschlag() > 0) {
            nachricht("Versucht einen Wuchtschlag (Level " + e.wuchtschlag() + ") auszuführen...", NachrichtenTyp.SPEZIAL, PAUSE_NORMAL);
        }

        if (!e.getroffen()) {
            if (e.mitSpezialangriff()) {
                nachricht("Der Spezialangriff ist fehlgeschlagen!", NachrichtenTyp.SPEZIAL, PAUSE_LANG);
            }
            nachricht(e.angreifer() + " scheitert " + e.ziel() + " anzugreifen.", typ, PAUSE_LANG);
            return;
        }

        if (e.finte() > 0) {
            nachricht("Die Finte hat geklappt! " + e.ziel() + " ist verwirrt!", NachrichtenTyp.SPEZIAL, PAUSE_LANG);
        }
        if (e.wuchtschlag() > 0) {
            nachricht("Der Wuchtschlag hat geklappt! Zusätzlicher Schaden wird verursacht!", NachrichtenTyp.SPEZIAL, PAUSE_LANG);
        }
        nachricht(e.angreifer() + " greift " + e.ziel() + " mit " + e.rohSchaden() + " Schaden an!", NachrichtenTyp.SCHADEN, PAUSE_LANG);
        nachricht(e.ziel() + " versucht zu parieren...", NachrichtenTyp.PARADE, PAUSE_NORMAL);

        if (e.pariert()) {
            nachricht(e.ziel() + " pariert erfolgreich!", NachrichtenTyp.PARADE, PAUSE_LANG);
            return;
        }
        nachricht(e.ziel() + " konnte nicht parieren!", NachrichtenTyp.PARADE, PAUSE_LANG);
        nachricht(e.ziel() + " nimmt " + e.erlittenerSchaden() + " Schaden.", NachrichtenTyp.SCHADEN, PAUSE_KURZ);
        // hp erst updaten wenn die schadens-nachricht auch zu sehen ist
        sende(new Ereignis.Zustand(aktuellerZustand(), 0));
        if (e.zielBesiegt()) {
            String text = e.angreiferIstSpieler() ? e.ziel() + " wurde besiegt!" : "Du gehst zu Boden...";
            nachricht(text, NachrichtenTyp.KAMPF_ENDE, PAUSE_NORMAL);
        }
    }

    @Override
    public void onSpielerAmZug() {
        nachricht("Du bist dran! Wähle deine Aktion.", NachrichtenTyp.INFO, PAUSE_NORMAL);
    }

    @Override
    public void onKampfEnde(boolean gewonnen) {
        if (!gewonnen) {
            nachricht("GAME OVER - Du wurdest besiegt!", NachrichtenTyp.KAMPF_ENDE, PAUSE_LANG);
            setzePhase(Spielphase.GAME_OVER, null, PAUSE_VOR_STORY);
            return;
        }

        nachricht(">>> Kampf gewonnen!", NachrichtenTyp.KAMPF_ENDE, PAUSE_LANG);

        RoomType raum = getAktuellerRaum();
        List<String> belohnung = new ArrayList<>();
        UpgradeType upgrade = raum.getUpgrade();
        if (upgrade != null) {
            player.upgrade(upgrade);
            nachricht("Upgrade: " + upgrade.beschreibung(), NachrichtenTyp.UPGRADE, PAUSE_NORMAL);
            belohnung.add(upgrade.beschreibung());
        }
        int geheilt = player.heal(schwierigkeit.heilungFuer(player.getLifeTotal(), player.getMaxLife()));
        if (geheilt > 0) {
            nachricht("Du ruhst dich kurz aus und heilst " + geheilt + " HP.", NachrichtenTyp.UPGRADE, PAUSE_NORMAL);
            belohnung.add("+" + geheilt + " HP geheilt");
        }
        sende(new Ereignis.Zustand(aktuellerZustand(), 0));

        String story = raum.endStory.story;
        if (!belohnung.isEmpty()) {
            story += "\n\n(" + String.join(", ", belohnung) + ")";
        }
        setzePhase(Spielphase.RAUM_ENDE_STORY, story, PAUSE_VOR_STORY);
    }

    // === Hilfsmethoden ===
    private void setzePhase(Spielphase neuePhase, String text, long pause) {
        this.phase = neuePhase;
        sende(new Ereignis.Phase(neuePhase, text, pause));
    }

    private void nachricht(String text, NachrichtenTyp typ, long pause) {
        sende(new Ereignis.Nachricht(text, typ, pause));
    }

    private void sende(Ereignis ereignis) {
        if (listener != null) {
            listener.onEreignis(ereignis);
        }
    }

    /**
     * HP vom Spieler und die noch lebenden Gegner
     */
    public AnzeigeZustand aktuellerZustand() {
        List<AnzeigeZustand.GegnerAnzeige> gegner = new ArrayList<>();
        if (fightManager != null && fightManager.isKampfLaeuft()) {
            for (Enemy enemy : fightManager.getEnemiesLeftToRight()) {
                gegner.add(new AnzeigeZustand.GegnerAnzeige(enemy.getType(), enemy.getLifeTotal(), enemy.getMaxLife()));
            }
        }
        return new AnzeigeZustand(player.getLifeTotal(), player.getMaxLife(), List.copyOf(gegner));
    }

    // === GETTER ===
    public Spielphase getPhase() {
        return phase;
    }

    public boolean wartetAufSpieler() {
        return phase == Spielphase.KAMPF && fightManager.isWaitingForPlayerAction();
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public RoomType getAktuellerRaum() {
        return world.getAktuellerRaum();
    }

    public Schwierigkeit getSchwierigkeit() {
        return schwierigkeit;
    }
}
