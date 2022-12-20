package com.example.demo1;

import Models.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.IUser;

import java.util.Objects;


/**
 * The activity tied to the screen used for changing username
 */
public class Username extends AppCompatActivity {
    private TextView username;
    private ImageButton apply;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_username_screen);
        user = new User();
        /**
         * getting user to update username
         */
        user.getUser(this, new IUser() {
            @Override
            public int onSuccess() {
                return 0;
            }

            @Override
            public int onError() {
                return 0;
            }
        }, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), false);


        username = (TextView) findViewById(R.id.username);
        apply = (ImageButton) findViewById(R.id.apply_but);

        apply.setOnClickListener(view -> {
            String input = username.getText().toString();
            if(input.isEmpty()){
                username.setError("Please provide username");
                username.requestFocus();
            }
            else if(input.equals("username")){
                username.setError("Please provide username");
                username.requestFocus();
            }
            else if(input.length() > 9){
                username.setError("please make shorter than 10 characters");
                username.requestFocus();
            }
            else{
                user.setUsername(input);
                user.updateUser(Username.this, new IUser() {
                    @Override
                    public int onSuccess() {
                        Intent I = new Intent(Username.this, UserHome.class);
                        startActivity(I);
                        return 0;
                    }

                    @Override
                    public int onError() {
                        return -1;
                    }
                }, false);
            }
        });
    }
}
