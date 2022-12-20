package Models;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.example.demo1.R;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.ITextViews;
import lombok.SneakyThrows;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Mainly this class is to help keep game screen organized and connect to the
 * poker websockets.
 */
public class GameInstance{
    WebSocketClient mWebSocketClient;
    private ArrayList<User> users;
    private ITextViews views;
    private int currentPlayerIndex;
    private ArrayList<Integer> indiciesOfFolded;

    private final int INDEX_OF_LIST_PLAYERS = 0;
    private final int INDEX_OF_GAME_STATE = 1;
    private final int INDEX_OF_CURRENT_PLAYER = 2;
    private final int INDEX_OF_WINNER = 3;
    private final int INDEX_OF_WINNER_CARDS = 4;

    private final int MAX_PLAYERS = 6;

    public boolean callOnce = true;
    private int currentGameMoney = 0;

    /**
     * this constructor makes a game instance object for each player.
     * @param user the user that is using the device
     * @param views callback for updating views on the screen
     * @throws URISyntaxException
     * @throws JSONException
     */
    public GameInstance(User user, ITextViews views, boolean testing) throws URISyntaxException, JSONException {
        if(!testing)
            connectWebSocket(user);
        users = new ArrayList<>();
        user.setFolded(false);
        users.add(user);
        user.setIndexOnScreen(0);

        this.views = views;
        indiciesOfFolded = new ArrayList<>();
    }

    public ArrayList<User> getuserList(){
        return users;
    }

    public ArrayList<Integer> getFoldedList(){
            return indiciesOfFolded;
    }

    private void connectWebSocket(User user) throws URISyntaxException, JSONException {
        URI uri;
        uri = new URI("ws://coms-309-046.cs.iastate.edu:8080/poker/" + FirebaseAuth.getInstance().getUid());

        mWebSocketClient = new WebSocketClient(Objects.requireNonNull(uri)) {


            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("connected to " + uri.toString());
            }

            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onMessage(String msg) {
                System.out.println("MESSSSAGEEEEEEEEEEEEEEEE\n"+msg+"\nEND MESSSSSSSSSSSAGE");
                if (msg.startsWith("[{")){
                    String getPlayersOnly = msg.split("\\*\\*")[INDEX_OF_LIST_PLAYERS];
                    String gameStatus = msg.split("\\*\\*")[INDEX_OF_GAME_STATE];
                    JSONArray usersJSON = new JSONArray(getPlayersOnly);
                    JSONObject gameJSON = new JSONObject(gameStatus);
                    /**
                     * format that will be sent during game
                     *              <listOfPlayers>**<gameState>**<userID>**null**null
                     *
                     * format that will be sent when game is over
                     *              <listOfPlayers>**<gameState>**<userID>**<GameWinner>**<ListOfWinnersToShow>
                     */
                    ArrayList<User> list = user.JSONtolist(usersJSON);

                    //finding new users to add to screen and updating current users on screen
                    for (User i : list) {
                        switch(findIndexOfUserID(i.id)){
                            case 0:
                                if(gameJSON.getInt("highest_bet") != i.getBet()) {
                                    views.setCallButton();
                                } else {
                                    views.setCheckButton();
                                }
                                views.MyCard1(i.getCard1());
                                views.MyCard2(i.getCard2());
                                views.MyMoney(i.getCurrent_game_money());
                                currentGameMoney = i.getCurrent_game_money();
                                views.setSliderTo(0);
                                views.setBet(i.bet);
                                break;
                            case 1:
                                views.Player1Username(i.getUsername(), false);
                                views.Player1Money("$" + i.getCurrent_game_money() + "");
                                views.Player1Bet(i.bet);
                                break;
                            case 2:
                                views.Player2Username(i.getUsername(), false);
                                views.Player2Money("$" + i.getCurrent_game_money() + "");
                                views.Player2Bet(i.bet);
                                break;
                            case 3:
                                views.Player3Username(i.getUsername(), false);
                                views.Player3Money("$" + i.getCurrent_game_money() + "");
                                views.Player3Bet(i.bet);
                                break;
                            case 4:
                                views.Player4Username(i.getUsername(), false);
                                views.Player4Money("$" + i.getCurrent_game_money() + "");
                                views.Player4Bet(i.bet);
                                break;
                            case 5:
                                views.Player5Username(i.getUsername(), false);
                                views.Player5Money("$" + i.getCurrent_game_money() + "");
                                views.Player5Bet(i.bet);
                                break;
                            default:
                                break;
                        }
                        if (!user.getId().equals(i.getId()) && !checkObjects(i) && users.size() != MAX_PLAYERS) {
                            users.add(i);
                            System.out.println("---------------------");
                            System.out.println("PLAYER: " + i.getUsername());
                            System.out.println(i.getCurrent_game_money());
                            System.out.println("---------------------");
                            i.setIndexOnScreen(users.size()-1);
                            if (users.size() == 2 && list.get(0).getId().equals(user.getId()) && callOnce) {
                                mWebSocketClient.send("initGame");
                                callOnce = false;
                            }

                            currentPlayerIndex++;
                            new Handler(Looper.getMainLooper()).post(() -> toView(i, false));
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            views.TableCard1(gameJSON.getInt("public_card1"));
                            views.TableCard2(gameJSON.getInt("public_card2"));
                            views.TableCard3(gameJSON.getInt("public_card3"));
                            views.TableCard4(gameJSON.getInt("public_card4"));
                            views.TableCard5(gameJSON.getInt("public_card5"));
                            views.pot(gameJSON.getInt("pot"));
                            views.setHighestBet(gameJSON.getInt("highest_bet"));

                            //checks if there is a winner and will display
                            if(!msg.split("\\*\\*")[INDEX_OF_WINNER].equals("null")){
                                String finalWinner = msg.split("\\*\\*")[INDEX_OF_WINNER];
                                JSONObject players_to_show_cards = new JSONObject(msg.split("\\*\\*")[INDEX_OF_WINNER_CARDS]);
                                /*
                                JSONArray players_to_show_cards = new JSONArray(msg.split("\\*\\*")[INDEX_OF_WINNER_CARDS]);
                                ArrayList<User> winnersHands = user.JSONtolist(players_to_show_cards);

                                */
                                //views.setWinner(finalWinner);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    });
                    //set all players who have visible dot to white
                    new Handler(Looper.getMainLooper()).post(() -> views.setWhite());

                    //find players who folded and set their dot to grey
                    for(int i = 0; i < users.size(); i++){
                        if(users.get(i).getFolded())
                            indiciesOfFolded.add(users.get(i).getIndexOnScreen());
                    }
                       // new Handler(Looper.getMainLooper()).post(() -> views.setFolded(indiciesOfFolded));

                    //set player green
                    String userID = msg.split("\\*\\*")[INDEX_OF_CURRENT_PLAYER];
                    if(!userID.equals("null"))
                        new Handler(Looper.getMainLooper()).post(() -> views.setGreen(findIndexOfUserID(userID)));

                }
                //removes user who left from screen
                else if(msg.contains("Has Left")){
                    String playerUsername = msg.split(":")[1];

                    for(int i = 0; i < users.size(); i++){
                        if(users.get(i).getUsername().equals(playerUsername)){
                            removePlayer(users.get(i).getIndexOnScreen(), false);
                            users.remove(i);
                            currentPlayerIndex = users.get(i).getIndexOnScreen();

                        }
                    }

                }
            }

            @Override
            public void onClose ( int errorCode, String reason,boolean remote){
                System.out.println("Connection closed " + uri.toString());
                System.out.println("Connection closed " + remote + reason);
                if (remote)
                    views.ToastComments(reason);
            }

            @Override
            public void onError (Exception e){
                Log.i("Websocket", "Error " + e.getMessage());
            }

        };
        mWebSocketClient.connect();
    }

    /**
     * calling callback method in game screen to set textviews for other
     * players
     * @param user the user data
     */
    public void toView(User user, boolean test){
        if(!test) {
            new Handler(Looper.getMainLooper()).post(() -> {
                switch (currentPlayerIndex) {
                    case 1:
                        views.Player1Username(user.getUsername(), false);
                        views.Player1Money("$" + user.getCurrent_game_money() + "");
                        views.Player1Bet(user.bet);
                        break;
                    case 2:
                        views.Player2Username(user.getUsername(), false);
                        views.Player2Money("$" + user.getCurrent_game_money() + "");
                        views.Player2Bet(user.bet);
                        break;
                    case 3:
                        views.Player3Username(user.getUsername(), false);
                        views.Player3Money("$" + user.getCurrent_game_money() + "");
                        views.Player3Bet(user.bet);
                        break;
                    case 4:
                        views.Player4Username(user.getUsername(), false);
                        views.Player4Money("$" + user.getCurrent_game_money() + "");
                        views.Player4Bet(user.bet);
                        break;
                    case 5:
                        views.Player5Username(user.getUsername(), false);
                        views.Player5Money("$" + user.getCurrent_game_money() + "");
                        views.Player5Bet(user.bet);
                        break;
                    default:
                        break;
                }
            });
        }
        else{
            switch (currentPlayerIndex) {
                case 1:
                    views.Player1Username(user.getUsername(), false);
                    views.Player1Money("$" + user.getCurrent_game_money() + "");
                    views.Player1Bet(user.bet);
                    break;
                case 2:
                    views.Player2Username(user.getUsername(), false);
                    views.Player2Money("$" + user.getCurrent_game_money() + "");
                    views.Player2Bet(user.bet);
                    break;
                case 3:
                    views.Player3Username(user.getUsername(), false);
                    views.Player3Money("$" + user.getCurrent_game_money() + "");
                    views.Player3Bet(user.bet);
                    break;
                case 4:
                    views.Player4Username(user.getUsername(), false);
                    views.Player4Money("$" + user.getCurrent_game_money() + "");
                    views.Player4Bet(user.bet);
                    break;
                case 5:
                    views.Player5Username(user.getUsername(), false);
                    views.Player5Money("$" + user.getCurrent_game_money() + "");
                    views.Player5Bet(user.bet);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * removes the player from view and their corresponding dot
     * @param index the index of the player
     */
    public void removePlayer(int index, boolean test){
        if(!test) {
            new Handler(Looper.getMainLooper()).post(() -> {
                switch (index) {
                    case 1:
                        views.Player1Username("waiting...", true);
                        views.Player1Money("");
                        views.Player1Bet(0);
                        break;
                    case 2:
                        views.Player2Username("waiting...", true);
                        views.Player2Money("");
                        views.Player2Bet(0);
                        break;
                    case 3:
                        views.Player3Username("waiting..", true);
                        views.Player3Money("");
                        views.Player3Bet(0);
                        break;
                    case 4:
                        views.Player4Username("waiting...", true);
                        views.Player4Money("");
                        views.Player4Bet(0);
                        break;
                    case 5:
                        views.Player5Username("waiting...", true);
                        views.Player5Money("");
                        views.Player5Bet(0);
                        break;
                    default:
                        break;
                }
            });
        }
        else {
            switch (index) {
                case 1:
                    views.Player1Username("waiting...", true);
                    views.Player1Money("");
                    views.Player1Bet(0);
                    break;
                case 2:
                    views.Player2Username("waiting...", true);
                    views.Player2Money("");
                    views.Player2Bet(0);
                    break;
                case 3:
                    views.Player3Username("waiting..", true);
                    views.Player3Money("");
                    views.Player3Bet(0);
                    break;
                case 4:
                    views.Player4Username("waiting...", true);
                    views.Player4Money("");
                    views.Player4Bet(0);
                    break;
                case 5:
                    views.Player5Username("waiting...", true);
                    views.Player5Money("");
                    views.Player5Bet(0);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * helper method to check if the user is already in the arraylist
     * couldn't use .contains due to it being a shallow check
     * @param user the user to check if in the arraylist
     * @return true if user is in the arraylist already, false is not
     */
    private boolean checkObjects(User user){
        for (User i : users){
            if(user.getId().equals(i.getId()))
                return true;
        }
        return false;
    }

    /**
     * finds the matching userID in the list of current players
     * @param userID the ID to match with
     * @return the index of the User On screen
     */
    private int findIndexOfUserID(String userID){
        for(User i : users){
            if(i.getId().equals(userID)) {
                System.out.println(i.getUsername());
                return i.getIndexOnScreen();
            }
        }
        return -1;
    }

    public void send(String message) {
        mWebSocketClient.send(message);
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex){
        this.currentPlayerIndex = currentPlayerIndex;
    }

    /**
     * called from game screen class when back button is clicked
     */
    public int closeWebsocket(){
        mWebSocketClient.close();
        return currentGameMoney;
    }

}
