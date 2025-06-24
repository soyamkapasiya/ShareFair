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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.kapasiya.sharefair.adapters.GroupLogoAdapter;
import com.kapasiya.sharefair.dtos.Group;
import com.kapasiya.sharefair.dtos.GroupMember;
import com.kapasiya.sharefair.dtos.User;
import com.kapasiya.sharefair.dtos.UserGroup;
import com.kapasiya.sharefair.fragments.BillsFragment;
import com.kapasiya.sharefair.fragments.FriendsFragment;
import com.kapasiya.sharefair.fragments.GroupsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private User user;
    private GroupLogoAdapter adapter;
    private List<Group> groupLogos; // Fixed: Added proper generic type
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
    private FrameLayout fragmentContainer; // Fixed: Added private modifier
    private DatabaseReference usersRef;
    private DatabaseReference database;
    private ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance().getReference();
        usersRef = database.child("users");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        user = new User();

        initViews();
        setupListeners();

        if (firebaseUser != null) {
            loadUserDataRealTime();
        }

        setupRecyclerView();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        showMainContent();
        setupBackPressHandler();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.group_logo_recycler);
        groupLogos = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new GroupLogoAdapter(groupLogos);
        recyclerView.setAdapter(adapter);

        // Fetch groups from database
        fetchGroupsFromDatabase();
    }

    private void fetchGroupsFromDatabase() {
        if (firebaseUser == null) {
            return;
        }

        // First, get user's groups from their profile
        String userId = firebaseUser.getUid();
        DatabaseReference userGroupsRef = database.child("users").child(userId).child("groups");

        userGroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear existing data
                groupLogos.clear();
                adapter.notifyDataSetChanged();

                if (dataSnapshot.exists()) {
                    List<String> groupIds = new ArrayList<>();

                    // Collect all group IDs first
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        String groupId = groupSnapshot.getKey();
                        if (groupId != null) {
                            groupIds.add(groupId);
                        }
                    }

                    // Fetch details for all groups
                    if (!groupIds.isEmpty()) {
                        fetchMultipleGroupDetails(groupIds);
                    }
                } else {
                    // No groups found for user
                    showEmptyGroupsMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this,
                        "Failed to load groups: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMultipleGroupDetails(List<String> groupIds) {
        final int totalGroups = groupIds.size();
        final List<Group> fetchedGroups = new ArrayList<>();

        for (String groupId : groupIds) {
            DatabaseReference groupRef = database.child("groups").child(groupId);

            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Group group = dataSnapshot.getValue(Group.class);
                        if (group != null) {
                            fetchedGroups.add(group);
                        }
                    }

                    // Check if all groups have been fetched
                    if (fetchedGroups.size() == totalGroups) {
                        updateRecyclerViewWithGroups(fetchedGroups);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DashboardActivity.this,
                            "Failed to load group details: " + databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateRecyclerViewWithGroups(List<Group> fetchedGroups) {
        // Clear existing data and add new groups
        groupLogos.clear();
        groupLogos.addAll(fetchedGroups);

        // Sort groups by creation time (newest first) or by name
        groupLogos.sort((g1, g2) -> Long.compare(g2.getCreatedAt(), g1.getCreatedAt()));

        // Notify adapter of data changes
        adapter.notifyDataSetChanged();

        // Show success message if groups were loaded
        if (!groupLogos.isEmpty()) {
            Toast.makeText(DashboardActivity.this,
                    groupLogos.size() + " groups loaded",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmptyGroupsMessage() {
        // You can customize this method to show an empty state view
        Toast.makeText(DashboardActivity.this,
                "No groups found. Create your first group!",
                Toast.LENGTH_LONG).show();
    }

    // Method to refresh groups data
    public void refreshGroups() {
        if (adapter != null) {
            fetchGroupsFromDatabase();
        }
    }

    // Single group detail fetch (keeping for individual updates)
    private void fetchGroupDetails(String groupId) {
        DatabaseReference groupRef = database.child("groups").child(groupId);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (group != null) {
                        addOrUpdateGroupInRecyclerView(group);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this,
                        "Failed to load group details: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrUpdateGroupInRecyclerView(Group newGroup) {
        // Check if group already exists and update it
        for (int i = 0; i < groupLogos.size(); i++) {
            if (groupLogos.get(i).getGroupId().equals(newGroup.getGroupId())) {
                groupLogos.set(i, newGroup);
                adapter.notifyItemChanged(i);
                return;
            }
        }

        // If group doesn't exist, add it
        groupLogos.add(newGroup);
        adapter.notifyItemInserted(groupLogos.size() - 1);
    }

    // Alternative method: Fetch all groups (if you want to show all groups, not just user's groups)
    private void fetchAllGroupsFromDatabase() {
        DatabaseReference groupsRef = database.child("groups");

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupLogos.clear();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        // Optional: Filter groups based on user membership
                        if (isUserMemberOfGroup(group)) {
                            groupLogos.add(group);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this,
                        "Failed to load groups: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUserMemberOfGroup(Group group) {
        if (firebaseUser == null || group.getMembers() == null) {
            return false;
        }

        return group.getMembers().containsKey(firebaseUser.getUid());
    }

    private void initViews() {
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

        premiumButton.setOnClickListener(v ->
                Toast.makeText(DashboardActivity.this, "Premium features", Toast.LENGTH_SHORT).show());

        activityButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
                Toast.makeText(DashboardActivity.this, "Opening Activity", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(DashboardActivity.this, "Activity feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        balanceCard.setOnClickListener(v ->
                Toast.makeText(DashboardActivity.this, "Balance details", Toast.LENGTH_SHORT).show());

        expenseManagementCard.setOnClickListener(v ->
                Toast.makeText(DashboardActivity.this, "Starting Personal Expense Management", Toast.LENGTH_SHORT).show());

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

        fabAdd.setOnClickListener(v ->
                Toast.makeText(DashboardActivity.this, "Add new expense", Toast.LENGTH_SHORT).show());
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
                selectedGroupType = "Personal"; // Fixed: Direct string instead of getString
                updateGroupTypeSelection(groupHome, groupTrip, groupPersonal, groupOther);
            });

            groupOther.setOnClickListener(v -> {
                selectedGroupType = "Other"; // Fixed: Direct string instead of getString
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
            Toast.makeText(this, "Error opening create group dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createGroup(String groupName, String groupType) {
        if (firebaseUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (groupName.trim().isEmpty()) {
            Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique group ID
        String groupId = database.child("groups").push().getKey();
        if (groupId == null) {
            Toast.makeText(this, "Failed to generate group ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create group object
        Group newGroup = new Group();
        newGroup.setGroupId(groupId);
        newGroup.setGroupName(groupName.trim());
        newGroup.setGroupType(groupType);
        newGroup.setCreatedBy(firebaseUser.getUid());
        newGroup.setCreatedAt(System.currentTimeMillis());

        // Add creator as first member
        GroupMember creator = new GroupMember();
        creator.setUserId(firebaseUser.getUid());
        creator.setName(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "");
        creator.setEmail(firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "");
        creator.setJoinedAt(System.currentTimeMillis());
        creator.setRole("admin");

        Map<String, GroupMember> members = new HashMap<>(); // Fixed: Added proper generic types
        members.put(firebaseUser.getUid(), creator);
        newGroup.setMembers(members);

        // Save group to database
        database.child("groups").child(groupId).setValue(newGroup)
                .addOnSuccessListener(aVoid -> {
                    // Group created successfully, now update user's group list
                    updateUserGroupsList(groupId, groupName, groupType);
                    Toast.makeText(this, "Group '" + groupName + "' created successfully!",
                            Toast.LENGTH_SHORT).show();

                    // Refresh the RecyclerView to show the new group
                    refreshGroups();

                    // Navigate to groups tab
                    bottomNavigationView.setSelectedItemId(R.id.navigation_groups);
                    loadFragment(new GroupsFragment());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create group: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void updateUserGroupsList(String groupId, String groupName, String groupType) {
        if (firebaseUser == null) return;

        String userId = firebaseUser.getUid();
        DatabaseReference userGroupsRef = database.child("users").child(userId).child("groups");

        // Create user group reference
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupId(groupId);
        userGroup.setGroupName(groupName);
        userGroup.setGroupType(groupType);
        userGroup.setRole("admin");
        userGroup.setJoinedAt(System.currentTimeMillis());

        userGroupsRef.child(groupId).setValue(userGroup)
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update user groups: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void loadUserDataRealTime() {
        if (firebaseUser == null) return;

        String userId = firebaseUser.getUid();
        DatabaseReference userRef = usersRef.child(userId);

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User fetchedUser = dataSnapshot.getValue(User.class);
                    if (fetchedUser != null) {
                        user = fetchedUser;
                        onUserDataLoaded(user);
                    }
                } else {
                    createNewUserRecord();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this,
                        "Failed to load user data: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        userRef.addValueEventListener(userListener);
    }

    private void createNewUserRecord() {
        if (firebaseUser == null) return;

        user = new User();
        user.setUserId(firebaseUser.getUid());
        user.setName(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "");
        user.setEmail(firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "");
        user.setPhone(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "");

        updateUserDataRealTime();
    }

    private void updateUserDataRealTime() {
        if (firebaseUser == null || user == null) return;

        String userId = firebaseUser.getUid();
        user.setUserId(userId);
        user.setName(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : user.getName());
        user.setEmail(firebaseUser.getEmail() != null ? firebaseUser.getEmail() : user.getEmail());
        user.setPhone(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : user.getPhone());

        usersRef.child(userId).setValue(user, (error, ref) -> {
            if (error != null) {
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied. Please check your Firebase rules.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save user data: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onUserDataLoaded(User user) {
        // Update UI elements with user data
        // You can update profile information, balance, etc. here
        // Example: Update a TextView with user name if you have one
        // TextView userNameText = findViewById(R.id.userNameText);
        // if (userNameText != null && user.getName() != null) {
        //     userNameText.setText(user.getName());
        // }
    }

    public User getCurrentUser() {
        return user;
    }

    public boolean isUserDataLoaded() {
        return firebaseUser != null && user != null && user.getUserId() != null;
    }

    private void updateGroupTypeSelection(LinearLayout groupHome, LinearLayout groupTrip,
                                          LinearLayout groupPersonal, LinearLayout groupOther) {
        try {
            // Reset all backgrounds
            groupHome.setBackgroundResource(R.drawable.group_type_selector);
            groupTrip.setBackgroundResource(R.drawable.group_type_selector);
            groupPersonal.setBackgroundResource(R.drawable.group_type_selector);
            groupOther.setBackgroundResource(R.drawable.group_type_selector);

            // Set selected background
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
                default: // "Home"
                    groupHome.setBackgroundResource(R.drawable.group_type_selected);
                    break;
            }
        } catch (Exception e) {
            // Fallback to color-based selection if drawable resources fail
            int selectedColor = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            int defaultColor = ContextCompat.getColor(this, android.R.color.transparent);

            groupHome.setBackgroundColor(selectedGroupType.equals("Home") ? selectedColor : defaultColor);
            groupTrip.setBackgroundColor(selectedGroupType.equals("Trip") ? selectedColor : defaultColor);
            groupPersonal.setBackgroundColor(selectedGroupType.equals("Personal") ? selectedColor : defaultColor);
            groupOther.setBackgroundColor(selectedGroupType.equals("Other") ? selectedColor : defaultColor);
        }
    }

    private void showMainContent() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            if (currentFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(currentFragment);
                transaction.commit();
                fragmentManager.executePendingTransactions();
            }

            if (mainScrollView != null) {
                mainScrollView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error showing main content: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(Fragment fragment) {
        try {
            if (mainScrollView != null) {
                mainScrollView.setVisibility(View.GONE);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading fragment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

                    if (currentFragment != null && mainScrollView != null && mainScrollView.getVisibility() == View.GONE) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        showMainContent();
                    } else {
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
        dismissDialog();
        removeUserListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseUser != null && userListener == null) {
            loadUserDataRealTime();
        }
    }

    private void dismissDialog() {
        if (createGroupDialog != null && createGroupDialog.isShowing()) {
            createGroupDialog.dismiss();
            createGroupDialog = null;
        }
    }

    private void removeUserListener() {
        if (userListener != null && firebaseUser != null) {
            usersRef.child(firebaseUser.getUid()).removeEventListener(userListener);
            userListener = null;
        }
    }
}