package com.example.android_assigment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assigment_part2.R;

import java.util.List;

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder> {

    private final List<HomeCard> items;

    public HomeCardAdapter(List<HomeCard> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HomeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_card, parent, false);
        return new HomeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCardViewHolder holder, int position) {
        HomeCard item = items.get(position);
        holder.groupNameTv.setText(item.getGroupName());
        holder.lastMessageTv.setText(item.getLastMessage());

        // לחיצה על כל הכרטיס תפתח את מסך ההודעות של הקבוצה
        holder.rootLayout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("groupId", item.getGroupId());
            bundle.putString("groupName", item.getGroupName());

            Navigation.findNavController(v)
                    .navigate(R.id.action_homeFragment_to_messegeFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class HomeCardViewHolder extends RecyclerView.ViewHolder {

        TextView groupNameTv;
        TextView lastMessageTv;
        LinearLayout rootLayout;

        HomeCardViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTv = itemView.findViewById(R.id.group_name_tv);
            lastMessageTv = itemView.findViewById(R.id.last_message_tv);
            rootLayout = itemView.findViewById(R.id.home_card_clickable_root);
        }
    }
}

