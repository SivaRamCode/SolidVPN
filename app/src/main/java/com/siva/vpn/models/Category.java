package com.siva.vpn.models;

public class Category {
    private String categoryname;
    private String categoryflag;

    public Category(String categoryname, String categoryflag) {
        this.categoryname = categoryname;
        this.categoryflag = categoryflag;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCategoryflag() {
        return categoryflag;
    }

    public void setCategoryflag(String categoryflag) {
        this.categoryflag = categoryflag;
    }
}
