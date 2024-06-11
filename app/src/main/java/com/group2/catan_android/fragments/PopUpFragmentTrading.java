package com.group2.catan_android.fragments;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.group2.catan_android.R;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.TradeMoveDto;
import com.group2.catan_android.data.service.MoveMaker;


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

        setButtonsListeners(view);

        view.findViewById(R.id.trading_popup_confirm).setOnClickListener(v -> {
            int[] giveresources = {0,0,0,0,0};
            int[] getresources = {0,0,0,0,0};
            boolean[] toPlayer = {true, true, true, true};
            try {
                MoveMaker.getInstance().makeMove(new TradeMoveDto(giveresources, getresources, toPlayer));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }
    public void setButtonsListeners(View view) {
        // get used Views
        Button[] playerButtons = new Button[]{
                view.findViewById(R.id.trading_popup_button_p1),
                view.findViewById(R.id.trading_popup_button_p2),
                view.findViewById(R.id.trading_popup_button_p3),
                view.findViewById(R.id.trading_popup_button_p4)
        };
        Button bank = view.findViewById(R.id.trading_popup_bank_button);
        Button confirm = view.findViewById(R.id.trading_popup_confirm);
        // set OnClickListeners


        int red = ContextCompat.getColor(view.getContext(), R.color.red);
        int green = ContextCompat.getColor(view.getContext(), R.color.GrassGreenHighlighted);
        for (int i = 0; i < playerButtons.length; i++) {
            final int j = i;
            playerButtons[j].setOnClickListener(v -> {
                int buttonColor = playerButtons[j].getBackgroundTintList().getDefaultColor();
                if(buttonColor==red){
                    int bankColor = bank.getBackgroundTintList().getDefaultColor();
                    if(bankColor==green){
                        bank.setBackgroundTintList(ColorStateList.valueOf(red));
                    }
                    playerButtons[j].setBackgroundTintList(ColorStateList.valueOf(green));
                } else if (buttonColor==green) {
                    //maybe that if all are set red bank automatically green?
                    playerButtons[j].setBackgroundTintList(ColorStateList.valueOf(red));
                }else{
                    return;
                }
            });
            bank.setOnClickListener(v -> {
                int bankColor = bank.getBackgroundTintList().getDefaultColor();
                if(bankColor==red){
                    setAllButtonsColor(playerButtons, red);
                    bank.setBackgroundTintList(ColorStateList.valueOf(green));
                } else if (bankColor==green) {
                    setAllButtonsColor(playerButtons, green);
                    bank.setBackgroundTintList(ColorStateList.valueOf(red));
                }
            });
        }

    }
    public void setAllButtonsColor(Button[] buttons, int color){
        for(Button b:buttons){
            b.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }
}
