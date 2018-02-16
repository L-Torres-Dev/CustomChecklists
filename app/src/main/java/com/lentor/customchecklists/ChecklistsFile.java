package com.lentor.customchecklists;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Luis Torres on 12/10/2017.
 */

public class ChecklistsFile {

    private static final int NAME_TYPE = 0;
    private static final int ELEMENT_TYPE = 1;
    private static final int D_ELEMENT_TYPE = 2;
    private static final int END_OF_CHECKLIST = 3;


    /**
     * Takes the data File containing all the Checklist data and passes it to a string
     * to return. This String will be passed to a String Object to be read.
     * @param context The Application Context
     * @param dataFileName The name of the file
     * @return The String Object containing all Checklist data
     */
    public static String loadChecklist(Context context, String dataFileName) {

        String loadedData = "";
        try{
            FileInputStream fis = context.openFileInput(dataFileName);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            loadedData = new String(buffer);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return loadedData;
    }

    /**
     * Overwrites the data file with all of the Checklists that the user created
     * or manipulated into the
     * @param context The Application Context
     * @param dataFileName The name of the file
     * @param data The String of data to be saved.
     */
    public static void saveChecklists(Context context, String dataFileName, String data)
    {
        OutputStream outputStream;

        try {
            outputStream = context.openFileOutput(dataFileName, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a String Object to be used to create txt file so that
     * the user can load the file later.
     * @param checklists the ArrayList of Checklist Objects to be re-written
     * in the form of a String Object
     * @return the string of data to be used to write the .txt file later
     */
    public static String writeData(ArrayList<Checklist> checklists)
    {
        StringBuilder sb = new StringBuilder("<");
        String str = "";

        char startChecklists = '<';								//char value that holds the start of the data
        char endChecklists = '>';								//char value that holds the end of the data
        char endElement = '!';									//char value that holds the end of an element
        char endElementType = '#';								//char value that holds the end of an element type
        char nextChecklist = '\n';								//char value that holds the end of a Checklist

        for(int i = 0; i < checklists.size(); i++)
        {
            sb.append(checklists.get(i).getName() + endElementType);

            for(int k = 0; k < checklists.get(i).getElements().size(); k++)
            {
                if(k < checklists.get(i).getElements().size() - 1)
                {
                    sb.append(checklists.get(i).getElements().get(k) + endElement);
                }

                else
                {
                    sb.append(checklists.get(i).getElements().get(k) + endElementType);
                }
            }

            if(checklists.get(i).getdElements().size() > 0)
            {
                for(int j = 0; j < checklists.get(i).getdElements().size(); j++)
                {
                    if(j < checklists.get(i).getdElements().size() - 1)
                    {
                        sb.append(checklists.get(i).getdElements().get(j) + endElementType);
                    }

                    else
                    {
                        sb.append(checklists.get(i).getdElements().get(j) + nextChecklist);
                    }
                }
            }

            else{
                sb.append(nextChecklist);
            }
        }
        sb.append(endChecklists);
        str = sb.toString();
        return str;
    }

    /**
     * Reads the String taken from a txt file and creates a
     * Checklist ArrayList out of it so the user can see all
     * of their checklists in the app
     * @param data the data to be read
     * @return the ArrayList of checklists
     */
    public static ArrayList<Checklist> readData(String data)
    {
        ArrayList<Checklist> checklists = new ArrayList<>();
        char startChecklists = '<';								//char value that holds the start of the data
        char endChecklists = '>';								//char value that holds the end of the data
        char endElement = '!';									//char value that holds the end of an element
        char endElementType = '#';								//char value that holds the end of an element type
        char nextChecklist = '\n';								//char value that holds the end of a Checklist

        int i = 1;
        int checklistCounter = 1;
        int checklistCount = countChecklists(data);

        if(data.charAt(0) == startChecklists)
        {
            StringBuilder name = new StringBuilder("");
            ArrayList<String> elements = new ArrayList<>();
            ArrayList<String> dElements = new ArrayList<>();

            int dataType = 0;

            while(data.charAt(i) != endChecklists)
            {
                StringBuilder sb = new StringBuilder("");

                //Detemine Which Element type is currently being iterated over.
                switch(dataType)
                {
                    case NAME_TYPE:
                        /*Ensures that on subsequent checklists the first character of the name of the
	        			is appended to the StringBuilder, sb, Object*/
                        if(checklistCounter > 1 && data.charAt(i - 1) != '\n')
                        {
                            i--;
                        }

                        //Make a loop that ends when the character being iterated over is the endElementType character
                        while(data.charAt(i) != endElementType)
                        {
                            sb.append(data.charAt(i));
                            i++;
                        }
                        dataType++;
                        name.append(sb.toString());
                        sb.delete(0, sb.length() - 1);
                        break;

                    case ELEMENT_TYPE:
                        while(data.charAt(i) != endElementType)
                        {
                            //Initiate boolean logic
                            boolean isEndElement = data.charAt(i) == endElement;
                            boolean isEndElementType = data.charAt(i) == endElementType;
                            boolean bool = (!isEndElement || isEndElementType) && (!isEndElementType || isEndElement);

                            //while loop to iterate over the Elements of the current checklist
                            while(bool)
                            {
                                //append the i'th character to the StringBuilder Object
                                sb.append(data.charAt(i));
                                i++;

                                //Re-evaluate boolean logic
                                isEndElement = data.charAt(i) == endElement;
                                isEndElementType = data.charAt(i) == endElementType;
                                bool = (!isEndElement || isEndElementType) && (!isEndElementType || isEndElement);
                            }
                            //convert Stringbuilder object to String then add it to the elements String ArrayList
                            //Afterward clear the StringBuilder Object so that it is an empty array of Characters
                            elements.add(sb.toString());
                            sb.delete(0, sb.length());

                            //checks to see if the current character of the ith iteration is the endElement character ('!')
                            //if it is, increment i by one. if not then skin the code. This code ensures that the loop never
                            //throws a StringIndexOutOfBoundsException.
                            if(isEndElement)
                            {
                                i++;
                            }

                        }

                        //This code checks if the the i'th character of the data is the endElementType character, ('#')
                        //This code ensures that the first character of the first dElement of the checklist is not skipped.
                        if(data.charAt(i) != endElementType)
                        {
                            i++;
                        }
                        dataType++;
                        break;

                    case D_ELEMENT_TYPE:
                        while(data.charAt(i) != nextChecklist)
                        {
                            //Initiate boolean logic
                            boolean isEndElement = data.charAt(i) == endElement;
                            boolean isEndChecklist = data.charAt(i) == nextChecklist;
                            boolean bool = !isEndElement && !isEndChecklist;

                            //while loop to iterate over the dElements of the current checklist
                            while(bool)
                            {
                                //append the i'th character to the StringBuilder
                                sb.append(data.charAt(i));
                                i++;

                                //Re-evaluate boolean logic
                                isEndElement = data.charAt(i) == endElement;
                                isEndChecklist = data.charAt(i) == nextChecklist;
                                bool = !isEndElement && !isEndChecklist;
                            }
                            //convert Stringbuilder object to String then add it to the dElements String ArrayList
                            //Afterward clear the StringBuilder Object so that it is an empty array of Characters
                            dElements.add(sb.toString());
                            sb.delete(0, sb.length());

                            //checks to see if the current character of the ith iteration is the endElement character ('!')
                            //if it is, increment i by one. if not then skin the code. This is to ensure that the first dElement
                            //of the checklist contains its first character.
                            if(isEndElement)
                            {
                                i++;
                            }

                        }

                        //Checks the checklistCounter to see if it's on the last checklist. if it is, decrement i so that
                        //the loop remains true for one last iteration. Without this code the outermost loop would skip
                        //the last checklist to create.
                        if(checklistCounter == checklistCount)
                        {
                            i--;
                        }
                        dataType++;
                        break;

                    case END_OF_CHECKLIST:
                        //Code to handle what happens when the a checklist is finished being iterated over..
                        //It instantiates a new Checklist Object and adds it to the checklists Arraylist.
                        checklists.add(new Checklist(name.toString(), elements, dElements));
                        dataType = 0;

                        //increment checklistCounter to keep track of which checklist the loop
                        //is iterating over
                        checklistCounter++;

                        //Reset the loop variables so that they are ready fo the next loop.
                        name = new StringBuilder("");
                        elements = new ArrayList<>();
                        dElements = new ArrayList<>();
                        break;

                    default:
                        System.out.println("ERROR NO DATA TYPE FOUND");
                }
                i++;
            }
        }

        else {
            System.out.println("ERROR, DATA NOT FOUND OR INCORRECT FORMAT!");
        }

        return checklists;
    }

    /**
     * Returns a string containing data of a single Checklist within an array of
     * Checklists.
     * @param checklists the Arrray of Checklists to search within
     * @param index The index at which the Checklist will be located in the data
     * @return the String Object containing the data of the Checklists
     */
    public static String readSingleChecklist(ArrayList<Checklist> checklists, int index){
        int stringIndexStart = 0;
        int stringIndexEnd;

        String data = writeData(checklists);

        for(int i = 0; i < index; i++){
            stringIndexStart = data.indexOf('\n', stringIndexStart + 1);
        }

        stringIndexEnd = data.indexOf('\n', stringIndexStart + 1);
        String newData = "<";
        newData += data.substring(stringIndexStart + 1, stringIndexEnd) + "\n>";


        return newData;
    }

    /**
     * Code that counts the number of checklists from the data
     * This code is run before the initial loop so that the computer
     * keeps track of which checklist it is currently evaluating
     * @param data the String of data being iterated over
     * @return the number of checklists in the data String
     */
    private static int countChecklists(String data)
    {
        char newLine = '\n';
        int counter = 0;

        for(int i = 0; i < data.length(); i++)
        {
            if(data.charAt(i) == newLine)
            {
                counter++;
            }
        }

        return counter;
    }
}
