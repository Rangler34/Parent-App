package ca.cmpt276.parentApp.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Children class - instance stores children's names and profile pictures
 */
public class Children {

    private final int CHILD_NOT_FOUND = -1;

    private ArrayList<String> names;
    private ArrayList<Uri> pictures;

    public Children() {
        names = new ArrayList<>();
        pictures = new ArrayList<>();
    }

    public ArrayList<String> getChildrenNameList() {
        return names;
    }

    public ArrayList<Uri> getPictureList() {
        return pictures;
    }

    public void addChildName(String name) {
        names.add(name);
    }

    public void addChildPic(Uri pic) {
        pictures.add(pic);
    }

    public void removeChildName(int i) {
        names.remove(i);
    }

    public void removeChild(int i) {
        names.remove(i);
        pictures.remove(i);
    }

    public void editChildNameAt(int i, String newName) {
        names.set(i, newName);
    }

    public void editChildPicAt(int i, Uri newPic) {
        pictures.set(i, newPic);
    }

    public String getChildNameAt(int i) {
        return names.get(i);
    }

    public Uri getChildPicAt(int i) {
        return pictures.get(i);
    }

    public int getNumChildren() {
        return names.size();
    }

    public int getIndexOfChild(String name) {
        for (int i=0; i< names.size(); i++) {
            if (name.compareTo(names.get(i)) == 0) {
                return i;
            }
        }
        return CHILD_NOT_FOUND;
    }
}
