package com.example.issah.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.issah.R;
import com.example.issah.adapters.KumbukaAdapter;
import com.example.issah.models.KumbukaItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class KumbukaFragment extends Fragment {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private KumbukaAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kumbuka, container, false);

        toolbar = view.findViewById(R.id.kumbuka_toolbar);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);

        setupRecyclerView();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setupRecyclerView() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            emptyStateText.setText("Please sign in to view your items");
            return;
        }

        Query query = db.collection("items")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<KumbukaItem> options = new FirestoreRecyclerOptions.Builder<KumbukaItem>()
                .setQuery(query, KumbukaItem.class)
                .build();

        adapter = new KumbukaAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmptyState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmptyState();
            }
        });
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}