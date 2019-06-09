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
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public MessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.messagePlaceholder);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public MessageAdapter(ArrayList<Message> arrayList) {
        mMessageList = arrayList;
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_message, parent, false);
        MessageViewHolder mvh = new MessageViewHolder(v, mListener);
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


        holder.mTextView.setText(currentMessage.getText());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
