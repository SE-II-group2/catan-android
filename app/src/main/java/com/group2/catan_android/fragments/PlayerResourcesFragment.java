package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group2.catan_android.R;
import com.group2.catan_android.data.Constants;
import com.group2.catan_android.gamelogic.Player;

public class PlayerResourcesFragment extends Fragment {
    private int[] resources;
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (resources!=null) {
            updateResources(resources);
        }
    }
    public void setResources(int[] resources) {
        this.resources = resources;
        if (getView() != null) {
            updateResources(resources);
        }
    }

    public void updateResources(Player player) {
        wheatCount.setText(String.valueOf(player.getResources()[Constants.LOGIC_KEY_WHEAT]));
        sheepCount.setText(String.valueOf(player.getResources()[Constants.LOGIC_KEY_SHEEP]));
        woodCount.setText(String.valueOf(player.getResources()[Constants.LOGIC_KEY_WOOD]));
        brickCount.setText(String.valueOf(player.getResources()[Constants.LOGIC_KEY_BRICK]));
        stoneCount.setText(String.valueOf(player.getResources()[Constants.LOGIC_KEY_STONE]));
    }

    public void updateResources(int[] resources) {
        wheatCount.setText(String.valueOf(resources[Constants.LOGIC_KEY_WHEAT]));
        sheepCount.setText(String.valueOf(resources[Constants.LOGIC_KEY_SHEEP]));
        woodCount.setText(String.valueOf(resources[Constants.LOGIC_KEY_WOOD]));
        brickCount.setText(String.valueOf(resources[Constants.LOGIC_KEY_BRICK]));
        stoneCount.setText(String.valueOf(resources[Constants.LOGIC_KEY_STONE]));
    }

}