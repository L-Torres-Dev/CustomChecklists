package com.lentor.customchecklists;

import java.util.ArrayList;

/**
 * Created by Luis Torres on 12/10/2017.
 */

public class Checklist {

    private String name;
    private ArrayList<String> elements;
    private ArrayList<String> dElements;

    Checklist(){

    }

    Checklist(String name, ArrayList<String> elements){

        this.name = name;
        this.elements = elements;
    }

    Checklist(String name, ArrayList<String> elements, ArrayList<String> dElements){

        this.name = name;
        this.elements = elements;
        this.dElements = dElements;
    }

    public String listElements()
    {
        String str = "";

        for(int i = 0; i < elements.size(); i++)
        {
            str += elements.get(i) + " ";
        }

        return str;
    }

    public String listDElements()
    {
        String str = "";

        for(int i = 0; i < dElements.size(); i++)
        {
            str += dElements.get(i) + " ";
        }

        return str;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getElements() {
        return elements;
    }

    public void setElements(ArrayList<String> elements) {
        this.elements = elements;
    }

    public ArrayList<String> getdElements() {
        return dElements;
    }

    public void setdElements(ArrayList<String> dElements) {
        this.dElements = dElements;
    }
}
