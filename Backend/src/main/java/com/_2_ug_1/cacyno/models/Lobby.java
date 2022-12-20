package com._2_ug_1.cacyno.models;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Api(value = "Lobby", description = "Lobby Entity that holds the data of the Lobby state")
@Entity(name = "Lobby")
@Table(name = "game")
/**
 * Lobby table that holds the values of the lobby of the game
 */
public class Lobby {
    @ApiModelProperty(notes = "The id of the lobby", name = "id")
    @Id
    @Column(name = "id")
    /**
     * holds the id of the lobby
     */
    private String id;
    @ApiModelProperty(notes = "holds the value of the name of the lobby", name = "lobbyname")
    @Column(name = "lobbyname")
    /**
     * hold the name of the lobby
     */
    private String lobbyname;
    @ApiModelProperty(notes = "keeps track if the lobby is active or not", name = "active")
    @Column(name = "active")
    /**
     * is used to determine if the game is active or not
     */
    private boolean active;

    /**
     * gets the id of the lobby
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * sets the id of the lobby
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * gets the name of the lobby
     *
     * @return lobby name
     */
    public String getLobbyname() {
        return lobbyname;
    }

    /**
     * sets the name of the lobby
     *
     * @param lobbyname
     */
    public void setLobbyname(String lobbyname) {
        this.lobbyname = lobbyname;
    }

    /**
     * gets if the lobby is active or not
     *
     * @return
     */
    public boolean getActive() {
        return active;
    }

    /**
     * sets the lobby to active or inactive
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}