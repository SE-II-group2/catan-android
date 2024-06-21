package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.GameActivity;
import com.group2.catan_android.R;
import com.group2.catan_android.adapter.DevelopmentCardListAdapter;
import com.group2.catan_android.adapter.OnDevelopmentCardClickListener;
import com.group2.catan_android.data.live.game.UseProgressCardDto;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.LocalPlayerViewModel;

import java.util.List;

public class PopUpFragment extends DialogFragment implements OnDevelopmentCardClickListener {

    private DevelopmentCardListAdapter devCardAdapter;
    private MoveMaker moveMaker;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.devCardRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        devCardAdapter = new DevelopmentCardListAdapter(this);
        recyclerView.setAdapter(devCardAdapter);
        LocalPlayerViewModel localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(LocalPlayerViewModel.initializer)).get(LocalPlayerViewModel.class);
        localPlayerViewModel.getPlayerMutableLiveData().observe(getViewLifecycleOwner(), player -> {
            if(player != null){
                updateAdapterWithPlayerData(player);
            }
        });
        moveMaker = MoveMaker.getInstance();
        return view;
    }
    private void updateAdapterWithPlayerData(Player player) {
        List<ProgressCardType> progressCards = player.getProgressCards();
        devCardAdapter.setItems(progressCards);
    }

    public void onDevelopmentCardClick(ProgressCardType cardType){
        switch(cardType) {
            case KNIGHT:
                makeAllRobbersVisible();
                break;
            case ROAD_BUILDING:
                try {
                    moveMaker.makeMove(new UseProgressCardDto(ProgressCardType.ROAD_BUILDING, null, null));
                } catch (Exception e) {
                    MessageBanner.makeBanner(getActivity(), MessageType.ERROR, e.getMessage()).show();
                }
                MessageBanner.makeBanner(getActivity(), MessageType.INFO, "Resources for two roads added!").show();
                closeFragment();
                break;
            case YEAR_OF_PLENTY:
                replaceFragment(new YearOfPlentyFragment());
                break;
            case MONOPOLY:
                replaceFragment(new MonopolyFragment());
                break;
            case VICTORY_POINT:
                try {
                    moveMaker.makeMove(new UseProgressCardDto(ProgressCardType.VICTORY_POINT, null, null));
                } catch (Exception e) {
                    MessageBanner.makeBanner(getActivity(), MessageType.ERROR, e.getMessage()).show();
                }
                MessageBanner.makeBanner(getActivity(), MessageType.INFO, "One Victory Point added!").show();
                closeFragment();
                break;
            default:
                break;
        }
    }

    private void closeFragment(){
        assert getActivity() != null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    private void replaceFragment(PopUpFragment fragment){
        assert  getActivity() != null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.show(fragmentManager, "popup_fragment");
        closeFragment();
    }

    private void makeAllRobbersVisible(){
        GameActivity gameActivity = (GameActivity) getActivity();
        assert gameActivity != null;
        gameActivity.makeAllRobberViewsClickableComingFromProgressCard();
        MessageBanner.makeBanner(getActivity(), MessageType.INFO, "Choose the robber position!").show();
        closeFragment();
    }
}
