package com.example.demo1;

import Models.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.IUser;
/**
 * The activity tied to the settings screen
 */
public class Settings extends AppCompatActivity {
    private ImageButton back;
    public ImageButton apply;
    public TextView username;
    private ImageButton logout;
    private Switch displayName;
    private User user;
    private IUser callback;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        user = new User();
        callback = new IUser() {
            @Override
            public int onSuccess() {
                return 0;
            }

            @Override
            public int onError() {
                return -1;
            }
        };
        user.getUser(this, callback, FirebaseAuth.getInstance().getCurrentUser().getUid(), false);
        back = findViewById(R.id.settings_back);
        apply = findViewById(R.id.apply_button);
        username = findViewById(R.id.editTextTextPersonName);
        logout = findViewById(R.id.logout_but);
        displayName = findViewById(R.id.display_name_toggle);
        displayName.setOnCheckedChangeListener(null);

        if(getIntent().getBooleanExtra("displayname", false))
            displayName.setChecked(true);

        back.setOnClickListener(view -> user.updateUser(Settings.this, new IUser() {
            @Override
            public int onSuccess() {
                Intent intent = new Intent(Settings.this, UserHome.class);
                intent.putExtra("displayname", user.getDisplayName());
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
                return 0;
            }

            @Override
            public int onError() {
                return 0;
            }
        }, false));
        apply.setOnClickListener(view -> {
            String input = username.getText().toString();
            if(input.isEmpty()){
                username.setError("Please provide username");
                username.requestFocus();
            }
            else{
                user.setUsername(input);
                user.updateUser(Settings.this, new IUser() {
                    @Override
                    public int onSuccess() {
                        Intent I = new Intent(Settings.this, UserHome.class);
                        startActivity(I);
                        return 0;
                    }

                    @Override
                    public int onError() {
                        return 0;
                    }
                }, false);
            }
        });
        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            user.updateUser(Settings.this, new IUser() {
                @Override
                public int onSuccess() {
                    Intent I = new Intent(Settings.this, MainActivity.class);
                    startActivity(I);
                    Toast.makeText(Settings.this, "User logged out", Toast.LENGTH_SHORT).show();
                    return 0;
                }

                @Override
                public int onError() {
                    return 0;
                }
            }, false);
        });
        displayName.setOnClickListener(view -> user.setDisplayName(!user.getDisplayName()));
    }
}
