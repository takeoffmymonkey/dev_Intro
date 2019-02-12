package com.example.intro.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intro.model.Event;
import com.example.intro.model.EventHelper;
import com.example.intro.R;

public class AddEditActivity extends AppCompatActivity {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    EditText name, tags, content, comment;
    CheckBox complete;
    Button save;

    Event e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // UI elements
        name = (EditText) findViewById(R.id.et_Name);
        tags = (EditText) findViewById(R.id.et_Tags);
        content = (EditText) findViewById(R.id.et_Content);
        comment = (EditText) findViewById(R.id.et_Comment);
        complete = (CheckBox) findViewById(R.id.cb_Complete);
        save = (Button) findViewById(R.id.bt_save);

        // Listener for the button
        setButtonListener(save);
    }


    private void setButtonListener(Button save) {
        save.setOnClickListener(view -> {
            // Prepare Event object
            e = new Event();
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
}