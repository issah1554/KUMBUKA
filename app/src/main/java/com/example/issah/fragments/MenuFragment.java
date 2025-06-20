package com.example.issah.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.issah.R;

public class MenuFragment extends Fragment {

    private void setupMenu(View root, int id, String label, int iconRes, Fragment fragment) {
        View item = root.findViewById(id);
        ((TextView) item.findViewById(R.id.menu_label)).setText(label);
        ((ImageView) item.findViewById(R.id.menu_icon)).setImageResource(iconRes);
        item.setOnClickListener(v -> navigateToFragment(fragment));
    }

    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMenu(view, R.id.about_us, "About Us", R.drawable.ic_info, new AboutUsFragment());
        setupMenu(view, R.id.contact, "Contact Us", R.drawable.ic_mail, new ContactUsFragment());
        setupMenu(view, R.id.account_settings, "Account Settings", R.drawable.ic_account_circle, new AccountSettingsFragment());
        setupMenu(view, R.id.app_settings, "App Settings", R.drawable.ic_settings, new AppSettingsFragment());
        setupMenu(view, R.id.privacy_policy, "Privacy Policy", R.drawable.ic_shield, new PrivacyPolicyFragment());
        setupMenu(view, R.id.complaints, "Complaints", R.drawable.ic_warning, new ComplaintsFragment());
        setupMenu(view, R.id.help, "Help", R.drawable.ic_support_agent, new HelpFragment());
        setupMenu(view, R.id.report, "Report", R.drawable.ic_report, new ReportFragment());
        setupMenu(view, R.id.receipt, "Receipt", R.drawable.ic_receipt, new com.example.issah.fragments.ReceiptFragment());
    }
}