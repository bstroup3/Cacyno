package com.example.demo1;

import Models.User;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.IUser;
/**
 * The activity tied to the home screen
 */
public class UserHome extends AppCompatActivity {
    private ImageButton settings;
    private ImageButton play;
    private ImageButton leaderboard;
    private TextView money;
    private TextView username;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        settings = findViewById(R.id.settings);
        money = findViewById(R.id.moneyRequest);
        username = findViewById(R.id.currentUser);
        play = findViewById(R.id.play_create_but);
        leaderboard = findViewById(R.id.leaderboard_button);


        user = new User();
        user.getUser(this, new IUser() {
            @Override
            public int onSuccess() {
                Log.e("SUCCESS GETTING USER ", user.getUsername());
                money.append(user.getMoney()+"");
                username.append(user.getUsername());
                return 0;
            }
            @Override
            public int onError(){
                return -1;
            }
        }, FirebaseAuth.getInstance().getCurrentUser().getUid(), false);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(UserHome.this, Settings.class);
            intent.putExtra("displayname", user.getDisplayName());
            intent.putExtra("username", user.getUsername());
            startActivity(intent);
        });
        play.setOnClickListener(view -> {
            Intent I = new Intent(UserHome.this, LobbySelector.class);
            I.putExtra("username", user.getUsername());
            startActivity(I);
        });
        leaderboard.setOnClickListener(view -> {
            startActivity(new Intent(UserHome.this, Leaderboard.class));
        });
    }
}
