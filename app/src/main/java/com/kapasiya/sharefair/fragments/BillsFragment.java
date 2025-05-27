package com.kapasiya.sharefair.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.kapasiya.sharefair.R;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BillsFragment extends Fragment {

    private LinearLayout addBillButton, splitBillButton, scanReceiptButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bills, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        addBillButton = view.findViewById(R.id.addBillButton);
        splitBillButton = view.findViewById(R.id.splitBillButton);
        scanReceiptButton = view.findViewById(R.id.scanReceiptButton);
    }

    private void setupListeners() {
        addBillButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Add Bill clicked", Toast.LENGTH_SHORT).show());

        splitBillButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Split Bill clicked", Toast.LENGTH_SHORT).show());

        scanReceiptButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Scan Receipt clicked", Toast.LENGTH_SHORT).show());
    }
}