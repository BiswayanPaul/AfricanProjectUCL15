package com.example.user.thenewavaafrican2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.RadioButton;

import java.util.PriorityQueue;

public class MyProfileNew extends ActionBarActivity
{
    ImageView callprofilepicture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile_new);

        callprofilepicture = (ImageView) findViewById(R.id.profilePicture);
        //Preferences Read
        SharedPreferences settings = getSharedPreferences("UsrPrefs", 0);
        String name = settings.getString("CurUsr", "No User");
            readPrevVals(name);

        callprofilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    public void readPrevVals(String name)
    {
        Log.d("Debug", "Preferences curUsr = " + name);
        //Database Access
        UserDbHelper mDbHelper = UserDbHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try
            {
                //Db query
                Cursor c = db.query(UserContract.UserEntry.TABLE_NAME,
                        null,
                        "name = ?",
                        new String[] {name},
                        null,
                        null,
                        null);
                c.moveToFirst();
                //Writing new values to textviews
                ((TextView) findViewById(R.id.nametab)).setText(c.getString(c.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_NAME)));
                ((TextView) findViewById(R.id.passtab)).setText(c.getString(c.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_PASS)));
                ((TextView) findViewById(R.id.agetab)).setText(Integer.toString(c.getInt(c.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_AGE))));
                setInfect(c.getInt(c.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_INFECTED)));
                c.close();
            }
            catch(Exception e)
            {
                Log.e("Error", "User has no database values. Resetting Current User Preference.");
                SharedPreferences settings = getSharedPreferences("UsrPrefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("CurUsr", "No User");
                editor.apply();
            }

    }
    public void setInfect(int x)
    {
        //Sets the radiobuttons according to infected status.
        RadioButton rb = (RadioButton) findViewById(R.id.radio_infected);
        RadioButton rb2 = (RadioButton) findViewById(R.id.radio_notinfected);
        if(x == 0)
        {
            rb.setChecked(false);
            rb2.setChecked(true);
        }
        else
        {
            rb.setChecked(true);
            rb2.setChecked(false);
        }
    }
    public int getInfect()
    {
        //Getting an integer value based on which radio button is checked
        RadioButton rb = (RadioButton) findViewById(R.id.radio_infected);
        if(rb.isChecked())
        {
            return 1;
        }
        return 0;
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if (resCode == RESULT_OK) {
            if (reqCode == 1)
                callprofilepicture.setImageURI(data.getData());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile_new, menu);
        UserDbHelper mDbHelper = UserDbHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + UserContract.UserEntry.COLUMN_NAME_NAME + " FROM " + UserContract.UserEntry.TABLE_NAME, null);
        menu.add("New User");
        while(c.moveToNext())
        {
            menu.add(c.getString(0));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        SharedPreferences settings = getSharedPreferences("UsrPrefs", 0);
        //All of this is just testing the Database
        if(item.getTitle().equals("New User"))
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("CurUsr", "No User");
            editor.apply();
            finish();
            startActivity(getIntent());
        }
        else
        {
            PasswordDialogFragment.onCreateDialog(getApplicationContext(), this, (String) item.getTitle(), settings);
        }
        return true;
    }
    public void onRadioButtonClicked(View view)
    {   //Checking which one of the infected button
        //Can only choose one!
        boolean checked = ((RadioButton)view).isChecked();
        switch(view.getId())
        {
            case R.id.radio_notinfected:
                if (checked)
                    ((RadioButton) findViewById(R.id.radio_infected)).setChecked(false);
                    break;
            case R.id.radio_infected:
                if (checked)
                    ((RadioButton) findViewById(R.id.radio_notinfected)).setChecked(false);
                    break;
        }
    }
    public void saveChanges(View v)
    {
        String message = "User successfully added!";
        if(!checkIfNull())
        {
            //Display Error
            Log.e("Error", "Null fields. All fields must be filled.");
            //Show prompt sayings login success and creates new logic success dialog
            AlertDialog.Builder nullFields = new AlertDialog.Builder(this);
            nullFields.setTitle("Error");
            nullFields.setMessage("All fields must be filled, please check and try again!");
            nullFields.setPositiveButton("Okay", null);
            nullFields.create();
            nullFields.show();
        }
        else
        {
            //Preferences Read
            SharedPreferences settings = getSharedPreferences("UsrPrefs", 0);
            String curUsr = settings.getString("CurUsr", "No User");
            //Database Access
            UserDbHelper mDbHelper = UserDbHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if(!curUsr.equals("No User"))
            {
                //delete previous user
                db.delete(UserContract.UserEntry.TABLE_NAME, UserContract.UserEntry.COLUMN_NAME_NAME + "=?",new String[] {curUsr});
                Log.d("Debug", "Deleted User " + UserContract.UserEntry.COLUMN_NAME_NAME);
                message = "User Updated!";
            }
            //read new vals
            String name = (String)((TextView) findViewById(R.id.nametab)).getText().toString();
            int age = Integer.parseInt(((TextView) findViewById(R.id.agetab)).getText().toString());
            int inf = getInfect();
            String pass = (String)((TextView) findViewById(R.id.passtab)).getText().toString();
            //Creates new User
            User usr = new User(name, age, inf, pass);
            Log.d("Debug", "Created User with attributes: " + usr.getName() +  ", " + usr.getAge() + ", " + usr.getInfected() + ", " + usr.getPassword());
            //Save File
            usr.saveProfile(db);
            Globals.curUsr = usr;

            //update user prefs
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("CurUsr", usr.getName());
            editor.apply();
            readPrevVals(usr.getName());
            Globals.restoreState(getApplicationContext(), name);
            finish();
            startActivity(getIntent());
        }
    }
    public boolean checkIfNull()
    {
        if((((TextView) findViewById(R.id.nametab)).getText().toString()).equals(""))
        {
            return false;
        }
        if((((TextView) findViewById(R.id.agetab)).getText().toString()).equals(""))
        {
            return false;
        }
        if(!XOR(((RadioButton) findViewById(R.id.radio_notinfected)).isChecked(), ((RadioButton) findViewById(R.id.radio_infected)).isChecked()))
        {
            return false;
        }
        if((((TextView) findViewById(R.id.passtab)).getText().toString()).equals(""))
        {
            return false;
        }
        return true;
    }

    //Logical Exclusive Or
    public static boolean XOR(boolean x, boolean y)
    {
        return ( ( x || y ) && ! ( x && y ) );
    }
}

