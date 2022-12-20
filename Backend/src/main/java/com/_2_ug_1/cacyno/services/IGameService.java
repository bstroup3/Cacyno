package com._2_ug_1.cacyno.services;

import com._2_ug_1.cacyno.models.Game;

import java.util.List;

/**
 * handels logic for poker and updates database accordingly
 */
public interface IGameService {
    /**
     * Add a new game to the database
     *
     * @param game the game to be added to the database
     * @return the game added to the database
     */
    Game save(Game game);

    /**
     * returns all games from the database as a list
     *
     * @return list of all games from the database
     */
    List<Game> findAll();

    /**
     * gets the game with the specified id
     *
     * @param id the id of the requested id
     * @return a game that has the same id as specified
     */
    Game findById(String id);

    /**
     * deletes a game specified by id from the database
     *
     * @param id the id of the game to be deleted
     */
    void deleteById(String id);

    /**
     * sets up a game so that user may make play calls in a preditable way. Resets values for the game and users in game
     * and deals cards.
     *
     * @param gameId the id of the game to be initialized
     * @return the updated game, Returns nothing if no game is found
     */
    Game gameInit(String gameId);

    /**
     * The user with id equal to userId will attempt to make a play on the game with an id equal to gameId. The user
     * will only be able to make a play if it is their turn. At the end of the last round, when the last player ends
     * their turn the end game sequence begins.
     *
     * @param gameId the Id of the game the user is attempting to play on
     * @param userId the Id of the user in the game attempting to make a play
     * @param bet Determins the play the user will make in the game. Bet less than 0: the user will fold,
     * Bet equals 0: the user will check, Bet greater than 0 and valid: the user will spend some of their in
     * game money to bet.
     * @return the updated game if the user is in the game, it is their turn and
     * they made a valid bet, if the game is not found, user is not found, or move is invalid
     * then it will return null
     */
    Game play(String gameId, String userId, int bet);
}
