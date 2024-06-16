package com.group2.catan_android.fragments;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group2.catan_android.R;
import com.group2.catan_android.data.live.game.AcceptMoveDto;
import com.group2.catan_android.data.live.game.TradeMoveDto;
import com.group2.catan_android.data.live.game.TradeOfferDto;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.viewmodel.ActivePlayerViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;

import java.util.ArrayList;
import java.util.List;


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
    private TradeOfferDto tradeOfferDto;
    private PlayerResourcesFragment getResourcesFragment;
    private PlayerResourcesFragment giveResourcesFragment;

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
        getResourcesFragment=new PlayerResourcesFragment();
        giveResourcesFragment = new PlayerResourcesFragment();
        manager.beginTransaction().add(R.id.trade_offer_get_fragment, getResourcesFragment).commitNow();
        manager.beginTransaction().add(R.id.trade_offer_give_fragment, giveResourcesFragment).commitNow();
        if(tradeOfferDto!=null){
            updateResources(tradeOfferDto);
        }

        PlayerListViewModel playerListViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PlayerListViewModel.initializer)).get(PlayerListViewModel.class);
        playerListViewModel.getPlayerMutableLiveData().observe(getViewLifecycleOwner(), data ->{
            List<Player> playerList = new ArrayList<>(data);
            playerText.setText("Player "+findPlayerByID(tradeOfferDto.getPlayerID(), playerList).getDisplayName());
        });

        ActivePlayerViewModel localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(ActivePlayerViewModel.initializer)).get(ActivePlayerViewModel.class);
        accept.setOnClickListener(v -> {
            localPlayerViewModel.getPlayerMutableLiveData().observe(getViewLifecycleOwner(), player -> {
                if(!player.resourcesSufficient(tradeOfferDto.getGiveResources())){
                    Toast.makeText(getContext(), "You do not have the necessary resources", Toast.LENGTH_SHORT).show();
                    closeFragment();
                }else{
                    try {
                        MoveMaker.getInstance().makeMove(new AcceptMoveDto(tradeOfferDto));
                        closeFragment();
                        //close Tradingpopup?
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            });
        decline.setOnClickListener(v -> {
                closeFragment();
            });

    }
    public void updateResources(TradeOfferDto tradeOfferDto){
        getResourcesFragment.setPlayer(new Player("", 0, tradeOfferDto.getGetResources(), 0));
        giveResourcesFragment.setPlayer(new Player("", 0, negateAllValues(tradeOfferDto.getGiveResources()), 0));// or just make them positive?
    }
    public void setTradeOfferDto(TradeOfferDto tradeOfferDto){
        this.tradeOfferDto=tradeOfferDto;
        if (getView() != null) {
            updateResources(this.tradeOfferDto);
        }
    }
    private void closeFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(TradeOfferFragment.this);
        fragmentTransaction.commit();
    }
    private Player findPlayerByID(int id, List<Player> playerList){
        for(Player p:playerList){
            if(p.getInGameID()==id){
                return p;
            }
        }
        return null;
    }
    private int[] negateAllValues(int[] input){
        int[] result = new int[input.length];
        for(int i=0;i<input.length;i++){
            result[i]=-input[i];
        }
        return result;
    }

}