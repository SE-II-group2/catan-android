package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.R;
import com.group2.catan_android.adapter.DevelopmentCardListAdapter;

import java.util.Arrays;
import java.util.List;

public class PopUpFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private DevelopmentCardListAdapter devCardAdapter;
    private List<String> testItems = Arrays.asList("Knight", "VictoryPoint", "Road Building", "Year of Plenty", "Monopoly");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up, container, false);
        recyclerView = view.findViewById(R.id.devCardRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        devCardAdapter = new DevelopmentCardListAdapter(testItems);
        recyclerView.setAdapter(devCardAdapter);
        return view;
    }

}
