package com.example.alarmapp.timer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;

public class TimerFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timer_tab_fragment, container, false);
    }
}
