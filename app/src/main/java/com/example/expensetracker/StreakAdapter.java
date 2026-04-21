package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StreakAdapter extends RecyclerView.Adapter<StreakAdapter.StreakVH> {

    private final List<StreakMonth> months;

    public StreakAdapter(List<StreakMonth> months) {
        this.months = months;
    }

    @NonNull
    @Override
    public StreakVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_streak_month, parent, false);
        return new StreakVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StreakVH holder, int position) {
        StreakMonth month = months.get(position);

        // Set emoji inside dot
        if (month.isCurrentStreak()) {
            holder.tvDot.setText("🔥");
            holder.tvDot.setBackground(
                    holder.itemView.getContext().getResources()
                            .getDrawable(R.drawable.streak_dot_current, null));
        } else if (month.hasActivity()) {
            holder.tvDot.setText("✅");
            holder.tvDot.setBackground(
                    holder.itemView.getContext().getResources()
                            .getDrawable(R.drawable.streak_dot_active, null));
        } else {
            holder.tvDot.setText("○");
            holder.tvDot.setBackground(
                    holder.itemView.getContext().getResources()
                            .getDrawable(R.drawable.streak_dot_inactive, null));
        }

        // Short month label e.g. "Apr\n2025"
        String[] parts = month.getDisplayName().split(" ");
        holder.tvLabel.setText(parts.length == 2 ? parts[0] + "\n" + parts[1]
                : month.getDisplayName());
    }

    @Override
    public int getItemCount() { return months.size(); }

    static class StreakVH extends RecyclerView.ViewHolder {
        TextView tvDot, tvLabel;
        StreakVH(@NonNull View itemView) {
            super(itemView);
            tvDot   = itemView.findViewById(R.id.tvStreakDot);
            tvLabel = itemView.findViewById(R.id.tvStreakMonthLabel);
        }
    }
}
