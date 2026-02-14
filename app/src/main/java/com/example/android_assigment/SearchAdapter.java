package com.example.android_assigment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assigment_part2.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    public interface OnAddClickListener {
        void onAddClick(SearchItem item);
    }

    private final List<SearchItem> items;
    private final OnAddClickListener listener;

    public SearchAdapter(List<SearchItem> items, OnAddClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_card, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchItem item = items.get(position);

        holder.nameTv.setText(item.getDisplayName());
        holder.typeTv.setText(item.getType() == SearchItem.Type.USER ? "משתמש" : "קבוצה");
        holder.addBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView typeTv;
        ImageButton addBtn;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.user_or_group_name);
            typeTv = itemView.findViewById(R.id.search_item_type);
            addBtn = itemView.findViewById(R.id.add_group_or_user_button);
        }
    }
}

