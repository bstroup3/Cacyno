package com._2_ug_1.cacyno;

import com._2_ug_1.cacyno.game.Poker;
import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.models.User;
import com.google.common.collect.Sets;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class Poker_Test {
    private Poker _sut;
    private LinkedList<User> _users;
    private User _baseUser;
    private Game _game;
    private Game _baseGame;
    private Game _mockGame;
    private Poker _msut;
    private LinkedList<User>_musers;
    private int blind = 50;

    @BeforeEach
    public void Poker_Test() {
        _game = new Game();
        _game.setId("1");
        _game.setRound(-1);
        _game.setPublic_card1(-1);
        _game.setPublic_card2(-1);
        _game.setPublic_card3(-1);
        _game.setPublic_card4(-1);
        _game.setPublic_card5(-1);
        _game.setPot(-1);
        _users = new LinkedList<>();
        _sut = new Poker(_game);

        for (int i = 0; i < 7; i++) {
            User user = new User();
            user.setId(Integer.toString(_users.size()));
            user.setGame(_game);
            user.setCurrent_game_money(10000);
            user.setCard1(-1);
            user.setCard2(-1);
            if (i < 6) {
                _users.add(user);
                _sut.addPlayer(user);
            } else {
                _baseUser = user;
            }
        }
        _sut.initGame();


        _mockGame = new Game();
        _mockGame.setId("1");
        _mockGame.setRound(-1);
        _mockGame.setPublic_card1(-1);
        _mockGame.setPublic_card2(-1);
        _mockGame.setPublic_card3(-1);
        _mockGame.setPublic_card4(-1);
        _mockGame.setPublic_card5(-1);
        _mockGame.setPot(-1);
        _musers = new LinkedList<>();
        _msut = new Poker(_mockGame);

        for (int i = 0; i < 7; i++) {
            User user = new User();
            user.setId(Integer.toString(_musers.size()));
            user.setGame(_mockGame);
            user.setCurrent_game_money(10000);
            user.setCard1(-1);
            user.setCard2(-1);
            if (i < 6) {
                _musers.add(user);
                _msut.addPlayer(user);
            } else {
                _baseUser = user;
            }
        }
        _msut.initGame();
    }

    @Test
    public void poker_initGame_BasicTest() {
        for (int i = 0; i < _users.size(); i++) {
            User u = _users.get(i);
            assertFalse(u.getFolded());
            assertNotEquals(-1, u.getCard1());
            assertNotEquals(-1, u.getCard2());
            if (i == _users.size() - 2) {
                assertEquals(_baseUser.getCurrent_game_money() - blind, u.getCurrent_game_money()); //blind
            } else if (i == _users.size() - 1) {
                assertEquals(_baseUser.getCurrent_game_money() - blind * 2, u.getCurrent_game_money()); //blind
            } else {
                assertEquals(_baseUser.getCurrent_game_money(), u.getCurrent_game_money());
            }
        }
        assertEquals(_game.getPot(), blind + blind * 2);
        assertEquals(_game.getRound(), 0);
        _sut = new Poker(_game);
        assertFalse(_sut.initGame());
        _sut.addPlayer(_baseUser);
        assertFalse(_sut.initGame());
    }

    public void poker_bet_BasicTest() {
        String oldCards = new String("");
        for (User u : _users) {
            oldCards += u.getCard1();
            oldCards += u.getCard2();
        }
        for (int i = 0; i < 500; i += 100) {
            for (int j = 0; j < _users.size(); j++) {
                for (int k = 0; k < _users.size(); k++) {
                    User u = _users.get(k);
                    if (j != k) { //not turn to play
                        int beforeBet = u.getCurrent_game_money();
                        assertFalse(_sut.bet(u, _game.getHighest_bet() - u.getBet()));
                        assertEquals(beforeBet, u.getCurrent_game_money());
                    }
                }
                User u = _users.get(j);
                assertTrue(_sut.bet(u, 100));
                if (j == _users.size() - 2) {
                    assertEquals(_baseUser.getCurrent_game_money() - (i + 100) - blind
                            , u.getCurrent_game_money()); //blind
                } else if (j == _users.size() - 1) {
                    if (u.getCurrent_game_money() != 12500) { //chance they win
                        assertEquals(_baseUser.getCurrent_game_money() - (i + 100) - blind * 2
                                , u.getCurrent_game_money(), 50); //blind
                    }
                } else {
                    assertEquals(_baseUser.getCurrent_game_money() - (i + 100), u.getCurrent_game_money());
                }
            }
            if (i == 3) {
                assertNotEquals(-1, _game.getPublic_card1());
                assertNotEquals(-1, _game.getPublic_card2());
                assertNotEquals(-1, _game.getPublic_card3());
                assertNotEquals(-1, _game.getPublic_card4());
                assertNotEquals(-1, _game.getPublic_card5());
            }
            if (i < 4) {
                assertEquals(blind + (blind * 2) + (i + 100) * _users.size(), _game.getPot());
                assertEquals(i, _game.getRound());
            }
        }
        assertEquals(blind + (blind * 2), _game.getPot());
        assertEquals(0, _game.getRound());
        String newCards = new String("");
        for (User u : _users) {
            newCards += u.getCard1();
            newCards += u.getCard2();
        }
        assertNotEquals(oldCards, newCards);
        assertTrue(_sut.bet(_users.get(1), 1)); //new leader
    }


    @Test
    public void check_round_poker() {
        assertEquals(0,_msut.getGame().getRound());
    }

    @Test
    public void check_winner_money() {
        int size = _users.size();
        int beforeBets = _users.get(0).getCurrent_game_money();
        int rounds = 0;
        int Pot = 0;
        for (int i = 0; i < 5; i++) {//goes through entire game
            assertEquals(_msut.getGame().getRound(), i);
            for (int j = 0; j < size; j++) {

                if (_users.peek().getId() != _msut.getToPlayNextId()) {
                    int tmp = 0;
                }
                if (i == 0 && _users.size() == 2) {//first round buffer/call for small blind
                    assertTrue(_msut.bet(_users.poll(), 50));
                } else if (i == 0 && _users.size() == 1) {//first round buffer/call for big blind
                    assertTrue(_msut.bet(_users.poll(), 0));
                } else {
                    assertTrue(_msut.bet(_users.poll(), 100));
                    if (i == 4 && _users.size() == 1) {
                        Pot = _msut.getGame().getPot();
                        Pot += 100;
                    }
                }
                rounds = i + 1;
            }
            _users.addAll(_msut.getPlayers());
        }//should be done here
        assertEquals(_msut.getGame().getRound(), 0);//check to make sure game is actually over
        User u = new User();
        if (_msut.getWinner().size() == 1) {//Only one winner
            for (int i = 0; i < size; i++) {//get winner
                if (_users.get(i).getId() == _msut.getWinner().get(0)) {
                    u = _users.get(i);
                    break;
                }
            }
            assertEquals(beforeBets - (100 * rounds) + Pot, u.getCurrent_game_money(), 100);//player bets 100 per round (100*rounds) //buffer of 100 due to the blinds

        } else {//Multiple Winners
            Pot = Pot / _msut.getWinner().size();
            for (int i = 0; i < _msut.getWinner().size(); i++) {//get winner
                for (int j = 0; j < size; j++) {
                    if (_users.get(j).getId() == _msut.getWinner().get(i)) {
                        u = _users.get(j);
                        assertEquals(beforeBets - (100 * rounds) + Pot, u.getCurrent_game_money(), 100);//buffer for blind
                    }
                }
            }

        }

    }

    @Test
    public void poker_test_rounds() {//tests if all of the rounds work in a game and if all of the players can bet in order with the blinds
        int size = _users.size();
        for (int i = 0; i < 5; i++) {
            assertEquals(_msut.getGame().getRound(), i);
            for (int j = 0; j < size; j++) {

                if (_users.peek().getId() != _msut.getToPlayNextId()) {
                    int tmp = 0;
                }
                if (i == 0 && _users.size() == 2) {//first round buffer/call for small blind
                    assertTrue(_msut.bet(_users.poll(), 50));
                } else if (i == 0 && _users.size() == 1) {//first round buffer/call for big blind
                    assertTrue(_msut.bet(_users.poll(), 0));
                } else {
                    assertTrue(_msut.bet(_users.poll(), 100));
                }
            }
            _users.addAll(_msut.getPlayers());
        }

    }

    /**
     * tests all combinations of players folding
     * TODO: remove player and add player can be tested in a very similar way
     */
    @Test
    public void poker_bet_folded() {
        List<List<List<Integer>>> AllfoldOnTurns = new ArrayList<>();
        List<Integer> players = new ArrayList<>();
        for (int i = 0; i < _users.size(); i++) {
            players.add(i);
        }
        calculateFoldOnTurns(Sets.newHashSet(players), Sets.newHashSet(), 0, AllfoldOnTurns);
        for (int i = 0; i < AllfoldOnTurns.size(); i++) {
            test_folded(AllfoldOnTurns.get(i));
        }
    }

    /**
     * adds all the combinations of lists of player, turn they fold on to a list
     *
     * @param players
     * @param blackList
     * @param turn
     * @param _foldOnTurns
     * @return
     */
    private List<List<List<Integer>>> calculateFoldOnTurns(Set<Integer> players, Set<Integer> blackList, int turn
            , List<List<List<Integer>>> _foldOnTurns) {
        if (turn > 3 || players.size() - blackList.size() == 1)
            return _foldOnTurns;
        if (players.size() - blackList.size() == 2) {
            Set<Integer> legalPlayers = players.stream().filter(x ->
                    !blackList.contains(x)).collect(Collectors.toSet());
            List<List<Integer>> foldOrders = new ArrayList<>();
            List<Integer> list = new ArrayList<>();
            list.add((Integer) (legalPlayers.toArray())[0]);
            list.add(turn);
            foldOrders.add(list);
            _foldOnTurns.add(foldOrders);
            foldOrders = new ArrayList<>();
            list = new ArrayList<>();
            list.add((Integer) (legalPlayers.toArray())[1]);
            list.add(turn);
            foldOrders.add(list);
            _foldOnTurns.add(foldOrders);
            return _foldOnTurns;
        } else {
            Set<Integer> legalPlayers = players.stream().filter(x ->
                    !blackList.contains(x)).collect(Collectors.toSet());
            for (int x = legalPlayers.size(); x > 0; x--) {
                Set<Set<Integer>> combinations = Sets.combinations(legalPlayers, Math.min(x, 5));
                for (Set<Integer> s : combinations) {
                    if (!s.isEmpty()) {
                        Set<Integer> newBlacklist = Sets.newHashSet(blackList);
                        newBlacklist.addAll(s);
                        List<List<Integer>> foldOrders = new ArrayList<>();
                        List<Integer> list;
                        for (Integer i : s) {
                            list = new ArrayList<>();
                            list.add(i);
                            list.add(turn);
                            foldOrders.add(list);
                        }
                        if (!foldOrders.isEmpty())
                            _foldOnTurns.add(foldOrders);
                        List<List<List<Integer>>> deepTurns = new ArrayList<>();
                        calculateFoldOnTurns(players, newBlacklist, turn + 1, deepTurns);
                        for (List<List<Integer>> deepList : deepTurns) {
                            if (!foldOrders.isEmpty() || !deepList.isEmpty()) {
                                deepList.addAll(foldOrders);
                                _foldOnTurns.add(deepList);
                            }
                        }
                    }
                }
            }
        }
        return calculateFoldOnTurns(players, blackList, turn + 1, _foldOnTurns);
    }

    /**
     * goes through a game while players fold on a List<Integer> { player index, round }
     *
     * @param toFold
     */
    private void test_folded(List<List<Integer>> toFold) { //list of { { whoFolds, round } , ... }
        Poker_Test();
        int numFolded = 0;
        boolean firstRound = true;
        boolean endGame = false;
        int prevGamePot = 0;
        List<Integer> playerBets = new ArrayList<>();
        int size = _users.size();
        for (int i = 0; i < _users.size(); i++) {
            if (i == _users.size() - 2) {
                playerBets.add(_baseUser.getCurrent_game_money() - blind);
            } else if (i == _users.size() - 1) {
                playerBets.add(_baseUser.getCurrent_game_money() - blind * 2);
            } else {
                playerBets.add(_baseUser.getCurrent_game_money());
            }
        }
        for (int i = 0; i < 5 && numFolded < _users.size() - 1; i++) {
            for (int j = 0; j < _users.size() && numFolded < _users.size() - 1 && !endGame; j++) {
                int round = i;
                int user = j;
                List<List<Integer>> folded = toFold.stream().filter(x -> {
                    return x.get(0) == user && x.get(1) <= round;
                }).collect(Collectors.toList());
                if (!folded.isEmpty()) {
                    if (folded.stream().anyMatch(x -> { //fold this round
                        return x.get(1) == round;
                    })) {
                        User u = _users.get(j);
                        assertTrue(_sut.bet(u, -1), "Poker: \n" + _sut.toString());
                        numFolded++;
                        if (_game.getRound() == 0 && (!firstRound || numFolded < _users.size() - 1)) {
                            assertEquals(playerBets.get(j), u.getCurrent_game_money(), 100 + prevGamePot);
                            endGame = true;
                        } else {
                            assertEquals(playerBets.get(j), u.getCurrent_game_money(), "Poker: \n" + _sut.toString());
                        }
                    }
                } else {
                    for (int k = 0; k < _users.size(); k++) {
                        User u = _users.get(k);
                        if (j != k) { //not turn to play
                            int beforeBet = u.getCurrent_game_money();
                            assertFalse(_sut.bet(u, 100));
                            assertEquals(beforeBet, u.getCurrent_game_money());
                        }
                    }
                    User u = _users.get(j);
                    assertTrue(_sut.bet(u, 100));
                    playerBets.set(j, playerBets.get(j) - 100);
                    if (_game.getRound() == 0 && (!firstRound || numFolded < _users.size() - 1)) {
                        assertEquals(playerBets.get(j), u.getCurrent_game_money(), 100 + prevGamePot);
                        endGame = true;
                    } else {
                        prevGamePot += 100;
                        assertEquals(playerBets.get(j), u.getCurrent_game_money());
                    }
                }
                prevGamePot = _game.getPot();
                if (_game.getRound() > 0) {
                    firstRound = false;
                }

            }
            if (i < 4 && numFolded < _users.size() - 1) {
                int sumOfBets = 0;
                for (Integer num : playerBets) {
                    sumOfBets += num;
                }
                assertEquals(_baseUser.getCurrent_game_money() * _users.size() - sumOfBets, _game.getPot());
                assertEquals(endGame ? 0 : i + 1, _game.getRound());
            }
        }
        assertEquals(blind + (blind * 2), _game.getPot(), 100);
        assertEquals(0, _game.getRound());
        assertTrue(_sut.bet(_users.get(1), 100)); //new leader
    }
}
