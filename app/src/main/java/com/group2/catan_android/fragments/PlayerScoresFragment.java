package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.group2.catan_android.R;
import com.group2.catan_android.gamelogic.Player;

import java.util.Comparator;
import java.util.List;

public class PlayerScoresFragment extends Fragment {
    private TextView[] playerScoreViews;
    private ImageView[] activePlayerViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_scores, container, false);

        TextView firstPlayer = view.findViewById(R.id.firstPlayerScore);
        TextView secondPlayer = view.findViewById(R.id.secondPlayerScore);
        TextView thirdPlayer = view.findViewById(R.id.thirdPlayerScore);
        TextView fourthPlayer = view.findViewById(R.id.fourthPlayerScore);

        playerScoreViews = new TextView[]{firstPlayer,secondPlayer,thirdPlayer,fourthPlayer};

        setupActivePlayerIndicators(view);

        return view;
    }

    public void setupActivePlayerIndicators(View v){
        ImageView firstPlayerActive = v.findViewById(R.id.firstPlayerActive);
        ImageView secondPlayerActive = v.findViewById(R.id.secondPlayerActive);
        ImageView thirdPlayerActive = v.findViewById(R.id.thirdPlayerActive);
        ImageView fourthPlayerActive = v.findViewById(R.id.fourthPlayerActive);

        activePlayerViews = new ImageView[]{firstPlayerActive, secondPlayerActive, thirdPlayerActive, fourthPlayerActive};
        hideActivePlayerIndicators();
    }

    private void hideActivePlayerIndicators() {
        for(ImageView activePlayerView : activePlayerViews){
            activePlayerView.setVisibility(View.INVISIBLE);
        }
    }

    public void updateScores(List<Player> playerList){
        hideActivePlayerIndicators();

        for(int i = 0; i < playerList.size(); i++){
            String playerScore = playerList.get(i).getDisplayName() + ": " + playerList.get(i).getVictoryPoints();
            playerScoreViews[i].setText(playerScore);
            playerScoreViews[i].setTextColor(playerList.get(i).isConnected() ? playerList.get(i).getColor() : 0xFF606060); //grey

            if(playerList.get(i).isActive()){
                Log.d("Scores","activePlayer " + playerList.get(i).getDisplayName() + "setVisible");
                activePlayerViews[i].setVisibility(View.VISIBLE);
            }
        }
    }

    public void setFinalScores(List<Player> playerList){
        playerList.sort(Comparator.comparingInt(Player::getVictoryPoints).reversed());
        hideActivePlayerIndicators();

        for(int i = 0; i < playerList.size(); i++){
            String playerScore = playerList.get(i).getDisplayName() + ": " + playerList.get(i).getVictoryPoints();
            playerScoreViews[i].setText(playerScore);
            playerScoreViews[i].setTextColor(0xFFFFFFFF);
            if(i == 0){
                playerScoreViews[i].setTextColor(0xFF1aff00);
            }
        }
    }
}