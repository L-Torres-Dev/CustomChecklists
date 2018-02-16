package com.lentor.customchecklists;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity Class that will handle the functionality of the activity_new_checklist Layout.
 * This Activity will be responsible for allowing the user to create a new Checklist to use
 * for later.
 */
public class NewChecklistActivity extends AppCompatActivity {

    //Application Context
    Context context;

    //Declare Widget Objects
    Button doneButton;
    Button discardButton;
    Button addElementButton;
    LinearLayout mainLayout;
    LinearLayout elementLayout;
    LinearLayout nameLayout;
    EditText editNameText;
    ArrayList<EditText> elements;

    private int screenWidth;                         //will hold the width of the the devices Screen
    private static int deleteButtons = 0;            //will hold the number of delete buttons in the activity
    private static int editWidth;                    //width of the edit text views of each Element to be added


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_checklist);
        context = getApplicationContext();

        //set the keyboard to always be below an EditText View
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //set the widgets to their respective Views
        doneButton =  findViewById(R.id.Done_Button);
        discardButton = findViewById(R.id.Discard_Button);
        addElementButton = findViewById(R.id.Add_Element_Button);
        editNameText = findViewById(R.id.Name_text);
        editNameText.setWidth(convertPxToDp(450));


        //get Screen size and set the value of it to screenWidth
        screenWidth = getActualScreenSize(context);

        //set the editWidth to half of the screenWidth
        editWidth = screenWidth / 2;


        mainLayout = findViewById(R.id.Main_Layout);
        mainLayout.setMinimumWidth(screenWidth);

        nameLayout = findViewById(R.id.Name_Layout);

        elementLayout = findViewById(R.id.Element_Layout);
        elements = new ArrayList<>();

        //Give the addElementButton its functionality
        addElementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addElement();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                //Checks to see if any elements were added. If no Elements were added the if statement
                //block will be skipped and the user will be prompted to enter something. This if
                //statement is also skipped if the list is invalid, meaning one of the EditText views
                //is empty etc.
                if(elementLayout.getChildCount() > 2 && isValidList()){
                    toMainActivity(true);
                    finish();
                }
                //this code is run to prompt the user to fix errors within the Checklist being created
                //this prompt shows if one of the EditText views is empty including the editNameText
                else{
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewChecklistActivity.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Please add Elements to the list, and make sure they are not empty")
                            .setTitle("NOTICE");

                    // 3. Add an ok Button to exit the AlertDialog box
                    builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    // 4. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();


                    dialog.show();
                }
            }
        });

        /**
         * Discards all work the user did and goes back to the main Activity
         * when calling the toMainActivity method, the checklistDone parameter is set to false
         * because there is not checklist data to send to the main Activity.
         */
        discardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                toMainActivity(false);
                finish();
            }

        });
    }

    /**
     * Method that takes the user back to the mainActivity
     * if the doneButton was pressed checklistDone will be
     * passed as true. Otherwise, it is false.
     * @param checklistDone the boolean that holds whether
     * the done button was pressed
     */
    private void toMainActivity(boolean checklistDone) {

        //if true, a String containing all the new Checklist data is passed to the Main Activity
        //else, simply go to the Main Activity without passing any information at all.
        if(checklistDone){
            Intent intent = new Intent(NewChecklistActivity.this, MainActivity.class);
            String str = createChecklistFileString();
            intent.putExtra("Checklist", str);
            startActivity(intent);

        }else{
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Takes all of the data in the NewChecklistActivity Layout and builds
     * a Checklist Object with all of the data. Afterward the checklist is added
     * to the ArrayList of Checklist Objects and passed to ChecklistsFile.writeData()
     * method to write all of the data of the Checklist in the form of a String
     * NOTE: Because of the nature of the ChecklistsFile Class, the ArrayList of
     * Checklists is required.
     * @return
     */
    private String createChecklistFileString() {
        String str;
        String name = editNameText.getText().toString();
        ArrayList<String> elements = new ArrayList<>();
        ArrayList<String> dElements = new ArrayList<>();


        ArrayList<Checklist> checklists = new ArrayList<>();

        for(int i = 2; i < elementLayout.getChildCount(); i++){
            LinearLayout currentLayout = (LinearLayout) elementLayout.getChildAt(i);
            EditText currentEditText = (EditText) currentLayout.getChildAt(0);
            elements.add(currentEditText.getText().toString());
        }

        checklists.add(new Checklist(name, elements, dElements));

        str = ChecklistsFile.writeData(checklists);


        return str;
    }

    /**
     * Converts a pixel to Android's unit dp's, or Density-independent pixels.
     * @param px the int value in the unit pixels
     * @return the int value in the unit dp
     */
    private int convertPxToDp(int px){
        return Math.round(px/(Resources.getSystem().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }


    /**
     * Adds a new Element to the checklist
     */
    private void addElement(){

        /*gets the current parent layout of the addElementButton and assigns a
        reference variable to the layout Object*/
        LinearLayout parentLayout = (LinearLayout) addElementButton.getParent();

        /*Create a new LinearLayout in which the addElementButton will be added
        and set its parameters*/
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        /*Create a new deleteButton so the user can delete all the views of
        the new LinearLayout that was just instantiated and set its parameters*/
        final Button deleteButton = new Button(context);
        deleteButton.setId(deleteButtons);
        deleteButton.setText("Delete");

        //give the new deleteButton Object its functionality
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /*first, create a reference to the addElementButton's parent
                LinearLayout so that we can remove the button from it*/
                LinearLayout removeLayout = (LinearLayout) addElementButton.getParent();

                //second, remove the addElementButton from the parent LinearLayout
                removeLayout.removeView(addElementButton);

                /*remove the deleteButton's parent LinearLayout from its parent Layout,
                the elementLayout. (NOTICE: the deleteButtons parent layout is NOT
                necessarily the same layout as the addElementButton's parent Layout)*/
                elementLayout.removeView((LinearLayout) deleteButton.getParent());

                /*create a reference to the LinearLayout that addElementButton will be moved
                to (the addElementButton is always to be at the last index of children in
                in the elementLayout)*/

                LinearLayout layout = (LinearLayout) elementLayout.getChildAt(elementLayout.getChildCount() - 1);
                if(deleteButtons > 1)
                {
                    layout.addView(addElementButton, 1);
                }
                else {
                    layout.addView(addElementButton);
                }


                //decrement deleteButtons to keep track of the number of
                //elements in the elementLayout
                deleteButtons--;
            }
        });

        deleteButtons++;

       /* Instantiate a new EditText for the next Element to be added to the
        Checklist*/
        EditText newEditText = new EditText(context);
        newEditText.setWidth(editWidth);
        //newEditText.setWidth(screenWidth);

        //sets the newEditText to only have one line
        newEditText.setSingleLine(true);

        //makes the new edit text in focus upon creation
        newEditText.requestFocus();

       /* Set an OnKeyListener to the new edit text created so that when the user
        presses enter it adds a new element to the checklist exactly the way that
        The addElementButton does.*/
        newEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    //call the function that does what the AddElementButton would do
                    addElement();
                    return true;
                }
                return false;
            }
        });

        //Add and remove View objects to and from the appropriate layouts
        linearLayout.addView(newEditText);
        parentLayout.removeView(addElementButton);
        linearLayout.addView(addElementButton);
        linearLayout.addView(deleteButton);

        //Add the final layout to the elementLayout
        elementLayout.addView(linearLayout);
    }

    /**
     * Checks if all the input is valid in the NewChecklistActivity
     * Called when the user presses the doneButton.
     * @return whether or not the List is valid
     */
    private boolean isValidList(){

        //Check the editNameText
        if(editNameText.getText().toString().equals("") || onlyHasSpace(editNameText.getText().toString())){
            return false;
        }

        //Check all the EditTexts that were generated Programmatically
        for(int i = 2; i < elementLayout.getChildCount(); i++)
        {

            LinearLayout layout = (LinearLayout) elementLayout.getChildAt(i);
            EditText edit = (EditText) layout.getChildAt(0);
            String str = edit.getText().toString();
            Log.i("CHECKLIST", "String: " + str);

            if(str.equals("") || onlyHasSpace(str)){
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a particular String only has spaces
     * @param str the string to be checked
     * @return boolean that holds the value of whether
     * or not the String only has spaces
     */
    private boolean onlyHasSpace(String str){

        if(!str.equals("")){
            //simply loop through the string until the computer finds any char other
            //than a space (' ').
            for(int i = 0; i < str.length(); i++){
                if(str.charAt(i) != ' '){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets the Screen Width in DP. (This method may not be necessary)
     * @param context
     * @return
     */
    public static int getScreenWidthInDPs(Context context){

        /*
            DisplayMetrics
                A structure describing general information about a display,
                such as its size, density, and font scaling.
        */
        DisplayMetrics dm = new DisplayMetrics();

        /*
            WindowManager
                The interface that apps use to talk to the window manager.
                Use Context.getSystemService(Context.WINDOW_SERVICE) to get one of these.
        */

        /*
            public abstract Object getSystemService (String name)
                Return the handle to a system-level service by name. The class of the returned
                object varies by the requested name. Currently available names are:

                WINDOW_SERVICE ("window")
                    The top-level window manager in which you can place custom windows.
                    The returned object is a WindowManager.
        */

        /*
            public abstract Display getDefaultDisplay ()

                Returns the Display upon which this WindowManager instance will create new windows.

                Returns
                The display that this window manager is managing.
        */

        /*
            public void getMetrics (DisplayMetrics outMetrics)
                Gets display metrics that describe the size and density of this display.
                The size is adjusted based on the current rotation of the display.

                Parameters
                outMetrics A DisplayMetrics object to receive the metrics.
        */
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        Log.i("CHECKLIST", "Actual size: " + dm.widthPixels);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;
    }

    /**
     * Gets the Screen size of the Device. (This method may not be necessary)
     * @param context
     * @return
     */
    public static int getActualScreenSize(Context context){

        /*
            DisplayMetrics
                A structure describing general information about a display,
                such as its size, density, and font scaling.
        */
        DisplayMetrics dm = new DisplayMetrics();

        /*
            WindowManager
                The interface that apps use to talk to the window manager.
                Use Context.getSystemService(Context.WINDOW_SERVICE) to get one of these.
        */

        /*
            public abstract Object getSystemService (String name)
                Return the handle to a system-level service by name. The class of the returned
                object varies by the requested name. Currently available names are:

                WINDOW_SERVICE ("window")
                    The top-level window manager in which you can place custom windows.
                    The returned object is a WindowManager.
        */

        /*
            public abstract Display getDefaultDisplay ()

                Returns the Display upon which this WindowManager instance will create new windows.

                Returns
                The display that this window manager is managing.
        */

        /*
            public void getMetrics (DisplayMetrics outMetrics)
                Gets display metrics that describe the size and density of this display.
                The size is adjusted based on the current rotation of the display.

                Parameters
                outMetrics A DisplayMetrics object to receive the metrics.
        */
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;

    }
}
