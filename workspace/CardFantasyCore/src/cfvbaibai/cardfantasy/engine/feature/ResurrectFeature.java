package cfvbaibai.cardfantasy.engine.feature;

import cfvbaibai.cardfantasy.CardFantasyRuntimeException;
import cfvbaibai.cardfantasy.Randomizer;
import cfvbaibai.cardfantasy.data.Feature;
import cfvbaibai.cardfantasy.engine.CardInfo;
import cfvbaibai.cardfantasy.engine.FeatureResolver;
import cfvbaibai.cardfantasy.engine.GameUI;
import cfvbaibai.cardfantasy.engine.Hand;

public final class ResurrectFeature {
    public static boolean apply(FeatureResolver resolver, Feature feature, CardInfo card) {
        if (!card.isDead()) {
            throw new CardFantasyRuntimeException("Cannot resurrect undead card: " + card.getShortDesc(true));
        }
        int rate = feature.getImpact();
        if (Randomizer.roll100() < rate) {
            GameUI ui = resolver.getStage().getUI();
            ui.useSkill(card, card, feature);
            card.getOwner().getGrave().removeCard(card);
            Hand hand = card.getOwner().getHand();
            if (hand.isFull()) {
                ui.cardToDeck(card.getOwner(), card);
                card.getOwner().getDeck().addCard(card);
            } else {
                ui.cardToHand(card.getOwner(), card);
                hand.addCard(card);
            }
            return true;
        }
        return false;
    }
}
