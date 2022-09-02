package ru.vanisus;

import java.net.URL;

public class User {
    public User(String name, java.net.URL URL) {
        this.name = name;
        this.url = URL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.net.URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    private String name;
    private java.net.URL url;
}
