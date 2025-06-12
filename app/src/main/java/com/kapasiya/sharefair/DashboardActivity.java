package com.kapasiya.sharefair;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kapasiya.sharefair.fragments.BillsFragment;
import com.kapasiya.sharefair.fragments.FriendsFragment;
import com.kapasiya.sharefair.fragments.GroupsFragment;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout accountButton;
    private LinearLayout premiumButton;
    private LinearLayout activityButton;
    private CardView balanceCard;
    private CardView expenseManagementCard;
    private LinearLayout createNewGroup;
    private FloatingActionButton fabAdd;
    private View mainScrollView;
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

        // Setup back press handler
        setupBackPressHandler();
    }

    private void initViews() {
        // Top navigation buttons
        accountButton = findViewById(R.id.accountButton);
        premiumButton = findViewById(R.id.premiumButton);
        activityButton = findViewById(R.id.activityButton);

        // Card views
        balanceCard = findViewById(R.id.balanceCard);
        expenseManagementCard = findViewById(R.id.expenseManagementCard);

        // Group related views
        createNewGroup = findViewById(R.id.createNewGroup);

        // Bottom navigation and FAB
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fabAdd = findViewById(R.id.fabAdd);

        // Main dashboard content containers
        mainScrollView = findViewById(R.id.mainScrollView);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupListeners() {
        // Top navigation listeners
        accountButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardActivity.this, "Opening Account", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(DashboardActivity.this, "Account feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        premiumButton.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Premium features", Toast.LENGTH_SHORT).show();
            // You can add premium activity navigation here
        });

        activityButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardActivity.this, "Opening Activity", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(DashboardActivity.this, "Activity feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Card view listeners
        balanceCard.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Balance details", Toast.LENGTH_SHORT).show();
            // You can navigate to balance details activity here
        });

        expenseManagementCard.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Starting Personal Expense Management", Toast.LENGTH_SHORT).show();
            // You can navigate to expense management activity here
        });

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
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Add new expense", Toast.LENGTH_SHORT).show();
            // You can open add expense dialog or activity here
        });
    }

    private void showCreateGroupDialog() {
        try {
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
            btnCancel.setOnClickListener(v -> createGroupDialog.dismiss());

            // Create button listener
            btnCreate.setOnClickListener(v -> {
                String groupName = etGroupName.getText().toString().trim();

                if (groupName.isEmpty()) {
                    Toast.makeText(DashboardActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Handle group creation here
                createGroup(groupName, selectedGroupType);
                createGroupDialog.dismiss();
            });

            // Show dialog
            createGroupDialog.show();

        } catch (Exception e) {
            Toast.makeText(this, "Error opening create group dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void createGroup(String groupName, String groupType) {
        // Handle group creation logic here
        // You can save to database, update UI, etc.
        Toast.makeText(this, "Group '" + groupName + "' created as " + groupType + " type",
                Toast.LENGTH_LONG).show();

        // Optional: Navigate to groups fragment to show the new group
        bottomNavigationView.setSelectedItemId(R.id.navigation_groups);
        loadFragment(new GroupsFragment());
    }

    private void updateGroupTypeSelection(LinearLayout groupHome, LinearLayout groupTrip,
                                          LinearLayout groupPersonal, LinearLayout groupOther) {
        try {
            // Reset all backgrounds to unselected state
            groupHome.setBackgroundResource(R.drawable.group_type_selector);
            groupTrip.setBackgroundResource(R.drawable.group_type_selector);
            groupPersonal.setBackgroundResource(R.drawable.group_type_selector);
            groupOther.setBackgroundResource(R.drawable.group_type_selector);

            // Set selected background
            switch (selectedGroupType) {
                case "Home":
                    groupHome.setBackgroundResource(R.drawable.group_type_selected);
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
        } catch (Exception e) {
            // Fallback: use default android backgrounds if custom drawables don't exist
            groupHome.setBackgroundColor(selectedGroupType.equals("Home") ?
                    getResources().getColor(android.R.color.holo_blue_light) :
                    getResources().getColor(android.R.color.transparent));
            groupTrip.setBackgroundColor(selectedGroupType.equals("Trip") ?
                    getResources().getColor(android.R.color.holo_blue_light) :
                    getResources().getColor(android.R.color.transparent));
            groupPersonal.setBackgroundColor(selectedGroupType.equals("Personal") ?
                    getResources().getColor(android.R.color.holo_blue_light) :
                    getResources().getColor(android.R.color.transparent));
            groupOther.setBackgroundColor(selectedGroupType.equals("Other") ?
                    getResources().getColor(android.R.color.holo_blue_light) :
                    getResources().getColor(android.R.color.transparent));
        }
    }

    private void showMainContent() {
        try {
            // Clear any existing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if (currentFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(currentFragment);
                transaction.commitAllowingStateLoss();
            }

            // Hide fragment container and show main content
            if (mainScrollView != null) {
                mainScrollView.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            Toast.makeText(this, "Error showing main content", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(Fragment fragment) {
        try {

            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.VISIBLE);
            }

            // Load the fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading fragment", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

                    // If there's a fragment currently displayed, go back to home
                    if (currentFragment != null && fragmentContainer.getVisibility() == View.VISIBLE) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        showMainContent();
                    } else {
                        // If on main screen, exit app
                        finish();
                    }
                } catch (Exception e) {
                    // Fallback to default behavior
                    finish();
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
            createGroupDialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dismiss dialog if activity is paused
        if (createGroupDialog != null && createGroupDialog.isShowing()) {
            createGroupDialog.dismiss();
        }
    }
}