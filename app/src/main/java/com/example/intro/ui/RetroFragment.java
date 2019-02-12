package com.example.intro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.intro.R;

import static com.example.intro.ui.MainActivity.dbHelper;

public class RetroFragment extends Fragment {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();


    // newInstance constructor for creating fragment with arguments
    public static RetroFragment newInstance(int page, String title) {
        RetroFragment fragment = new RetroFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retro, container,
                false);

        // Prepare list, its listener and adapter
        ListView listView = (ListView) view.findViewById(R.id.lvEvents);
        setOnClickListenerForList(listView);
        EventsAdapter adapter = new EventsAdapter(getContext(),
                dbHelper.createNotesCursor());
        listView.setAdapter(adapter);

        // Prepare Add button
        FloatingActionButton fab = (FloatingActionButton)
                view.findViewById(R.id.fab);
        setOnClickListenerForButton(fab);

        // Return the view
        return view;
    }


    private void setOnClickListenerForList(ListView listView) {
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // Do smth
        });
    }


    private void setOnClickListenerForButton(FloatingActionButton fab) {
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(),
                    AddEditActivity.class);
            startActivity(intent);
        });
    }
}