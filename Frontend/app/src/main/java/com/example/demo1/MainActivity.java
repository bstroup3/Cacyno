package com.example.demo1;

import Models.User;
import Utilities.AccountChecker;
import Utilities.AccountCreation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.IUser;

/**
 * The activity tied to the signup screen
 */
public class MainActivity extends AppCompatActivity{

    public EditText emailId, passwd;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    User user;
    AccountChecker accountChecker;
    AccountCreation accountCreation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountChecker = new AccountChecker();
        accountCreation = new AccountCreation();
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.ETemail);
        passwd = findViewById(R.id.ETpassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        signIn = findViewById(R.id.TVSignIn);


        Toast toast = null;
        user = new User();
        btnSignUp.setOnClickListener(view -> {
            String emailID = emailId.getText().toString();
            String paswd = passwd.getText().toString();
            if (accountCreation.createAccount(emailID, paswd, emailId, passwd, MainActivity.this)) {
                firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(MainActivity.this, (OnCompleteListener) task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this.getApplicationContext(),
                                "SignUp unsuccessful: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        user.firstTimeAppend(MainActivity.this, new IUser() {
                            @Override
                            public int onSuccess() {
                                startActivity(new Intent(MainActivity.this, Username.class));
                                return 0;
                            }

                            @Override
                            public int onError() {
                                return 0;
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        signIn.setOnClickListener(view -> {
            Intent I = new Intent(MainActivity.this, Login.class);
            startActivity(I);
        });
    }


}