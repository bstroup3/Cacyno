package com.example.demo1;

import Models.Lobby;
import Models.LobbyOperations;
import Models.User;
import Utilities.GameChecker;
import Utilities.GameCreation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.ILobby;
import interfaces.IUser;

import java.util.Objects;
import java.util.UUID;

/**
 * The activity tied to the game creation screen
 */
public class CreateGame extends AppCompatActivity {
    private TextView lobbyname;
    private TextView moneyAmount;
    private User user;
    private IUser callback;
    private GameCreation gameCreation;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game);

        //buttons
        lobbyname = findViewById(R.id.create_lobbyname);
        moneyAmount = findViewById(R.id.game_money);
        ImageButton back = findViewById(R.id.back_create_game);
        ImageButton create = findViewById(R.id.createNewGame);

        user = new User();
        gameCreation = new GameCreation(new GameChecker());
        callback = new IUser() {
            /**
             * if user is successfully transferred from endpoint to a user object
             * @return int 0 for success
             */
            @Override
            public int onSuccess() {
                System.out.println("Done updating");
                return 0;
            }

            /**
             * if the current user is not successfully transferred from endpoint to a user object
             * @return int -1 for error
             */
            @Override
            public int onError() {
                return -1;
            }
        };

        user.getUser(this, callback, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), false);

        create.setOnClickListener(v -> {
                //inputs
                String lobbyInput = lobbyname.getText().toString();
                String money = moneyAmount.getText().toString();
                //check if inputs are in correct format and make lobby
                if(gameCreation.createGame(lobbyInput, money, lobbyname, moneyAmount, CreateGame.this, user)) {
                    Lobby newLobby = new Lobby();
                    LobbyOperations ops = new LobbyOperations();

                    newLobby.setLobbyname(lobbyInput);
                    newLobby.setActive(true);
                    newLobby.setId(UUID.randomUUID().toString());

                    newLobby.newLobby(CreateGame.this, new ILobby() {
                        @Override
                        public int onSuccess() {
                            user.setMoney(user.getMoney() - Integer.parseInt(money));
                            user.setCurrent_game_money(Integer.parseInt(money));
                            user.setGameId(ops.lobbyToJSON(newLobby));

                            user.updateUser(CreateGame.this,  new IUser() {
                                /**
                                 * if user is successfully transferred from endpoint to a user object
                                 * @return int 0 for success
                                 */
                                @Override
                                public int onSuccess() {
                                    Intent intent = new Intent(CreateGame.this, GameScreen.class);
                                    startActivity(intent);
                                    return 0;
                                }

                                /**
                                 * if the current user is not successfully transferred from endpoint to a user object
                                 * @return int -1 for error
                                 */
                                @Override
                                public int onError() {
                                    return -1;
                                }
                            }, true);
                            System.out.println("Done updating");
                            return 0;
                        }

                        @Override
                        public int onError() {
                            return 0;
                        }
                    });
                }
                else{
                    Toast.makeText(CreateGame.this, "NO", Toast.LENGTH_SHORT).show();
                }
        });
        back.setOnClickListener(v -> {
            /**
             * when the back button is clicked, this method simply takes you back to the list of lobbies available
             * @param view the current view of this device
             */
            Intent intent = new Intent(CreateGame.this, LobbySelector.class);
            startActivity(intent);
        });

    }
}
