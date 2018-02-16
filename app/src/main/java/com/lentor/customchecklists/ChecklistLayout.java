package com.lentor.customchecklists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity Class that will handle to functionality of the activity_checklist_layout Layout.
 * This Activity will hold all the elements of the current Checklist being displayed
 * as well as functionality to edit the Checklist.
 */
public class ChecklistLayout extends Activity {

    LinearLayout checklistLayout;
    Context context;

    Checklist checklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_layout);

        Intent newChecklistIntent = getIntent();
        String intentData = newChecklistIntent.getStringExtra("Checklist");

        checklistLayout = findViewById(R.id.checklist_layout);

        context = getApplicationContext();

        //Will be the loop that adds the amount of elements to the ChecklistLayout Activity
        for(int i = 0; i < 1; i++){
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(context);
            textView.setText(intentData);

            layout.addView(textView);

            checklistLayout.addView(layout);
        }

    }
}
