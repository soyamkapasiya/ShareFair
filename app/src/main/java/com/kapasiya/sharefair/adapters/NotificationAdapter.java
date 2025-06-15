package com.kapasiya.sharefair.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kapasiya.sharefair.R;
import com.kapasiya.sharefair.dtos.BillItems;
import com.kapasiya.sharefair.dtos.NotificationItems;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationItems> notificationItems;

    public NotificationAdapter(List<NotificationItems> notificationItems) {
        this.notificationItems = notificationItems;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItems items = notificationItems.get(position);

        holder.notificationDateTime.setText(items.getNotificationDateTime());
        holder.notificationTitle.setText(items.getNotificationTitle());
    }

    @Override
    public int getItemCount() {
        return notificationItems != null ? notificationItems.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView notificationDateTime;
        TextView notificationTitle;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.notification_card);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationDateTime = itemView.findViewById(R.id.notification_datetime);
        }
    }
}
