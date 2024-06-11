package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group2.catan_android.R;


public class TradeOfferFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trade_offer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFragmentsAndButtons(view);
    }

    public void setFragmentsAndButtons(View view){
        // get used Views
        FragmentManager manager = getChildFragmentManager();
        TextView playerText = view.findViewById(R.id.trade_offer_player_text);
        Button accept = view.findViewById(R.id.trade_offer_accept);
        Button decline = view.findViewById(R.id.trade_offer_decline);
        // configure Views
        manager.beginTransaction().add(R.id.trade_offer_get_fragment, new PlayerResourcesFragment()).commit();
        manager.beginTransaction().add(R.id.trade_offer_give_fragment, new PlayerResourcesFragment()).commit();
        accept.setOnClickListener(v -> {
                //accept happens
            });
        decline.setOnClickListener(v -> {
                //decline
            });

    }

}