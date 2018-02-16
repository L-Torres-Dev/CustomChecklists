package com.lentor.customchecklists;

import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


/**
 * REALLY CLOSE!! The data from NewChecklistActivity is transfering to the
 * MainActivity successfully! Just need to figure out how to get the button
 * for the checklist to show up!
 */
public class MainActivity extends AppCompatActivity {

    Context context;
    ArrayList<Checklist> checklists;

    LinearLayout buttonLayout;
    Button newChecklistButton;

    ImageButton menuButton;

    String data;
    static final String FILE_NAME = "dataFile";

    private static int checklistCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        buttonLayout = findViewById(R.id.Button_Layout);
        newChecklistButton = findViewById(R.id.NewChecklistButton);
        menuButton = findViewById(R.id.Menu_Button);

        Intent newChecklistIntent = getIntent();
        String intentData = newChecklistIntent.getStringExtra("Checklist");

        Log.i("CHECKLIST", "Button Layout count: " + buttonLayout.getChildCount());

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                Toast toast = Toast.makeText(context, "delete successful", Toast.LENGTH_SHORT);
                                toast.show();
                                return true;
                            case R.id.delete_all:

                                // 1. Instantiate an AlertDialog.Builder with its constructor
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                // 2. Chain together various setter methods to set the dialog characteristics
                                builder.setMessage("Are you sure you want to delete all Checklists?")
                                        .setTitle("WARNING!");

                                // 3. Add the buttons
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                        context.deleteFile(FILE_NAME);
                                        buttonLayout.removeAllViews();
                                        checklistCount = 0;
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });

                                // 4. Get the AlertDialog from create()
                                AlertDialog dialog = builder.create();
                                dialog.show();


                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        Log.i("FILELIST", "INTENT DATA: " + intentData);

        if(intentData != null){
            Log.i("FILELIST", "THE INTENT IS NOT NULL!");
            //if there is already a file with Checklists data in it
            if(context.fileList().length > 0){
                Log.i("FILELIST", "File found!");
                String currentData = ChecklistsFile.loadChecklist(context, FILE_NAME);
                ArrayList<Checklist> currentChecklists = ChecklistsFile.readData(currentData);
                currentChecklists.add(ChecklistsFile.readData(intentData).get(0));

                data = ChecklistsFile.writeData(currentChecklists);
                ChecklistsFile.saveChecklists(context, FILE_NAME, data);
                Log.i("FILELIST", "THE INTENT: " + data);
            }
            //if there is no file made yet.
            else{
                ArrayList<Checklist> currentChecklists = ChecklistsFile.readData(intentData);
                data = ChecklistsFile.writeData(currentChecklists);
                ChecklistsFile.saveChecklists(context, FILE_NAME, data);
                //Log.i("CHECKLIST", data);
            }
        }


        newChecklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewChecklistActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Log.i("FILELIST", "Number of files: " + context.fileList().length);
        Log.i("FILELIST", "Dir: " + context.getFilesDir());

        buttonLayoutSetup();

    }

    /**
     * Adds a button to the MainActivity Layout for every Checklist that is stored locally
     * on the device.
     */
    private void buttonLayoutSetup()
    {
        //Checks if there is a file for the app. There should never be more than one.
        if(context.fileList().length > 0){
            if (context.fileList()[0].equals(FILE_NAME)) {
                data = ChecklistsFile.loadChecklist(context, FILE_NAME);
                checklists = ChecklistsFile.readData(data);

                checklistCount = 0;

                for (Checklist checklist : checklists) {
                    Button button = new Button(context);
                    button.setText(checklist.getName());

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ChecklistLayout.class);
                            String checklistString = ChecklistsFile.readSingleChecklist(checklists, checklistCount);
                            intent.putExtra("Checklist", checklistString);
                            startActivity(intent);
                        }
                    });

                    checklistCount ++;
                    buttonLayout.addView(button, buttonLayout.getChildCount() - 1);
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LIFECYCLE", "MainActivity: onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LIFECYCLE", "MainActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LIFECYCLE", "MainActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LIFECYCLE", "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LIFECYCLE", "MainActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LIFECYCLE", "MainActivity: onDestroy()");
    }


}
