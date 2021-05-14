package com.berete.go4lunch.ui.chat;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.berete.go4lunch.databinding.FragmentConversationsBinding;
import com.berete.go4lunch.ui.chat.models.Conversation;

import java.util.List;

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.ViewHolder> {

    private final List<Conversation> conversations;

    public ConversationsListAdapter(List<Conversation> items) {
        conversations = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentConversationsBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Conversation mItem;

        public ViewHolder(FragmentConversationsBinding binding) {
            super(binding.getRoot());
        }
    }
}