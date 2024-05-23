package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        return view;
    }

    public void updateScores(List<Player> players){
        for(int i = 0; i < players.size(); i++){
            String playerScore = players.get(i).getDisplayName() + ": " + String.valueOf(players.get(i).getVictoryPoints());
            playerScoreViews[i].setText(playerScore);
            playerScoreViews[i].setTextColor(players.get(i).getColor());
        }
    }
}