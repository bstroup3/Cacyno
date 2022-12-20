package com.example.demo1;

import Models.User;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import interfaces.IUser;

import java.util.ArrayList;
import java.util.Comparator;

public class Leaderboard extends AppCompatActivity {
    ArrayList<User> users;
    private LinearLayout linearLayout;
    private ImageButton back;
    private ImageView easterEgg;
    private boolean descending;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_view);
        linearLayout = findViewById(R.id.leaderboard_linear);
        back = findViewById(R.id.lb_back);
        easterEgg = findViewById(R.id.lb_background);

        users = new ArrayList<>();
        User user = new User();
        user.calltoServer(this, users, new IUser() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public int onSuccess() {
                //sorting by money
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    users.sort((o1, o2) -> o2.getMoney() - o1.getMoney());
                    descending = true;
                }
                //adding users to view
                for(User i : users) {
                    setRow(i);
                }
                return 0;
            }

            @Override
            public int onError() {
                return 0;
            }
        });

        back.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, UserHome.class)));

        easterEgg.setOnClickListener(v -> {
            linearLayout.removeAllViews();
            if(descending)
                users.sort((Comparator.comparingInt(User::getMoney)));

            else
                users.sort((o1, o2) -> o2.getMoney() - o1.getMoney());
            descending = !descending;
            for(User i : users)
                setRow(i);
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setRow(User user){
        View row = getLayoutInflater().inflate(R.layout.leaderboard_row, linearLayout);
        row.setId(View.generateViewId());

        TextView name = row.findViewById(R.id.lb_username);
        name.setId(View.generateViewId());

        TextView money = row.findViewById(R.id.lb_score);
        money.setId(View.generateViewId());

        name.setText(user.getUsername());
        money.setText(String.valueOf(user.getMoney()));
    }
}
