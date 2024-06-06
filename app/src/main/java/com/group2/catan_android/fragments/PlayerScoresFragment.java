package com.group2.catan_android.fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.group2.catan_android.R;
import com.group2.catan_android.gamelogic.Player;

import java.util.List;

public class PlayerScoresFragment extends Fragment {
    private TextView firstPlayer;
    private TextView secondPlayer;
    private TextView thirdPlayer;
    private TextView fourthPlayer;
    private TextView[] playerScoreViews;
    private ImageView[] activePlayerViews;
    private ImageView firstPlayerActive;
    private ImageView secondPlayerActive;
    private ImageView thirdPlayerActive;
    private ImageView fourthPlayerActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_scores, container, false);

        firstPlayer = view.findViewById(R.id.firstPlayerScore);
        secondPlayer = view.findViewById(R.id.secondPlayerScore);
        thirdPlayer = view.findViewById(R.id.thirdPlayerScore);
        fourthPlayer = view.findViewById(R.id.fourthPlayerScore);

        playerScoreViews = new TextView[4];
        playerScoreViews[0] = firstPlayer;
        playerScoreViews[1] = secondPlayer;
        playerScoreViews[2] = thirdPlayer;
        playerScoreViews[3] = fourthPlayer;

        setupActivePlayerIndicators(view);

        return view;
    }

    public void setupActivePlayerIndicators(View v){
        firstPlayerActive = v.findViewById(R.id.firstPlayerActive);
        secondPlayerActive = v.findViewById(R.id.secondPlayerActive);
        thirdPlayerActive = v.findViewById(R.id.thirdPlayerActive);
        fourthPlayerActive = v.findViewById(R.id.fourthPlayerActive);

        activePlayerViews = new ImageView[]{firstPlayerActive,secondPlayerActive,thirdPlayerActive,fourthPlayerActive};

        for(ImageView activePlayerView : activePlayerViews){
            activePlayerView.setVisibility(View.INVISIBLE);
        }
    }

    public void updateScores(List<Player> playerList, Player activePlayer){
        for(int i = 0; i < playerList.size(); i++){
            String playerScore = playerList.get(i).getDisplayName() + ": " + String.valueOf(playerList.get(i).getVictoryPoints());
            playerScoreViews[i].setText(playerScore);
            playerScoreViews[i].setTextColor(playerList.get(i).getColor());

            activePlayerViews[i].setVisibility(View.INVISIBLE);
            if(playerList.get(i).getInGameID() == activePlayer.getInGameID()){
                activePlayerViews[i].setBackgroundColor(View.VISIBLE);
            }
        }
    }
}