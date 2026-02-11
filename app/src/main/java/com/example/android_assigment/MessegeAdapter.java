package com.example.android_assigment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assigment_part2.R;

import java.util.List;

public class MessegeAdapter extends RecyclerView.Adapter<MessegeAdapter.MessegeViewHolder> {

    private final List<Messege> items;

    public MessegeAdapter(List<Messege> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MessegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_card, parent, false);
        return new MessegeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessegeViewHolder holder, int position) {
        Messege item = items.get(position);
        holder.usernameChatTv.setText(item.getUserName());
        holder.userMessageTv.setText(item.getMessage());
        holder.msgTimeTv.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class MessegeViewHolder extends RecyclerView.ViewHolder {

        TextView usernameChatTv;
        TextView userMessageTv;
        TextView msgTimeTv;

        MessegeViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameChatTv = itemView.findViewById(R.id.username_chat);
            userMessageTv = itemView.findViewById(R.id.user_message);
            msgTimeTv = itemView.findViewById(R.id.msg_time);
        }
    }
}

