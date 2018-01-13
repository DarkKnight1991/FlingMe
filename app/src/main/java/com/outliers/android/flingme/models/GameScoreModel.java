package com.outliers.android.flingme.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by outliersasu on 11/24/17.
 */

public class GameScoreModel implements Parcelable {

    long id;
    int score;
    int movementDifficulty;
    int speedBonus;
    int timeTaken;
    int catchAttempts;

    public GameScoreModel(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMovementDifficulty() {
        return movementDifficulty;
    }

    public void setMovementDifficulty(int movementDifficulty) {
        this.movementDifficulty = movementDifficulty;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }

    public void setSpeedBonus(int speedBonus) {
        this.speedBonus = speedBonus;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getCatchAttempts() {
        return catchAttempts;
    }

    public void setCatchAttempts(int catchAttempts) {
        this.catchAttempts = catchAttempts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(score);
        dest.writeInt(movementDifficulty);
        dest.writeInt(speedBonus);
        dest.writeInt(timeTaken);
        dest.writeInt(catchAttempts);
    }

    protected GameScoreModel(Parcel in) {
        id = in.readLong();
        score = in.readInt();
        movementDifficulty = in.readInt();
        speedBonus = in.readInt();
        timeTaken = in.readInt();
        catchAttempts = in.readInt();
    }

    public static final Creator<GameScoreModel> CREATOR = new Creator<GameScoreModel>() {
        @Override
        public GameScoreModel createFromParcel(Parcel in) {
            return new GameScoreModel(in);
        }

        @Override
        public GameScoreModel[] newArray(int size) {
            return new GameScoreModel[size];
        }
    };

    public static ArrayList<GameScoreModel> parseJsonArrayToList(JSONArray gameHistory){
        ArrayList<GameScoreModel> list = new ArrayList<>();
        for(int i=0; i<gameHistory.length(); i++){
            try {
                JSONObject gameObj = gameHistory.getJSONObject(i);
                GameScoreModel model = new GameScoreModel();
                model.setId(gameObj.getLong("id"));
                model.setScore(gameObj.getInt("score"));
                model.setMovementDifficulty(gameObj.getInt("movementDiff"));
                model.setSpeedBonus(gameObj.getInt("speedBonus"));
                model.setCatchAttempts(gameObj.getInt("catchAttempts"));
                model.setTimeTaken(gameObj.getInt("timeTaken"));
                list.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
/*
gameObject.put("id",finishTime);
        gameObject.put("score",score);
        gameObject.put("movementDiff",degreeFactor);
        gameObject.put("speedBonus",speed);
        gameObject.put("timeTaken",time);
        gameObject.put("catchAttempts",catchAttempts);*/
