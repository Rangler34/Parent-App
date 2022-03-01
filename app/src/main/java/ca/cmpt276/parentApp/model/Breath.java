package ca.cmpt276.parentApp.model;

//Breath class - a class that represents the data needed for a Take Breath activity, including the
//total number of breaths the child should take and the current state of the child (inhaling/exhaling)

public class Breath {

    private int numBreath;
    private boolean isInhaling = true;

    public Breath(int breaths) {
        this.numBreath = breaths;
    }

    public int getNumBreath() {
        return numBreath;
    }

    public void setNumBreath(int numBreath) {
        this.numBreath = numBreath;
    }

    public boolean isInhaling() {
        return isInhaling;
    }

    public void reduceBreathAmount() {
        numBreath--;
    }

    public void setInhaling(boolean inhaling) {
        isInhaling = inhaling;
    }
}
