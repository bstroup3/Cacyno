package com._2_ug_1.cacyno.models;



public class Card implements Comparable<Card>{
    private int rank;
    private int suit;

    public Card(int rank, int suit) {
        this.rank=rank;
        this.suit=suit;
    }

    @Override
    public int compareTo(Card card) {
        return rank-card.rank;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSuit() {
        return suit;
    }
}
