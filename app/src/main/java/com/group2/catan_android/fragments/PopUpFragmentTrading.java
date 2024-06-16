package com.group2.catan_android.fragments;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.res.ColorStateList;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.group2.catan_android.R;
import com.group2.catan_android.data.live.game.TradeMoveDto;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.viewmodel.ActivePlayerViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


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
        setUp(view);
    }

    private Player localPlayer;
    private TradingResourceSelectionFragment giveResourceFragment;
    private TradingResourceSelectionFragment getResourceFragment;
    private PlayerResourcesFragment playerResources;
    public void setUp(View view){
        setFragments();
        ActivePlayerViewModel localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(ActivePlayerViewModel.initializer)).get(ActivePlayerViewModel.class);
        localPlayerViewModel.getPlayerMutableLiveData().observe(this, player -> {
            this.localPlayer = player;
            playerResources.setPlayer(localPlayer);
        });

        PlayerListViewModel playerListViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PlayerListViewModel.initializer)).get(PlayerListViewModel.class);
        playerListViewModel.getPlayerMutableLiveData().observe(this, data ->{
            List<Player> playerList = new ArrayList<>(data);
            playerList.sort(Comparator.comparingInt(Player::getInGameID));
            setButtonsAndListeners(view, playerList);
        });


    }
    public void setFragments(){
        this.giveResourceFragment = new TradingResourceSelectionFragment();
        this.getResourceFragment = new TradingResourceSelectionFragment();
        this.playerResources = new PlayerResourcesFragment();
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction().add(R.id.trading_popup_topfragment, giveResourceFragment).commitNow();
        manager.beginTransaction().add(R.id.trading_popup_middlefragment, getResourceFragment).commitNow();
        manager.beginTransaction().add(R.id.trading_popup_bottomfragment,playerResources).commitNow();
        manager.beginTransaction().add(R.id.trading_popup_time_selection, new TradingTimeSelection()).commitNow();//needed?
    }
    public void setButtonsAndListeners(View view, List<Player> playerListOriginal) {
        List<Player> playerList = new ArrayList<>(playerListOriginal);
        // get used Views
        List<Button> playerButtons = new ArrayList<>();
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p1));
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p2));
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p3));

        Button bank = view.findViewById(R.id.trading_popup_bank_button);
        Button confirm = view.findViewById(R.id.trading_popup_confirm);

        // set OnClickListeners
        int red = ContextCompat.getColor(view.getContext(), R.color.red);
        int green = ContextCompat.getColor(view.getContext(), R.color.GrassGreenHighlighted);
        //not including Buttons if there are no players for them
        while(playerList.size()-1<playerButtons.size()){
            playerButtons.get(playerButtons.size()-1).setVisibility(View.INVISIBLE);
            playerButtons.remove(playerButtons.size()-1);
        }

        for (int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getInGameID()==localPlayer.getInGameID()){
                playerList.remove(i);
                i--;
                continue;
            }
            int j = i;
            playerButtons.get(j).setText(playerList.get(j).getDisplayName());
            playerButtons.get(j).setOnClickListener(v -> {
                int buttonColor = playerButtons.get(j).getBackgroundTintList().getDefaultColor();
                if(buttonColor==red){
                    int bankColor = bank.getBackgroundTintList().getDefaultColor();
                    if(bankColor==green){
                        bank.setBackgroundTintList(ColorStateList.valueOf(red));
                    }
                    playerButtons.get(j).setBackgroundTintList(ColorStateList.valueOf(green));
                } else if (buttonColor==green) {
                    playerButtons.get(j).setBackgroundTintList(ColorStateList.valueOf(red));
                    if(areAllPlayerButtonsNotGreen(playerButtons, green)){ //=all are red
                        bank.setBackgroundTintList(ColorStateList.valueOf(green));
                    }
                }
            });
        }
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
        confirm.setOnClickListener(v -> {
            //toPlayer:
            int[] toPlayer = new int[playerButtons.size()]; //playerList.size();  ?
            if(bank.getBackgroundTintList().getDefaultColor()==green){
                Arrays.fill(toPlayer, -1);
            }else{
                for(int i=0;i<playerButtons.size();i++){
                    toPlayer[i] = (playerButtons.get(i).getBackgroundTintList().getDefaultColor()==green) ? playerList.get(i).getInGameID() : -1;
                }
            }
            // check invalid Input:
            int[] giveResources = giveResourceFragment.getSetResources();
            negate(giveResources);
            if(!localPlayer.resourcesSufficient(giveResources)){
                Toast.makeText(getContext(), "You do not have the selected give-resources", Toast.LENGTH_SHORT).show();
            }else {
                //could check bank-trade, but it is server duty
                try {
                    MoveMaker.getInstance().makeMove(new TradeMoveDto(giveResources, getResourceFragment.getSetResources(), toPlayer));
                    //close Tradingpopup?
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            // toPlayer ID = -1 -> nicht an ihn!
        });
    }
    public void setAllButtonsColor(List<Button> buttons, int color){
        for(Button b:buttons){
            b.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }
    public boolean areAllPlayerButtonsNotGreen(List<Button> playerButtons, int colorGreen){
        for(int i=0;i<playerButtons.size();i++){
            if(playerButtons.get(i).getBackgroundTintList().getDefaultColor()==colorGreen){
                return false;
            }
        }
        return true;
    }
    public void negate(int[] giveResources){
        for(int i=0;i<giveResources.length;i++){
            giveResources[i]=-giveResources[i];
        }
    }
}
