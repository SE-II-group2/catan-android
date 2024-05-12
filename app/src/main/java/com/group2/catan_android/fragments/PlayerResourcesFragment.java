package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group2.catan_android.R;
import com.group2.catan_android.gamelogic.Player;

public class PlayerResourcesFragment extends Fragment {

    private TextView woodCount;
    private TextView brickCount;
    private TextView sheepCount;
    private TextView wheatCount;
    private TextView stoneCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_player_resources, container, false);

        woodCount = view.findViewById(R.id.woodCount);
        brickCount = view.findViewById(R.id.brickCount);
        sheepCount = view.findViewById(R.id.sheepCount);
        wheatCount = view.findViewById(R.id.wheatCount);
        stoneCount = view.findViewById(R.id.stoneCount);

        return view;
    }

    public void updateResources(Player player) {
        wheatCount.setText(String.valueOf(player.getResources()[0]));
        sheepCount.setText(String.valueOf(player.getResources()[1]));
        woodCount.setText(String.valueOf(player.getResources()[2]));
        brickCount.setText(String.valueOf(player.getResources()[3]));
        stoneCount.setText(String.valueOf(player.getResources()[4]));
    }

}