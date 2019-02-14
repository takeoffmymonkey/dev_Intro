package com.example.intro.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intro.model.Event;
import com.example.intro.model.EventHelper;
import com.example.intro.R;

import static com.example.intro.ui.RetroFragment.EVENT_ID;

public class AddEditActivity extends AppCompatActivity {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    Event e;

    // UI elements
    EditText name, tags, content, comment;
    CheckBox complete;
    Button save, delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Construct Event
        long id = getIntent().getLongExtra(EVENT_ID, 0);
        e = EventHelper.buildEvent(id);

        // UI elements
        name = (EditText) findViewById(R.id.et_Name);
        name.setText(e.getName());
        tags = (EditText) findViewById(R.id.et_Tags);
        tags.setText(e.getTags());
        content = (EditText) findViewById(R.id.et_Content);
        content.setText(e.getContent());
        comment = (EditText) findViewById(R.id.et_Comment);
        comment.setText(e.getComment());
        complete = (CheckBox) findViewById(R.id.cb_Complete);
        save = (Button) findViewById(R.id.bt_save);
        setSaveButtonListener();
        delete = (Button) findViewById(R.id.bt_delete);
        setDeleteButtonListener();
        if (EventHelper.isNewEvent(e)) delete.setVisibility(View.GONE);
    }


    private void setSaveButtonListener() {
        save.setOnClickListener(view -> {
            // Pass data to Event object
            e.setName(name.getText().toString());
            e.setTags(tags.getText().toString());
            e.setContent(content.getText().toString());
            e.setComment(comment.getText().toString());

            // Update DB and inform user
            if (EventHelper.updateDbWithEvent(e)) {
                Toast.makeText(this, e.getName() + " saved",
                        Toast.LENGTH_SHORT).show();
            }

            // Launch previous activity
            Intent intent = new Intent(AddEditActivity.this,
                    MainActivity.class);
            startActivity(intent);
        });
    }


    private void setDeleteButtonListener() {
        delete.setOnClickListener(view -> {
            if (EventHelper.deleteEventFromDb(e)) {
                Toast.makeText(this, e.getName()
                                + " deleted",
                        Toast.LENGTH_SHORT).show();
            }

            // Launch previous activity
            Intent intent = new Intent(AddEditActivity.this,
                    MainActivity.class);
            startActivity(intent);
        });
    }
}