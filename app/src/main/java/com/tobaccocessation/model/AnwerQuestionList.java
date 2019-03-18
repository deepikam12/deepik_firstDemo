package com.tobaccocessation.model;

import java.io.Serializable;

/**
 * Created by Deepika.Mishra on 11/16/2018.
 */

public class AnwerQuestionList implements Serializable {
    public String getAns_id() {
        return ans_id;
    }

    public void setAns_id(String ans_id) {
        this.ans_id = ans_id;
    }

    public String getQues_id() {
        return ques_id;
    }

    public void setQues_id(String ques_id) {
        this.ques_id = ques_id;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    private  String ans_id;
    private  String ques_id;
    private  String ans;
}
