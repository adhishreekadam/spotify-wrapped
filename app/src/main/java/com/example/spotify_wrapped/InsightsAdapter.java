package com.example.spotify_wrapped;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.InsightsViewHolder> {
    private List<Insight> insights;

    public InsightsAdapter(List<Insight> insights) {
        this.insights = insights;
    }

    @NonNull
    @Override
    public InsightsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insight, parent, false);
        return new InsightsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightsViewHolder holder, int position) {
        Insight insight = insights.get(position);
        holder.title.setText(insight.getTitle());
        holder.description.setText(insight.getDescription());
    }

    @Override
    public int getItemCount() {
        return insights.size();
    }

    public static class InsightsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        public InsightsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.insight_card_title);
            description = itemView.findViewById(R.id.insight_card_description);
        }
    }
}