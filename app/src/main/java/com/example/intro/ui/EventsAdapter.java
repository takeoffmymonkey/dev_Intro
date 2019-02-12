package com.example.intro.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.intro.R;
import com.example.intro.model.DbHelper;

import static com.example.intro.ui.MainActivity.dbHelper;

public class EventsAdapter extends CursorAdapter {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();


    EventsAdapter(Context context, Cursor c) {
        super(context, c);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_retro,
                viewGroup, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get Name value from DB and set it to text view
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        String name = cursor.getString(cursor.getColumnIndexOrThrow
                (dbHelper.EVENTS_NAME_COLUMN));
        tvName.setText(name);
    }
}
