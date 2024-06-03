package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group2.catan_android.R;


// fixme format code!
public class TradingTimeSelection extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trading_time_selection, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPlusAndMinusListener(view);
    }

    public void setPlusAndMinusListener(View view){
        // get used Views
        TextView count = view.findViewById(R.id.trading_popup_time);
        ImageView plus = view.findViewById(R.id.trading_popup_time_plus);
        ImageView minus = view.findViewById(R.id.trading_popup_time_minus);
        // set OnClickListeners
        plus.setOnClickListener(v->{
            int num = getNumberofTextView(count);
            if(num==-1){return;}//Error
            String res = ++num +"s";
            count.setText(res);
        });
        minus.setOnClickListener(v->{
            int num = getNumberofTextView(count);
            if(num==-1){return;}//Error
            num--;
            if(num<1){
                Toast.makeText(getContext(), "clicked resource can not be less or equal to zero", Toast.LENGTH_SHORT).show();
                return;
            }
            String res = num+"s";
            count.setText(res);
        });
    }

    /**
     * returns the number of the given TextView and handles all errors that may occur by making a Toast
     * @param view the TextView that shows the selected time period
     * @return the number of the TextView without the "s" in the end
     *         -1 if an error occurred.
     */
    public int getNumberofTextView(TextView view){
        CharSequence content = view.getText();
        if(content==null){
            Toast.makeText(getContext(), "clicked resource does not have content! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        String text = content.toString();
        if(text.length()<2){
            Toast.makeText(getContext(), "clicked resource increasing did not work! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        String num=text.substring(0, text.length()-1);
        if(text.charAt(text.length()-1)!='s'){
            Toast.makeText(getContext(), "clicked resource does not have s at the end! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        try {
            return Integer.parseInt(num);
        }catch(NumberFormatException e){
            Toast.makeText(getContext(), "clicked resource parsing failed! ERROR!", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }
}