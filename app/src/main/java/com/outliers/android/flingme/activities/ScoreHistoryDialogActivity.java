package com.outliers.android.flingme.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.outliers.android.flingme.R;
import com.outliers.android.flingme.adapters.GameScoresAdapter;
import com.outliers.android.flingme.application.FlingMeApplication;
import com.outliers.android.flingme.constants.AppConstants;
import com.outliers.android.flingme.interfaces.GameHistoryInterface;
import com.outliers.android.flingme.models.GameScoreModel;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by outliersasu on 11/24/17.
 */

public class ScoreHistoryDialogActivity extends FlingMeParentActivity implements GameHistoryInterface {

    RecyclerView recyclerView;
    GameScoresAdapter adapter;
    SharedPreferences appPref;

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.layout_score_history);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        }
        else{
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        }
        int screenH = outMetrics.heightPixels;
        int screenW = outMetrics.widthPixels;

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(screenH*0.7));
        View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);

        appPref = ((FlingMeApplication)this.getApplication()).appPref;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            JSONArray gameHistory = new JSONArray(appPref.getString(AppConstants.PREF_KEY_GAME_HISTORY,"[]"));
            adapter = new GameScoresAdapter(this, GameScoreModel.parseJsonArrayToList(gameHistory),this);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            finish();
            return;
        }

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void delete(long id) {
        String scoreArrayString = appPref.getString(AppConstants.PREF_KEY_GAME_HISTORY,"[]");
        try {
            JSONArray scoreJsonArray = new JSONArray(scoreArrayString);
            JSONArray temp = new JSONArray();
            for(int i=0; i<scoreJsonArray.length(); i++){
                if(scoreJsonArray.getJSONObject(i).getLong("id") != id){
                    temp.put(scoreJsonArray.getJSONObject(i));
                }
            }
            appPref.edit().putString(AppConstants.PREF_KEY_GAME_HISTORY,temp.toString()).commit();
            adapter = new GameScoresAdapter(this, GameScoreModel.parseJsonArrayToList(temp),this);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
