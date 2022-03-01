package ca.cmpt276.parentApp.model;

import android.net.Uri;

import java.lang.reflect.Array;
import java.util.ArrayList;

//TaskManager class - a class that stores the list of tasks.
//Each task is an array list of its own; it is an array list that consists of the entire history of turns on that task.

public class TaskManager {

    private final ArrayList<ArrayList<TurnOnTask>> taskList;
    private static final String UNASSIGNED = "unassigned";
    private static final int TASK_NOT_FOUND = -1;

    public TaskManager(){
        taskList = new ArrayList<>();
    }

    public ArrayList<ArrayList<TurnOnTask>> getTaskList() {
        return taskList;
    }


    public void addTask(String taskDescription){
        ArrayList<TurnOnTask> newTask = new ArrayList<>();
        TurnOnTask firstTurn = new TurnOnTask(taskDescription);
        newTask.add(firstTurn);
        taskList.add(newTask);
    }

    public void removeTask(int i){
        taskList.remove(i);
    }

    public void editTaskDescriptionAt(int i, String newTaskDescription){
        ArrayList<TurnOnTask> currTask = taskList.get(i);
        for (TurnOnTask eachTurn : currTask) {
            eachTurn.setTaskDescription(newTaskDescription);
        }
    }

    public ArrayList<TurnOnTask> getTaskAt(int i){
        return taskList.get(i);
    }

    public int getIndexOfTask(String taskDescription) {
        for (int i=0; i < taskList.size(); i++) {
            if (getCurrentTurnOnTaskAt(i).getTaskDescription().equals(taskDescription)) {
                return i;
            }
        }
        return TASK_NOT_FOUND;
    }

    public int getNumTasks(){
        return taskList.size();
    }

    public void removeChildFromAllAssignedTasks(String name) {
        for (int i=0; i<getNumTasks(); i++) {
            TurnOnTask currTurn = getCurrentTurnOnTaskAt(i);
            if (currTurn.getChildName().equals(name)) {
                currTurn.setChildName(UNASSIGNED);
            }
        }
    }

    public TurnOnTask getCurrentTurnOnTaskAt(int i) {
        ArrayList<TurnOnTask> currTask = taskList.get(i);
        return currTask.get(currTask.size() -1);
    }

    public void assignTaskToNextChild(int taskIndex, String nextChild) {
        TurnOnTask currTurn = getCurrentTurnOnTaskAt(taskIndex);
        String taskDescription = currTurn.getTaskDescription();
        TurnOnTask nextTurn = new TurnOnTask(taskDescription);
        nextTurn.setChildName(nextChild);
        nextTurn.setChildImageUsingChildName(nextChild);
        taskList.get(taskIndex).add(nextTurn);
    }

    public void updateChildNameInAllTurns(String oldName, String newName) {
        for (int i=0; i < getNumTasks(); i++) {
            ArrayList<TurnOnTask> currTask = taskList.get(i);
            for (int j = 0; j < currTask.size(); j++) {
                if (currTask.get(j).getChildName().equals(oldName)) {
                    currTask.get(j).setChildName(newName);
                }
            }
        }
    }

    public void updateChildImageIDInAllTurns(String childName, Uri newImageID) {
        for (int i=0; i < getNumTasks(); i++) {
            ArrayList<TurnOnTask> currTask = taskList.get(i);
            for (int j = 0; j < currTask.size(); j++) {
                if (currTask.get(j).getChildName().equals(childName)) {
                    currTask.get(j).setChildImageIDUsingURI(newImageID);
                }
            }
        }
    }

}
