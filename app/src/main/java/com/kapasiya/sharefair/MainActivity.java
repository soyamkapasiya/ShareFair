package com.kapasiya.sharefair;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kapasiya.sharefair.fragments.BillsFragment;
import com.kapasiya.sharefair.fragments.FriendsFragment;
import com.kapasiya.sharefair.fragments.GroupsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout accountButton;
    private LinearLayout premiumButton;
    private LinearLayout activityButton;
    private CardView balanceCard;
    private LinearLayout createNewGroup;
    private FloatingActionButton fabAdd;
    private LinearLayout mainDashboardContent;
    private FrameLayout fragmentContainer;

    // Dialog related variables
    private Dialog createGroupDialog;
    private String selectedGroupType = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        // Initialize views
        initViews();

        // Setup listeners
        setupListeners();

        // Set default selection for bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Show main dashboard content by default
        showMainContent();

        setupBackPressHandler();
    }

    private void initViews() {
        // Top navigation buttons
        accountButton = findViewById(R.id.accountButton);
        premiumButton = findViewById(R.id.premiumButton);
        activityButton = findViewById(R.id.activityButton);

        // Card views
        balanceCard = findViewById(R.id.balanceCard);

        // Group related views
        createNewGroup = findViewById(R.id.createNewGroup);

        // Bottom navigation and FAB
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fabAdd = findViewById(R.id.fabAdd);

        // Main dashboard content container
        mainDashboardContent = findViewById(R.id.main);

        // Fragment container (should be different from main dashboard content)
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupListeners() {
        // Top navigation listeners
        accountButton.setOnClickListener(v -> {
            // Navigate to NextActivity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Account button clicked", Toast.LENGTH_SHORT).show();
        });

        premiumButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Premium button clicked", Toast.LENGTH_SHORT).show());

        activityButton.setOnClickListener(v -> {
            // Navigate to NextActivity
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Activity button clicked", Toast.LENGTH_SHORT).show();
        });

        // Card view listeners
        balanceCard.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Balance details", Toast.LENGTH_SHORT).show());

        // Group related listeners
        createNewGroup.setOnClickListener(v -> showCreateGroupDialog());

        // Bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                showMainContent();
                return true;
            } else if (itemId == R.id.navigation_bills) {
                loadFragment(new BillsFragment());
                return true;
            } else if (itemId == R.id.navigation_groups) {
                loadFragment(new GroupsFragment());
                return true;
            } else if (itemId == R.id.navigation_friends) {
                loadFragment(new FriendsFragment());
                return true;
            }
            return false;
        });

        // FAB listener
        fabAdd.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Add new expense/transaction", Toast.LENGTH_SHORT).show());
    }

    private void showCreateGroupDialog() {
        createGroupDialog = new Dialog(this);
        createGroupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_group, null);
        createGroupDialog.setContentView(dialogView);

        // Make dialog background transparent
        if (createGroupDialog.getWindow() != null) {
            createGroupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set dialog width to 90% of screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.9);
            createGroupDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Initialize dialog views
        EditText etGroupName = dialogView.findViewById(R.id.etGroupName);
        LinearLayout groupHome = dialogView.findViewById(R.id.groupHome);
        LinearLayout groupTrip = dialogView.findViewById(R.id.groupTrip);
        LinearLayout groupPersonal = dialogView.findViewById(R.id.groupPersonal);
        LinearLayout groupOther = dialogView.findViewById(R.id.groupOther);
        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView btnCreate = dialogView.findViewById(R.id.btnCreate);

        // Set default selection (Home)
        selectedGroupType = "Home";
        updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);

        // Group type selection listeners
        groupHome.setOnClickListener(v -> {
            selectedGroupType = "Home";
            updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
        });

        groupTrip.setOnClickListener(v -> {
            selectedGroupType = "Trip";
            updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
        });

        groupPersonal.setOnClickListener(v -> {
            selectedGroupType = "Personal";
            updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
        });

        groupOther.setOnClickListener(v -> {
            selectedGroupType = "Other";
            updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
        });

        // Cancel button listener
        btnCancel.setOnClickListener(v -> {
            createGroupDialog.dismiss();
        });

        // Create button listener
        btnCreate.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();

            if (groupName.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Handle group creation here
            createGroup(groupName, selectedGroupType);
            createGroupDialog.dismiss();
        });

        // Show dialog
        createGroupDialog.show();

    }

    private void createGroup(String groupName, String groupType) {
        // Handle group creation logic here
        // You can save to database, update UI, etc.

        Toast.makeText(this, "Group '" + groupName + "' created as " + groupType + " type",
                Toast.LENGTH_LONG).show();

    }

    private void updateGroupTypeSelection(LinearLayout groupHome, LinearLayout groupTrip,
                                          LinearLayout groupPersonal, LinearLayout groupOther) {
        // Reset all backgrounds to unselected state
        groupHome.setBackgroundResource(R.drawable.group_type_selector);
        groupTrip.setBackgroundResource(R.drawable.group_type_selector);
        groupPersonal.setBackgroundResource(R.drawable.group_type_selector);
        groupOther.setBackgroundResource(R.drawable.group_type_selector);

        // Set selected background (you need to create a selected state drawable)
        switch (selectedGroupType) {
            case "Home":
                groupHome.setBackgroundResource(R.drawable.group_type_selected); // Use proper background
                break;
            case "Trip":
                groupTrip.setBackgroundResource(R.drawable.group_type_selected);
                break;
            case "Personal":
                groupPersonal.setBackgroundResource(R.drawable.group_type_selected);
                break;
            case "Other":
                groupOther.setBackgroundResource(R.drawable.group_type_selected);
                break;
        }
    }

    private void showMainContent() {
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
        }

        if (mainDashboardContent != null) {
            mainDashboardContent.setVisibility(View.VISIBLE);
        }

        // Clear any existing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(currentFragment);
            transaction.commitAllowingStateLoss();
        }
    }

    private void loadFragment(Fragment fragment) {
        // Hide main dashboard content and show fragment container
        if (mainDashboardContent != null) {
            mainDashboardContent.setVisibility(View.GONE);
        }

        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
        }

        // Load the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Add this in your onCreate() method or wherever you initialize your activity
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

                // If there's a fragment currently displayed, go back to home
                if (currentFragment != null) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    showMainContent();
                } else {
                    // If you want to allow the default back behavior, call:
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up dialog
        if (createGroupDialog != null && createGroupDialog.isShowing()) {
            createGroupDialog.dismiss();
        }
    }
}