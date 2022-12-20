package com._2_ug_1.cacyno.game;

import com._2_ug_1.cacyno.models.Card;
import com._2_ug_1.cacyno.models.Hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HandChecker {


//    public static void main(String args[]) {
//        Hand hand = getHandRank(new int[]{1,14,27,5,18,11,36});
//        int[] ranks = hand.getRanks();
//        System.out.println(hand.getHand());
//        for (int j : ranks) {
//            System.out.println(j);
//        }
//    }

    public Hand getHandRank(int[] cardsParam) {
        int ranks[] = new int[7];
        int suits[] = new int[7];
        Card cards[] = new Card[7];
        for (int i = 0; i <= 6; i++) {
            cards[i] = new Card(ranks[i] = cardsParam[i] % 13, suits[i] = cardsParam[i] / 13);
        }
        for (int i = 0; i<ranks.length; i++) {
            if(ranks[i]==0) {
                ranks[i]=13;
                cards[i].setRank(13);
            }
        }
        Arrays.sort(cards);
        Arrays.sort(ranks);
        Arrays.sort(suits);
        for (int i = 0; i <= 3; i++) {
            if (contains(cardsParam, i * 13 + 13) && contains(cardsParam, i * 13 + 9) && contains(cardsParam, i * 13 + 10) && contains(cardsParam, i * 13 + 11) && contains(cardsParam, i * 13 + 12)) {
                return new Hand(Hands.ROYALFLUSH, new int[5]);
            }
        }

        for (int i = 6; i >=4; i--) {
            for (int j = i - 1; j >= 3; j--) {
                for (int k = j - 1; k >=2; k--) {
                    for (int l = k - 1; l >=1; l--) {
                        for (int m = l - 1; m >=0; m--) {
                            Card[] temp = new Card[]{cards[m], cards[l], cards[k], cards[j], cards[i]};
                            if (isStraightFlush(temp)) {
                                return new Hand(Hands.STRAIGHTFLUSH, new int[]{temp[4].getRank(), temp[3].getRank(), temp[2].getRank(), temp[1].getRank(), temp[0].getRank()});
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (ranks[i] == ranks[i + 3]) {
                int[] tempHand = new int[5];
                tempHand[0] = ranks[i];
                tempHand[1] = ranks[i + 1];
                tempHand[2] = ranks[i + 2];
                tempHand[3] = ranks[i + 3];
                int num = 4;
                for (int j = ranks.length-1; num < 5; j--) {
                    if (j < i || j > i + 3) {
                        tempHand[num] = ranks[j];
                        num++;
                    }
                }
                return new Hand(Hands.FOUROFAKIND, tempHand);
            }
        }


            for (int i = 0; i < 4; i++) {
                for (int j = i + 2; j < 6; j++) {
                    if (ranks[i] == ranks[i + 1] && j<5 && ranks[j] == ranks[j + 2]) {
                        return new Hand(Hands.FULLHOUSE, new int[]{ranks[j], ranks[j + 1], ranks[j + 2], ranks[i], ranks[i + 1]});
                    }
                    if (ranks[i] == ranks[i + 2] && ranks[j] == ranks[j + 1]) {
                        return new Hand(Hands.FULLHOUSE, new int[]{ranks[i], ranks[i + 1], ranks[i + 2], ranks[j], ranks[j + 1]});
                    }
                }
            }


        for (int i = 6; i >=4; i--) {
            for (int j = i - 1; j >= 3; j--) {
                for (int k = j - 1; k >=2; k--) {
                    for (int l = k - 1; l >=1; l--) {
                        for (int m = l - 1; m >=0; m--) {
                            Card[] temp = new Card[]{cards[m], cards[l], cards[k], cards[j], cards[i]};
                            if (temp[0].getSuit() == temp[1].getSuit() && temp[0].getSuit() == temp[2].getSuit() && temp[0].getSuit() == temp[3].getSuit() && temp[0].getSuit() == temp[4].getSuit()) {
                              return new Hand(Hands.FLUSH, new int[]{temp[4].getRank(), temp[3].getRank(), temp[2].getRank(), temp[1].getRank(), temp[0].getRank()});
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Integer> tempStraightHand = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            if (!tempStraightHand.contains(ranks[i])) {
                tempStraightHand.add(ranks[i]);
            }
        }

        for (int i = tempStraightHand.size()-6; i >=0; i--) {
            if (tempStraightHand.get(i) + 4 == tempStraightHand.get(i + 4) || (tempStraightHand.contains(13) && tempStraightHand.contains(1) && tempStraightHand.contains(2) && tempStraightHand.contains(3) && tempStraightHand.contains(4))) {
                return new Hand(Hands.STRAIGHT, new int[]{tempStraightHand.get(i + 4)});
            }
        }

        for (int i = 0; i < 3; i++) {
            if (ranks[i] == ranks[i + 2]) {
                int[] tempHand = new int[5];
                tempHand[0] = ranks[i];
                tempHand[1] = ranks[i + 1];
                tempHand[2] = ranks[i + 2];
                int num = 3;
                for (int j = ranks.length-1; num < 5; j--) {
                    if (j < i || j > i + 2) {
                        tempHand[num] = ranks[j];
                        num++;
                    }
                }
                return new Hand(Hands.THREEOFAKIND, tempHand);
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 6; j++) {
                if (ranks[i] == ranks[i + 1] && ranks[j] == ranks[j + 1]) {
                    for (int k = ranks.length-1; k >=0; k--) {
                        if (k != i && k != i + 1 && k != j && k != j + 1) {
                            if (ranks[i] > ranks[j]) {
                                return new Hand(Hands.TWOPAIR, new int[]{ranks[i], ranks[i + 1], ranks[j], ranks[j + 1], ranks[k]});
                            } else {
                                return new Hand(Hands.TWOPAIR, new int[]{ranks[j], ranks[j + 1], ranks[i], ranks[i + 1], ranks[k]});
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i <= 5; i++) {
            if (ranks[i] == ranks[i + 1]) {
                int[] tempHand = new int[5];
                tempHand[0] = ranks[i];
                tempHand[1] = ranks[i + 1];
                int num = 2;
                for (int j = ranks.length-1; num < 5; j--) {
                    if (j < i || j > i + 1) {
                        tempHand[num] = ranks[j];
                        num++;
                    }
                }
                return new Hand(Hands.PAIR, tempHand);
            }
        }


        return new Hand(Hands.HIGHCARD, Arrays.copyOfRange(ranks, 0, 4));
    }

    private static boolean contains(int[] cardsParam, int num) {
        for (int j : cardsParam) {
            if (j == num) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStraightFlush(Card cards[]) {
        Arrays.sort(cards);
        if (cards[0].getRank() + 1 == cards[1].getRank()&&cards[0].getRank() + 2 == cards[2].getRank()&&cards[0].getRank() + 3 == cards[3].getRank()&&cards[0].getRank() + 4 == cards[4].getRank()) {
            for (int i = 0; i <= 4; i++) {
                if (cards[0].getSuit() != cards[i].getSuit()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
