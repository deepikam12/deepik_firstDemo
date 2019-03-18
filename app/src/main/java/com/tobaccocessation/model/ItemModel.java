package com.tobaccocessation.model;

import java.io.Serializable;

/**
 * Created by Deepika.Mishra on 10/31/2018.
 */
public class ItemModel implements Serializable {
    private String id;
    private String name;
    private boolean isSelect;


    public ItemModel() {
        this.name = name;

    }


    public ItemModel(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public ItemModel(String id, String name, boolean isSelect) {
        this.id = id;
        this.name = name;
        this.isSelect = isSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

}