package Utilities;

import Models.User;
import android.content.Context;
import android.widget.TextView;

/**
 * This class is to create a game
 */
public class GameCreation {
    GameChecker  gameChecker;

    /**
     * Constructor for a game to add to db
     * @param checker the checker needed to make sure game is in correct format
     */
    public GameCreation(GameChecker checker){
        gameChecker = checker;
    }

    /**
     * this method will create a game but make sure the parameters are correct
     * @param lobbyname the name of the lobby
     * @param moneyAmount the amount
     * @param name the textview for sending error if lobby name entry is wrong
     * @param money the textview for sending error if money entry is wrong
     * @param con the context of application
     * @param user the user that wants to create game
     * @return true if can create game or false if parameters wrong
     */
    public boolean createGame(String lobbyname, String moneyAmount, TextView name, TextView money, Context con, User user){
        return gameChecker.checkLobbyName(lobbyname, name) && gameChecker.checkMoneyAmount(moneyAmount, money, user);
    }
}
