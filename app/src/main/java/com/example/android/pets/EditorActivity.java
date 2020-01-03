/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.lang.UCharacter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.UserContract;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mAmountEditText;

    /** EditText field to enter the pet's weight */
    private EditText mMobileEditText;
    private EditText mAddressEdit;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    private int mGender;

    private SQLiteDatabase db;
    private Intent intent;
    private final int PET_LOADER = 1;
    private Uri currentPetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_user_name);
        mAmountEditText = (EditText) findViewById(R.id.edit_user_amount);
        mMobileEditText = (EditText) findViewById(R.id.edit_user_mobile);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mAddressEdit  = (EditText) findViewById(R.id.edit_user_address);

        intent = getIntent();
         currentPetUri = intent.getData();
        if(currentPetUri == null){
            setTitle("Add a new User");
        }else{
            setTitle("Edit a User");
            getSupportLoaderManager().initLoader(PET_LOADER,null,this);
        }



        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = UserContract.UserEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = UserContract.UserEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = UserContract.UserEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Insert Data
                   saveData();
                // finished
                Toast.makeText(this, "Pet Saved", Toast.LENGTH_LONG).show();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* used to insert data to the database */
    private void saveData(){


        //Crassh Prevention


        /**we save in add new mode else no */
        if(currentPetUri == null){

            String mobile = mMobileEditText.getText().toString();
            String nameString = mNameEditText.getText().toString().trim();
            String amountString = mAmountEditText.getText().toString().trim();
            String address = mAddressEdit.getText().toString().trim();
            /*if (weightInt != 0) {
                weightInt = Integer.parseInt(mMobileEditText.getText().toString());
            }*/
            if ( TextUtils.isEmpty(nameString) || TextUtils.isEmpty(amountString) || mGender == UserContract.UserEntry.GENDER_UNKNOWN) {return;}


            ContentValues values = new ContentValues();
            values.put(UserContract.UserEntry.COLUMN_USER_NAME, nameString);
            values.put(UserContract.UserEntry.COLUMN_USER_AMOUNT, amountString);
            values.put(UserContract.UserEntry.COLUMN_USER_MOBILE, mobile);
            values.put(UserContract.UserEntry.COLUMN_USER_GENRE, mGender);
            values.put(UserContract.UserEntry.COLUMN_USER_ADDRESS, address);

            Uri newRowUri = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI,values);
            if (newRowUri == null) {
                Toast.makeText(this,"Error when saving data",Toast.LENGTH_LONG);
            }else{
                Toast.makeText(this,"User Saved",Toast.LENGTH_LONG);
            }

        }else{

            //int paidInt= Integer.parseInt(mMobileEditText.getText().toString());
            String nameString = mNameEditText.getText().toString().trim();
            String addressString = mAddressEdit.getText().toString();
            String mobileString = mMobileEditText.getText().toString();
            String breedString = mAmountEditText.getText().toString().trim();
          /*  if (weightInt != 0) {
                weightInt = Integer.parseInt(mMobileEditText.getText().toString());
            }*/

            if ( TextUtils.isEmpty(nameString) || TextUtils.isEmpty(breedString) || mGender == UserContract.UserEntry.GENDER_UNKNOWN) {return;}

            ContentValues values = new ContentValues();
            values.put(UserContract.UserEntry.COLUMN_USER_NAME,nameString);
            values.put(UserContract.UserEntry.COLUMN_USER_AMOUNT,breedString);
           // values.put(UserContract.UserEntry.COLUMN_USER_PAID,paidInt);
            values.put(UserContract.UserEntry.COLUMN_USER_GENRE,mGender);
            values.put(UserContract.UserEntry.COLUMN_USER_MOBILE, mobileString);
            values.put(UserContract.UserEntry.COLUMN_USER_ADDRESS, addressString);

           int numberOfRowUpdate = getContentResolver().update(currentPetUri,values,null,null);
           if(numberOfRowUpdate > 0){
               Toast.makeText(this,"Pet Update",Toast.LENGTH_LONG);
           }else{
               Toast.makeText(this,"Error when updating pet",Toast.LENGTH_LONG);
           }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String Projection[] = {UserContract.UserEntry._ID,
                              UserContract.UserEntry.COLUMN_USER_NAME,
                                UserContract.UserEntry.COLUMN_USER_AMOUNT,
                                UserContract.UserEntry.COLUMN_USER_GENRE,
                                UserContract.UserEntry.COLUMN_USER_PAID,
                                UserContract.UserEntry.COLUMN_USER_MOBILE,
                                UserContract.UserEntry.COLUMN_USER_ADDRESS};


        return new CursorLoader(this,currentPetUri,Projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.v("DATA:" , data.getColumnName(0) + "");
        Log.v("DATA:" , data.getColumnName(1) + "");
        Log.v("DATA:" , data.getColumnName(2) + "");
        Log.v("DATA:" , data.getColumnName(3) + "");
        Log.v("DATA:" , data.getColumnName(4) + "");
        Log.v("DATA:" , data.getColumnName(5) + "");
        Log.v("DATA:" , data.getColumnName(6) + "");
        if(data.moveToFirst()) {
            int nameIdIndex = data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_NAME);
            int amountIndex = data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_AMOUNT);
            int genreIndex = data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_GENRE);
            int paidIndex = data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_PAID);
            int mobileIndex = data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_MOBILE);
            int addressIndex = data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_ADDRESS);
            String mobile = Integer.toString(data.getInt(mobileIndex));
            String address = Integer.toString(data.getInt(addressIndex));


            String paid = Integer.toString(data.getInt(paidIndex));
            int gender = data.getInt(genreIndex);

            Log.v("Index:", data.getString(nameIdIndex) + data.getString(amountIndex) + data.getColumnIndex(UserContract.UserEntry.COLUMN_USER_MOBILE));
            mNameEditText.setText(data.getString(nameIdIndex));
            mAmountEditText.setText(data.getString(amountIndex));
            mMobileEditText.setText(data.getString(mobileIndex));
            mAddressEdit.setText(data.getString(addressIndex));

            switch (gender){
                case UserContract.UserEntry.GENDER_FEMALE :
                    mGenderSpinner.setSelection(2);
                    break;
                case UserContract.UserEntry.GENDER_MALE :
                    mGenderSpinner.setSelection(1);
                    break;
                case UserContract.UserEntry.GENDER_UNKNOWN :
                    mGenderSpinner.setSelection(0);
                    break;

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}