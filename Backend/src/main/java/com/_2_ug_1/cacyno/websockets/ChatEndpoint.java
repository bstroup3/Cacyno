package com._2_ug_1.cacyno.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for communication in games
 */
@ServerEndpoint("/chat/{gameId}/{userName}")
@Component
public class ChatEndpoint {

    private static Map<String, Map<String, Session>> _gameSessionMap = new HashMap<>();

    private final Logger _logger = LoggerFactory.getLogger(ChatEndpoint.class);

    /**
     * Creates a link between players in game
     *
     * @param session  user's session
     * @param gameId   chat channel
     * @param userName username to identity user
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session
            , @PathParam("gameId") String gameId
            , @PathParam("userName") String userName) throws IOException {
        _logger.info("Entered into Open");
        if (_gameSessionMap.containsKey(gameId)) {
            _gameSessionMap.get(gameId).put(userName, session);
        } else {
            Map<String, Session> userSessionMap = new HashMap<>();
            userSessionMap.put(userName, session);
            _gameSessionMap.put(gameId, userSessionMap);
        }
        String message = "User: " + userName + " has joined.";
        _gameSessionMap.get(gameId).forEach((id, s) -> {
            synchronized (s) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * server response to receiving a text message
     *
     * @param session  user's session
     * @param gameId   chat channel
     * @param userName username to identity user
     * @param message  text message to broadcast to all players with the same game id
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session
            , @PathParam("gameId") String gameId
            , @PathParam("userName") String userName
            , String message) throws IOException {
        _logger.info("Entered into Message: Got Message: " + message);
        if (message.startsWith("@")) {
            _gameSessionMap.get(gameId).get(userName).getBasicRemote().sendText(message);
            String targetUser = message.split(" ").toString(); //todo: no spaces in usernames
            _gameSessionMap.get(gameId).get(targetUser.substring(1)).getBasicRemote().sendText(
                    "@" + userName + " " + message);
        } else {
            _gameSessionMap.get(gameId).forEach((id, sess) -> {
                synchronized (sess) {
                    try {
                        sess.getBasicRemote().sendText(userName + ": " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * closes the communication session
     *
     * @param session  user's session
     * @param gameId   chat channel
     * @param userName username to identity user
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session
            , @PathParam("gameId") String gameId
            , @PathParam("userName") String userName) throws IOException {
        _logger.info("Entered into Close");
        if (_gameSessionMap.get(gameId).size() <= 1) {
            _gameSessionMap.remove(gameId);
        } else {
            _gameSessionMap.get(gameId).remove(userName);
        }
        String message = "User: " + userName + " has disconnected.";
        _gameSessionMap.get(gameId).forEach((id, s) -> {
            synchronized (s) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * reports errors
     *
     * @param session   user's session
     * @param gameId    chat channel
     * @param userName  username to identity user
     * @param throwable reported exception
     */
    @OnError
    public void onError(Session session
            , @PathParam("gameId") String gameId
            , @PathParam("userId") String userName
            , Throwable throwable) {
        _logger.info("Entered into Error");
    }
}
