package com.berete.go4lunch.ui.chat;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.berete.go4lunch.databinding.ItemConversationBinding;

import java.util.List;

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.ViewHolder> {

    private final List<String> conversations;

    public ConversationsListAdapter(List<String> items) {
        conversations = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemConversationBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.content.setText(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public ViewHolder(ItemConversationBinding binding) {
            super(binding.getRoot());
            content = binding.content;
        }
    }
}