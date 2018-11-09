package com.simple.app.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simple.app.simplemailbox.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MailListActivityFragment extends Fragment {

    public MailListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mail_list, container, false);
    }
}
