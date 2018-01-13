package com.outliers.android.flingme.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.outliers.android.flingme.R;
import com.outliers.android.flingme.constants.AppConstants;

/**
 * Created by outliersasu on 11/26/17.
 */

public class SettingsActivity extends FlingMeParentActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner_difficulty;
    SharedPreferences appPref;

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_settings);

        appPref = getSharedPreferences(AppConstants.APP_PREF_NAME,MODE_PRIVATE);

        spinner_difficulty = (Spinner) findViewById(R.id.spinner_difficulty);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Easy","Moderate","Pro"});
        spinner_difficulty.setAdapter(spinnerAdapter);
        spinner_difficulty.setSelection(appPref.getInt(AppConstants.PREF_KEY_DIFFICULTY_LEVEL,0));
        spinner_difficulty.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor = appPref.edit();
        switch (position){
            case 0 :
                editor.putInt(AppConstants.PREF_KEY_DIFFICULTY_LEVEL,position);
                //TODO other tasks here if required
                break;

            case 1 :
                editor.putInt(AppConstants.PREF_KEY_DIFFICULTY_LEVEL,position);
                break;

            case 2 :
                editor.putInt(AppConstants.PREF_KEY_DIFFICULTY_LEVEL,position);
                break;
        }
        //Toast.makeText(this, position+"",Toast.LENGTH_SHORT).show();
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings_done:
                finish();
                return true;
        }
        return false;
    }
}
