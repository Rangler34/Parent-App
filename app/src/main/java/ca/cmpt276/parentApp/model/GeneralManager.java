package ca.cmpt276.parentApp.model;

import android.net.Uri;

/*
General Manager - a singleton class that serves as a master manager that allows other
classes/activities to access all data stored for this app, including data on coin flips,
children information, and tasks.
 */
public class GeneralManager {


    private static volatile GeneralManager INSTANCE = null;

    private final static Children children = new Children();
    private final CoinFlipManager coinFlipManager = new CoinFlipManager();
    private final TaskManager taskManager = new TaskManager();

    private GeneralManager() {}

    public static GeneralManager getInstance(){
        if (INSTANCE == null) {
            synchronized (GeneralManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GeneralManager();
                }
            }
        }
        return INSTANCE;
    }

    public static Children getChildren() {
        return children;
    }

    public CoinFlipManager getCoinFlipManager() {return coinFlipManager;}
    public TaskManager getTaskManager() {return taskManager;}

    public void updateChildNameInAllLists(String childName, String newName) {
        coinFlipManager.updateChildNameInCoinFlipList(childName, newName);
        taskManager.updateChildNameInAllTurns(childName, newName);
    }

    public void updateChildPicInAllLists(String childName, Uri newPicID) {
        coinFlipManager.updateChildPicInCoinFlipList(childName, newPicID);
        taskManager.updateChildImageIDInAllTurns(childName, newPicID);
    }
}
