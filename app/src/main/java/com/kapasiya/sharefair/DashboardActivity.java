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
import androidx.core.content.ContextCompat;
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
    private ScrollView mainScrollView;
    private Dialog createGroupDialog;
    private String selectedGroupType = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        initViews();

        setupListeners();

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        showMainContent();

        setupBackPressHandler();
    }

    private void initViews() {
        FrameLayout fragmentContainer;
        accountButton = findViewById(R.id.accountButton);
        premiumButton = findViewById(R.id.premiumButton);
        activityButton = findViewById(R.id.activityButton);

        balanceCard = findViewById(R.id.balanceCard);
        expenseManagementCard = findViewById(R.id.expenseManagementCard);

        createNewGroup = findViewById(R.id.createNewGroup);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fabAdd = findViewById(R.id.fabAdd);

        mainScrollView = findViewById(R.id.mainScrollView);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupListeners() {
        accountButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardActivity.this, "Opening Account", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(DashboardActivity.this, "Account feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        premiumButton.setOnClickListener(v -> Toast.makeText(DashboardActivity.this, "Premium features", Toast.LENGTH_SHORT).show());

        activityButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardActivity.this, "Opening Activity", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(DashboardActivity.this, "Activity feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        balanceCard.setOnClickListener(v -> Toast.makeText(DashboardActivity.this, "Balance details", Toast.LENGTH_SHORT).show());

        expenseManagementCard.setOnClickListener(v -> Toast.makeText(DashboardActivity.this, "Starting Personal Expense Management", Toast.LENGTH_SHORT).show());

        createNewGroup.setOnClickListener(v -> showCreateGroupDialog());

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

        fabAdd.setOnClickListener(v -> Toast.makeText(DashboardActivity.this, "Add new expense", Toast.LENGTH_SHORT).show());
    }

    private void showCreateGroupDialog() {
        try {
            createGroupDialog = new Dialog(this);
            createGroupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_group, null);
            createGroupDialog.setContentView(dialogView);

            if (createGroupDialog.getWindow() != null) {
                createGroupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = (int) (displayMetrics.widthPixels * 0.9);
                createGroupDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            EditText etGroupName = dialogView.findViewById(R.id.etGroupName);
            LinearLayout groupHome = dialogView.findViewById(R.id.groupHome);
            LinearLayout groupTrip = dialogView.findViewById(R.id.groupTrip);
            LinearLayout groupPersonal = dialogView.findViewById(R.id.groupPersonal);
            LinearLayout groupOther = dialogView.findViewById(R.id.groupOther);
            TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
            TextView btnCreate = dialogView.findViewById(R.id.btnCreate);

            selectedGroupType = "Home";
            updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);

            groupHome.setOnClickListener(v -> {
                selectedGroupType = "Home";
                updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
            });

            groupTrip.setOnClickListener(v -> {
                selectedGroupType = "Trip";
                updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
            });

            groupPersonal.setOnClickListener(v -> {
                selectedGroupType = getString(R.string.personal);
                updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
            });

            groupOther.setOnClickListener(v -> {
                selectedGroupType = getString(R.string.other);
                updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
            });

            btnCancel.setOnClickListener(v -> createGroupDialog.dismiss());

            btnCreate.setOnClickListener(v -> {
                String groupName = etGroupName.getText().toString().trim();

                if (groupName.isEmpty()) {
                    Toast.makeText(DashboardActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                createGroup(groupName, selectedGroupType);
                createGroupDialog.dismiss();
            });

            createGroupDialog.show();

        } catch (Exception e) {
            Toast.makeText(this, "Error opening create group dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void createGroup(String groupName, String groupType) {
        Toast.makeText(this, "Group '" + groupName + "' created as " + groupType + " type",
                Toast.LENGTH_LONG).show();

        bottomNavigationView.setSelectedItemId(R.id.navigation_groups);
        loadFragment(new GroupsFragment());
    }

    private void updateGroupTypeSelection(LinearLayout groupHome, LinearLayout groupTrip,
                                          LinearLayout groupPersonal, LinearLayout groupOther) {
        try {
            groupHome.setBackgroundResource(R.drawable.group_type_selector);
            groupTrip.setBackgroundResource(R.drawable.group_type_selector);
            groupPersonal.setBackgroundResource(R.drawable.group_type_selector);
            groupOther.setBackgroundResource(R.drawable.group_type_selector);

            switch (selectedGroupType) {
                case "Trip":
                    groupTrip.setBackgroundResource(R.drawable.group_type_selected);
                    break;
                case "Personal":
                    groupPersonal.setBackgroundResource(R.drawable.group_type_selected);
                    break;
                case "Other":
                    groupOther.setBackgroundResource(R.drawable.group_type_selected);
                    break;
                default:
                    groupHome.setBackgroundResource(R.drawable.group_type_selected);
            }
        } catch (Exception e) {
            groupHome.setBackgroundColor(selectedGroupType.equals("Home") ?
                    ContextCompat.getColor(this, android.R.color.holo_blue_light) :
                    ContextCompat.getColor(this, android.R.color.transparent));
            groupTrip.setBackgroundColor(selectedGroupType.equals("Trip") ?
                    ContextCompat.getColor(this, android.R.color.holo_blue_light) :
                    ContextCompat.getColor(this, android.R.color.transparent));
            groupPersonal.setBackgroundColor(selectedGroupType.equals("Personal") ?
                    ContextCompat.getColor(this, android.R.color.holo_blue_light) :
                    ContextCompat.getColor(this, android.R.color.transparent));
            groupOther.setBackgroundColor(selectedGroupType.equals("Other") ?
                    ContextCompat.getColor(this, android.R.color.holo_blue_light) :
                    ContextCompat.getColor(this, android.R.color.transparent));
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

            // Show the main dashboard content (ScrollView) and hide fragment container
            if (mainScrollView != null) {
                mainScrollView.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error showing main content", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(Fragment fragment) {
        try {
            // Hide the main ScrollView when loading fragments
            if (mainScrollView != null) {
                mainScrollView.setVisibility(View.GONE);
            }

            // Load the fragment into the fragment container
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

                    // If we're showing a fragment and not the main content, go back to home
                    if (currentFragment != null && mainScrollView != null && mainScrollView.getVisibility() == View.GONE) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        showMainContent();
                    } else {
                        // Exit the app
                        finish();
                    }
                } catch (Exception e) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (createGroupDialog != null && createGroupDialog.isShowing()) {
            createGroupDialog.dismiss();
            createGroupDialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (createGroupDialog != null && createGroupDialog.isShowing()) {
            createGroupDialog.dismiss();
        }
    }
}