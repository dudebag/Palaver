package com.dudebag.palaver;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> mMessageList;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.messagePlaceholder);

        }
    }

    public MessageAdapter(ArrayList<Message> arrayList) {
        mMessageList = arrayList;
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_message, parent, false);
        MessageViewHolder mvh = new MessageViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message currentMessage = mMessageList.get(position);

        if (mMessageList.get(position).isOwn()) {
            holder.mTextView.setGravity(Gravity.RIGHT);
        }

        else {
            holder.mTextView.setGravity(Gravity.LEFT);
        }
        //else {
            //holder.mTextView.setGravity(Gravity.START);
        //}
        holder.mTextView.setText(currentMessage.getText());




        //holder.mTextView.setGravity();
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
