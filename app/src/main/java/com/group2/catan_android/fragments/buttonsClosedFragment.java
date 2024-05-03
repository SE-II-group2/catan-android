package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.group2.catan_android.R;

public class buttonsClosedFragment extends Fragment  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buttons_closed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView build = getActivity().findViewById(R.id.build);
        ImageView trade = getActivity().findViewById(R.id.trade);
        ImageView card_usage = getActivity().findViewById(R.id.card_usage);
        ImageView help = getActivity().findViewById(R.id.help);

        build.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment newFragment = new buttonsOpenFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.leftButtonsFragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Write what shall happen when pressing the trade button

            }
        });

        card_usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Write what shall happen when pressing the development card usage button

            }
        });


        // TODO: Decide weather to have a fourth button on top
        /*
        extra_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Write what shall happen when pressing the extra button

            }
        });
         */
    }
}