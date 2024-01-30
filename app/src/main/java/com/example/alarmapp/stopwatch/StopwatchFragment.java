package com.example.alarmapp.stopwatch;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.alarmapp.R;

public class StopwatchFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stopwatch_tab_fragment, container, false);
    }
}
