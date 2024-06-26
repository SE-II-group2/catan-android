package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group2.catan_android.R;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.fragments.interfaces.FragmentSwitcher;
import com.group2.catan_android.fragments.interfaces.OnButtonClickListener;
import com.group2.catan_android.fragments.interfaces.OnButtonEventListener;

public class ButtonsOpenFragment extends Fragment implements OnButtonEventListener {

    ImageView exit;
    ImageView road;
    ImageView village;
    ImageView city;
    ImageView progressCard;

    private OnButtonClickListener mListener;

    private ButtonType activeButton;

    private FragmentSwitcher mFragmentSwitcher;
    private boolean viewsCreated = false;

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
        exit = getActivity().findViewById(R.id.exit);
        road = getActivity().findViewById(R.id.road);
        village = getActivity().findViewById(R.id.village);
        city = getActivity().findViewById(R.id.city);
        progressCard = getActivity().findViewById(R.id.progressCard);

        exit.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.EXIT);
            if(mFragmentSwitcher != null){
                mFragmentSwitcher.onSwitchButtonPressed();
            }
        });

        road.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.ROAD);
        });

        village.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.VILLAGE);
        });

        city.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.CITY);
        });

        progressCard.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.PROGRESS_CARD);
        });

        viewsCreated = true;
    }

    @Override
    public void onButtonEvent(ButtonType button) {
        if(activeButton != button){
            village.setBackgroundResource(0);
            city.setBackgroundResource(0);
            road.setBackgroundResource(0);

            switch(button){
                case ROAD: road.setBackgroundResource(R.drawable.button_clicked_border); break;
                case VILLAGE: village.setBackgroundResource(R.drawable.button_clicked_border); break;
                case CITY: city.setBackgroundResource(R.drawable.button_clicked_border);break;
                default: //do nothing
            }

            activeButton = button;

        } else{
            village.setBackgroundResource(0);
            city.setBackgroundResource(0);
            road.setBackgroundResource(0);
            activeButton = null;
        }

    }

    public void setFragmentSwitcher(FragmentSwitcher switcher){
        mFragmentSwitcher = switcher;
    }
    public void makeButtonsClickable() {
        if(viewsCreated) {
            road.setClickable(true);
            village.setClickable(true);
            city.setClickable(true);
            progressCard.setClickable(true);
            exit.setClickable(true);
        }
    }

    public void makeButtonsUnclickable() {
        if(viewsCreated) {
            road.setClickable(false);
            village.setClickable(false);
            city.setClickable(false);
            progressCard.setClickable(false);
            exit.setClickable(false);
        }
    }
}