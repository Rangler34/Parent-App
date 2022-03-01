package ca.cmpt276.parentApp.model;

import android.net.Uri;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//TurnOnTask class - a class that represents each turn on a task.
//Stores the details of each turn, including the date the turn took place and the details of the
//child that was on the task for that turn.

public class TurnOnTask {

    private final GeneralManager genManager = GeneralManager.getInstance();
    private String taskDescription;
    private String date;
    private String childName;
    private Uri childImageID;
    private static final String UNASSIGNED = "unassigned";

    public TurnOnTask() {}

    public TurnOnTask(String taskDescription) {
        this.taskDescription = taskDescription;
        this.childName = UNASSIGNED;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String name) {
        this.childName = name;
    }

    public Uri getChildImageID() {
        return childImageID;
    }

    public void setChildImageUsingChildName(String name) {
        int index = genManager.getChildren().getIndexOfChild(name);
        this.childImageID = genManager.getChildren().getChildPicAt(index);
    }

    public void setChildImageIDUsingURI(Uri imageID) {
        childImageID = imageID;
    }

    public String getDateOfTurn() {
        return date;
    }

    public void recordDateOfTurn() {
        LocalDate localDate = LocalDate.now();
        date = localDate.format(DateTimeFormatter.ofPattern("MMM dd"));
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

}
