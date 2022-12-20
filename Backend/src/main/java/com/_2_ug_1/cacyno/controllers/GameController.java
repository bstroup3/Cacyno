package com._2_ug_1.cacyno.controllers;

import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.services.IGameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The GameController class is a class that allows the front end to call and change games in the database
 */
@RequestMapping("/game")
@RestController
public class GameController {
    @Autowired
    IGameService _gameService;

    /**
     * constructs a new GameController
     *
     * @param _gameService The GameService that interfaces between the database, game logic and user
     */
    @Autowired
    public GameController(IGameService _gameService) {
        this._gameService = _gameService;
    }

    /**
     * Add a new game to the database
     *
     * @param game the game to be added to the database
     * @return the game added to the database
     */
    @ApiOperation(value = "saveGame", notes = "Adds a game to the database")
    @ApiParam(name = "Game", value = "the game to be added to the database")
    @PostMapping
    public Game saveGame(@RequestBody Game game) {
        return _gameService.save(game);
    }

    /**
     * returns all games from the database as a list
     *
     * @return list of all games from the database
     */
    @ApiOperation(value = "getAll", notes = "Returns all games in the data base")
    @GetMapping
    public List<Game> getAll() {
        return _gameService.findAll();
    }

    /**
     * deletes a game specified by id from the database
     *
     * @param id the id of the game to be deleted
     */
    @ApiOperation(value = "deleteGame", notes = "Removes the game with an id equal to id from the data base")
    @ApiParam(name = "id", value = "the id of the game to be removed")
    @DeleteMapping(path = "{id}")
    public void deleteGame(@PathVariable("id") String id) {
        _gameService.deleteById(id);
    }

    /**
     * updates a game in the database by matching the ids
     *
     * @param game the game that will replace the old game in the database
     * @return the updated game
     */
    @ApiOperation(value = "updateGame", notes = "Replaces the game with an id equal to the given games id with the" +
            "given game. Returns the updated game or null if no game could be found")
    @ApiParam(name = "game", value = "the game that will update the old one with matching id")
    @PutMapping
    public Game updateGame(@RequestBody Game game) {
        return _gameService.save(game);
    }

    /**
     * gets the game with the specified id
     *
     * @param id the id of the requested game
     * @return a game that has the same id as specified
     */
    @ApiOperation(value = "getGameById", notes = "Returns the game with and id equal to id. Returns null if no game" +
            " is found")
    @ApiParam(name = "id", value = "the id of the game to be returned")
    @GetMapping(path = "{id}")
    public Game getGameById(@PathVariable("id") String id) {
        return _gameService.findById(id);
    }

    /**
     * sets up a game so that user may make play calls in a preditable way. Resets values for the game and users in game
     * and deals cards.
     *
     * @param id the id of the game to be initialized
     * @return the updated game, Returns nothing if no game is found
     */
    @ApiOperation(value = "initGameById", notes = "Resets values for the game and users in game" +
            ", and deals cards. Returns the updated game, Returns nothing if no game is found")
    @ApiParam(name = "id", value = "the id of the game to initialized")
    @GetMapping(path = "init/{id}")
    public Game initGameById(@PathVariable("id") String id) {
        return _gameService.gameInit(id);
    }

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
    @ApiOperation(value = "userPlay", notes = "The user with id equal to userId will" +
            "attempt to make a play on the game with an id equal to gameId. The user will only be able to make a play" +
            " if it is their turn. At the end of the last round, when the last player ends their turn the end" +
            " game sequence begins. Will return the updated game if the user is in the game, it is their turn and" +
            " they made a valid bet, if the game is not found, user is not found, or move is invalid" +
            " then it will return null")
    @GetMapping(path = "play/{gameId}/{userId}/{bet}") //bet -1 to fold, TODO: edge check
    public Game userPlay(@PathVariable("gameId") @ApiParam(name = "gameId", value = "the game the user is attempting to play on") String gameId
            , @PathVariable("userId") @ApiParam(name = "userId", value = "the user in the game attempting to make a play") String userId
            , @PathVariable("bet") @ApiParam(name = "bet", value = "Determins the play the user will make in the game. Bet < 0: the user will fold, " +
            "Bet = 0: the user will check, Bet > 0 and valid: the user will spend some of their in game money to bet") int bet) {
        return _gameService.play(gameId, userId, bet);
    }

}
