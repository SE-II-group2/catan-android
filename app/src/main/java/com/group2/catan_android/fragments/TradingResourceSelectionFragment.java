package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group2.catan_android.R;
import com.group2.catan_android.data.Constants;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.TradePopUpViewModel;
import com.group2.catan_android.viewmodel.TradeResourcesViewModel;

import java.util.Locale;


public class TradingResourceSelectionFragment extends Fragment {
    public static final int UI_KEY_WOOD = 0;
    public static final int UI_KEY_BRICK = 1;
    public static final int UI_KEY_SHEEP = 2;
    public static final int UI_KEY_WHEAT = 3;
    public static final int UI_KEY_STONE = 4;
    TextView[] count;
    private TradeResourcesViewModel tradeResourcesViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradeResourcesViewModel = new ViewModelProvider(this).get(TradeResourcesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trading_resource_selection, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPlusAndMinusListener(view);
    }

    public void setPlusAndMinusListener(View view){
        // get used Views
        ImageView[] plus = new ImageView[]{
                view.findViewById(R.id.resource_selection_wood_plus),
                view.findViewById(R.id.resource_selection_brick_plus),
                view.findViewById(R.id.resource_selection_sheep_plus),
                view.findViewById(R.id.resource_selection_wheat_plus),
                view.findViewById(R.id.resource_selection_stone_plus),
        };
        ImageView[] minus = {
                view.findViewById(R.id.resource_selection_wood_minus),
                view.findViewById(R.id.resource_selection_brick_minus),
                view.findViewById(R.id.resource_selection_sheep_minus),
                view.findViewById(R.id.resource_selection_wheat_minus),
                view.findViewById(R.id.resource_selection_stone_minus),
        };
        count = new TextView[]{
                view.findViewById(R.id.resource_selection_wood_count),
                view.findViewById(R.id.resource_selection_brick_count),
                view.findViewById(R.id.resource_selection_sheep_count),
                view.findViewById(R.id.resource_selection_wheat_count),
                view.findViewById(R.id.resource_selection_stone_count)
        };

        // set OnClickListeners
        plus[UI_KEY_WOOD].setOnClickListener(v -> tradeResourcesViewModel.increase(Constants.LOGIC_KEY_WOOD));
        plus[UI_KEY_BRICK].setOnClickListener(v -> tradeResourcesViewModel.increase(Constants.LOGIC_KEY_BRICK));
        plus[UI_KEY_SHEEP].setOnClickListener(v -> tradeResourcesViewModel.increase(Constants.LOGIC_KEY_SHEEP));
        plus[UI_KEY_WHEAT].setOnClickListener(v -> tradeResourcesViewModel.increase(Constants.LOGIC_KEY_WHEAT));
        plus[UI_KEY_STONE].setOnClickListener(v -> tradeResourcesViewModel.increase(Constants.LOGIC_KEY_STONE));

        minus[UI_KEY_WOOD].setOnClickListener(v -> tradeResourcesViewModel.decrease(Constants.LOGIC_KEY_WOOD));
        minus[UI_KEY_BRICK].setOnClickListener(v -> tradeResourcesViewModel.decrease(Constants.LOGIC_KEY_BRICK));
        minus[UI_KEY_SHEEP].setOnClickListener(v -> tradeResourcesViewModel.decrease(Constants.LOGIC_KEY_SHEEP));
        minus[UI_KEY_WHEAT].setOnClickListener(v -> tradeResourcesViewModel.decrease(Constants.LOGIC_KEY_WHEAT));
        minus[UI_KEY_STONE].setOnClickListener(v -> tradeResourcesViewModel.decrease(Constants.LOGIC_KEY_STONE));

        //Observe Viewmodel
        tradeResourcesViewModel.getResourceLiveData().observe(getViewLifecycleOwner(), resources -> {
            count[UI_KEY_WOOD].setText(String.format(Locale.getDefault(), "%d", resources[Constants.LOGIC_KEY_WOOD]));
            count[UI_KEY_BRICK].setText(String.format(Locale.getDefault(), "%d", resources[Constants.LOGIC_KEY_BRICK]));
            count[UI_KEY_SHEEP].setText(String.format(Locale.getDefault(), "%d", resources[Constants.LOGIC_KEY_SHEEP]));
            count[UI_KEY_WHEAT].setText(String.format(Locale.getDefault(), "%d", resources[Constants.LOGIC_KEY_WHEAT]));
            count[UI_KEY_STONE].setText(String.format(Locale.getDefault(), "%d", resources[Constants.LOGIC_KEY_STONE]));
        });
    }
    public int[] getSetResources(){
        return tradeResourcesViewModel.getResources();
    }
}