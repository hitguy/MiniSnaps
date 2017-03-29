package com.example.peira.minisnaps;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.peira.minisnaps.User;

import java.util.Collections;
import java.util.zip.Inflater;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class AccountActivity extends AppCompatActivity {


    private String TAG = "AccountActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference current_user_db;
    private FirebaseListAdapter<String> firebaseListAdapter;

    private String userId;
    private String itemkey;
    private String itemkeymd;
    private TextView nametextview;
    private ListView mListView;
    private CheckBox mcheckbox;
    private boolean deletemode;
    private MenuItem checkitem;
    private MenuItem cancelitem;
    private MenuItem chooseallitem;
    private MenuItem delmulnotes;

    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mListView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(mListView);

        mProgress = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent loginIntent = new Intent(AccountActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                else
                {
                    userId = mAuth.getCurrentUser().getUid();

                    Log.d(TAG,userId.toString());
                    //User user = new User("MANOLIOSOMPHXTHS");
                    //mDatabase.child("Users").child(userId).setValue(user);



                    FilLListView();
                    GetUserName();





                }


            }
        };

        //GetUserName();



    }



    private void FilLListView()
    {
        current_user_db = mDatabase.child("Notes").child(userId);
        firebaseListAdapter = new FirebaseListAdapter<String>(
                this,
                String.class,
                R.layout.custom_list_item,
                current_user_db
        ) {

            @Override
            protected void populateView(View v, String model, int position) {

                DatabaseReference itemRef = getRef(position);
                String itemkey = itemRef.getKey();

                TextView textView = (TextView) v.findViewById(R.id.lvItemTitle);
                TextView textView2 = (TextView) v.findViewById(R.id.lvItemContent);
                textView.setText(itemkey);
                textView2.setText(model);

            }
        };

        mListView.setAdapter(firebaseListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if(deletemode)
                {
                    mcheckbox = (CheckBox) mListView.getChildAt(position).findViewById(R.id.lvcheckBox);
                    if(mcheckbox.isChecked())
                        mcheckbox.setChecked(false);
                    else
                        mcheckbox.setChecked(true);
                }


            }
        });

        /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                DatabaseReference itemRef = firebaseListAdapter.getRef(pos);
                String itemkey = itemRef.getKey();
                Toast.makeText(getApplicationContext(), itemkey,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int position = info.position;

            DatabaseReference itemRef = firebaseListAdapter.getRef(position);
            itemkey = itemRef.getKey();

            Toast.makeText(getApplicationContext(), itemkey,
                    Toast.LENGTH_SHORT).show();

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(AccountActivity.this, EditorActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("BOOL",false);
                extras.putString("STR",itemkey);
                intent.putExtras(extras);
                startActivity(intent);
                return true;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to delete this note?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                current_user_db.child(itemkey).removeValue();
                                Toast.makeText(AccountActivity.this,"Deleted note",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                // remove stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void GetUserName()
    {
        nametextview = (TextView)findViewById(R.id.welcome_user);
        mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, user.name);
                nametextview.setText("Welcome, " + user.name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        checkitem = menu.findItem(R.id.check);
        cancelitem = menu.findItem(R.id.cancel);
        chooseallitem = menu.findItem(R.id.chooseall);
        delmulnotes = menu.findItem(R.id.item3);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.item1:

                Toast.makeText(AccountActivity.this,"Add", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountActivity.this,EditorActivity.class);
                intent.putExtra("BOOL",true);
                startActivity(intent);
                return true;
            case R.id.check:
                checkitem.setVisible(false);
                cancelitem.setVisible(false);
                chooseallitem.setVisible(false);
                delmulnotes.setVisible(true);

                for (int x = 0; x<mListView.getChildCount();x++){
                    mcheckbox = (CheckBox)mListView.getChildAt(x).findViewById(R.id.lvcheckBox);
                    if(mcheckbox.isChecked()){
                        DatabaseReference itemRef2 = firebaseListAdapter.getRef(x);
                        itemkeymd = itemRef2.getKey();
                        current_user_db.child(itemkeymd).removeValue();
                    }
                    mcheckbox.setVisibility(View.INVISIBLE);
                }

                Toast.makeText(AccountActivity.this,"Deleted notes",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.cancel:
                checkitem.setVisible(false);
                cancelitem.setVisible(false);
                chooseallitem.setVisible(false);
                delmulnotes.setVisible(true);
                deletemode = false;

                for (int x = 0; x<mListView.getChildCount();x++) {

                    mcheckbox = (CheckBox) mListView.getChildAt(x).findViewById(R.id.lvcheckBox);
                    if(mcheckbox.isChecked()) {
                        mcheckbox.setChecked(false);
                    }

                    mcheckbox.setVisibility(View.INVISIBLE);
                }
                return true;
            case R.id.item3:

                checkitem.setVisible(true);
                cancelitem.setVisible(true);
                chooseallitem.setVisible(true);
                delmulnotes.setVisible(false);
                if(!deletemode)
                    deletemode = true;
                else
                    deletemode = false;
                for (int x = 0; x<mListView.getChildCount();x++){
                    mcheckbox = (CheckBox)mListView.getChildAt(x).findViewById(R.id.lvcheckBox);

                    mcheckbox.setVisibility(View.VISIBLE);
                    /*if(mcheckbox.isChecked()){
                        DatabaseReference itemRef2 = firebaseListAdapter.getRef(x);
                        itemkeymd = itemRef2.getKey();
                        current_user_db.child(itemkeymd).removeValue();
                    }*/
                }
                return true;
            case R.id.chooseall:
                for (int x = 0; x<mListView.getChildCount();x++) {
                    mcheckbox = (CheckBox) mListView.getChildAt(x).findViewById(R.id.lvcheckBox);

                    if(!mcheckbox.isChecked())
                        mcheckbox.setChecked(true);
                }
                return true;
            case R.id.item4:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onPause() {
        super.onPause();

        checkitem.setVisible(false);
        cancelitem.setVisible(false);
        chooseallitem.setVisible(false);
        delmulnotes.setVisible(true);
        deletemode = false;
    }

    private void logout() {

        mAuth.signOut();
        finish();

    }


}




