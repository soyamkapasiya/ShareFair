package com.kapasiya.sharefair.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kapasiya.sharefair.R;

public class GroupsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        initGroupData();

        return view;
    }

    private void initViews(View view) {

    }

    private void setupRecyclerView() {

    }

    private void setupListeners() {

    }

    private void initGroupData() {

    }

}