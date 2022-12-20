package com.example.demo1;

import Models.Lobby;
import Models.LobbyOperations;
import Models.User;
import Utilities.GameChecker;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.ILobby;
import interfaces.IUser;

import java.util.ArrayList;

/**
 * The activity tied to the lobby selection screen
 */
public class LobbySelector extends AppCompatActivity{
    private ScrollView scroll;
    private LinearLayout layout;
    private ConstraintLayout constraintLayout;
    ArrayList<Lobby> list;
    private ImageButton refresh;
    private ImageButton back;
    private ImageButton creategame;
    private int nextId;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_selector_screen);

        //getting buttons and layouts
        constraintLayout = findViewById(R.id.lobbyView);
        back = findViewById(R.id.back);
        refresh = constraintLayout.findViewById(R.id.refresh);
        creategame = findViewById(R.id.create);

        //instantiations for lobbies and user
        Lobby lb = new Lobby();
        list = new ArrayList<>();
        LobbyOperations opsLob = new LobbyOperations();
        user = new User();

        //getting user to set gameID when enters
        user.getUser(this, new IUser() {
            @Override
            public int onSuccess() {
                Log.e("SUCCESS GETTING USER ", user.getUsername());
                return 0;
            }
            @Override
            public int onError(){
                return -1;
            }
        }, FirebaseAuth.getInstance().getCurrentUser().getUid(), false);


        //wait for lobbies to be appended to list to display on screen
        lb.calltoServer(this, list, new ILobby(){
            /**
             * if lobbies are successfully added to a list of lobby objects, then this method will add a
             * view for each lobby on screen with unique id
             * to be able to join or spectate a game.
             * @return int 0 for success
             */
            @Override
            public int onSuccess(){

                //setting ids for names of lobbies, join buttons and spectate buttons
                int indexForNameId = 1;
                int indexForJoinId = 1000;
                int indexForSpectateId = 10000;

                //adding lobbies to screen
                for(Lobby i : list) {
                    setLobbyView(indexForNameId, indexForJoinId, indexForSpectateId, opsLob, i);
                }
                return 0;
            }
            /**
             * if lobbies are not successfully added to a list of lobby objects
             * @return int -1 for not successful
             */
            @Override
            public int onError(){
                return -1;
            }
        });

        layout = findViewById(R.id.linear);
        scroll = findViewById(R.id.scroll);

        refresh.setOnClickListener(new View.OnClickListener() {
            /**
             * if the refresh button is clicked then all lobbies on screen are removed and a call to endpoint
             * to get all lobbies is made
             * @param view current view of the device
             */
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
                list.removeAll(list);
                lb.calltoServer(view.getContext(), list, new ILobby(){
                    /**
                     * if lobbies are successfully added to a list of lobby objects, then this method will add a
                     * view for each lobby on screen with unique id
                     * to be able to join or spectate a game.
                     * @return int 0 for success
                     */
                    @Override
                    public int onSuccess(){
                        //setting ids for names of lobbies, join buttons and spectate buttons
                        int indexForNameId = 1;
                        int indexForJoinId = 1000;
                        int indexForSpectateId = 10000;

                        //adding lobbies to screen
                        for(Lobby i : list) {
                            if(i.getActive())
                                setLobbyView(indexForNameId, indexForJoinId, indexForSpectateId, opsLob, i);
                        }
                        return 0;
                    }
                    /**
                     * if lobbies are not successfully added to a list of lobby objects
                     * @return int -1 for not successful
                     */
                    @Override
                    public int onError(){
                        return -1;
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            /**
             * when the back button is clicked, this method will take you to the user home screen.
             * @param view current view of this device
             */
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LobbySelector.this, UserHome.class);
                startActivity(i);

            }
        });
        creategame.setOnClickListener(new View.OnClickListener() {
            /**
             * when the "create a game" button is clicked, this method will take you to a screen where you will
             * be able to create a new game.
             * @param view current view of this device
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LobbySelector.this, CreateGame.class);
                startActivity(intent);
            }
        });

    }

    public void setLobbyView(int indexForNameId, int indexForJoinId, int indexForSpectateId, LobbyOperations opsLob, Lobby i){
        View newLobbbyRow = getLayoutInflater().inflate(R.layout.lobby_row, layout);
        TextView lobbyName = findViewById(R.id.lobbyname);
        lobbyName.setId(indexForNameId);
        lobbyName.setText(i.getLobbyname());
        ImageButton joinBut = findViewById(R.id.join);
        joinBut.setId(indexForJoinId++);
        int setIndex = indexForNameId;

        //setting join button listeners
        joinBut.setOnClickListener(v -> {

            View join = getLayoutInflater().inflate(R.layout.enter_game_money, constraintLayout);
            join.bringToFront();
            TextView amount = findViewById(R.id.join_money);
            ImageButton joinGame = findViewById(R.id.join_in_game);
            ImageButton back = findViewById(R.id.back_to_lobby_selector);

            GameChecker ops = new GameChecker();
            joinGame.setOnClickListener(v1 -> {
                if(ops.checkMoneyAmount(amount.getText().toString(), amount, user)){
                    user.setGameId(opsLob.lobbyToJSON(i));
                    user.setMoney(user.getMoney() - Integer.parseInt(amount.getText().toString()));
                    user.setCurrent_game_money(Integer.parseInt(amount.getText().toString()));
                    user.updateUser(LobbySelector.this, new IUser() {
                        @Override
                        public int onSuccess() {
                            startActivity(new Intent(LobbySelector.this, GameScreen.class));
                            return 0;
                        }

                        @Override
                        public int onError() {
                            return -1;
                        }
                    }, true);
                }
            });

            back.setOnClickListener(v12 -> startActivity(new Intent(LobbySelector.this, LobbySelector.class)));
        });

        ImageButton spectate = findViewById(R.id.spectate);
        spectate.setId(indexForSpectateId++);

        spectate.setOnClickListener(v -> {
            user.setGameId(opsLob.lobbyToJSON(i));
            user.set_spectator(true);
            user.updateUser(LobbySelector.this, new IUser() {
                @Override
                public int onSuccess() {
                    return 0;
                }

                @Override
                public int onError() {
                    return -1;
                }
            }, true);
            startActivity(new Intent(LobbySelector.this, GameScreen.class));
        });
        indexForNameId++;
        nextId = indexForNameId;
    }




}
