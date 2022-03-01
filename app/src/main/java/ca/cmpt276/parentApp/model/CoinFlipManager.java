package ca.cmpt276.parentApp.model;

import android.net.Uri;

import java.util.ArrayList;

/**
 * CoinFlipManager class - manage coin flipping data between model and ui packages
 */
public class CoinFlipManager {

    private final ArrayList<CoinFlip> coinFlipList;
    private final Children pickerQueue;
    private String currentPicker;

    public CoinFlipManager() {
        coinFlipList = new ArrayList<>();
        pickerQueue = new Children();
    }

    public ArrayList<CoinFlip> getCoinFlipList() {
        return coinFlipList;
    }

    public Children getPickerQueue() {
        return pickerQueue;
    }

    public String getCurrentPicker() {
        return currentPicker;
    }

    public void setCurrentPicker(String newPicker) {
        currentPicker = newPicker;
    }

    public CoinFlip getCoinFlip(int index) {
        return coinFlipList.get(index);
    }

    public void addCoinFlip(CoinFlip coinFlip) {
        coinFlipList.add(coinFlip);
        if (coinFlip.getPickerName() == null) {
            return;
        }
        moveChildToEndOfPickerQueue(currentPicker);
        setCurrentPicker(pickerQueue.getChildNameAt(0));
    }

    public void deleteCoinFlipAtIndex(int i) {
        coinFlipList.remove(i);
    }

    public int getNumCoinFlips() {
        return coinFlipList.size();
    }

    public void addChildToPickerQueue(String name) {
        pickerQueue.addChildName(name);
    }

    public void removeChildFromPickerQueue(int i) {
        pickerQueue.removeChildName(i);
    }

    private void moveChildToEndOfPickerQueue(String name) {
        int index = pickerQueue.getIndexOfChild(name);
        pickerQueue.removeChildName(index);
        pickerQueue.addChildName(name);
    }

    public void updateChildNameInCoinFlipList(String oldName, String newName) {
        for (CoinFlip coinFlip : coinFlipList) {
            //update: a null object reference in when editing a name @CoinFlipManager.java:92 @EditChildActivity.java:74
            try{
                if (coinFlip.getPickerName().equals(oldName)) {
                    coinFlip.setPickerName(newName);
                }
            }catch (NullPointerException e){
                //do nothing, only catch the exception
            }
        }
    }

    public void updateChildPicInCoinFlipList(String childName, Uri newPicID) {
        for (CoinFlip coinFlip : coinFlipList) {
            //update: a null object reference in when editing a name @CoinFlipManager.java:92 @EditChildActivity.java:74
            try{
                if (coinFlip.getPickerName().equals(childName)) {
                    coinFlip.setPickerImageID(newPicID);
                }
            }catch (NullPointerException e){
                //do nothing, only catch the exception
            }
        }
    }
}
