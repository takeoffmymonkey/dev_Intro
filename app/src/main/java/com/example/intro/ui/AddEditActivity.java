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

    Event e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        EditText name = (EditText) findViewById(R.id.addEdit_Name);
        EditText tags = (EditText) findViewById(R.id.addEdit_Tags);
        EditText content = (EditText) findViewById(R.id.addEdit_Content);
        EditText comment = (EditText) findViewById(R.id.addEdit_Comment);
        CheckBox complete = (CheckBox) findViewById(R.id.addEdit_Complete);

        Button save = (Button) findViewById(R.id.addEdit_save);
        save.setOnClickListener(view -> {

            e = new Event();
            e.setName(name.getText().toString());
            e.setTags(tags.getText().toString());
            e.setContent(content.getText().toString());
            e.setComment(comment.getText().toString());

            Intent intent = new Intent(AddEditActivity.this,
                    MainActivity.class);
            if (EventHelper.updateEvent(e)) {
                Toast.makeText(this, e.getName() + " saved",
                        Toast.LENGTH_SHORT).show();
            }
            startActivity(intent);
        });

    }
}