package com.group2.catan_android.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import android.util.Log;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.group2.catan_android.R;
import com.group2.catan_android.data.live.game.MakeTradeOfferMoveDto;
import com.group2.catan_android.data.model.SelectablePlayer;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.LocalPlayerViewModel;
import com.group2.catan_android.viewmodel.TradePopUpViewModel;

import java.util.ArrayList;
import java.util.List;


public class PopUpFragmentTrading extends DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradePopUpViewModel = new ViewModelProvider(requireActivity(), ViewModelProvider.Factory.from(TradePopUpViewModel.initializer)).get(TradePopUpViewModel.class);
        localPlayerViewModel = new ViewModelProvider(requireActivity(), ViewModelProvider.Factory.from(LocalPlayerViewModel.initializer)).get(LocalPlayerViewModel.class);
    }

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
        params.height=ViewGroup.LayoutParams.MATCH_PARENT;
        params.horizontalMargin=one.getHeight();
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState==null){
            setNewFragments();
        }else {
            getOldFragments();
        }
        setUp(view);
    }
    private TradePopUpViewModel tradePopUpViewModel;
    private LocalPlayerViewModel localPlayerViewModel;
    private TradingResourceSelectionFragment giveResourceFragment;
    private TradingResourceSelectionFragment getResourceFragment;
    private PlayerResourcesFragment playerResources;
    private List<Button> playerButtons;
    private Button bankButton;

    private Context mServerErrorContext;
    public void setUp(View view){
        tradePopUpViewModel.selectAll();
        playerButtons = new ArrayList<>();
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p1));
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p2));
        playerButtons.add(view.findViewById(R.id.trading_popup_button_p3));
        for(int i=0;i<playerButtons.size();i++){
            final int j = i;
            playerButtons.get(i).setOnClickListener(v -> tradePopUpViewModel.togglePlayer(j));
        }

        bankButton = view.findViewById(R.id.trading_popup_bank_button);
        bankButton.setOnClickListener(v -> tradePopUpViewModel.deselectAll());

        Button confirm = view.findViewById(R.id.trading_popup_confirm);
        confirm.setOnClickListener(v -> confirm());


        observeLiveData();
    }
    public void setNewFragments(){
        FragmentManager manager = getChildFragmentManager();
        giveResourceFragment = new TradingResourceSelectionFragment();
        manager.beginTransaction()
                .replace(R.id.trading_popup_topfragment, giveResourceFragment)
                .commit();
        getResourceFragment = new TradingResourceSelectionFragment();
        manager.beginTransaction()
                .replace(R.id.trading_popup_middlefragment, getResourceFragment)
                .commit();
        playerResources = new PlayerResourcesFragment();
        manager.beginTransaction()
                .replace(R.id.trading_popup_bottomfragment, playerResources)
                .commit();
    }
    private void getOldFragments(){
        FragmentManager manager = getChildFragmentManager();
        giveResourceFragment = (TradingResourceSelectionFragment) manager.findFragmentById(R.id.trading_popup_topfragment);
        getResourceFragment = (TradingResourceSelectionFragment) manager.findFragmentById(R.id.trading_popup_middlefragment);
        playerResources = (PlayerResourcesFragment) manager.findFragmentById(R.id.trading_popup_bottomfragment);
    }
    private void observeLiveData(){
        tradePopUpViewModel.getSelectablePlayerMutableLiveData().observe(getViewLifecycleOwner(), this::updateButtons);
        localPlayerViewModel.getPlayerMutableLiveData().observe(getViewLifecycleOwner(), playerResources::updateResources);
    }
    private void updateButtons(List<SelectablePlayer> selectablePlayers){
        boolean atLeastOneSelected = false;
        for(Button b : playerButtons){
            b.setVisibility(View.INVISIBLE);
        }
        for(int i=0;i<selectablePlayers.size();i++){
            if(selectablePlayers.get(i).isSelected()){
                playerButtons.get(i).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.GrassGreenHighlighted)));
                playerButtons.get(i).setText(selectablePlayers.get(i).getPlayer().getDisplayName());
                atLeastOneSelected=true;
            }else{
                playerButtons.get(i).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.red)));
            }
            playerButtons.get(i).setVisibility(View.VISIBLE);
        }
        if(atLeastOneSelected){
            bankButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.red)));
        }else{
            bankButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.GrassGreenHighlighted)));
        }
    }
    public void confirm(){
        try {
            MoveMaker.getInstance().makeMove(new MakeTradeOfferMoveDto(giveResourceFragment.getSetResources(), getResourceFragment.getSetResources(), tradePopUpViewModel.getSelectedPlayerIds()), this::onServerError);
        } catch (Exception e){
            MessageBanner.makeBanner(requireActivity(), MessageType.ERROR, e.getMessage()).show();
        } finally {
            closeFragment();
        }
    }

    private void closeFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }


    private void onServerError(Throwable t){
        if(mServerErrorContext != null) {
            if (mServerErrorContext instanceof FragmentActivity)
                MessageBanner.makeBanner((FragmentActivity) mServerErrorContext, MessageType.ERROR, "SERVER: " + t.getMessage()).show();
            else
                Toast.makeText(mServerErrorContext, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e("Trading", "Uncaught serverError", t);
        }
    }

    public void setServerErrorContext(Context context){
        mServerErrorContext = context;
    }
}
