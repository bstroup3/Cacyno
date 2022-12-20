package com._2_ug_1.cacyno.models;

import com._2_ug_1.cacyno.game.Hands;

public class Hand {
    Hands hand;
    int[] ranks;

    public Hand(Hands hand, int[] ranks) {
        this.hand = hand;
        this.ranks = ranks;
    }

    public Hands getHand() {
        return hand;
    }

    public int[] getRanks() {
        return ranks;
    }

}
