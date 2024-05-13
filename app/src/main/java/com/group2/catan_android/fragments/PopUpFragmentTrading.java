package com.group2.catan_android.fragments;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.group2.catan_android.R;
import com.group2.catan_android.gamelogic.Player;

public class PopUpFragmentTrading extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pop_up_trading, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstraintLayout width = getActivity().findViewById(R.id.width);
        ConstraintLayout one = getActivity().findViewById(R.id.one);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        if(width==null||one==null||params==null){
            Toast.makeText(getContext(), "Getting width or one or params failed!", Toast.LENGTH_SHORT).show();return;}
        params.width=width.getWidth()-one.getHeight();
        params.height=WindowManager.LayoutParams.MATCH_PARENT;
        params.horizontalMargin=one.getHeight();
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction().add(R.id.trading_popup_time_selection, new TradingTimeSelection()).commit();
        getChildFragmentManager().beginTransaction().add(R.id.trading_popup_bottomfragment,new PlayerResourcesFragment()).commit();
        manager.beginTransaction().add(R.id.trading_popup_topfragment, new TradingResourceSelectionFragment()).commit();
        manager.beginTransaction().add(R.id.trading_popup_middlefragment, new TradingResourceSelectionFragment()).commit();
    }
}
