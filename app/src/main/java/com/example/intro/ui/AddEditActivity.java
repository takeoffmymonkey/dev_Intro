package com.example.intro.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intro.model.Event;
import com.example.intro.model.EventHelper;
import com.example.intro.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.intro.ui.RetroFragment.EVENT_ID;

public class AddEditActivity extends AppCompatActivity {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    Event e;
    ArrayAdapter<String> tagsAdapter;
    List<String> tags = EventHelper.getTagsList();
    List<AutoCompleteTextView> actvsTags = new ArrayList<>();

    // UI elements
    EditText etName, etContent, etComment;
    CheckBox cbComplete;
    Button btAddTag, btCleanTag, btSave, btDelete;
    AutoCompleteTextView actvTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Construct Event
        long id = getIntent().getLongExtra(EVENT_ID, 0);
        e = EventHelper.buildEvent(id);

        // Create adapter for tags
        tagsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, tags);

        // Add UI elements
        etName = (EditText) findViewById(R.id.et_Name);
        etName.setText(e.getName());
        etContent = (EditText) findViewById(R.id.et_Content);
        etContent.setText(e.getContent());
        etComment = (EditText) findViewById(R.id.et_Comment);
        etComment.setText(e.getComment());
        cbComplete = (CheckBox) findViewById(R.id.cb_Complete);
        btSave = (Button) findViewById(R.id.bt_save);
        setSaveButtonListener();
        btDelete = (Button) findViewById(R.id.bt_delete);
        setDeleteButtonListener();
        if (EventHelper.isNewEvent(e)) btDelete.setVisibility(View.GONE);
        actvTag = (AutoCompleteTextView) findViewById(R.id.actv_Tag);
        actvTag.setText(e.getTags());
        setTagsAdapter(actvTag);
        actvsTags.add(actvTag);
        btCleanTag = (Button) findViewById(R.id.actv_Clean_Tag);
        setCleanTagButtonListener();
        btAddTag = (Button) findViewById(R.id.bt_Add_Tag);
        setAddTagButtonListener();
    }


    private void setCleanTagButtonListener() {
        btCleanTag.setOnClickListener(view -> {
            actvTag.setText("");
        });
    }


    private void setAddTagButtonListener() {
        btAddTag.setOnClickListener(view -> {
            // Create view
            LayoutInflater li = getLayoutInflater();
            View vNewTag = li.inflate(R.layout.tags_item_add_edit, null);

            // Set up AutoCompleteTextView and add it to list of actv's
            AutoCompleteTextView actvNewTag = (AutoCompleteTextView)
                    vNewTag.findViewById(R.id.actv_Tag_Item);
            setTagsAdapter(actvNewTag);
            actvsTags.add(actvNewTag);

            // Set up delete button
            Button btDelete = (Button) vNewTag.findViewById
                    (R.id.actv_Kill_Tag_Item);
            setDeleteTagButtonListener(btDelete, actvNewTag);

            // Add view to parent and set focus
            ViewGroup parent = (ViewGroup) findViewById(R.id.ll_Tags);
            parent.addView(vNewTag);
            actvNewTag.requestFocus();
        });
    }


    private void setDeleteTagButtonListener(Button bt,
                                            AutoCompleteTextView actv) {
        bt.setOnClickListener(view -> {
            View v = (View) bt.getParent();
            v.setVisibility(View.GONE);
            actvsTags.remove(actv);
        });
    }


    private void setTagsAdapter(AutoCompleteTextView actv) {
        actv.setAdapter(tagsAdapter);
    }


    private void setSaveButtonListener() {
        btSave.setOnClickListener(view -> {
            // Gather data and pass it to Event object
            e.setName(etName.getText().toString());
            e.setContent(etContent.getText().toString());
            e.setComment(etComment.getText().toString());
            StringBuilder sb = new StringBuilder();
            for (AutoCompleteTextView actv : actvsTags) {
                sb.append(actv.getText().toString());
                sb.append(",");
            }
            e.setTags(sb.toString());

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
        btDelete.setOnClickListener(view -> {
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