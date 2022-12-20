package com._2_ug_1.cacyno.game;

import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.models.User;
import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

/**
 * calculates game states based on user input
 */
public class Poker {
    private LinkedList<User> _players;
    private Queue<User> _toPlay;
    private LinkedList<User> _turnOrder;
    private int _blind = 50;
    private Deck _deck;
    private final int _maxPlayers = 6;
    private boolean _gameInit;
    private Game _game;
    private List<User> _tooPoor;
    private List<String> _winner;

    private String _showHands;
    private String _oldHands;
    private String _showDownGame;

    public static void main(String Args[]) {
        Game g = new Game();
        g.setId("1");
        g.setRound(-1);
        LinkedList<User> users = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            User u = new User();
            u.setId(Integer.toString(users.size()));
            u.setGame(g);
            u.setCurrent_game_money(10000);
            users.add(u);
        }
        Poker p = new Poker(g);
        users.forEach(x -> p.addPlayer(x));
        p.initGame();
        for (int i = 0; i < 20; i++) {
            users.forEach(x -> {
                p.bet(x, g.getHighest_bet() - x.getBet());
                if(g.getHighest_bet() - x.getBet() != 0) {
                    System.out.println(g.getHighest_bet() - x.getBet());
                }
            });
            System.out.println(g.getRound());
        }
        int tmp = 0;
    }

    /**
     * constructor for Poker
     *
     * @param game game model from game repo
     */
    public Poker(Game game) {
        _game = game;
        _deck = new Deck(this);
        _turnOrder = new LinkedList<>();
        _toPlay = new LinkedList<>();
        _players = new LinkedList<>();
        _tooPoor = new ArrayList<>();
        _winner = new LinkedList<>();
        _showHands = new String();
        _oldHands = new String();
        _showDownGame = new String();
    }

    /**
     * returns a list of internal objects
     *
     * @return poker as a string
     */
    public String toString() {
        Gson gson = new Gson();
        String poker = new String("");
        poker += "@_players: size:" + _players.size() + "\n" + gson.toJson(_players) + ", ";
        poker += "@_toPlay: \n" + gson.toJson(_toPlay) + ", ";
        poker += "@_turnOrder: \n" + gson.toJson(_turnOrder) + ", ";
        poker += "@_blind: \n" + _blind + ", ";
        poker += "@_deck: \n" + _deck.toString() + ", ";
        poker += "@_maxPlayers: \n" + _maxPlayers + ", ";
        poker += "@_gameInit: \n" + _gameInit + ", ";
        poker += "@_game: \n" + gson.toJson(_game);
        return poker;
    }

    /**
     * initializes the game, and starts the first round
     *
     * @return a boolean on if the game was successfully started
     */
    public boolean initGame() {
        if (_players.size() < 2) //need at least 2 players
            return false;
        _turnOrder.clear();
        _toPlay.clear();
        _game.setRound(0);
        _players.forEach(x -> {
            x.setFolded(false);
            x.setAllIn(false);
            x.setBet(0);
            x.setHighest_round_bet(0);
            _turnOrder.add(x);
        });
        _toPlay.addAll(_turnOrder);

        for (int i = 0; i < _turnOrder.size(); i++) {
            if (_toPlay.size() == 2) {
                break;
            } else {
                _toPlay.remove();
            }
        }
        if (_toPlay.peek().getCurrent_game_money() < _blind) {
            _tooPoor.add(_toPlay.peek());
            removePlayer(_toPlay.poll());
            return initGame();
        }
        _toPlay.poll();
        if (_toPlay.peek().getCurrent_game_money() < _blind * 2) {
            _toPlay.poll(); //get #2
            _tooPoor.add(_toPlay.peek());
            removePlayer(_toPlay.poll());
            return initGame();
        }
        _toPlay.clear();
        _toPlay.addAll(_turnOrder);
        for (int i = 0; i < _turnOrder.size(); i++) {
            if (_toPlay.size() == 2) {
                _toPlay.peek().setBet(_blind);
                _toPlay.peek().setHighest_round_bet(_blind);
                _toPlay.peek().setCurrent_game_money(_toPlay.poll().getCurrent_game_money() - _blind);
                _toPlay.peek().setBet(_blind * 2);
                _toPlay.peek().setHighest_round_bet(_blind * 2);
                _toPlay.peek().setCurrent_game_money(_toPlay.poll().getCurrent_game_money() - _blind * 2);
                break;
            } else {
                _toPlay.remove();
            }
        }
        _toPlay.clear();
        _toPlay.addAll(_turnOrder);
        _game.setHighest_bet(_blind * 2);
        _game.setHighest_gameRound_bet(_blind * 2);
        _game.setPublic_card1(-1);
        _game.setPublic_card2(-1);
        _game.setPublic_card3(-1);
        _game.setPublic_card4(-1);
        _game.setPublic_card5(-1);
        _game.setPot(_blind + _blind * 2);
        _deck.deal();
        _showDownGame = null;
        _showHands = new String();
        _oldHands = new String();
        _gameInit = true;
        return true;
    }

    /**
     * The input from the users to process the next state of the game
     *
     * @param u   user attempting to play
     * @param bet the amount/action the user is making. bet >= 0 will attempt to increase the user's bet, -1 will fold
     * @return a boolean detailing if the user's play was legal/successful and that the game moved to a new state
     */
    public boolean bet(User u, int bet) {
        if (_toPlay.size() < 1 || !_toPlay.peek().getId().equals(u.getId())) //is turn to play
            return false;
        if (_toPlay.peek().getCurrent_game_money() < bet) { //can make play
            return false;
        }
        if (_toPlay.peek().getCurrent_game_money() == bet) {
            _toPlay.peek().setAllIn(true);
        }

        if (bet >= 0) {
            _toPlay.peek().setBet(_toPlay.peek().getBet() + bet);
            if (_toPlay.peek().getBet() < _game.getHighest_bet()) {
                if (_toPlay.peek().isAllIn()) {
                    _toPlay.peek().setCurrent_game_money(0);
                    _game.setPot(_game.getPot() + bet);
                    _game.setHighest_bet(_toPlay.peek().getBet());

                    if (_toPlay.peek().getHighest_round_bet() + bet > _toPlay.peek().getHighest_round_bet()) {
                        _toPlay.peek().setHighest_round_bet(_toPlay.peek().getHighest_round_bet() + bet);
                    }
                    if (_toPlay.peek().getHighest_round_bet() > _game.getHighest_gameRound_bet()) {
                        _game.setHighest_gameRound_bet(_toPlay.peek().getHighest_round_bet());
                    }

                } else {
                    return false;
                }
            } else if (_toPlay.peek().getBet() == _game.getHighest_bet()) {//Call

                if (_toPlay.peek().getHighest_round_bet() + bet > _toPlay.peek().getHighest_round_bet()) {
                    _toPlay.peek().setHighest_round_bet(_toPlay.peek().getHighest_round_bet() + bet);
                }
                if (_toPlay.peek().getHighest_round_bet() > _game.getHighest_gameRound_bet()) {
                    _game.setHighest_gameRound_bet(_toPlay.peek().getHighest_round_bet());
                }

                _game.setHighest_bet(_toPlay.peek().getBet());
                _toPlay.peek().setCurrent_game_money(_toPlay.poll().getCurrent_game_money() - bet);
                _game.setPot(_game.getPot() + bet);
            } else {//New highest bet

                if (_toPlay.peek().getHighest_round_bet() + bet > _toPlay.peek().getHighest_round_bet()) {
                    _toPlay.peek().setHighest_round_bet(_toPlay.peek().getHighest_round_bet() + bet);
                }
                if (_toPlay.peek().getHighest_round_bet() > _game.getHighest_gameRound_bet()) {
                    _game.setHighest_gameRound_bet(_toPlay.peek().getHighest_round_bet());
                }

                _game.setHighest_bet(_toPlay.peek().getBet());

                LinkedList<User> tempList = new LinkedList<>();
                tempList.clear();
                tempList.addAll(_turnOrder);

                while (tempList.peek().getId().equals(_toPlay.peek().getId())) {//should never infinite loop
                    tempList.add(tempList.poll());
                }
                tempList.removeIf(x -> _toPlay.contains(x) || x.getFolded() || x.isAllIn());
                _toPlay.addAll(tempList);

                _toPlay.peek().setCurrent_game_money(_toPlay.poll().getCurrent_game_money() - bet);
                _game.setPot(_game.getPot() + bet);
            }
        } else {
            _toPlay.poll().setFolded(true);
        }
        endRound();
        return true;
    }

    private void endRound() {
        int notFolded = 0;
        int notFoldedorAllIn = 0;
        for (User u : _players) {
            if (!u.getFolded())
                notFolded++;
        }
        for (User u : _players) {
            if (!u.getFolded() && !u.isAllIn())
                notFoldedorAllIn++;
        }

        if (_toPlay.size() > 0 && notFolded > 1) //dont end if people need to play
            return;
        if (_game.getRound() > 3 || notFolded < 2 || notFoldedorAllIn < 2) {
            endGame(); //infinite loop if everyone folds, should never happen
            return;
        }
        _players.forEach(x -> x.setHighest_round_bet(0));
        _game.setHighest_gameRound_bet(0);
        _game.setRound(_game.getRound() + 1);
        _deck.dealPublicCards();
        for (int i = 0; i < _turnOrder.size(); i++) { //should be clear
            if (!_turnOrder.get(i).getFolded() && !_turnOrder.get(i).isAllIn()) {
                _toPlay.add(_turnOrder.get(i));
            }
        }
    }

    private void endGame() {
        for (int i = _game.getRound(); i < 5; i++) { //deal the rest of the cards
            _game.setRound(i);
            _deck.dealPublicCards();
        }

        Gson gson = new Gson();
        _showDownGame = gson.toJson(_game);
        _oldHands = gson.toJson(_players);
        _showHands = gson.toJson(_players.stream().filter(x -> !x.getFolded()).collect(Collectors.toList()));

        HandComparator compare = new HandComparator();
        ArrayList<Integer> currentCards = new ArrayList<>();
        ArrayList<Integer> bestCards = new ArrayList<>();
        ArrayList<User> bestHand = new ArrayList<>();
        bestHand.add(_turnOrder.get(0));
        for (int i = 1; i < _turnOrder.size(); i++) {
            bestCards.add(bestHand.get(0).getCard1());
            bestCards.add(bestHand.get(0).getCard2());
            bestCards.add(_game.getPublic_card1());
            bestCards.add(_game.getPublic_card2());
            bestCards.add(_game.getPublic_card3());
            bestCards.add(_game.getPublic_card4());
            bestCards.add(_game.getPublic_card5());

            currentCards.add(_turnOrder.get(i).getCard1());
            currentCards.add(_turnOrder.get(i).getCard2());
            currentCards.add(_game.getPublic_card1());
            currentCards.add(_game.getPublic_card2());
            currentCards.add(_game.getPublic_card3());
            currentCards.add(_game.getPublic_card4());
            currentCards.add(_game.getPublic_card5());

            if (compare.compareHands(currentCards.stream().mapToInt(j -> j).toArray(), bestCards.stream().mapToInt(j -> j).toArray()) > 0) {
                if (_turnOrder.get(i).getFolded()) {
                    continue;
                } else {
                    bestHand = new ArrayList<>();
                    bestHand.add(_turnOrder.get(i));
                }
            } else if (compare.compareHands(currentCards.stream().mapToInt(j -> j).toArray(), bestCards.stream().mapToInt(j -> j).toArray()) == 0) {
                bestHand.add(_turnOrder.get(i));
            } else {
                continue;
            }

        }
        if (bestHand.size() == 1) {
            //winner winner chicken dinner
            _winner.add(bestHand.get(0).getId());
            bestHand.get(0).setCurrent_game_money(bestHand.get(0).getCurrent_game_money() + _game.getPot());
            _game.setPot(0);
        } else {
            int money = _game.getPot() / bestHand.size();
            for (int i = 0; i < bestHand.size(); i++) {
                _winner.add(bestHand.get(i).getId());
                bestHand.get(i).setCurrent_game_money(bestHand.get(i).getCurrent_game_money() + money);
            }
            _game.setPot(0);
        }

        _showHands = gson.toJson(_players.stream().filter(x -> x.getId().equals(_winner.get(0))).findFirst().get()); //TODO: Remove

        _game.setRound(0);
        _players.forEach(x -> {
            x.setFolded(false);
            x.setAllIn(false);
            x.setBet(0);
            x.setHighest_round_bet(0);
        });
        _turnOrder.add(_turnOrder.poll()); //change big blind
        _toPlay.clear();
        _toPlay.addAll(_turnOrder);
        boolean blindsReady = false;
        int prevGamesize = _turnOrder.size();
        while (!blindsReady) {
            for (int i = 0; i < prevGamesize; i++) {
                if (_players.size() < 2) { //not enough players to play
                    _gameInit = false;
                    return;
                }
                if (_toPlay.peek().getCurrent_game_money() < _blind) {
                    _tooPoor.add(_toPlay.peek());
                    removePlayer(_toPlay.poll());
                } else if (_toPlay.peek().getCurrent_game_money() < _blind * 2) {
                    //_turnOrder.poll(); //get #2
                    _tooPoor.add(_toPlay.peek());
                    removePlayer(_toPlay.poll());
                } else {
                    _toPlay.remove();
                }
            }
            if (_players.size() < 2) { //not enough players to play
                _gameInit = false;
                return;
            }
            blindsReady = true;
        }
        _toPlay.addAll(_turnOrder);
        while (_toPlay.size() > 2) {
            _toPlay.poll();
        }
        _toPlay.peek().setBet(_blind);
        _toPlay.peek().setHighest_round_bet(_blind);
        _toPlay.peek().setCurrent_game_money(_toPlay.poll().getCurrent_game_money() - _blind);
        _toPlay.peek().setBet(_blind * 2);
        _toPlay.peek().setHighest_round_bet(_blind * 2);
        _toPlay.peek().setCurrent_game_money(_toPlay.poll().getCurrent_game_money() - _blind * 2);
        _game.setPublic_card1(-1);
        _game.setPublic_card2(-1);
        _game.setPublic_card3(-1);
        _game.setPublic_card4(-1);
        _game.setPublic_card5(-1);
        _toPlay.clear();
        _toPlay.addAll(_turnOrder);
        _game.setHighest_bet(_blind * 2);
        _game.setHighest_gameRound_bet(_blind * 2);
        _game.setPot(_blind + _blind * 2);
        _deck.deal();
    }

    /**
     * adds a user to the game
     *
     * @param u user to be added
     * @return boolean for if the add was legal/successful
     */
    public boolean addPlayer(User u) { // warning will update them
        for (int i = 0; i < _players.size(); i++) {
            if (_players.get(i).getId().equals(u.getId())) {
                _players.set(i, u);
                return true;
            }
        }
        if (_players.size() < _maxPlayers) {
            u.setCard1(-1);
            u.setCard2(-1);
            u.setHighest_round_bet(0);
            u.setFolded(true);
            _turnOrder.add(u);
            _players.add(u);
            return true;
        }
        return false;
    } //TODO: add if they join mid game

    /**
     * removes a user from the game
     *
     * @param u user to be removed
     * @return boolean for if the remove was legal/successful
     */
    public boolean removePlayer(User u) { //TODO update them if they leave
        bet(u, -1); //fold if it is their turn
        boolean removed = false;
        removed = _players.removeIf(x -> x.getId().equals(u.getId()));
        _turnOrder.removeIf(x -> x.getId().equals(u.getId()));
        _toPlay.removeIf(x -> x.getId().equals(u.getId()));
        if (_players.size() < 2)
            _gameInit = false;
        return removed;
    } //TODO: add if they leave mid game

    /**
     * gets if the game was initialized
     *
     * @return boolean for if the game was initialized
     */
    public boolean getInitialized() {
        return _gameInit;
    }

    /**
     * gets the model of the game
     *
     * @return the model of the game
     */
    public Game getGame() {
        return _game;
    }

    /**
     * gets a list of players that were too poor and had to be removed
     *
     * @return
     */
    public List TooPoor() {
        return _tooPoor;
    }

    /**
     * gets a list of players in the poker game
     *
     * @return
     */
    public List getPlayers() {
        return _players;
    }

    /**
     * gets the current player's ID
     *
     * @return
     */
    public String getToPlayNextId() {
        if (_toPlay.peek() != null) {
            return _toPlay.peek().getId();
        } else {
            return null;
        }
    }

    /**
     * gets a List that contains the winner(s)'s id
     *
     * @return
     */
    public List<String> getWinner() {
        return _winner;
    }

    public String sendGameState() {
        Gson gson = new Gson();
        String gameState = new String(gson.toJson(_players) + "**" + gson.toJson(_game)
                + "**" + getToPlayNextId() + "**null**null");
        /*if(!(_showHands.isEmpty() || _oldHands.isEmpty() || _winner.isEmpty() || _showDownGame.isEmpty())) {
            gameState = new String(_oldHands + "**" + _showDownGame
                    + "**" + getToPlayNextId() + "**" + _players.stream().filter(x -> x.getId().equals(_winner.get(0))).findFirst().get().getId()
                    + "**" + _showHands);
            _showDownGame = null;
            _showHands = new String();
            _oldHands = new String();
        }*/
        return gameState;
    }

    private class Deck {
        private List<Integer> _cards = new ArrayList<>();
        private final Random _rand = new Random();
        private final Poker _poker;

        public Deck(Poker poker) {
            _poker = poker;
            _rand.setSeed(System.nanoTime());
        }

        public void deal() {
            _cards = new ArrayList<>();
            for (int i = 0; i < 52; i++) {
                _cards.add(i);
            }
            for (User u : _poker._players) {
                u.setCard1(_cards.remove(_rand.nextInt(_cards.size())));
                u.setCard2(_cards.remove(_rand.nextInt(_cards.size())));
            }
        }

        public void dealPublicCards() {
            switch (_poker.getGame().getRound()) {
                case 1:
                    _poker._game.setPublic_card1(_cards.remove(_rand.nextInt(_cards.size())));
                    _poker._game.setPublic_card2(_cards.remove(_rand.nextInt(_cards.size())));
                    _poker._game.setPublic_card3(_cards.remove(_rand.nextInt(_cards.size())));
                    break;
                case 2:
                    _poker._game.setPublic_card4(_cards.remove(_rand.nextInt(_cards.size())));
                    break;
                case 3:
                    _poker._game.setPublic_card5(_cards.remove(_rand.nextInt(_cards.size())));
                    break;
            }
        }

        public String toString() {
            return _cards.toString();
        }
    }
}
