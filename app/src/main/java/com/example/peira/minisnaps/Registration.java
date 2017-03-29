package com.example.peira.minisnaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText NameField;
    private EditText EmailField;
    private EditText PasswordField;

    private Button RegisterButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;


    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        mAuth = FirebaseAuth.getInstance();  //initialize mAuth

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Notes"); //initialize mDatabase2 to notes child


        mProgress = new ProgressDialog(this);

        NameField = (EditText) findViewById(R.id.Name_reg_field);
        EmailField = (EditText) findViewById(R.id.email_reg_field);
        PasswordField = (EditText) findViewById(R.id.password_reg_field);
        RegisterButton = (Button) findViewById(R.id.register_button);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });




    }

    private void startRegister() {
        final String name = NameField.getText().toString().trim();
        String email = EmailField.getText().toString().trim();
        String password = PasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signing Up...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid(); //Gets of the User from Auth

                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        current_user_db.child("name").setValue(name); //additional data that can't be saved to auth but should be stored on the firebase for each and every user

                        DatabaseReference current_user_db2 = mDatabase2.child(user_id);

                        current_user_db2.child("Your first note").setValue("I am your first note. Welcome to this app. You can write a note. Cause this is a note app :D");

                        mProgress.dismiss();

                        Intent accountIntent = new Intent(Registration.this, AccountActivity.class);
                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(accountIntent);

                    }


                }
            });

        }else{
            Toast.makeText(Registration.this, "Please fill all the required information", Toast.LENGTH_LONG).show();
        }


    }




}
