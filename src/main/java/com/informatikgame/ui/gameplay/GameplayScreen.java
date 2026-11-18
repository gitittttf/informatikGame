package com.informatikgame.ui.gameplay;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.informatikgame.core.AnzeigeZustand;
import com.informatikgame.core.Ereignis;
import com.informatikgame.core.GameManager;
import com.informatikgame.core.Spielphase;
import com.informatikgame.ui.GameScreen;
import com.informatikgame.ui.ScreenManager;

/**
 * Der Haupt-Gameplay-Screen. Alles was vom GameManager kommt läuft durch das
 * KampfLog, die Anzeige (Phase, HP, Gegner) ändert sich erst wenn das
 * passende Ereignis dran ist.
 */
public class GameplayScreen extends GameScreen {

    private static final int LOG_HOEHE = 15;

    private GameManager gameManager;
    private final KampfLog log = new KampfLog();
    private final StoryAnzeige story = new StoryAnzeige();
    private final KampfEingabe eingabe = new KampfEingabe();

    // was gerade angezeigt wird, kann hinter dem GameManager herhängen
    private Spielphase anzeigePhase;
    private AnzeigeZustand zustand = AnzeigeZustand.leer();
    private boolean karteOffen;
    private boolean abbrechenFragen;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void initialize() {
        // alles vom letzten spiel weg
        log.leeren();
        eingabe.reset();
        anzeigePhase = null;
        zustand = AnzeigeZustand.leer();
        karteOffen = false;
        abbrechenFragen = false;
        animationFrame = 0;

        gameManager.setListener(log::hinzufuegen);
        gameManager.starteSpiel();
        log.aktualisieren(this::anwenden);
    }

    private void anwenden(Ereignis ereignis) {
        if (ereignis instanceof Ereignis.Zustand z) {
            zustand = z.zustand();
            eingabe.zielBegrenzen(zustand.gegner().size());
        } else if (ereignis instanceof Ereignis.Phase p) {
            anzeigePhase = p.phase();
            if (p.text() != null) {
                story.setzeText(p.text(), System.currentTimeMillis());
            }
            if (p.phase() == Spielphase.KAMPF) {
                eingabe.reset();
            }
            if (p.phase() != Spielphase.KAMPF && p.phase() != Spielphase.WARTE_AUF_WEITER) {
                karteOffen = false;
            }
        }
    }

    private boolean eingabeMoeglich() {
        return anzeigePhase == Spielphase.KAMPF && log.istLeer() && gameManager.wartetAufSpieler();
    }

    @Override
    public void update() {
        animationFrame++;
        log.aktualisieren(this::anwenden);
    }

    // === Eingabe ===
    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (anzeigePhase == null) {
            return;
        }
        if (abbrechenFragen) {
            if (istJa(keyStroke)) {
                screenManager.switchToScreen("menu");
            } else if (istNein(keyStroke)) {
                abbrechenFragen = false;
            }
            return;
        }

        if (istTaste(keyStroke, 'm') && kannKarteOeffnen()) {
            karteOffen = !karteOffen;
            return;
        }
        if (karteOffen) {
            if (istTaste(keyStroke, 'x')) {
                karteOffen = false;
            }
            return;
        }

        boolean enter = keyStroke.getKeyType() == KeyType.Enter;
        switch (anzeigePhase) {
            case STORY, RAUM_ENDE_STORY -> {
                if (enter) {
                    if (!story.istFertig(System.currentTimeMillis())) {
                        story.alleZeigen();
                    } else if (log.istLeer()) {
                        gameManager.weiter();
                    }
                }
            }
            case WARTE_AUF_WEITER -> {
                if (enter) {
                    if (log.istLeer()) {
                        gameManager.weiter();
                    } else {
                        log.allesAnzeigen(this::anwenden);
                    }
                }
            }
            case KAMPF -> {
                if (!eingabeMoeglich()) {
                    if (enter) {
                        log.allesAnzeigen(this::anwenden);
                    }
                    return;
                }
                boolean angreifen = eingabe.taste(keyStroke, zustand.gegner().size(),
                        gameManager.getPlayer().getFinteLevel(), gameManager.getPlayer().getWuchtschlagLevel());
                if (angreifen) {
                    gameManager.spielerAngriff(eingabe.getZiel(), eingabe.getFinte(), eingabe.getWuchtschlag());
                    eingabe.neuerZug();
                }
            }
            case GAME_OVER, SIEG -> {
                if (enter) {
                    screenManager.switchToScreen("menu");
                }
            }
        }
    }

    @Override
    public boolean onEscape() {
        if (anzeigePhase == null) {
            return false;
        }
        if (abbrechenFragen) {
            abbrechenFragen = false;
        } else if (karteOffen) {
            karteOffen = false;
        } else if (anzeigePhase == Spielphase.GAME_OVER || anzeigePhase == Spielphase.SIEG) {
            screenManager.switchToScreen("menu");
        } else if (!(anzeigePhase == Spielphase.KAMPF && eingabe.zurueck())) {
            // nicht einfach rausfliegen, erst nachfragen
            abbrechenFragen = true;
        }
        return false;
    }

    private boolean kannKarteOeffnen() {
        return anzeigePhase == Spielphase.KAMPF || anzeigePhase == Spielphase.WARTE_AUF_WEITER;
    }

    private static boolean istTaste(KeyStroke keyStroke, char taste) {
        return keyStroke.getKeyType() == KeyType.Character
                && Character.toLowerCase(keyStroke.getCharacter()) == taste;
    }

    // === Render ===
    @Override
    public void render(TextGraphics graphics) {
        if (anzeigePhase == null) {
            return;
        }
        TerminalSize size = screenManager.getSize();
        long jetzt = System.currentTimeMillis();

        switch (anzeigePhase) {
            case STORY, RAUM_ENDE_STORY ->
                story.zeichne(graphics, size, jetzt, animationFrame);
            case GAME_OVER ->
                EndScreens.gameOver(graphics, size, gameManager.getAktuellerRaum().anzeigeName, animationFrame);
            case SIEG ->
                EndScreens.sieg(graphics, size, gameManager.getSchwierigkeit().anzeigeName, animationFrame);
            case KAMPF, WARTE_AUF_WEITER -> {
                if (karteOffen) {
                    var world = gameManager.getWorld();
                    int geschafft = world.getAktuelleRaumNummer() + (anzeigePhase == Spielphase.WARTE_AUF_WEITER ? 1 : 0);
                    KartenAnzeige.zeichne(graphics, size, world.getRaeume(), world.getAktuelleRaumNummer(), geschafft, animationFrame);
                } else {
                    zeichneSpielfeld(graphics, size);
                }
            }
        }

        if (abbrechenFragen) {
            drawDialog(graphics, "Spiel abbrechen und zurück ins Hauptmenü?");
        }
    }

    private void zeichneSpielfeld(TextGraphics graphics, TerminalSize size) {
        boolean imKampf = anzeigePhase == Spielphase.KAMPF;
        drawStatusBar(graphics, size, imKampf);

        int mainAreaY = 3;
        int columnWidth = size.getColumns() / 3;
        int panelHeight = size.getRows() - mainAreaY - LOG_HOEHE;

        SpielerPanel.zeichne(graphics, 0, mainAreaY, columnWidth, panelHeight,
                zustand, gameManager.getPlayer(), imKampf, eingabeMoeglich(), eingabe);
        KampfSzene.zeichne(graphics, columnWidth, mainAreaY, columnWidth, panelHeight,
                imKampf, zustand, eingabe.getZiel(), gameManager.getAktuellerRaum(), animationFrame);
        InfoPanel.zeichne(graphics, columnWidth * 2, mainAreaY, columnWidth, panelHeight,
                zustand, eingabe.getZiel(), imKampf);
        log.zeichne(graphics, 0, size.getRows() - LOG_HOEHE, size.getColumns(), LOG_HOEHE);
    }

    private void drawStatusBar(TextGraphics graphics, TerminalSize size, boolean imKampf) {
        TextColor bgColor = imKampf ? new TextColor.RGB(30, 0, 0) : new TextColor.RGB(0, 30, 0);
        graphics.setBackgroundColor(bgColor);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(size.getColumns(), 2), ' ');

        var world = gameManager.getWorld();
        String roomInfo = String.format("RAUM %d/%d - %s  [%s]",
                world.getAktuelleRaumNummer() + 1, world.getRaumAnzahl(),
                world.getAktuellerRaum().anzeigeName, gameManager.getSchwierigkeit().anzeigeName);
        graphics.putString(new TerminalPosition(2, 0), roomInfo);

        String status = imKampf ? "[KAMPF]" : "[ERKUNDUNG]";
        graphics.setForegroundColor(imKampf ? TextColor.ANSI.RED : TextColor.ANSI.GREEN);
        graphics.putString(new TerminalPosition(size.getColumns() / 2 - status.length() / 2, 0), status);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String hinweis = "M - Map | ESC - Menü";
        graphics.putString(new TerminalPosition(size.getColumns() - hinweis.length() - 2, 0), hinweis);

        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(ScreenManager.PRIMARY_COLOR);
        for (int x = 0; x < size.getColumns(); x++) {
            graphics.setCharacter(x, 2, '═');
        }
    }
}
