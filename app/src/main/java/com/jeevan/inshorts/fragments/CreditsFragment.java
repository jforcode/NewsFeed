package com.jeevan.inshorts.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.interfaces.MainActivityChangeListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditsFragment extends Fragment {


    public CreditsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() instanceof MainActivityChangeListener) {
            ((MainActivityChangeListener) getActivity()).setToolbarTitle("Credits");
        }
        return inflater.inflate(R.layout.fragment_credits, container, false);
    }

}
