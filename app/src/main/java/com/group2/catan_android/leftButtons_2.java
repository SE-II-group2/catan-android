package com.group2.catan_android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentTransition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link leftButtons_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class leftButtons_2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public leftButtons_2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment leftButtons_2.
     */
    // TODO: Rename and change types and number of parameters
    public static leftButtons_2 newInstance(String param1, String param2) {
        leftButtons_2 fragment = new leftButtons_2();
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

        return inflater.inflate(R.layout.fragment_left_buttons_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView build = getActivity().findViewById(R.id.exit);
        build.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment newFragment = new leftButtons_1();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.leftButtons, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        ImageView street = getActivity().findViewById(R.id.street);
        street.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /* build street button */
                // TODO: Make the player be able to place the street in possible locations and afterwards return the fragment "leftButtons" to "leftButtons_1".
                //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new leftButtons_1()).addToBackStack(null).commit();
            }
        });
        ImageView settlement = getActivity().findViewById(R.id.settlement);
        settlement.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /* build settlement button */
                // TODO: Make the player be able to place the settlement in possible locations and afterwards return the fragment "leftButtons" to "leftButtons_1".
                //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new leftButtons_1()).addToBackStack(null).commit();
            }
        });
        ImageView city = getActivity().findViewById(R.id.city);
        city.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /* build city button */
                // TODO: Make the player be able to place the city in possible locations and afterwards return the fragment "leftButtons" to "leftButtons_1".
                //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new leftButtons_1()).addToBackStack(null).commit();
            }
        });
        ImageView developmentcard = getActivity().findViewById(R.id.developmentcard);
        developmentcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Give the player a random development card and afterwards return the fragment "leftButtons" to "leftButtons_1".
                //getParentFragmentManager().beginTransaction().replace(R.id.leftButtons, new leftButtons_1()).addToBackStack(null).commit();
            }
        });
    }

}