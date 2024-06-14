package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.group2.catan_android.data.live.game.UseProgressCardDto;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.databinding.FragmentMonopolyCardBinding;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;

import javax.annotation.Nullable;

public class MonopolyFragment extends PopUpFragment {
    private FragmentMonopolyCardBinding binding;
    private MoveMaker moveMaker;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMonopolyCardBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        moveMaker = MoveMaker.getInstance();
        binding.buttonBricks.setOnClickListener(v -> useProgressCard(ResourceDistribution.HILLS));
        binding.buttonSheep.setOnClickListener(v -> useProgressCard(ResourceDistribution.PASTURE));
        binding.buttonStone.setOnClickListener(v -> useProgressCard(ResourceDistribution.MOUNTAINS));
        binding.buttonWheat.setOnClickListener(v -> useProgressCard(ResourceDistribution.FIELDS));
        binding.buttonWood.setOnClickListener(v -> useProgressCard(ResourceDistribution.FOREST));

        return view;
    }
    private void useProgressCard(ResourceDistribution resource){
        try {
            moveMaker.makeMove(new UseProgressCardDto(ProgressCardType.MONOPOLY, null, resource));
        } catch (Exception e) {
            MessageBanner.makeBanner(getActivity(), MessageType.ERROR, "An error occurred!").show();
            Log.d("ProgressCards", e.toString());
        }
        MessageBanner.makeBanner(getActivity(), MessageType.INFO, "Resources stolen!").show();
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
