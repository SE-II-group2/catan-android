package com.group2.catan_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.group2.catan_android.R;

public class RobberConfirmationFragment extends Fragment {

    private TextView textField;
    private Button buttonAccept;
    private Button buttonDeny;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_robber_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textField = view.findViewById(R.id.robberConfirmationTextField);
        buttonAccept = view.findViewById(R.id.button_accept);
        buttonDeny = view.findViewById(R.id.button_deny);

        buttonAccept.setOnClickListener(v -> {
            // Handle the Accept button click
            textField.setText("Accepted");
            // You can add additional actions here, such as navigating to another fragment
        });

        buttonDeny.setOnClickListener(v -> {
            // Handle the Deny button click
            textField.setText("Denied");
            // You can add additional actions here, such as navigating to another fragment
        });
    }
}
