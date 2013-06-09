package cfvbaibai.cardfantasy.test;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.data.Feature;
import cfvbaibai.cardfantasy.data.FeatureType;
import cfvbaibai.cardfantasy.engine.Board;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.CardStatusItem;
import cfvbaibai.cardfantasy.engine.Deck;
import cfvbaibai.cardfantasy.engine.FeatureEffect;
import cfvbaibai.cardfantasy.engine.Field;
import cfvbaibai.cardfantasy.engine.GameResult;
import cfvbaibai.cardfantasy.engine.GameUI;
import cfvbaibai.cardfantasy.engine.Grave;
import cfvbaibai.cardfantasy.engine.Hand;
import cfvbaibai.cardfantasy.engine.Phase;
import cfvbaibai.cardfantasy.engine.Player;
import cfvbaibai.cardfantasy.engine.StageInfo;

public class TestGameUI extends GameUI {

    public TestGameUI() {
    }

    @Override
    public void playerAdded(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public void roundStarted(Player player, int round) {
        say("================================================================================================================================");
        sayF("================================ Round %d started for player <%s> ================================",
                round, player.getId());
        say("================================================================================================================================");
    }

    @Override
    public void roundEnded(Player player, int round) {
        sayF("Round %d ended for player <%s>", round, player.getId());
    }

    @Override
    public void errorHappened(CardFantasyRuntimeException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void phaseChanged(Player player, Phase previous, Phase current) {
        sayF("Phase Changed: %s => %s", previous.name().toUpperCase(), current.name().toUpperCase());
    }

    @Override
    public void playerChanged(Player previousPlayer, Player nextPlayer) {
        sayF("Player Changed: <%s> => <%s>", previousPlayer.getId(), nextPlayer.getId());
    }

    @Override
    public void cardDrawed(Player drawer, CardInfo card) {
        sayF("<%s> draws a card: <%s (LV: %d)>", drawer.getId(), card.getId(), card.getLevel());
        showBoard();
    }

    @Override
    public void cantDrawDeckEmpty(Player drawer) {
        sayF("Player <%s>'s deck is empty. Cannot draw card.", drawer.getId());
        this.showBoard();
    }

    @Override
    public void cantDrawHandFull(Player drawer) {
        sayF("Player <%s>'s hand is full. Cannot draw card.", drawer.getId());
        this.showBoard();
    }

    @Override
    public List<CardInfo> summonCards(StageInfo stage) {
        List<CardInfo> summonedCards = new LinkedList<CardInfo>();
        Player player = stage.getActivePlayer();
        for (CardInfo card : player.getHand().toList()) {
            if (card.getSummonDelay() == 0) {
                summonedCards.add(card);
                sayF("<%s> summons card: <%s (LV: %d)>", player.getId(), card.getId(), card.getLevel());
            }
        }
        return summonedCards;
    }

    @Override
    public void attackCard(CardInfo attacker, CardInfo defender, Feature feature, int damage) {
        String featureClause = feature == null ? "" : (" by " + feature.getShortDesc() + "");
        int logicalRemainingHP = defender.getHP() - damage;
        if (logicalRemainingHP < 0) {
            sayF("%s attacks %s%s. Damage: %d (%d overflow). HP: %d -> 0.", attacker.getShortDesc(true),
                    defender.getShortDesc(true), featureClause, damage, -logicalRemainingHP, defender.getHP());
        } else {
            sayF("%s attacks %s%s. Damage: %d. HP: %d -> %d", attacker.getShortDesc(true), defender.getShortDesc(true),
                    featureClause, damage, defender.getHP(), logicalRemainingHP);
        }
    }

    @Override
    public void cardDead(CardInfo deadCard) {
        sayF("%s dies!", deadCard.getShortDesc(true));
    }

    @Override
    public void attackHero(CardInfo attacker, Player hero, Feature feature, int damage) {
        String featureClause = feature == null ? "" : (" by " + feature.getShortDesc() + "");
        int logicalRemainingHP = hero.getLife() - damage;
        if (logicalRemainingHP < 0) {
            sayF("%s attacks <%s> directly%s! Damage: %d (%d overflow). HP: %d -> %d", attacker.getShortDesc(true),
                    hero.getId(), featureClause, damage, -logicalRemainingHP, hero.getLife(), 0);
        } else {
            sayF("%s attacks <%s> directly%s! Damage: %d. HP: %d -> %d", attacker.getShortDesc(true), hero.getId(),
                    featureClause, damage, hero.getLife(), hero.getLife() - damage);
        }
    }

    @Override
    public void useSkill(CardInfo attacker, List<CardInfo> victims, Feature feature) {
        if (victims.isEmpty()) {
            sayF("%s cannot find target to use %s.", attacker.getShortDesc(true), feature.getShortDesc());
        } else {
            List<String> victimTexts = new LinkedList<String>();
            for (CardInfo victim : victims) {
                victimTexts.add(victim.getShortDesc(true));
            }
            String victimsText = StringUtils.join(victimTexts, ", ");
            String featureDesc = feature == null ? "����ͨ������" : feature.getShortDesc();
            sayF("%s uses %s to { %s }!", attacker.getShortDesc(true), featureDesc, victimsText);
        }
    }

    @Override
    public void useSkillToHero(CardInfo attacker, Player victimHero, Feature feature) {
        String featureDesc = feature == null ? "����ͨ������" : feature.getShortDesc();
        sayF("%s uses %s to hero <%s>!", attacker.getShortDesc(true), featureDesc, victimHero.getId());
    }

    @Override
    public void addCardStatus(CardInfo attacker, CardInfo victim, Feature feature, CardStatusItem item) {
        sayF("%s.%s adds a new status to %s: ��%s��", attacker.getShortDesc(true), feature.getShortDesc(),
                victim.getShortDesc(true), item.getShortDesc());
    }

    @Override
    public void gameEnded(GameResult result) {
        sayF("Game ended. Winner: <%s>, GameEndCause: %s", result.getWinner().getId(), result.getCause().toString());
    }

    @Override
    protected void gameStarted() {
        say("Game started!");
        sayF("Rule: MaxHandCard=%d, MaxRound=%d", getRule().getMaxHandCards(), getRule().getMaxRound());
        this.showBoard();
    }

    private void showBoard() {
        Board board = this.getBoard();
        say("-----------------------------------------------------------------------------");
        Player player = board.getPlayer(0);
        sayF("Player %d: %s - Life: %d", 0, player.getId(), player.getLife());
        showGrave(player.getGrave());
        showDeck(player.getDeck());
        showHand(player.getHand());
        say("");
        showField(player.getField());
        say("");

        player = board.getPlayer(1);
        say("");
        showField(player.getField());
        say("");
        showHand(player.getHand());
        showDeck(player.getDeck());
        showGrave(player.getGrave());
        sayF("Player %d: %s - Life: %d", 1, player.getId(), player.getLife());
        say("-----------------------------------------------------------------------------");
    }

    private void showGrave(Grave grave) {
        StringBuffer sb = new StringBuffer();
        sb.append("Grave: ");
        for (CardInfo card : grave.toList()) {
            sb.append(String.format("%s (LV=%d, IAT=%d, MHP=%d), ", card.getId(), card.getLevel(), card.getInitAT(),
                    card.getMaxHP()));
        }
        say(sb.toString());
    }

    private void showField(Field field) {
        StringBuffer sb = new StringBuffer();
        sb.append("Field: ");
        int i = 0;
        for (CardInfo card : field.getAliveCards()) {
            sb.append(String.format("\r\n[%d] %s (LV=%d, AT=%d/%d, HP=%d/%d, ST=%s, EF=%s)", i, card.getId(), card
                    .getLevel(), card.getAT(), card.getInitAT(), card.getHP(), card.getMaxHP(), card.getStatus()
                    .getShortDesc(), card.getEffectsDesc()));
            ++i;
        }
        say(sb.toString());
    }

    private void showHand(Hand hand) {
        StringBuffer sb = new StringBuffer();
        sb.append("Hand : ");
        for (CardInfo card : hand.toList()) {
            sb.append(String.format("%s (LV=%d, IAT=%d, MHP=%d, SD=%d), ", card.getId(), card.getLevel(),
                    card.getInitAT(), card.getMaxHP(), card.getSummonDelay()));
        }
        say(sb.toString());
    }

    private void showDeck(Deck deck) {
        StringBuffer sb = new StringBuffer();
        sb.append("Deck : ");
        for (CardInfo card : deck.toList()) {
            sb.append(String.format("%s (LV=%d, IAT=%d, MHP=%d), ", card.getId(), card.getLevel(), card.getInitAT(),
                    card.getMaxHP()));
        }
        say(sb.toString());
    }

    private static void sayF(String format, Object... args) {
        say(String.format(format, args));
    }

    private static void say(Object obj) {
        System.out.println(obj.toString());
    }

    @Override
    public void battleBegins() {
        this.showBoard();
    }

    @Override
    public void attackBlocked(CardInfo attacker, CardInfo defender, Feature atFeature, Feature dfFeature) {
        String attackerDesc = attacker.getShortDesc(true);
        if (atFeature == null && dfFeature == null) {
            sayF("%s is in status %s so cannot attack!", attackerDesc, attacker.getStatus().getShortDesc());
        } else if (atFeature == null && dfFeature != null) {
            sayF("%s's attack is blocked by %s due to %s!", attackerDesc, defender.getShortDesc(true),
                    dfFeature.getShortDesc());
        } else if (atFeature != null && dfFeature == null) {
            sayF("%s is in status %s so cannot activate %s!", attackerDesc, attacker.getStatus().getShortDesc(),
                    atFeature.getShortDesc());
        } else if (atFeature != null && dfFeature != null) {
            sayF("%s's feature %s is blocked by %s due to %s!", attackerDesc, atFeature.getShortDesc(),
                    defender.getShortDesc(true), dfFeature.getShortDesc());
        }
    }

    @Override
    public void adjustAT(CardInfo source, CardInfo target, int adjAT, Feature feature) {
        String verb = adjAT > 0 ? "increases" : "decreases";
        sayF("%s %s %s's AT by %s ! %d -> %d.", source.getShortDesc(true), verb, target.getShortDesc(true),
                feature.getShortDesc(), target.getAT(), target.getAT() + adjAT);
    }

    @Override
    public void blockDamage(CardInfo attacker, CardInfo defender, Feature feature, int originalDamage, int actualDamage) {
        sayF("%s blocks the attack from %s by %s. Damage: %d -> %d", defender.getShortDesc(true), attacker.getShortDesc(true),
                feature.getShortDesc(), originalDamage, actualDamage);
    }

    @Override
    public void debuffDamage(CardInfo card, CardStatusItem item, int damage) {
        sayF("%s gets damage in status %s. Damage: %d. HP: %d -> %d", card.getShortDesc(true), item.getShortDesc(), damage,
                card.getHP(), Math.max(0, card.getHP() - damage));
    }

    @Override
    public void cannotAction(CardInfo card) {
        sayF("%s is in status %s and cannot action!", card.getShortDesc(true), card.getStatus().getShortDesc());
    }

    @Override
    public void recoverAT(CardInfo card, FeatureType cause, int recoveredAT) {
        sayF("%s's AT recovered from ��%s��. AT: %d -> %d", card.getShortDesc(true), cause.name(), card.getAT(), card.getAT()
                - recoveredAT);
    }

    @Override
    public void healCard(CardInfo healer, CardInfo healee, Feature feature, int healHP) {
        sayF("%s heals %s by %s for %d points. HP: %d -> %d", healer.getShortDesc(true), healee.getShortDesc(true),
                feature.getShortDesc(), healHP, healee.getHP(), healee.getHP() + healHP);
    }

    @Override
    public void healHero(CardInfo healer, Player healee, Feature feature, int healHP) {
        sayF("%s heals %s by %s for %d points. HP: %d -> %d", healer.getShortDesc(true), healee.getShortDesc(),
                feature.getShortDesc(), healHP, healee.getLife(), healee.getLife() + healHP);
    }

    @Override
    public void loseAdjustAttackEffect(CardInfo ally, FeatureEffect effect) {
        sayF("%s loses effect caused by %s from %s. AT: %d -> %d.", ally.getShortDesc(true), effect.getCause()
                .getShortDesc(), effect.getSource().getShortDesc(true), ally.getAT(), ally.getAT() - effect.getValue());
    }

    @Override
    public void cardToDeck(Player player, CardInfo card) {
        sayF("%s is put into %s's deck.", card.getShortDesc(true), player.getShortDesc());
    }

    @Override
    public void cardToHand(Player player, CardInfo card) {
        sayF("%s is put into %s's hand.", card.getShortDesc(true), player.getShortDesc());
    }
}
