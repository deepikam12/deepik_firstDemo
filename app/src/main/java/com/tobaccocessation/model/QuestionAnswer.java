package com.tobaccocessation.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Deepika.Mishra on 11/13/2018.
 */

public class QuestionAnswer implements Serializable {

    private String id;
    private String about;
    private String extra_input;

    private String question_type;

    private ArrayList<Answers> answersList;
    private ArrayList<SuggestionList> suggestionList;

    public ArrayList<Answers> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(ArrayList<Answers> answersList) {
        this.answersList = answersList;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    private Options options;
    private String icon;

    private String sequence;
    private String mandatory;


    private String input_type;

    public String getOption_1() {
        return option_1;
    }

    public void setOption_1(String option_1) {
        this.option_1 = option_1;
    }

    public String getOption_2() {
        return option_2;
    }

    public void setOption_2(String option_2) {
        this.option_2 = option_2;
    }

    public String getOption_3() {
        return option_3;
    }

    public void setOption_3(String option_3) {
        this.option_3 = option_3;
    }

    public String getOption_4() {
        return option_4;
    }

    public void setOption_4(String option_4) {
        this.option_4 = option_4;
    }

    public String getOption_5() {
        return option_5;
    }

    public void setOption_5(String option_5) {
        this.option_5 = option_5;
    }

    public String getOption_6() {
        return option_6;
    }

    public void setOption_6(String option_6) {
        this.option_6 = option_6;
    }

    private String option_1;
    private String option_2;

    private String option_3;


    private String option_4;
    private String option_5;
    private String option_6;


    private String question;

    private String editable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtra_input() {
        return extra_input;
    }

    public void setExtra_input(String extra_input) {
        this.extra_input = extra_input;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }


    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        this.input_type = input_type;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<SuggestionList> getSuggestionList() {
        return suggestionList;
    }

    public void setSuggestionList(ArrayList<SuggestionList> suggestionList) {
        this.suggestionList = suggestionList;
    }
}
