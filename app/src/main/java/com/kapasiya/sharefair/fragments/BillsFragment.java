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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kapasiya.sharefair.R;
import com.kapasiya.sharefair.adapters.BillAdapter;
import com.kapasiya.sharefair.dtos.BillItems;

import java.util.ArrayList;
import java.util.List;

public class BillsFragment extends Fragment {

    private Button priorityButton;
    private Button groupButton;
    private Button allButton;
    private Button etcButton;
    private Button additionalButton1;
    private Button additionalButton2;
    private RecyclerView recyclerView;
    private BillAdapter adapter;
    private List<BillItems> billList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        initBillData();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_bills);
        groupButton = view.findViewById(R.id.groupButton);
        allButton = view.findViewById(R.id.allButton);
        priorityButton = view.findViewById(R.id.priorityButton);
        etcButton = view.findViewById(R.id.etcButton);
        additionalButton1 = view.findViewById(R.id.additionalButton1);
        additionalButton2 = view.findViewById(R.id.additionalButton2);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        billList = new ArrayList<>();
        adapter = new BillAdapter(billList);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {

        allButton.setOnClickListener(v -> {
            updateButtonSelection(allButton);
            filterBills("all");
            Toast.makeText(getContext(), "All clicked", Toast.LENGTH_SHORT).show();
        });

        priorityButton.setOnClickListener(v -> {
            updateButtonSelection(priorityButton);
            filterBills("priority");
            Toast.makeText(getContext(), "Priority clicked", Toast.LENGTH_SHORT).show();
        });

        groupButton.setOnClickListener(v -> {
            updateButtonSelection(groupButton);
            filterBills("group");
            Toast.makeText(getContext(), "Group clicked", Toast.LENGTH_SHORT).show();
        });

        etcButton.setOnClickListener(v -> {
            updateButtonSelection(etcButton);
            filterBills("etc");
            Toast.makeText(getContext(), "ETC clicked", Toast.LENGTH_SHORT).show();
        });

        additionalButton1.setOnClickListener(v -> {
            updateButtonSelection(additionalButton1);
            filterBills("filter1");
            Toast.makeText(getContext(), "Filter1 clicked", Toast.LENGTH_SHORT).show();
        });

        additionalButton2.setOnClickListener(v -> {
            updateButtonSelection(additionalButton2);
            filterBills("filter2");
            Toast.makeText(getContext(), "Filter2 clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateButtonSelection(Button selectedButton) {
        // Reset all buttons to default state
        resetButtonState(allButton);
        resetButtonState(priorityButton);
        resetButtonState(groupButton);
        resetButtonState(etcButton);
        resetButtonState(additionalButton1);
        resetButtonState(additionalButton2);

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
            // Use your app's primary color or customize as needed
            button.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_blue_bright));
            button.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void filterBills(String filterType) {
        // For now, show all bills regardless of filter
        // You can implement actual filtering logic here based on your requirements
        initBillData();
        adapter.notifyDataSetChanged();
    }

    private void initBillData() {
        billList.clear();

        // Add sample bill data
        billList.add(new BillItems(
                "29 May    Non Group",
                "Restaurant Bill",
                "You paid ₹150",
                "You get",
                "₹75"
        ));

        billList.add(new BillItems(
                "28 May    Group",
                "Grocery Shopping",
                "You paid ₹200",
                "You owe",
                "₹50"
        ));

        billList.add(new BillItems(
                "27 May    Non Group",
                "Movie Tickets",
                "You paid ₹300",
                "You get",
                "₹150"
        ));

        billList.add(new BillItems(
                "26 May    Group",
                "Taxi Fare",
                "You paid ₹80",
                "You get",
                "₹20"
        ));

        billList.add(new BillItems(
                "25 May    Non Group",
                "Coffee Shop",
                "You paid ₹120",
                "You owe",
                "₹60"
        ));

        billList.add(new BillItems(
                "24 May    Group",
                "Gas Bill",
                "You paid ₹500",
                "You get",
                "₹250"
        ));

        billList.add(new BillItems(
                "23 May    Non Group",
                "Online Shopping",
                "You paid ₹400",
                "You get",
                "₹200"
        ));

        billList.add(new BillItems(
                "22 May    Group",
                "Dinner Party",
                "You paid ₹600",
                "You owe",
                "₹100"
        ));

        // Notify adapter that data has changed
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}