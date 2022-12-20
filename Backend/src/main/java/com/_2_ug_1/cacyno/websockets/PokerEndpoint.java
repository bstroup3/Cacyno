package com._2_ug_1.cacyno.websockets;

import com._2_ug_1.cacyno.game.Poker;
import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.models.User;
import com._2_ug_1.cacyno.repos.IGameRepo;
import com._2_ug_1.cacyno.repos.IUserRepo;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;





/*
 * TODO: add admin for resets and starting
 */

/**
 * Creates connection between players and the server allowing for calls to a game object to play poker
 */
@ServerEndpoint("/poker/{userId}")
@Component
public class PokerEndpoint {
    private static final Map<String, Poker> _gamesMap = new HashMap<>();
    private static final Map<String, List<Session>> _gameSessionMap = new HashMap<>();
    private static final Map<String, Session> _userSessionMap = new HashMap<>();
    private static final Map<Session, String> _sessionUserMap = new HashMap<>();
    private final Logger _logger = LoggerFactory.getLogger(PokerEndpoint.class);
    private static String _errorStrings = "";

    private static IGameRepo _gameRepo;
    private static IUserRepo _userRepo;

    /**
     * sets the user and game repositories to pull information from
     *
     * @param userRepo the user repo
     * @param gameRepo the game repo
     */
    @Autowired
    public void setRepos(IUserRepo userRepo, IGameRepo gameRepo) {
        _userRepo = userRepo;
        _gameRepo = gameRepo;
    }

    /**
     * Creates the user's connection to the server and adds them to a specified game
     *
     * @param session user's session
     * @param userId  The id for a user in the user repo
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session
            , @PathParam("userId") String userId) throws IOException {
        _logger.info("Entered into Open: " + userId);
        if (userId.equals("0")) {
            _sessionUserMap.put(session, userId);
            _userSessionMap.put(userId, session);
            sendUserMessage("0", _errorStrings);
            return;
        }
        User u = getUser(userId);
        if (u == null) {//let them know
            sendErrorString(session, u, "User could not be found in repo");
        }
        if (u.getGame() == null) {
            sendErrorString(session, u, "Game can not be null");
        }
        Game g = getGame(u.getGame().getId());
        if (g == null) {
            sendErrorString(session, u, "Game could not be found in repo");
        }
        g.setActive(true);
        _gameRepo.save(g);
        u.setGame(g);
        if (!_gamesMap.containsKey(u.getGame().getId())) {
            Poker poker = new Poker(g);
            if (!poker.addPlayer(u)) {
                sendErrorString(session, u, "Failed to join game: " + poker);
            }
            _gamesMap.put(g.getId(), poker);
        } else {
            synchronized (_gamesMap.get(u.getGame().getId())) {
                _gamesMap.get(u.getGame().getId()).addPlayer(u);
            }
            /*if (!_gamesMap.get(u.getGame().getId()).addPlayer(u)) { //TODO: let them know it is full/better error detection
                sendErrorString(session, u, "Failed to join game: " + _gamesMap.get(u.getGame().getId()).toString());
            }*/
        }
        _userRepo.save(u);
        if (_gameSessionMap.containsKey(u.getGame().getId())) {
            _gameSessionMap.get(u.getGame().getId()).add(session);
        } else {
            List<Session> sessionList = new ArrayList<>();
            sessionList.add(session);
            _gameSessionMap.put(u.getGame().getId(), sessionList);
        }
        Poker poker = _gamesMap.get(u.getGame().getId());
        if (_userSessionMap.containsKey(userId)) {
            sendErrorString(session, u, "User is already in a session: " + poker.toString());
        }
        sendGameMessage(u);
        _sessionUserMap.put(session, userId);
        _userSessionMap.put(userId, session);
    }

    /*
     * TODO: update user and game(if needed) in repo/database
     */

    /**
     * Closes a user's session, removes them from the game and updates database
     *
     * @param session user's session
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        _logger.info("Entered into Close: " + _sessionUserMap.get(session));
        if (!_sessionUserMap.containsKey(session))
            return;
        User toRemove = getUser(_sessionUserMap.get(session));
        synchronized (_gamesMap.get(toRemove.getGame().getId())) {
            Poker p = _gamesMap.get(toRemove.getGame().getId());
            sendGameMessage(toRemove, "Has Left:" + toRemove.getUsername());
            if (p.TooPoor().contains(toRemove)) {
                sendUserMessage(toRemove.getId(), toRemove.getUsername() + "Has Been Kicked Due To Insufficient Funds");
            }
            _userSessionMap.remove(_sessionUserMap.get(session));
            _gamesMap.get(toRemove.getGame().getId()).removePlayer(toRemove);
            _sessionUserMap.remove(session);
            if (_gameSessionMap.get(toRemove.getGame().getId()).size() <= 1) {
                p.getGame().setActive(false);
                _gameRepo.save(p.getGame());
                _gameSessionMap.remove(toRemove.getGame().getId());
                _gamesMap.remove(toRemove.getGame().getId());
            } else {
                _gameSessionMap.get(toRemove.getGame().getId()).removeIf(x -> x.equals(session));
            }
            toRemove.setCurrent_game_money(toRemove.getMoney() + toRemove.getCurrent_game_money());
            _userRepo.save(toRemove);
        }
    }

    /*
     * TODO: broad cast a json of: current game model,  user model who played, list of all players in game. after a
     *  player makes a successful play
     * TODO: enforce/create a formated message system to receive player's plays
     */

    /**
     * receives player's moves for the game and sends out current game model,  user model who played,\
     * list of all players in game. after a player makes a successful play
     *
     * @param session user's session
     * @param message user's playing action
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        _logger.info("Entered into Message: " + _sessionUserMap.get(session) + ". Got Message: " + message);
        User u = getUser(_sessionUserMap.get(session));
        Poker p = _gamesMap.get(u.getGame().getId());
        if (message.equalsIgnoreCase("initGame")) {
            synchronized (_gamesMap.get(u.getGame().getId())) {
                p.initGame();
            }
            sendGameMessage(u);
        }
        if (p.getInitialized()) {
            if (message.substring(0, 3).equalsIgnoreCase("Bet")) {
                String tmp = message.split(" ")[1];
                int bet = Integer.parseInt(tmp); //read only int
                synchronized (_gamesMap.get(u.getGame().getId())) {
                    p.bet(u, bet);
                }
                sendGameMessage(u);
            }
        }
    }


    /**
     * reports errors
     *
     * @param session   user's session
     * @param throwable reported exception
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        _logger.info("Entered into Error. " + throwable.getMessage());

        try {
            User u = getUser(_sessionUserMap.get(session));
            Poker p = _gamesMap.get(u.getGame().getId());
            _errorStrings += _sessionUserMap.get(session) + ": " + throwable.getMessage() + ": \n";
            _errorStrings += p.toString() + "\n";
            onClose(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGameMessage(User u) {
        synchronized (_gamesMap.get(u.getGame().getId())) {
            Poker p = _gamesMap.get(u.getGame().getId());
            String message = p.sendGameState();
            _gameSessionMap.get(u.getGame().getId()).forEach(x -> {
                synchronized (x) {
                    try {
                        x.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void sendGameMessage(User u, String message) {
        synchronized (_gamesMap.get(u.getGame().getId())) {
            _gameSessionMap.get(u.getGame().getId()).forEach(x -> {
                synchronized (x) {
                    try {
                        if (!_sessionUserMap.get(x).equals(u.getId())) {
                            x.getBasicRemote().sendText(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void sendUserMessage(String userId, String message) {
        try {
            _userSessionMap.get(userId).getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private User getUser(String userId) {
        Future<User> u = null;
        try { //waiting for responce
            u = _userRepo.AsyncGetById(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return u.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Game getGame(String gameId) {
        Future<Game> g = null;
        try { //waiting for responce
            g = _gameRepo.AsyncGetById(gameId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return g.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendErrorString(Session session, User u, String message) {
        if (u == null) {
            try {
                session.getBasicRemote().sendText("User could not be found in repo.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new NullPointerException();
        }
        try {
            Gson gson = new Gson();
            session.getBasicRemote().sendText(message + ": \n" + _gamesMap.get(u.getGame().getId()).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            //onClose(session);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
