package com.tobaccocessation.model;

import java.io.Serializable;

/**
 * Created by Deepika.Mishra on 11/16/2018.
 */

public class Answers implements Serializable {
    public  String ansId;
    public  String answer;

    public Answers() {
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect;




    public Answers(String ansId, String answer,boolean isSelect) {
        this.ansId = ansId;
        this.answer = answer;
        this.isSelect = isSelect;

    }



    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
