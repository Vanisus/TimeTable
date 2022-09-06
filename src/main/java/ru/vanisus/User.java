package ru.vanisus;

import java.net.URL;

public class User {
    public User(String name, String URL) {
        this.name = name;
        this.url = URL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String name;
    private String url;
}
