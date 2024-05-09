package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.group2.catan_android.R;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.fragments.interfaces.OnButtonClickListener;

public class ButtonsOpenFragment extends Fragment {

    ImageView build;
    ImageView road;
    ImageView village;
    ImageView city;
    ImageView progressCard;

    private OnButtonClickListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof OnButtonClickListener) {
            mListener = (OnButtonClickListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buttons_open, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        build = getActivity().findViewById(R.id.exit);
        road = getActivity().findViewById(R.id.road);
        village = getActivity().findViewById(R.id.village);
        city = getActivity().findViewById(R.id.city);
        progressCard = getActivity().findViewById(R.id.progressCard);

        build.setOnClickListener(v -> {
            Fragment newFragment = new ButtonsClosedFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.leftButtonsFragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        road.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.ROAD);
            setButtonBorders(v);

            // TODO: Make the player be able to place the road in possible locations and afterwards return the fragment "leftButtons" to "ButtonsClosedFragment".
            //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new ButtonsClosedFragment()).addToBackStack(null).commit();
        });

        village.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.VILLAGE);
            setButtonBorders(v);

            // TODO: Make the player be able to place the settlement in possible locations and afterwards return the fragment "leftButtons" to "ButtonsClosedFragment".
            //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new ButtonsClosedFragment()).addToBackStack(null).commit();
        });

        city.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.CITY);
            setButtonBorders(v);

            // TODO: Make the player be able to place the city in possible locations and afterwards return the fragment "leftButtons" to "ButtonsClosedFragment".
            //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new ButtonsClosedFragment()).addToBackStack(null).commit();
        });

        progressCard.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.PROGRESS_CARD);
            setButtonBorders(v);

            // TODO: Give the player a random progress card and afterwards return the fragment "leftButtons" to "ButtonsClosedFragment".
            //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new ButtonsClosedFragment()).addToBackStack(null).commit();
        });

    }

    private void setButtonBorders(View v) {
        village.setBackgroundResource(0);
        city.setBackgroundResource(0);
        road.setBackgroundResource(0);
        progressCard.setBackgroundResource(0);
        v.setBackgroundResource(R.drawable.button_clicked_border);
    }

}