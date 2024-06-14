package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group2.catan_android.R;
import com.group2.catan_android.data.live.game.UseProgressCardDto;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.databinding.FragmentYearofplentyCardBinding;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YearOfPlentyFragment extends PopUpFragment {
    private MoveMaker moveMaker;
    private Map<ResourceDistribution, Integer> resourceCounts;
    private int totalSelected;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.group2.catan_android.databinding.FragmentYearofplentyCardBinding binding = FragmentYearofplentyCardBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        moveMaker = MoveMaker.getInstance();
        resourceCounts = new HashMap<>();
        totalSelected = 0;
        for (ResourceDistribution resource : ResourceDistribution.values()) {
            resourceCounts.put(resource, 0);
        }
        buttonSetUp(view);

        return view;
    }

    private void buttonSetUp(View view){
        buttonListener(view, R.id.upWood, R.id.downWood, R.id.woodText, ResourceDistribution.FOREST);
        buttonListener(view, R.id.upSheep, R.id.downSheep, R.id.sheepText, ResourceDistribution.PASTURE);
        buttonListener(view, R.id.upStone, R.id.downStone, R.id.stoneText, ResourceDistribution.MOUNTAINS);
        buttonListener(view, R.id.upBrick, R.id.downBrick, R.id.brickText, ResourceDistribution.HILLS);
        buttonListener(view, R.id.upWheat, R.id.doneWheat, R.id.wheatText, ResourceDistribution.FIELDS);
    }

    private void buttonListener(View view, int upButtonID, int downButtonID, int textID, ResourceDistribution resource){
        ImageButton upButton = view.findViewById(upButtonID);
        ImageButton downButton = view.findViewById(downButtonID);
        TextView textView = view.findViewById(textID);

        resourceCounts.put(resource, 0);

        upButton.setOnClickListener(v-> {
            if (totalSelected < 2) {
                int count = resourceCounts.get(resource) + 1;
                resourceCounts.put(resource, count);
                textView.setText(String.valueOf(count));
                totalSelected++;
                checkTotalCount();
            }
        });
        downButton.setOnClickListener(v -> {
            int count = resourceCounts.get(resource);
            if (count > 0) {
                count--;
                resourceCounts.put(resource, count);
                textView.setText(String.valueOf(count));
                totalSelected--;
            }
        });
    }
    private void checkTotalCount() {
        if (totalSelected == 2) {
            List<ResourceDistribution> selectedResources = new ArrayList<>();
            for (Map.Entry<ResourceDistribution, Integer> entry : resourceCounts.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    selectedResources.add(entry.getKey());
                }
            }
            useProgressCard(selectedResources);
        }
    }
    private void useProgressCard(List<ResourceDistribution> resources){
        try {
            moveMaker.makeMove(new UseProgressCardDto(ProgressCardType.YEAR_OF_PLENTY, resources, null));
        } catch (Exception e) {
            MessageBanner.makeBanner(getActivity(), MessageType.ERROR, "An error occurred!").show();
            Log.d("ProgressCards", e.toString());
        }
        MessageBanner.makeBanner(getActivity(), MessageType.INFO, "Resources received!").show();
        closeFragment();
    }
    private void closeFragment(){
        assert getActivity() != null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
