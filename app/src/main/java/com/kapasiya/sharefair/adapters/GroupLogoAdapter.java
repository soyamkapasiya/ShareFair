package com.kapasiya.sharefair.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.kapasiya.sharefair.R;
import com.kapasiya.sharefair.dtos.Group;
import java.util.List;

public class GroupLogoAdapter extends RecyclerView.Adapter<GroupLogoAdapter.GroupLogoViewHolder> {
    private final List<Group> groupLogos;

    public GroupLogoAdapter(List<Group> groupLogos) {
        this.groupLogos = groupLogos;
    }

    @NonNull
    @Override
    public GroupLogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_logo, parent, false);
        return new GroupLogoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupLogoViewHolder groupLogoViewHolder, int position) {
        Group group = groupLogos.get(position);
        groupLogoViewHolder.groupLogoName.setText(group.getGroupName());
    }

    @Override
    public int getItemCount() {
        return groupLogos != null ? groupLogos.size() : 0;
    }

    static class GroupLogoViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView groupLogoName;

        public GroupLogoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.group_logo_card);
            groupLogoName = itemView.findViewById(R.id.group_logo_name);
        }
    }
}