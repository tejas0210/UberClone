package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    enum State{
        SIGNUP,LOGIN;
    }

    private State state;
    private EditText edtUsername, edtPassword, edtDOP;
    private Button btnSignUp, btnOTL;
    private RadioButton rdbDriver, rdbPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Uber");

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtDOP = findViewById(R.id.edtDOP);

        rdbDriver = findViewById(R.id.rdbDriver);
        rdbPassenger = findViewById(R.id.rdbPassenger);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnOTL = findViewById(R.id.btnOTL);

        state = State.SIGNUP;

        btnOTL.setOnClickListener(this::onClick);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state==State.SIGNUP){
                    if(rdbDriver.isChecked()==false && rdbPassenger.isChecked()==false){
                        Toast.makeText(MainActivity.this,"Select one of the option from Driver and Passenger..",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());
                    if(rdbDriver.isChecked()){
                        appUser.put("As","Driver");
                    }
                    else if(rdbPassenger.isChecked()){
                        appUser.put("As","Passenger");
                    }

                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(MainActivity.this,"Signed Up!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else if(state==State.LOGIN){
                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(user!=null && e==null){
                                Toast.makeText(MainActivity.this,"Logged In!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.state_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View view) {
        if(edtDOP.getText().toString().equals("Driver") || edtDOP.getText().toString().equals("Passenger")){
            if(ParseUser.getCurrentUser()==null){
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user!=null && e==null){
                            Toast.makeText(MainActivity.this,"We have an anonymous user.",Toast.LENGTH_SHORT).show();
                            user.put("As",edtDOP.getText().toString());
                            user.saveInBackground();
                        }
                    }
                });
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.login:
                if(state == State.SIGNUP){
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                    btnSignUp.setText("Log in");
                }
                else if(state == State.LOGIN){
                    state = State.SIGNUP;
                    item.setTitle("Log In");
                    btnSignUp.setText("Sign Up");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}