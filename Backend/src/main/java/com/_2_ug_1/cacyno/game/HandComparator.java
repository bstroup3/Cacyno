package com._2_ug_1.cacyno.game;

import com._2_ug_1.cacyno.models.Hand;

import java.util.HashMap;

public class HandComparator {
    HashMap<Hands, Integer> HandMap = new HashMap<Hands, Integer>();

    public HandComparator() {
        HandMap.put(Hands.HIGHCARD, 0);
        HandMap.put(Hands.PAIR, 1);
        HandMap.put(Hands.TWOPAIR, 2);
        HandMap.put(Hands.THREEOFAKIND, 3);
        HandMap.put(Hands.STRAIGHT, 4);
        HandMap.put(Hands.FLUSH, 5);
        HandMap.put(Hands.FULLHOUSE, 6);
        HandMap.put(Hands.FOUROFAKIND, 7);
        HandMap.put(Hands.STRAIGHTFLUSH, 8);
        HandMap.put(Hands.ROYALFLUSH, 9);
    }

    public int compareHands(int[] cards1, int[] cards2) {
        HandChecker handChecker = new HandChecker();
        Hand hand1 = handChecker.getHandRank(cards1);
        Hand hand2 = handChecker.getHandRank(cards2);

        int[] hand1Ranks = hand1.getRanks();
        int[] hand2Ranks = hand2.getRanks();


        if (HandMap.get(hand1.getHand()) != HandMap.get(hand2.getHand())) {
            return HandMap.get(hand1.getHand()) - HandMap.get(hand2.getHand());
        }

        for (int i = 0; i < hand1Ranks.length; i++) {
            if (hand1Ranks[i] != hand2Ranks[i]) {
                return hand1Ranks[i] - hand2Ranks[i];
            }
        }
        return 0;
    }

}
