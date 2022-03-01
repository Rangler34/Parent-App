package ca.cmpt276.parentApp.model;

import android.net.Uri;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * CoinFlip class - a class that stores the details of a single coin flip, including the date and
 * time of the flip, the information about the picker, the result of the coin flip, etc.
 */
public class CoinFlip {

    private String dateTime;
    private String pickerName; // name of the child that picked heads vs tails
    private Uri pickerImageID;
    private int coinResult; // head = 0 or tail = 1
    private boolean isWon = false; //whether the ‘picker’ won

    //coin flip only, no children and sides picking
    public CoinFlip() {
        setDateTime();
        setCoinResult();
    }

    public CoinFlip(String pickerName, Uri pickerImageID, int sidePicked) {
        setDateTime();
        this.pickerName = pickerName;
        this.pickerImageID = pickerImageID;
        setCoinResult();
        if (sidePicked == coinResult) {
            setWon(true);
        } else {
            setWon(false);
        }
    }

    private void setCoinResult() {
        this.coinResult = generateRandomFlip();
    }

    private void setDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        dateTime = localDateTime.format(DateTimeFormatter.ofPattern("MMM dd @ hh:mm a"));
    }

    public void setPickerName(String pickerName) {
        this.pickerName = pickerName;
    }

    public void setPickerImageID(Uri imageID) {
        pickerImageID = imageID;
    }
    public Uri getPickerImageID() {
        return pickerImageID;
    }
    private void setWon(boolean isWon) {
        this.isWon = isWon;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPickerName() {
        return pickerName;
    }

    public int getCoinResult() {
        return coinResult;
    }

    public boolean isWon() {
        return isWon;
    }

    private int generateRandomFlip() {
        Random randNum = new Random();
        return randNum.nextInt(2);//start from 0
    }

}
