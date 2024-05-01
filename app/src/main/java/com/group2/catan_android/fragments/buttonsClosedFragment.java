package com.group2.catan_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.group2.catan_android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link buttonsClosedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class buttonsClosedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public buttonsClosedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment buttonsClosedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static buttonsClosedFragment newInstance(String param1, String param2) {
        buttonsClosedFragment fragment = new buttonsClosedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left_buttons_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView build = getActivity().findViewById(R.id.build);
        build.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment newFragment = new buttonsOpenFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.leftButtons, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        ImageView trade = getActivity().findViewById(R.id.trade);
        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Write what shall happen when pressing the trade button

            }
        });

        ImageView card_usage = getActivity().findViewById(R.id.card_usage);
        card_usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Write what shall happen when pressing the development card usage button

            }
        });

        // TODO: Decide weather to have a fourth button on top
        /*
        ImageView extra_button = getActivity().findViewById(R.id.help);
        extra_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Write what shall happen when pressing the extra button

            }
        });
         */
    }
}