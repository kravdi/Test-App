package com.kravdi.applicationa.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kravdi.applicationa.R;
import com.kravdi.applicationa.activities.MainActivity;

public class TestSectionFragment extends Fragment {

    private static TestSectionFragment fragment;

    public TestSectionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TestSectionFragment newInstance() {
        if(fragment == null)
            fragment = new TestSectionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        Button btnOk = (Button) rootView.findViewById(R.id.btn_ok);
        final EditText linkField = (EditText) rootView.findViewById(R.id.link);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = linkField.getText().toString();
                if(link.isEmpty()){
                    Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                } else {
                    if(MainActivity.isPackageInstalled(MainActivity.PACKAGE_NAME_B, getActivity())) {
                        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.kravdi.applicationb");
                        launchIntent.putExtra(MainActivity.LINK_TAG, link);
                        launchIntent.putExtra(MainActivity.FROM_A, "from_test");
                        startActivity(launchIntent);
                    }
                }
            }
        });

        linkField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(linkField.getWindowToken(), 0);
                }
            }
        });

        return rootView;
    }
}