package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.group2.catan_android.GameActivity;
import com.group2.catan_android.R;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.fragments.interfaces.FragmentSwitcher;
import com.group2.catan_android.fragments.interfaces.OnButtonClickListener;
import com.group2.catan_android.fragments.interfaces.OnButtonEventListener;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
public class ButtonsClosedFragment extends Fragment implements OnButtonEventListener {

    ImageView build;
    ImageView trade;
    ImageView cards;
    ImageView help;

    private OnButtonClickListener mListener;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buttons_closed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        build = getActivity().findViewById(R.id.build);
        trade = getActivity().findViewById(R.id.trade);
        cards = getActivity().findViewById(R.id.card_usage);
        help = getActivity().findViewById(R.id.help);

        build.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.BUILD);
            if(mFragmentSwitcher != null){
                mFragmentSwitcher.onSwitchButtonPressed();
            }
        });

        trade.setOnClickListener(v -> {
            PopUpFragmentTrading trading = new PopUpFragmentTrading();
            trading.setServerErrorContext(getActivity());
            trading.show(getActivity().getSupportFragmentManager(), "popup_fragment_trading");
        });

        cards.setOnClickListener(v -> {
            PopUpFragment popUpFragment = new PopUpFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            popUpFragment.show(fragmentManager, "popup_fragment");
        });

        help.setOnClickListener(v -> {
            mListener.onButtonClicked(ButtonType.HELP);
        });

        viewsCreated = true;
    }

    public void setFragmentSwitcher(FragmentSwitcher switcher){
        mFragmentSwitcher = switcher;
    }
    @Override
    public void onButtonEvent(ButtonType button) {
        cards.setBackgroundResource(0);
        trade.setBackgroundResource(0);
    }

    public void makeButtonsUnclickable(){
        if(viewsCreated) {
            build.setClickable(false);
            cards.setClickable(false);
            help.setClickable(false);
            trade.setClickable(false);
        }
    }

    public void makeButtonsClickable(){
        if(viewsCreated) {
            build.setClickable(true);
            cards.setClickable(true);
            help.setClickable(true);
            trade.setClickable(true);
        }
    }
}