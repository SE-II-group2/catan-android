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
import com.group2.catan_android.viewmodel.TradePopUpViewModel;
import com.group2.catan_android.viewmodel.TradeResourcesViewModel;


public class TradingResourceSelectionFragment extends Fragment {
    TextView[] count;
    private TradeResourcesViewModel tradeResourcesViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tradeResourcesViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(TradeResourcesViewModel.initializer)).get(TradeResourcesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trading_resource_selection, container, false);
    }
    // fixme extract methods
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
                int num = getNumberofTextView(count[j]);
                if (num == -1) {
                    return;
                }//Error
                count[j].setText(Integer.toString(++num));
            });
            minus[j].setOnClickListener(v -> {
                int num = getNumberofTextView(count[j]);
                if (num == -1) {
                    return;
                }//Error
                num--;
                if (num < 0) {
                    Toast.makeText(getContext(), "clicked resource can not be less than zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                count[j].setText(Integer.toString(num));
            });
        }
    }

    public int getNumberofTextView(TextView view){
        CharSequence content = view.getText();
        if(content==null){
            Toast.makeText(getContext(), "clicked resource does not have a count! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        String num = content.toString();
        if(num.isEmpty()){
            Toast.makeText(getContext(), "clicked resource increasing did not work! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        try {
            return Integer.parseInt(num);
        }catch(NumberFormatException e){
            Toast.makeText(getContext(), "clicked resource parsing failed! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }
    public int[] getSetResources(){
        int[] resources = new int[5];
        resources[2]=getNumberofTextView(count[0]);
        resources[3]=getNumberofTextView(count[1]);
        resources[1]=getNumberofTextView(count[2]);
        resources[0]=getNumberofTextView(count[3]);
        resources[4]=getNumberofTextView(count[4]);
        return resources;
    }
}