package com.kapasiya.sharefair;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kapasiya.sharefair.adapters.NotificationAdapter;
import com.kapasiya.sharefair.dtos.NotificationItems;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends Activity {

    private List<NotificationItems> notificationItems;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter; // You'll need to create this adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initNotificationData();
        setupRecyclerView();
        setupListeners();
    }

    private void setupListeners() {
        // Add your listeners here
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set adapter
        adapter = new NotificationAdapter(notificationItems);
        recyclerView.setAdapter(adapter);
    }

    private void initNotificationData() {
        // Initialize the list first
        notificationItems = new ArrayList<>();

        // Add sample notification data
        notificationItems.add(new NotificationItems(
                "21 May 2025 04:32",
                "Reminder: Meeting with Team"));

        notificationItems.add(new NotificationItems(
                "20 May 2025 14:15",
                "Payment received from John"));

        notificationItems.add(new NotificationItems(
                "20 May 2025 10:30",
                "New bill shared with you"));

        notificationItems.add(new NotificationItems(
                "19 May 2025 18:45",
                "Expense split updated"));

        notificationItems.add(new NotificationItems(
                "19 May 2025 12:20",
                "Reminder: Dinner bill pending"));

        notificationItems.add(new NotificationItems(
                "18 May 2025 16:00",
                "Payment request sent"));

        notificationItems.add(new NotificationItems(
                "18 May 2025 09:15",
                "Group expense added"));

        notificationItems.add(new NotificationItems(
                "17 May 2025 20:30",
                "Weekly summary available"));
    }
}