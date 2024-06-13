package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.R;
import com.group2.catan_android.adapter.DevelopmentCardListAdapter;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.viewmodel.ActivePlayerViewModel;

import java.util.List;

public class PopUpFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private DevelopmentCardListAdapter devCardAdapter;
    ActivePlayerViewModel localPlayerViewModel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up, container, false);
        recyclerView = view.findViewById(R.id.devCardRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        devCardAdapter = new DevelopmentCardListAdapter();
        recyclerView.setAdapter(devCardAdapter);
        localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(ActivePlayerViewModel.initializer)).get(ActivePlayerViewModel.class);
        localPlayerViewModel.getPlayerMutableLiveData().observe(getViewLifecycleOwner(), player -> {
            if(player != null){
                updateAdapterWithPlayerData(player);
            }
        });

        return view;
    }
    private void updateAdapterWithPlayerData(Player player) {
        List<ProgressCardType> progressCards = player.getProgressCards();
        devCardAdapter.setItems(progressCards);
    }
}
