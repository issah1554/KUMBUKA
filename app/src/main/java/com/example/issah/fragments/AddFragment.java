package com.example.issah.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.issah.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class AddFragment extends Fragment {

    private TextInputEditText titleEditText;
    private EditText contentEditText;
    private Spinner typeSpinner;
    private Button saveButton;

    // Formatting states
    private boolean isBold = false;
    private boolean isItalic = false;
    private int currentColor = Color.BLACK;

    // Undo/Redo stacks
    private Stack<Editable> undoStack = new Stack<>();
    private Stack<Editable> redoStack = new Stack<>();
    private boolean isUndoRedo = false;

    // Tag management
    private static final String PENDING_STYLE_KEY = "pendingStyle";
    private Map<String, Object> viewTags = new HashMap<>();

    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        initializeViews(view);
        setupSpinner();
        setupFormattingButtons(view);
        setupTextWatchers();
        setupSaveButton();

        return view;
    }

    private void initializeViews(View view) {
        typeSpinner = view.findViewById(R.id.typeSpinner);
        titleEditText = view.findViewById(R.id.titleEditText);
        contentEditText = view.findViewById(R.id.contentEditText);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.item_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void setupFormattingButtons(View view) {
        // Undo/Redo buttons
        view.findViewById(R.id.btn_undo).setOnClickListener(v -> undo());
        view.findViewById(R.id.btn_redo).setOnClickListener(v -> redo());

        // Text formatting buttons
        view.findViewById(R.id.btn_bold).setOnClickListener(v -> toggleBold());
        view.findViewById(R.id.btn_italic).setOnClickListener(v -> toggleItalic());
        view.findViewById(R.id.btn_code).setOnClickListener(v -> applyCodeFormat());
        view.findViewById(R.id.btn_color).setOnClickListener(v -> showCustomColorPicker());
        view.findViewById(R.id.btn_list).setOnClickListener(v -> applyListFormat());
        view.findViewById(R.id.btn_quote).setOnClickListener(v -> applyQuoteFormat());
    }

    private void setupTextWatchers() {
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUndoRedo) {
                    // Save current state before changes
                    undoStack.push(new SpannableStringBuilder(contentEditText.getText()));
                    redoStack.clear();
                    updateUndoRedoButtons();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handlePendingStyles(start, count);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveItem());
    }

    private void toggleBold() {
        isBold = !isBold;
        applyStyle(Typeface.BOLD, isBold);
        updateButtonStates();
    }

    private void toggleItalic() {
        isItalic = !isItalic;
        applyStyle(Typeface.ITALIC, isItalic);
        updateButtonStates();
    }

    private void applyStyle(int style, boolean isActive) {
        int start = contentEditText.getSelectionStart();
        int end = contentEditText.getSelectionEnd();

        if (start == end) {
            // Apply to next typed characters if nothing selected
            viewTags.put(PENDING_STYLE_KEY, new StyleSpan(style));
            return;
        }

        Editable editable = contentEditText.getText();
        StyleSpan[] spans = editable.getSpans(start, end, StyleSpan.class);

        // Remove existing spans of this style in selection
        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {
                editable.removeSpan(span);
            }
        }

        // Apply new span if activating
        if (isActive) {
            editable.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void handlePendingStyles(int start, int count) {
        Object pendingStyle = viewTags.get(PENDING_STYLE_KEY);
        if (pendingStyle != null && pendingStyle instanceof StyleSpan && count > 0) {
            contentEditText.getText().setSpan(
                    pendingStyle,
                    start,
                    start + count,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            viewTags.put(PENDING_STYLE_KEY, null);
        }
    }

    private void applyCodeFormat() {
        int start = contentEditText.getSelectionStart();
        int end = contentEditText.getSelectionEnd();
        String selectedText = contentEditText.getText().subSequence(start, end).toString();
        contentEditText.getText().replace(start, end, "`" + selectedText + "`");
        contentEditText.setSelection(start + selectedText.length() + 2);
    }

    private void showCustomColorPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_color_picker, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.color_primary).setOnClickListener(v -> {
            currentColor = ContextCompat.getColor(requireContext(), R.color.kumbuka_primary);
            applyTextColor(currentColor);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.color_accent).setOnClickListener(v -> {
            currentColor = ContextCompat.getColor(requireContext(), R.color.kumbuka_accent);
            applyTextColor(currentColor);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.color_dark).setOnClickListener(v -> {
            currentColor = ContextCompat.getColor(requireContext(), R.color.kumbuka_primary_dark);
            applyTextColor(currentColor);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.color_indigo).setOnClickListener(v -> {
            currentColor = Color.parseColor("#3F51B5"); // Indigo
            applyTextColor(currentColor);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void applyTextColor(int color) {
        int start = contentEditText.getSelectionStart();
        int end = contentEditText.getSelectionEnd();

        if (start == end) return;

        Editable editable = contentEditText.getText();
        ForegroundColorSpan[] spans = editable.getSpans(start, end, ForegroundColorSpan.class);

        // Remove existing color spans
        for (ForegroundColorSpan span : spans) {
            editable.removeSpan(span);
        }

        // Apply new color
        editable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void applyListFormat() {
        int start = contentEditText.getSelectionStart();
        contentEditText.getText().insert(start, "- ");
        contentEditText.setSelection(start + 2);
    }

    private void applyQuoteFormat() {
        int start = contentEditText.getSelectionStart();
        int end = contentEditText.getSelectionEnd();
        String selectedText = contentEditText.getText().subSequence(start, end).toString();
        contentEditText.getText().replace(start, end, "> " + selectedText);
        contentEditText.setSelection(start + selectedText.length() + 2);
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            isUndoRedo = true;
            redoStack.push(new SpannableStringBuilder(contentEditText.getText()));
            contentEditText.setText(undoStack.pop());
            isUndoRedo = false;
            updateUndoRedoButtons();
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            isUndoRedo = true;
            undoStack.push(new SpannableStringBuilder(contentEditText.getText()));
            contentEditText.setText(redoStack.pop());
            isUndoRedo = false;
            updateUndoRedoButtons();
        }
    }

    private void updateButtonStates() {
        View view = getView();
        if (view != null) {
            View boldButton = view.findViewById(R.id.btn_bold);
            View italicButton = view.findViewById(R.id.btn_italic);

            if (boldButton != null) boldButton.setSelected(isBold);
            if (italicButton != null) italicButton.setSelected(isItalic);
        }
    }

    private void updateUndoRedoButtons() {
        View view = getView();
        if (view != null) {
            View undoButton = view.findViewById(R.id.btn_undo);
            View redoButton = view.findViewById(R.id.btn_redo);

            if (undoButton != null) undoButton.setEnabled(!undoStack.isEmpty());
            if (redoButton != null) redoButton.setEnabled(!redoStack.isEmpty());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {
        String title = titleEditText.getText().toString().trim();
        String content = Html.toHtml(contentEditText.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        String type = typeSpinner.getSelectedItem().toString();

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter content", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        String email = currentUser.getEmail();

        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("content", content);
        item.put("type", type);
        item.put("timestamp", System.currentTimeMillis());
        item.put("userId", uid);
        item.put("userEmail", email);

        db.collection("items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Item saved!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error saving item: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void clearForm() {
        titleEditText.getText().clear();
        contentEditText.getText().clear();
        typeSpinner.setSelection(0);
        isBold = false;
        isItalic = false;
        currentColor = Color.BLACK;
        undoStack.clear();
        redoStack.clear();
        viewTags.clear();
        updateButtonStates();
        updateUndoRedoButtons();
    }
}