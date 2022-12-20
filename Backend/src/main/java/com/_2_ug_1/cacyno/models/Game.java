package com._2_ug_1.cacyno.models;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * object that has all the data for a game from the database
 */
@Api(value = "Game", description = "Game Entity that holds the data of the game state")
@Entity(name = "Game")
@Table(name = "game")
public class Game {
    @ApiModelProperty(notes = "The id of the game", name = "id")
    @Id
    @Column(name = "id")
    private String id;
    @ApiModelProperty(notes = "holds the data of the first card on the table", name = "public_card1")
    @Column(name = "public_card1", nullable = false, columnDefinition = "int default -1")
    private int public_card1;
    @ApiModelProperty(notes = "holds the data of the second card on the table", name = "public_card2")
    @Column(name = "public_card2", nullable = false, columnDefinition = "int default -1")
    private int public_card2;
    @ApiModelProperty(notes = "holds the data of the third card on the table", name = "public_card3")
    @Column(name = "public_card3", nullable = false, columnDefinition = "int default -1")
    private int public_card3;
    @ApiModelProperty(notes = "holds the data of the fourth card on the table", name = "public_card4")
    @Column(name = "public_card4", nullable = false, columnDefinition = "int default -1")
    private int public_card4;
    @ApiModelProperty(notes = "holds the data of the fifth card on the table", name = "public_card5")
    @Column(name = "public_card5", nullable = false, columnDefinition = "int default -1")
    private int public_card5;
    @ApiModelProperty(notes = "holds the data of the total money in the round", name = "pot")
    @Column(name = "pot", nullable = false, columnDefinition = "int default 0")
    private int pot;
    @ApiModelProperty(notes = "keeps track of how many rounds have been played in the game", name = "round")
    @Column(name = "round", nullable = false, columnDefinition = "int default 0")
    private int round;
    @ApiModelProperty(notes = "holds the name of the lobby", name = "lobbyname")
    @Column(name = "lobbyname", nullable = false, columnDefinition = "varchar(255) default ''")
    private String lobbyname;
    @ApiModelProperty(notes = "keeps track of if the game is active or not", name = "active")
    @Column(name = "active", nullable = false, columnDefinition = "boolean default false")
    private boolean active;
    @ApiModelProperty(notes = "keeps track of the highest bet of the game", name = "highest_bet")
    @Column(name = "highest_bet", nullable = false, columnDefinition = "int default 0")
    private int highest_bet;
    @ApiModelProperty(notes = "keeps track of the highest bet in the round", name = "highest_gameRound_bet")
    @Column(name = "highest_gameRound_bet", nullable = false, columnDefinition = "int default 0")
    private int highest_gameRound_bet;

    /**
     * @return the id of the game
     */
    public String getId() {
        return id;
    }

    /**
     * sets the id of the game
     *
     * @param id value to set id to
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * gets the value of the first card
     *
     * @return the value of the card
     */
    public int getPublic_card1() {
        return public_card1;
    }

    /**
     * sets the value of the first card
     *
     * @param public_card1 value to set the card to
     */
    public void setPublic_card1(int public_card1) {
        this.public_card1 = public_card1;
    }

    /**
     * gets the value of the second card
     *
     * @return the value of the card
     */
    public int getPublic_card2() {
        return public_card2;
    }

    /**
     * sets the value of the second card
     *
     * @param public_card2 value to set the card to
     */
    public void setPublic_card2(int public_card2) {
        this.public_card2 = public_card2;
    }

    /**
     * gets the value of the third card
     *
     * @return the value of the card
     */
    public int getPublic_card3() {
        return public_card3;
    }

    /**
     * sets the value of the card
     *
     * @param public_card3 value to set the card to
     */
    public void setPublic_card3(int public_card3) {
        this.public_card3 = public_card3;
    }

    /**
     * gets the value of the fourth card
     *
     * @return the value of the card
     */
    public int getPublic_card4() {
        return public_card4;
    }

    /**
     * sets the value of the fourth card
     *
     * @param public_card4 value to set the table card to
     */
    public void setPublic_card4(int public_card4) {
        this.public_card4 = public_card4;
    }

    /**
     * gets the value of the fifth card
     *
     * @return tje value of the card
     */
    public int getPublic_card5() {
        return public_card5;
    }

    /**
     * sets the value of the fifth card
     *
     * @param public_card5 value to set the card to
     */
    public void setPublic_card5(int public_card5) {
        this.public_card5 = public_card5;
    }

    /**
     * gets the value of the sum of all money bet
     *
     * @return the sum of all money bet
     */
    public int getPot() {
        return pot;
    }

    /**
     * sets the value of the sum of all money bet
     *
     * @param pot value to set the sum of money bet to
     */
    public void setPot(int pot) {
        this.pot = pot;
    }

    /**
     * gets the value of the current round of the game
     *
     * @return the current round of the game
     */
    public int getRound() {
        return round;
    }

    /**
     * sets the value of the current round of the game
     *
     * @param round value to set the current round of the game to
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * gets the value of the lobby name
     *
     * @return the lobby name
     */
    public String getLobbyname() {
        return lobbyname;
    }

    /**
     * sets the value of the lobby name
     *
     * @param lobbyname value to set the lobby name to
     */
    public void setLobbyname(String lobbyname) {
        this.lobbyname = lobbyname;
    }

    /**
     * gets the boolean for if the game is active
     *
     * @return boolean for if the game is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * sets the value for if the game is active
     *
     * @param active value to set if the game is active to
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * gets the highest bet of the game
     *
     * @return
     */
    public int getHighest_bet() {
        return highest_bet;
    }

    /**
     * sets the highest bet of the game
     *
     * @param bet
     */
    public void setHighest_bet(int bet) {
        this.highest_bet = bet;
    }

    /**
     * gets the highest bet of the round
     * @return
     */
    public int getHighest_gameRound_bet() {
        return highest_gameRound_bet;
    }

    /**
     * sets the highest bet of the round
     * @param highest_gameRound_bet
     */
    public void setHighest_gameRound_bet(int highest_gameRound_bet) {
        this.highest_gameRound_bet = highest_gameRound_bet;
    }
}
