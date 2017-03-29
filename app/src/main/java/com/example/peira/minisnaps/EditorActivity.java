package com.example.peira.minisnaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditorActivity extends AppCompatActivity {

    public Button savebutton;
    public Button cancelbutton;
    public EditText notetitle;
    public EditText editor;

    private String notekey;
    private String notevalue;

    private String TAG = "EditorActivity";
    private Boolean newnote;
    private String itemkey;

    private DatabaseReference mDatabase;
    private DatabaseReference current_user_db;
    private FirebaseAuth mAuth;

    private String user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Notes");
        user_id = mAuth.getCurrentUser().getUid();
        current_user_db = mDatabase.child(user_id);



        savebutton = (Button) findViewById(R.id.saveBtn);
        cancelbutton = (Button) findViewById(R.id.cancelBtn);
        notetitle = (EditText) findViewById(R.id.notetitle);
        editor = (EditText) findViewById(R.id.editor);



        Bundle extras = getIntent().getExtras();
        newnote = extras.getBoolean("BOOL");
        if(!newnote) {
             itemkey = extras.getString("STR");
             notetitle.setText(itemkey);
             current_user_db.child(itemkey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String note = dataSnapshot.getValue(String.class);

                    editor.setText(note);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });

        }


        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notekey = notetitle.getText().toString();
                notevalue = editor.getText().toString();

                if(!TextUtils.isEmpty(notekey)) {
                    if (newnote)
                        WriteNewNote();
                    else
                        EditNote();

                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Title is not allowed to be empty",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void EditNote()
    {
        current_user_db.child(itemkey).removeValue();

        WriteNewNote();


    }

    void WriteNewNote()
    {
        Log.d(TAG,user_id);


        Log.d(TAG,notekey);
        Log.d(TAG,notevalue);
        current_user_db.child(notekey).setValue(notevalue);
        Toast.makeText(getApplicationContext(), "Saved",
                Toast.LENGTH_SHORT).show();

    }
}
