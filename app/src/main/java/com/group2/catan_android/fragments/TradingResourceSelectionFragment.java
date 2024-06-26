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
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.TradePopUpViewModel;
import com.group2.catan_android.viewmodel.TradeResourcesViewModel;


public class TradingResourceSelectionFragment extends Fragment {
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
        for(int i=0;i<plus.length;i++){
            final int j = i;
            plus[j].setOnClickListener(v -> {
                count[j].setText(Integer.toString(tradeResourcesViewModel.togglePlus(j)));
            });
            minus[j].setOnClickListener(v -> {
                int result= tradeResourcesViewModel.toggleMinus(j);
                if(result<=-1){
                    MessageBanner.makeBanner(requireActivity(), MessageType.ERROR, "Clicked Resource can not me less than zero!").show();
                }else {
                    count[j].setText(Integer.toString(result));
                }
            });
        }
    }
    public int[] getSetResources(){
        return tradeResourcesViewModel.getResources();
    }
}