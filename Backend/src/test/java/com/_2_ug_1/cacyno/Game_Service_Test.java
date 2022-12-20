package com._2_ug_1.cacyno;

import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.models.User;
import com._2_ug_1.cacyno.repos.IGameRepo;
import com._2_ug_1.cacyno.repos.IUserRepo;
import com._2_ug_1.cacyno.services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class Game_Service_Test {
    private GameService _sut;
    private IUserRepo _userRepo;
    private IGameRepo _gameRepo;
    private List<User> _users;
    private List<Game> _games;

    @BeforeEach
    public void Game_Service_Test() {
        _userRepo = mock(IUserRepo.class);
        _gameRepo = mock(IGameRepo.class);
        _sut = new GameService(_gameRepo, _userRepo);
        _users = new ArrayList<>();
        _games = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Game game = new Game();
            game.setId(Integer.toString(i));
            game.setPublic_card1(-1);
            game.setPublic_card2(-1);
            game.setPublic_card3(-1);
            game.setPublic_card4(-1);
            game.setPublic_card5(-1);
            _games.add(game);
            for (int j = 0; j < 4 + i; j++) {
                User user = new User();
                user.setId(Integer.toString(_users.size()));
                user.setGame(game);
                user.setCurrent_game_money(1000);
                user.setCard1(-1);
                user.setCard2(-1);
                _users.add(user);
            }
        }

        doAnswer(x -> {
            List<User> users = new ArrayList<>();
            for (User u : _users) {
                if (u.getGame().getId().equals(x.getArgument(0))) {
                    users.add(u);
                }
            }
            Future<List<User>> future = mock(Future.class); //probably a better way...
            when(future.get()).thenReturn(users);
            return future;
        }).when(_userRepo).getAllByGameId(any(String.class));

        doAnswer(x -> {
            for (Game g : _games) {
                if (g.getId().equals(x.getArgument(0))) {
                    Future<Game> future = mock(Future.class); //probably a better way...
                    when(future.get()).thenReturn(g);
                    return future;
                }
            }
            return null;
        }).when(_gameRepo).AsyncGetById(any(String.class));

        doAnswer(x -> {
            for (User u : _users) {
                if (u.getId().equals(((User) x.getArgument(0)).getId())) {
                    _users.set(_users.indexOf(u), x.getArgument(0));
                    return x.getArgument(0);
                }
            }
            return null;
        }).when(_userRepo).save(any(User.class));

        doAnswer(x -> {
            for (Game g : _games) {
                if (g.getId().equals(((Game) x.getArgument(0)).getId())) {
                    _games.set(_games.indexOf(g), x.getArgument(0));
                    return x.getArgument(0);
                }
            }
            return null;
        }).when(_gameRepo).save(any(Game.class));
    }

    @Test
    public void GameService_GameInit_BasicTest() {
        String testId = "0";
        _sut.gameInit(testId);
        for (User user : _users) {
            if (user.getGame().getId().equals(testId)) {
                assertNotEquals(-1, user.getCard1());
                assertNotEquals(-1, user.getCard2());
            } else {
                assertEquals(-1, user.getCard1());
                assertEquals(-1, user.getCard2());
            }
        }
        for (int i = 0; i < _games.size(); i++) {
            if (i == 0) {
                assertNotEquals(-1, _games.get(i).getPublic_card1());
                assertNotEquals(-1, _games.get(i).getPublic_card2());
                assertNotEquals(-1, _games.get(i).getPublic_card3());
                assertNotEquals(-1, _games.get(i).getPublic_card4());
                assertNotEquals(-1, _games.get(i).getPublic_card5());
            } else {
                assertEquals(-1, _games.get(i).getPublic_card1());
                assertEquals(-1, _games.get(i).getPublic_card2());
                assertEquals(-1, _games.get(i).getPublic_card3());
                assertEquals(-1, _games.get(i).getPublic_card4());
                assertEquals(-1, _games.get(i).getPublic_card5());
            }
        }
    }

    @Test
    public void GameService_GameInit_NoCardsRepeat() {
        String testId = "0";
        for (int j = 0; j < 19; j++) { //max possible players, will break above 23
            User user = new User();
            user.setId(Integer.toString(_users.size()));
            user.setGame(_games.get(0));
            user.setCurrent_game_money(1000);
            _users.add(user);
        }
        _sut.gameInit(testId);
        List<Integer> testList = new ArrayList<>();
        for (User u : _users) {
            if (u.getGame().getId().equals(testId)) {
                testList.add(u.getCard1());
                testList.add(u.getCard2());
            }
        }
        testList.add(_games.get(0).getPublic_card1());
        testList.add(_games.get(0).getPublic_card2());
        testList.add(_games.get(0).getPublic_card3());
        testList.add(_games.get(0).getPublic_card4());
        testList.add(_games.get(0).getPublic_card5());

        assertEquals(51, testList.size());
        int numOfMissedCards = 0;
        for (int i = 0; i < 52; i++) {
            if (!testList.contains(i))
                numOfMissedCards++;
        }
        assertEquals(1, numOfMissedCards);
    }

    @Test
    public void GameService_Play_bet() {
        _sut.gameInit("0");
        List<String> hasBet = new ArrayList<>();
        int bet = 100;

        hasBet.add("0");
        testBet(hasBet, _users.get(0), bet);

        testBet(hasBet, _users.get(2), bet);

        hasBet.add("1");
        testBet(hasBet, _users.get(1), bet);

        testBet(hasBet, _users.get(0), bet);

        hasBet.add("2");
        testBet(hasBet, _users.get(2), bet);
    }

    private void testBet(List<String> hasBet, User user, int betAmount) {
        int initMoney = 1000;
        assertNotNull(_sut.play("0", user.getId(), betAmount));
        for (User u : _users) {
            if (hasBet.contains(u.getId())) {
                assertEquals(initMoney - betAmount, u.getCurrent_game_money());
            } else {
                assertEquals(initMoney, u.getCurrent_game_money());
            }
        }
        assertEquals(betAmount * hasBet.size(), _games.get(0).getPot());
        assertEquals(0, _games.get(0).getRound());
    }

    @Test
    public void GameService_Play_fold() {
        _sut.gameInit("0");
        int initMoney = _users.get(0).getCurrent_game_money();
        int initPot = _games.get(0).getPot();
        assertNotNull(_sut.play("0", "0", -1));
        for (User u : _users) {
            if (u.getId().equals("0")) {
                assertEquals(true, u.getFolded());
                assertEquals(initMoney, u.getCurrent_game_money());
            } else {
                assertEquals(false, u.getFolded());
            }
        }
        assertEquals(0, _games.get(0).getPot()); //TODO acount for blinds
    }

    @Test
    public void GameService_Play_StartNewGame() {
        _sut.gameInit("0");
        int betAmount = 10;
        int initMoney = 1000;
        List<String> hasBet;
        List<User> oldData = new ArrayList<>();
        List<Integer> oldTableCards = new ArrayList<>();

        for (User u : _users.subList(0, 4)) { //copy prev game data
            User user = new User();
            user.setCurrent_game_money(u.getCurrent_game_money());
            user.setPosition(u.getPosition());
            user.setCard1(u.getCard1());
            user.setCard2(u.getCard2());
            oldData.add(user);
        }
        String oldCards = new String();
        oldCards += Integer.toString(_games.get(0).getPublic_card1());
        oldCards += Integer.toString(_games.get(0).getPublic_card2());
        oldCards += Integer.toString(_games.get(0).getPublic_card3());
        oldCards += Integer.toString(_games.get(0).getPublic_card4());
        oldCards += Integer.toString(_games.get(0).getPublic_card5());

        for (int i = 0; i < 4; i++) { //play an entire game
            hasBet = new ArrayList<>();
            assertEquals(i, _games.get(0).getRound());
            for (int j = 0; j < 4; j++) {
                hasBet.add(_users.get(j).getId());
                assertNotNull(_sut.play("0", hasBet.get(hasBet.size() - 1), betAmount));
                for (User u : _users) {
                    if (u.getGame().getId().equals("0")) {
                        if (hasBet.contains(u.getId())) {
                            assertEquals(initMoney - betAmount * (i + 1), u.getCurrent_game_money());
                        } else {
                            assertEquals(initMoney - betAmount * i, u.getCurrent_game_money());
                        }
                    } else {
                        assertEquals(initMoney, u.getCurrent_game_money());
                    }
                }
            }
            if (i < 3) {
                assertEquals(betAmount * hasBet.size() * (i + 1), _games.get(0).getPot());
            }
        }

        //check for new round
        assertEquals(0, _games.get(0).getRound());
        assertEquals(0, _games.get(0).getPot()); //TODO add blinds
        String newCards = new String();
        newCards += Integer.toString(_games.get(0).getPublic_card1());
        newCards += Integer.toString(_games.get(0).getPublic_card2());
        newCards += Integer.toString(_games.get(0).getPublic_card3());
        newCards += Integer.toString(_games.get(0).getPublic_card4());
        newCards += Integer.toString(_games.get(0).getPublic_card5());

        for (int i = 0; i < 4; i++) {
            assertNotEquals(oldData.get(i).getCurrent_game_money(), _users.get(i).getCurrent_game_money());
            assertNotEquals(oldData.get(i).getPosition(), _users.get(i).getPosition());
            oldCards += Integer.toString(oldData.get(i).getCard1()) + Integer.toString(oldData.get(i).getCard2());
            newCards += Integer.toString(_users.get(i).getCard1()) + Integer.toString(_users.get(i).getCard2());
            assertEquals(false, _users.get(i).getFolded());
            assertEquals(0, _users.get(i).getBet());
        }

        assertNotEquals(oldCards, newCards); //very unlikely
    }
}
