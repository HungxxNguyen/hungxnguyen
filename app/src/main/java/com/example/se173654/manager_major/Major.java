package com.example.se173654.manager_major;

public class Major {
    private String id;
    private String nameMajor;

    // Constructor mặc định (yêu cầu bởi Retrofit)
    public Major() {}

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameMajor() {
        return nameMajor;
    }

    public void setNameMajor(String nameMajor) {
        this.nameMajor = nameMajor;
    }
}