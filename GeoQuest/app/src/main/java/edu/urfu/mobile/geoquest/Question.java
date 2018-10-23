package edu.urfu.mobile.geoquest;

public class Question {
    private int textResId;
    private boolean trueAnswer;

    public Question(int textResId, boolean trueAnswer) {
        this.textResId = textResId;
        this.trueAnswer = trueAnswer;
    }

    public int getTextResId() {
        return textResId;
    }

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }

    public boolean isAnswerTrue() {
        return trueAnswer;
    }

    public void setTrueAnswer(boolean trueAnswer) {
        this.trueAnswer = trueAnswer;
    }
}