package com.group2.catan_android.fragments;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.util.Log;
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
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.LocalPlayerViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;
import com.group2.catan_android.viewmodel.TradeViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class TradeOfferFragment extends Fragment {
    private TradeOfferDto tradeOfferDto;
    private PlayerResourcesFragment getResourcesFragment;
    private PlayerResourcesFragment giveResourcesFragment;
    private TradeViewModel tradeViewModel;

    private TextView offerFromTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradeViewModel =  new ViewModelProvider(requireActivity(), ViewModelProvider.Factory.from(TradeViewModel.initializer)).get(TradeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trade_offer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null){
            giveResourcesFragment = new PlayerResourcesFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.trade_offer_give_fragment, giveResourcesFragment)
                    .commit();

            getResourcesFragment = new PlayerResourcesFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.trade_offer_get_fragment, getResourcesFragment)
                    .commit();
        } else {
            giveResourcesFragment = (PlayerResourcesFragment) getChildFragmentManager().findFragmentById(R.id.trade_offer_give_fragment);
            getResourcesFragment = (PlayerResourcesFragment) getChildFragmentManager().findFragmentById(R.id.trade_offer_get_fragment);
        }

        Button acceptButton = view.findViewById(R.id.trade_offer_accept);
        acceptButton.setOnClickListener(v -> accept());

        Button declineButton = view.findViewById(R.id.trade_offer_decline);
        declineButton.setOnClickListener(v -> closeFragment());

        offerFromTextView = view.findViewById(R.id.trade_offer_player_text);

        tradeViewModel.getTradeOfferDtoMutableLiveData().observe(getViewLifecycleOwner(), this::update);
    }

    public void update(TradeOfferDto dto){
        this.tradeOfferDto = dto;
        getResourcesFragment.updateResources(dto.getGetResources());
        giveResourcesFragment.updateResources(negateAllValues(dto.getGiveResources()));
        offerFromTextView.setText(dto.getFromPlayer().getDisplayName());
    }

    public void accept(){
        if(tradeOfferDto == null){
            MessageBanner.makeBanner(requireActivity(), MessageType.WARNING, "No Offer available").show();
        } else {
            try {
                MoveMaker.getInstance().makeMove(new AcceptMoveDto(tradeOfferDto), this::onServerError);
            } catch (Exception e){
                MessageBanner.makeBanner(requireActivity(), MessageType.ERROR, e.getMessage()).show();
            } finally {
                closeFragment();
            }
        }
    }

    private void closeFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(TradeOfferFragment.this);
        fragmentTransaction.commit();
    }
    private int[] negateAllValues(int[] input){
        int[] result = new int[input.length];
        for(int i=0;i<input.length;i++){
            result[i]=-input[i];
        }
        return result;
    }

    private void onServerError(Throwable t){
        MessageBanner.makeBanner(getActivity(), MessageType.ERROR, "SERVER: " + t.getMessage()).show();
        closeFragment();
    }
}