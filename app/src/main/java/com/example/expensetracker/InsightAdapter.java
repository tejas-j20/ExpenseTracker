package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InsightAdapter extends RecyclerView.Adapter<InsightAdapter.InsightVH> {

    private final List<InsightCard> cards;

    public InsightAdapter(List<InsightCard> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public InsightVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_insight_card, parent, false);
        return new InsightVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightVH holder, int position) {
        InsightCard card = cards.get(position);
        holder.tvEmoji.setText(card.getEmoji());
        holder.tvTitle.setText(card.getTitle());
        holder.tvValue.setText(card.getValue());
        holder.tvSubtitle.setText(card.getSubtitle());
        holder.accentBar.setBackgroundColor(card.getColor());
    }

    @Override
    public int getItemCount() { return cards.size(); }

    static class InsightVH extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvTitle, tvValue, tvSubtitle;
        View     accentBar;

        InsightVH(@NonNull View itemView) {
            super(itemView);
            tvEmoji    = itemView.findViewById(R.id.tvEmoji);
            tvTitle    = itemView.findViewById(R.id.tvInsightTitle);
            tvValue    = itemView.findViewById(R.id.tvInsightValue);
            tvSubtitle = itemView.findViewById(R.id.tvInsightSubtitle);
            accentBar  = itemView.findViewById(R.id.accentBar);
        }
    }
}
