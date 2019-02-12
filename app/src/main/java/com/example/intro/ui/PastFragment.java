package com.example.intro.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.intro.R;
import com.example.intro.model.DbHelper;

public class PastFragment extends Fragment {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();
    public static DbHelper dbHelper;

    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static PastFragment newInstance(int page, String title) {
        PastFragment fragmentFirst = new PastFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past, container, false);
        dbHelper = new DbHelper(getContext());

        ListView listView = (ListView) view.findViewById(R.id.lvEvents);
        setOnClickListenerForList(listView);
        EventsAdapter adapter = new EventsAdapter(getContext(), dbHelper.createNotesCursor(null));
        listView.setAdapter(adapter);

//        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
//        tvLabel.setText(page + " -- " + title);
        return view;
    }

    private void setOnClickListenerForList(ListView listView) {
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(getContext(), "yo", Toast.LENGTH_SHORT).show();
        });
    }
}
