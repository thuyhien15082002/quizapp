package com.example.doan.model;

public class QuestionModel
{
    String quesID;
    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    int corectAns;

    public QuestionModel( String quesID, String question, String optionA, String optionB, String optionC, String optionD, int corectAns) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.corectAns = corectAns;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorectAns() {
        return corectAns;
    }

    public void setCorectAns(int corectAns) {
        this.corectAns = corectAns;
    }

    public String getQuesID() {
        return quesID;
    }

    public void setQuesID(String quesID) {
        this.quesID = quesID;
    }
}
