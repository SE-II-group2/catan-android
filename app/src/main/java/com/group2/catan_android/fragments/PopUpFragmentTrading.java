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
import com.group2.catan_android.data.model.SelectablePlayer;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.LocalPlayerViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;
import com.group2.catan_android.viewmodel.TradePopUpViewModel;

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
    private TradePopUpViewModel tradePopUpViewModel;
    private LocalPlayerViewModel localPlayerViewModel;
    private PlayerListViewModel playerListViewModel;
    private Player localPlayer;
    private TradingResourceSelectionFragment giveResourceFragment;
    private TradingResourceSelectionFragment getResourceFragment;
    private PlayerResourcesFragment playerResources;
    private List<Button> playerButtons;
    private Button bankButton;
    public void setUp(View view){
        setFragments();
        tradePopUpViewModel = new ViewModelProvider(requireActivity(), ViewModelProvider.Factory.from(TradePopUpViewModel.initializer)).get(TradePopUpViewModel.class);

        observeLiveData();
        playerButtons = new ArrayList<>();
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p1));
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p2));
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p3));
        for(int i=0;i<playerButtons.size();i++){
            final int j = i;
            playerButtons.get(i).setOnClickListener(v -> {
                tradePopUpViewModel.togglePlayer(j);
            });
        }
        bankButton = view.findViewById(R.id.trading_popup_bank_button);
        Button confirm = view.findViewById(R.id.trading_popup_confirm);


    }
    public void setFragments(){
        this.giveResourceFragment = new TradingResourceSelectionFragment();
        this.getResourceFragment = new TradingResourceSelectionFragment();
        this.playerResources = new PlayerResourcesFragment();
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction().add(R.id.trading_popup_topfragment, giveResourceFragment).commitNow();
        manager.beginTransaction().add(R.id.trading_popup_middlefragment, getResourceFragment).commitNow();
        manager.beginTransaction().add(R.id.trading_popup_bottomfragment,playerResources).commitNow();
    }
    private void observeLiveData(){
        tradePopUpViewModel.getSelectablePlayerMutableLiveData().observe(requireActivity(), this::updateButtons);
    }
    private void updateButtons(List<SelectablePlayer> selectablePlayers){
        boolean atLeastOneSelected = false;
        for(int i=0;i<selectablePlayers.size();i++){
            if(selectablePlayers.get(i).isSelected()){
                playerButtons.get(i).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.GrassGreenHighlighted)));
                atLeastOneSelected=true;
            }else{
                playerButtons.get(i).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.red)));
            }
        }
        if(atLeastOneSelected){
            bankButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.red)));
        }else{
            bankButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.GrassGreenHighlighted)));
        }
    }
    public void setButtonsAndListeners(View view, List<Player> playerListOriginal) {
        List<Player> playerList = new ArrayList<>(playerListOriginal);
        // get used View
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
            List<Integer> toPlayers = new ArrayList<>(); //playerList.size();  ?
            if(bank.getBackgroundTintList().getDefaultColor()==green){
                for(int i=0;i<playerButtons.size();i++){
                    toPlayers.add(-1);
                }
            }else{
                for(int i=0;i<playerButtons.size();i++){
                    toPlayers.add((playerButtons.get(i).getBackgroundTintList().getDefaultColor()==green) ? playerList.get(i).getInGameID() : -1);
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
                    MoveMaker.getInstance().makeMove(new TradeMoveDto(giveResources, getResourceFragment.getSetResources(), toPlayers), this::onServerError);
                    //close Tradingpopup?
                } catch (Exception e) {
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void onServerError(Throwable t){
        MessageBanner.makeBanner(getActivity(), MessageType.ERROR, "SERVER: " + t.getMessage()).show();
    }
}
