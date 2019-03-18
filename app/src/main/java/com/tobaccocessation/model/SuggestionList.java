package com.tobaccocessation.model;

import java.io.Serializable;

/**
 * Created by Deepika.Mishra on 1/3/2019.
 */

public class SuggestionList implements Serializable {
    private String title;
    private String content;
    private String sugg_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getSugg_id() {
        return sugg_id;
    }

    public void setSugg_id(String sugg_id) {
        this.sugg_id = sugg_id;
    }


}
