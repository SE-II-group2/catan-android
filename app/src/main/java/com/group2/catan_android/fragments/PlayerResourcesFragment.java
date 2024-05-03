package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group2.catan_android.R;
import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;

public class PlayerResourcesFragment extends Fragment implements ResourceUpdateListener {

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
    public void onResourcesUpdated(int[] resources) {
        updateResources(resources);
    }

    public void updateResources(int[] resources) {
        woodCount.setText(String.valueOf(resources[0]));
        brickCount.setText(String.valueOf(resources[1]));
        sheepCount.setText(String.valueOf(resources[2]));
        wheatCount.setText(String.valueOf(resources[3]));
        stoneCount.setText(String.valueOf(resources[4]));
    }

}