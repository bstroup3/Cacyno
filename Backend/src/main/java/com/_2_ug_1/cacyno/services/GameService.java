package com._2_ug_1.cacyno.services;

import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.models.User;
import com._2_ug_1.cacyno.repos.IGameRepo;
import com._2_ug_1.cacyno.repos.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Component("IGameService")
public class GameService implements IGameService {
    @Autowired
    private IGameRepo _gameRepo;
    @Autowired
    private IUserRepo _userRepo;

    /**
     * constructer for gameService
     * @param _gameRepo interface to pull the games from the database
     * @param _userRepo interface to pull the user from the database
     */
    @Autowired
    public GameService(IGameRepo _gameRepo, IUserRepo _userRepo) {
        this._gameRepo = _gameRepo;
        this._userRepo = _userRepo;
    }

    /**
     * sets up a game so that user may make play calls in a preditable way. Resets values for the game and users in game
     * and deals cards.
     *
     * @param gameId the id of the game to be initialized
     * @return the updated game, Returns nothing if no game is found
     */
    @Override
    public Game gameInit(String gameId) {
        Game game = null;
        List<User> users = null;
        try { //need to wait for database
            game = _gameRepo.AsyncGetById(gameId).get();
            users = _userRepo.getAllByGameId(gameId).get();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        if (game == null || users == null) {
            return null; //handle this better
        }

        Deck deck = new Deck();
        game.setPot(0);
        game.setRound(0);
        int i = 0;
        for (User u : deck.Deal(users)) {
            u.setPosition(i++);
            u.setFolded(false);
            u.setHasPlayed(false);
            _userRepo.save(u);
        }
        return _gameRepo.save(deck.DealPublicCards(game));
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
    @Override
    public Game play(String gameId, String userId, int bet) {
        Game game = null;
        List<User> users = null;
        try { //need to wait for database
            game = _gameRepo.AsyncGetById(gameId).get();
            users = _userRepo.getAllByGameId(gameId).get();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        if (game == null || users == null) {
            return null; //handle this better
        }

        users.sort(new Comparator<User>() { //order for turns
            @Override
            public int compare(User o1, User o2) {
                return o1.getPosition() - o2.getPosition();
            }
        });

        if (game.getRound() < 4) { //check if final round
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (!u.getHasPlayed() && !u.getFolded()) { //check turn order
                    if (u.getId().equals(userId) && bet <= u.getCurrent_game_money()) { //check if players turn and can make bet
                        if (bet < 0) { //todo: edge case last person folds
                            u.setFolded(true);
                        } else {
                            u.setBet(u.getBet() + bet);
                            game.setPot(game.getPot() + bet);
                            u.setCurrent_game_money(u.getCurrent_game_money() - bet);
                        }
                        u.setHasPlayed(true);
                        _userRepo.save(u);
                        for (int j = i + 1; j < users.size(); j++) { //is last person to play
                            if (!users.get(j).getFolded()) {
                                return _gameRepo.save(game);
                            }
                        }
                        game.setRound(game.getRound() + 1); //end round if last playing
                        if (game.getRound() < 4) {
                            for (User v : users) {
                                v.setHasPlayed(false);
                                _userRepo.save(v);
                            }
                            return _gameRepo.save(game);
                        }
                        return endGame(game, users);
                    } else {
                        return game;
                    }
                }
            }
        } else {
            return endGame(game, users);
        }
        return game;
    }

    private Game endGame(Game game, List<User> users) { //todo: rank game cards and add winnings
        Deck deck = new Deck();
        game.setRound(0);
        game.setPot(0);
        for (User u : deck.Deal(users)) {
            u.setHasPlayed(false);
            u.setBet(0);
            u.setFolded(false);
            u.setPosition(u.getPosition() + 1 < users.size() ? u.getPosition() + 1 : 0); //change who goes first
            _userRepo.save(u);//todo: add blinds
        }
        return _gameRepo.save(deck.DealPublicCards(game));
    }

    /**
     * Add a new game to the database
     *
     * @param game the game to be added to the database
     * @return the game added to the database
     */
    @Override
    public Game save(Game game) { //add validation
        return _gameRepo.save(game);
    }

    /**
     * returns all games from the database as a list
     *
     * @return list of all games from the database
     */
    @Override
    public List<Game> findAll() {
        return _gameRepo.findAll();
    }

    /**
     * gets the game with the specified id
     *
     * @param id the id of the requested id
     * @return a game that has the same id as specified
     */
    @Override
    public Game findById(String id) {
        return _gameRepo.findById(id).orElse(null);
    }

    /**
     * deletes a game specified by id from the database
     *
     * @param id the id of the game to be deleted
     */
    @Override
    public void deleteById(String id) {
        _gameRepo.deleteById(id);
    }

    private class Deck {
        private ArrayList<Integer> _cards;
        private final Random _rand;

        public Deck() {
            _rand = new Random();
            _rand.setSeed(System.nanoTime());
        }

        public Deck(int[] publicCards, List<User> users) {
            _cards = new ArrayList<>();
            _rand = new Random();
            _rand.setSeed(System.nanoTime());
            for (int i = 0; i < 52; i++) {
                _cards.add(i);
            }
            for (User u : users) {
                _cards.remove((Integer) u.getCard1());
                _cards.remove((Integer) u.getCard2());
            }
            for (int n : publicCards) {
                _cards.remove((Integer) n);
            }
        }

        public List<User> Deal(List<User> users) {
            _cards = new ArrayList<>();
            for (int i = 0; i < 52; i++) {
                _cards.add(i);
            }
            for (User u : users) {
                u.setCard1(_cards.remove(_rand.nextInt(_cards.size())));
                u.setCard2(_cards.remove(_rand.nextInt(_cards.size())));
            }
            return users;
        }

        public Game DealPublicCards(Game game) {
            game.setPublic_card1(_cards.remove(_rand.nextInt(_cards.size())));
            game.setPublic_card2(_cards.remove(_rand.nextInt(_cards.size())));
            game.setPublic_card3(_cards.remove(_rand.nextInt(_cards.size())));
            game.setPublic_card4(_cards.remove(_rand.nextInt(_cards.size())));
            game.setPublic_card5(_cards.remove(_rand.nextInt(_cards.size())));
            return game;
        }

    }
}
