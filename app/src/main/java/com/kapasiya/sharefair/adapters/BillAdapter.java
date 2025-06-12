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

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private final List<BillItems> billItems;

    public BillAdapter(List<BillItems> billItems) {
        this.billItems = billItems;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bills, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position){
        BillItems items = billItems.get(position);

        holder.billDate.setText(items.getBillDate());
        holder.billTitle.setText(items.getBillTitle());
        holder.billAmountPaid.setText(items.getBillAmountPaid());
        holder.billPayBy.setText(items.getBillPayBy());
        holder.billPayAmount.setText(items.getBillPayAmount());
    }

    @Override
    public int getItemCount() {
        return billItems != null ? billItems.size() : 0; // Fixed: return actual list size
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView billDate;
        TextView billTitle;
        TextView billAmountPaid;
        TextView billPayBy;
        TextView billPayAmount;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.bill_card);
            billDate = itemView.findViewById(R.id.bill_date);
            billTitle = itemView.findViewById(R.id.bill_title);
            billAmountPaid = itemView.findViewById(R.id.bill_amount_paid);
            billPayBy = itemView.findViewById(R.id.bill_pay_by);
            billPayAmount = itemView.findViewById(R.id.bill_pay_amount);
        }
    }
}