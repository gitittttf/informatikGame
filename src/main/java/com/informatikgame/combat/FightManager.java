package com.informatikgame.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import com.informatikgame.entities.AngriffsErgebnis;
import com.informatikgame.entities.Character;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.entities.Wuerfel;
import com.informatikgame.world.EnemyType;

/**
 * FightManager - Kampf-System und Runden-Management
 *
 * Jede Runde handeln alle nach Initiative (höchste zuerst). Gegner handeln
 * sofort, beim Spieler wird angehalten bis die UI executePlayerAction()
 * aufruft. Was passiert, geht als Ereignis an den Listener, die Texte macht
 * der GameManager.
 */
public class FightManager {

    public interface CombatEventListener {

        void onRundenStart(int runde);

        void onAngriff(AngriffsErgebnis ergebnis);

        /**
         * Spieler ist dran, ui soll eingabe freigeben
         */
        void onSpielerAmZug();

        void onKampfEnde(boolean gewonnen);
    }

    private final Player player;
    private final Wuerfel wuerfel;
    private final List<Enemy> enemiesLeftToRight = new ArrayList<>();
    private CombatEventListener eventListener;
    private PriorityQueue<Character> currentActionQueue = new PriorityQueue<>(Character.initiativeComparator);
    private boolean waitingForPlayerAction;
    private boolean kampfLaeuft;
    private int currentRound;

    public FightManager(Player player, Wuerfel wuerfel) {
        this.player = player;
        this.wuerfel = wuerfel;
    }

    public void setCombatEventListener(CombatEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Startet einen neuen Kampf
     *
     * @param gegner Gegner von links nach rechts
     */
    public void startFight(EnemyType... gegnerTypen) {
        List<Enemy> gegner = new ArrayList<>();
        for (EnemyType type : gegnerTypen) {
            gegner.add(new Enemy(type));
        }
        startFight(gegner);
    }

    public void startFight(List<Enemy> gegner) {
        enemiesLeftToRight.clear();
        for (Enemy enemy : gegner) {
            enemy.setRandomizedInitiative(enemy.getInitiative() + wuerfel.w6());
            enemiesLeftToRight.add(enemy);
        }
        player.setRandomizedInitiative(player.getInitiative() + wuerfel.w6());

        currentRound = 1;
        waitingForPlayerAction = false;
        kampfLaeuft = true;
        neueRunde();
        processNextAction();
    }

    private void neueRunde() {
        if (eventListener != null) {
            eventListener.onRundenStart(currentRound);
        }

        currentActionQueue = new PriorityQueue<>(Character.initiativeComparator);
        currentActionQueue.add(player);
        currentActionQueue.addAll(enemiesLeftToRight);
    }

    private void processNextAction() {
        // gegner handeln hintereinander weg, beim spieler wird angehalten
        while (kampfLaeuft) {
            if (!player.isAlive() || enemiesLeftToRight.isEmpty()) {
                endFight();
                return;
            }

            Character actingFighter = currentActionQueue.poll();
            if (actingFighter == null) {
                currentRound++;
                neueRunde();
                continue;
            }

            if (!actingFighter.isAlive()) {
                continue;
            }

            if (actingFighter instanceof Player) {
                waitingForPlayerAction = true;
                if (eventListener != null) {
                    eventListener.onSpielerAmZug();
                }
                return;
            }

            Enemy enemy = (Enemy) actingFighter;
            int finte = wuerfel.zahlBis(enemy.getFinteLevel());
            int wuchtschlag = wuerfel.zahlBis(enemy.getWuchtschlagLevel());
            AngriffsErgebnis ergebnis = enemy.attack(player, finte, wuchtschlag, wuerfel);
            if (eventListener != null) {
                eventListener.onAngriff(ergebnis);
            }
        }
    }

    /**
     * Spieler greift an
     *
     * @return false wenn die eingabe ungültig war oder gerade nicht dran
     */
    public boolean executePlayerAction(int targetEnemyIndex, int finteLevel, int wuchtschlagLevel) {
        if (!waitingForPlayerAction) {
            return false;
        }
        if (targetEnemyIndex < 0 || targetEnemyIndex >= enemiesLeftToRight.size()) {
            return false;
        }
        if (finteLevel < 0 || finteLevel > player.getFinteLevel()
                || wuchtschlagLevel < 0 || wuchtschlagLevel > player.getWuchtschlagLevel()) {
            return false;
        }

        waitingForPlayerAction = false;
        Enemy target = enemiesLeftToRight.get(targetEnemyIndex);
        AngriffsErgebnis ergebnis = player.attack(target, finteLevel, wuchtschlagLevel, wuerfel);
        if (!target.isAlive()) {
            // tote gegner sofort raus, damit man sie nicht nochmal anwählen kann
            enemiesLeftToRight.remove(target);
        }
        if (eventListener != null) {
            eventListener.onAngriff(ergebnis);
        }

        processNextAction();
        return true;
    }

    private void endFight() {
        if (!kampfLaeuft) {
            return;
        }
        kampfLaeuft = false;
        waitingForPlayerAction = false;
        if (eventListener != null) {
            eventListener.onKampfEnde(player.isAlive());
        }
    }

    public boolean isWaitingForPlayerAction() {
        return waitingForPlayerAction;
    }

    public boolean isKampfLaeuft() {
        return kampfLaeuft;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemiesLeftToRight() {
        return Collections.unmodifiableList(enemiesLeftToRight);
    }
}
