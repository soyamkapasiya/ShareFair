package com.kapasiya.sharefair;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kapasiya.sharefair.fragments.BillsFragment;
import com.kapasiya.sharefair.fragments.GroupsFragment;
import com.kapasiya.sharefair.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout accountButton, premiumButton, activityButton;
    private CardView balanceCard, draftsPromoCard, expenseManagementCard;
    private LinearLayout createNewGroup;
    private FloatingActionButton fabAdd;
    private LinearLayout mainDashboardContent;
    private FrameLayout fragmentContainer;

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
    }

    private void initViews() {
        // Top navigation buttons
        accountButton = findViewById(R.id.accountButton);
        premiumButton = findViewById(R.id.premiumButton);
        activityButton = findViewById(R.id.activityButton);

        // Card views
        balanceCard = findViewById(R.id.balanceCard);
        draftsPromoCard = findViewById(R.id.draftsPromoCard);
        expenseManagementCard = findViewById(R.id.expenseManagementCard);

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
        accountButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Account button clicked", Toast.LENGTH_SHORT).show());

        premiumButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Premium button clicked", Toast.LENGTH_SHORT).show());

        activityButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Activity button clicked", Toast.LENGTH_SHORT).show());

        // Card view listeners
        balanceCard.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Balance details", Toast.LENGTH_SHORT).show());

        draftsPromoCard.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Try Drafts feature", Toast.LENGTH_SHORT).show());

        expenseManagementCard.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Start expense management", Toast.LENGTH_SHORT).show());

        // Group related listeners
        createNewGroup.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Create new group", Toast.LENGTH_SHORT).show());

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
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        // FAB listener
        fabAdd.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Add new expense/transaction", Toast.LENGTH_SHORT).show());
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

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // If there's a fragment currently displayed, go back to home
        if (currentFragment != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            showMainContent();
        } else {
            super.onBackPressed();
        }
    }
}