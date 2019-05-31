package com.dudebag.palaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private ArrayList<Friend> mFriendList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //es wird ein View-Holder benötigt, deswegen inner class
    public static class FriendViewHolder extends RecyclerView.ViewHolder{

        // Variable für Freundesnamen die übertragen werden
        public TextView friendName;

        public FriendViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendPlaceholder);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public FriendAdapter(ArrayList<Friend> arrayList) {
        mFriendList = arrayList;
    }

    //strg und i drücken dann werden die fehlenden 3 Methoden angezeigt

    //layout muss an adapter gegeben werden
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_friend, parent, false);
        FriendViewHolder fvh = new FriendViewHolder(v, mListener);
        return fvh;
    }

    //hier geben wir die Werte weiter
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend currentFriend = mFriendList.get(position);

        holder.friendName.setText(currentFriend.getName());

    }

    //wie groß ist die Liste
    @Override
    public int getItemCount() {
        return mFriendList.size();
    }
}
