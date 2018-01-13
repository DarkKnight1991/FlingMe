package com.outliers.android.flingme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.outliers.android.flingme.R;
import com.outliers.android.flingme.constants.AppConstants;
import com.outliers.android.flingme.interfaces.GameHistoryInterface;
import com.outliers.android.flingme.models.GameScoreModel;
import com.outliers.android.flingme.utils.ProjectUtil;

import java.util.ArrayList;

/**
 * Created by outliersasu on 11/24/17.
 */

public class GameScoresAdapter extends RecyclerView.Adapter<GameScoresAdapter.MainViewHolder> {

    Context context;
    ArrayList<GameScoreModel> gameScoreModels;
    GameHistoryInterface gameHistoryInterface;

    public GameScoresAdapter(Context context, ArrayList<GameScoreModel> gameScores, GameHistoryInterface gameHistoryInterface){
        this.context = context;
        this.gameScoreModels = gameScores;
        this.gameHistoryInterface = gameHistoryInterface;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{

        public MainViewHolder(View view){
            super(view);
        }
    }

    public class GameScoreViewHolder extends MainViewHolder{

        TextView tvScore,tvMovementDiff,tvSpeedBonus,tvTimeTaken,tvCatchAttempts,tvTime;
        ImageView ivDelete;

        public GameScoreViewHolder(View view){
            super(view);

            tvScore = (TextView) view.findViewById(R.id.tv_score);
            tvMovementDiff = (TextView) view.findViewById(R.id.tv_movement_diff);
            tvSpeedBonus = (TextView) view.findViewById(R.id.tv_speed);
            tvTimeTaken = (TextView) view.findViewById(R.id.tv_time_taken);
            tvCatchAttempts = (TextView) view.findViewById(R.id.tv_attempts);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameHistoryInterface.delete(gameScoreModels.get(getLayoutPosition()).getId());
                }
            });
        }
    }

    @Override
    public GameScoresAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_game_score,parent,false);
        return new GameScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameScoresAdapter.MainViewHolder holder, int position) {
        if(holder instanceof GameScoreViewHolder){
            GameScoreViewHolder vh = (GameScoreViewHolder) holder;
            vh.tvScore.setText("You scored "+gameScoreModels.get(position).getScore());
            vh.tvMovementDiff.setText("Movement Difficulty Bonus : "+gameScoreModels.get(position).getMovementDifficulty());
            vh.tvSpeedBonus.setText("Speed Bonus : "+ gameScoreModels.get(position).getSpeedBonus());
            vh.tvTimeTaken.setText("Time Taken : "+ gameScoreModels.get(position).getTimeTaken()+" seconds");
            vh.tvCatchAttempts.setText("Failed Attempts : "+gameScoreModels.get(position).getCatchAttempts());
            vh.tvTime.setText("Finish Time : "+ ProjectUtil.formatDateTime(gameScoreModels.get(position).getId(), null));
            vh.tvTime.setTag(gameScoreModels.get(position).getId());
        }
    }

    @Override
    public int getItemCount() {
        return gameScoreModels.size();
    }
}
