package com.dudebag.palaver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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
        public ImageView mImageView;

        public MessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.messagePlaceholder);
            mImageView = itemView.findViewById(R.id.image_placeholder);

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

        //von mir geschrieben
        if (mMessageList.get(position).isOwn()) {
            holder.mTextView.setGravity(Gravity.RIGHT);
        }

        //von ihm geschrieben
        else {
            holder.mTextView.setGravity(Gravity.LEFT);
        }

        //GPS
        /*if (!mMessageList.get(position).getX().equals("")) {
            holder.mTextView.setTextColor(Color.parseColor("#FF6B00"));
        }*/

        //Image
        if (!mMessageList.get(position).getPic().equals("")) {

            byte[] decodeMap = Base64.decode(mMessageList.get(position).getPic(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodeMap, 0, decodeMap.length);

            holder.mImageView.setImageBitmap(decodedByte);
            holder.mTextView.setGravity(Gravity.RIGHT);
        }


        holder.mTextView.setText(currentMessage.getText());
        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
