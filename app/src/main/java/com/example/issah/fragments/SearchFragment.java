package com.example.issah.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.issah.R;

public class SearchFragment extends Fragment {

    private EditText searchEditText;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Setup toolbar
        Toolbar toolbar = view.findViewById(R.id.search_toolbar);
        toolbar.setTitle("Search");

        // Setup search functionality
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            showFilterOptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            // Implement your search logic here
            Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch(String query) {
        if (!query.isEmpty()) {
            // Implement your search logic here
            Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
        }
    }

    private void showFilterOptions() {
        // Implement filter options dialog or navigation
        Toast.makeText(getContext(), "Filter options", Toast.LENGTH_SHORT).show();
    }
}