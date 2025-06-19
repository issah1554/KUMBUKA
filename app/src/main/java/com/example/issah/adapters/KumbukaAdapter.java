package com.example.issah.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.issah.R;
import com.example.issah.models.KumbukaItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class KumbukaAdapter extends FirestoreRecyclerAdapter<KumbukaItem, KumbukaAdapter.KumbukaViewHolder> {

    private final Context context;

    public KumbukaAdapter(@NonNull FirestoreRecyclerOptions<KumbukaItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull KumbukaViewHolder holder, int position, @NonNull KumbukaItem model) {
        holder.titleTextView.setText(model.getTitle());
        holder.contentTextView.setText(Html.fromHtml(model.getContent()));
        holder.typeTextView.setText(model.getType());

        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(model.getImageUrl())
                    .into(holder.itemImageView);
            holder.itemImageView.setVisibility(View.VISIBLE);
        } else {
            holder.itemImageView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public KumbukaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kumbuka, parent, false);
        return new KumbukaViewHolder(view);
    }

    static class KumbukaViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView typeTextView;
        ImageView itemImageView;

        public KumbukaViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            contentTextView = itemView.findViewById(R.id.item_content);
            typeTextView = itemView.findViewById(R.id.item_type);
            itemImageView = itemView.findViewById(R.id.item_image);
        }
    }
}