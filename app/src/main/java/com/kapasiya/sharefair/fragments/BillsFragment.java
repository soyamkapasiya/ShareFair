package com.kapasiya.sharefair.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kapasiya.sharefair.R;

public class BillsFragment extends Fragment {

    private Button draftButton;
    private Button priorityButton;
    private Button groupButton;
    private Button allButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set attachToRoot to false and use proper container
        View view = inflater.inflate(R.layout.activity_bills, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        draftButton = view.findViewById(R.id.draftButton);
        groupButton = view.findViewById(R.id.groupButton);
        allButton = view.findViewById(R.id.allButton);
        priorityButton = view.findViewById(R.id.priorityButton);
    }

    private void setupListeners() {
        draftButton.setOnClickListener(v -> {
            updateButtonSelection(draftButton);
            Toast.makeText(getContext(), "Draft clicked", Toast.LENGTH_SHORT).show();
        });

        allButton.setOnClickListener(v -> {
            updateButtonSelection(allButton);
            Toast.makeText(getContext(), "All clicked", Toast.LENGTH_SHORT).show();
        });

        priorityButton.setOnClickListener(v -> {
            updateButtonSelection(priorityButton);
            Toast.makeText(getContext(), "Priority clicked", Toast.LENGTH_SHORT).show();
        });

        groupButton.setOnClickListener(v -> {
            updateButtonSelection(groupButton);
            Toast.makeText(getContext(), "Group clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateButtonSelection(Button selectedButton) {
        // Reset all buttons to default state
        resetButtonState(draftButton);
        resetButtonState(allButton);
        resetButtonState(priorityButton);
        resetButtonState(groupButton);

        // Set selected button state
        setSelectedButtonState(selectedButton);
    }

    private void resetButtonState(Button button) {
        if (button != null) {
            button.setBackgroundTintList(getResources().getColorStateList(android.R.color.transparent));
            button.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void setSelectedButtonState(Button button) {
        if (button != null) {
            // You can customize these colors based on your theme
            button.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_blue_bright));
            button.setTextColor(getResources().getColor(android.R.color.white));
        }
    }
}