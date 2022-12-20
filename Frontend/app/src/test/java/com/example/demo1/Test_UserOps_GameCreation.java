package com.example.demo1;

import Models.User;
import Models.UserOperations;
import Utilities.GameChecker;
import Utilities.GameCreation;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class Test_UserOps_GameCreation {

    @Mock
    JSONObject obj = mock(JSONObject.class);

    @Mock
    TextView lobby = mock(EditText.class);

    @Mock
    TextView money = mock(EditText.class);

    @Mock
    Context context = mock(Context.class);

    @Test
    public void gettingUserfromJsonObject() throws JSONException {
        User user = new User();
        UserOperations ops = new UserOperations();
        when(obj.getString("username")).thenReturn("manny");
        when(obj.getString("money")).thenReturn("1000");
        when(obj.getString("id")).thenReturn("123");
        when(obj.getString("displayname")).thenReturn("false");
        when(obj.getString("current_game_money")).thenReturn("0");
        when(obj.getString("card1")).thenReturn("0");
        when(obj.getString("card2")).thenReturn("0");
        when(obj.getString("folded")).thenReturn("false");
        when(obj.getString("bet")).thenReturn("0");
        when(obj.getString("position")).thenReturn("0");
        when(obj.getString("hasPlayed")).thenReturn("false");
        ops.JSONtoUser(obj, user, false);
        assertFalse(user.getDisplayName());
        assertEquals("manny", user.getUsername());
        assertEquals(1000, user.getMoney());
        assertEquals("123", user.getId());
        assertEquals(0, user.getCurrent_game_money());

    }
    @Test
    public void usertoJsonObject() throws JSONException {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("manny");
        when(user.getMoney()).thenReturn(1000);
        when(user.getId()).thenReturn("123");
        when(user.getDisplayName()).thenReturn(false);
        when(user.getCurrent_game_money()).thenReturn(0);
        JSONObject tester = new JSONObject();

        tester.put("id", user.getId());
        tester.put("username", user.getUsername());
        tester.put("money", user.getMoney());
        tester.put("displayname", user.getDisplayName());
        tester.put("current_game_money", user.getCurrent_game_money());
        UserOperations ops = new UserOperations();

        assertEquals(tester.getString("username"), ops.usertoJSON(user, false).getString("username"));
        assertEquals(tester.get("money"), ops.usertoJSON(user, false).get("money"));
        assertEquals(tester.getString("id"), ops.usertoJSON(user, false).getString("id"));
        assertEquals(tester.get("displayname"), ops.usertoJSON(user, false).get("displayname"));
        assertEquals(tester.get("current_game_money"), ops.usertoJSON(user, false).get("current_game_money"));
    }
    @Test
    public void gameCreateEmpty(){
        GameChecker gameChecker = mock(GameChecker.class);
        User user = mock(User.class);

        when(user.getMoney()).thenReturn(0);
        when(user.getUsername()).thenReturn("manny");

        when(gameChecker.checkLobbyName("", lobby)).thenReturn(false);
        when(gameChecker.checkMoneyAmount("", money, user)).thenReturn(false);

        GameCreation game = new GameCreation(gameChecker);

        assertFalse(game.createGame("", "", lobby, money, context, user));
    }

    @Test
    public void gameCreateTooLong(){
        GameChecker gameChecker = mock(GameChecker.class);
        User user = mock(User.class);

        when(user.getMoney()).thenReturn(100);
        when(user.getUsername()).thenReturn("manny");

        when(gameChecker.checkLobbyName("lobbynameistoolong", lobby)).thenReturn(false);
        when(gameChecker.checkMoneyAmount("100", money, user)).thenReturn(true);

        GameCreation game = new GameCreation(gameChecker);

        assertFalse(game.createGame("lobbynameistoolong", "100", lobby, money, context, user));
    }

    @Test
    public void gameCreateMoneyCheck(){
        GameChecker gameChecker = mock(GameChecker.class);
        User user = mock(User.class);

        when(user.getMoney()).thenReturn(100);
        when(user.getUsername()).thenReturn("manny");

        when(gameChecker.checkLobbyName("new lobby", lobby)).thenReturn(true);
        when(gameChecker.checkMoneyAmount("1d00", money, user)).thenReturn(false);

        GameCreation game = new GameCreation(gameChecker);

        assertFalse(game.createGame("new lobby", "1d00", lobby, money, context, user));
    }


    @Test
    public void gameCreateCorrect(){
        GameChecker gameChecker = mock(GameChecker.class);
        User user = mock(User.class);

        when(user.getMoney()).thenReturn(200);
        when(user.getUsername()).thenReturn("manny");

        when(gameChecker.checkLobbyName("new lobby", lobby)).thenReturn(true);
        when(gameChecker.checkMoneyAmount("100", money, user)).thenReturn(true);

        GameCreation game = new GameCreation(gameChecker);

        assertTrue(game.createGame("new lobby", "100", lobby, money, context, user));
    }

    @Test
    public void testingMoneyInputCases(){
        GameChecker gameChecker = new GameChecker();

        User user = mock(User.class);

        when(user.getMoney()).thenReturn(200);
        when(user.getUsername()).thenReturn("manny");
        /**
         testing not enough money but lobby name is correct
         */
        assertFalse(gameChecker.checkMoneyAmount("300", money, user));
        /**
         * testing letters in money
         */
        assertFalse(gameChecker.checkMoneyAmount("1d00", money, user));
        /**
         * testing money starting with 0
         */
        assertFalse(gameChecker.checkMoneyAmount("0100", money, user));
        /**
         * testing right amount
         */
        assertTrue(gameChecker.checkMoneyAmount("200", money, user));
        assertTrue(gameChecker.checkMoneyAmount("150", money, user));
    }
    @Test
    public void testingLobbyNameInputCases(){
        GameChecker gameChecker = new GameChecker();
        /**
         * testing empty string
         */
        assertFalse(gameChecker.checkLobbyName("", lobby));
        /**
         * testing longer than 12
         */
        assertFalse(gameChecker.checkLobbyName("thislobbynameislonger", lobby));
        /**
         * testing correct format
         */
        assertTrue(gameChecker.checkLobbyName("manny lobby", lobby));
    }





}