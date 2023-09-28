package com.center.technology.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String name;
    private String login;
    private String password;
    private double yourMoney = 500.0;

    public User(){}

    public User(String login) {
        this.login = login;
    }

    public User(String login, String password, double yourMoney) {
        this.login = login;
        this.password = password;
        this.yourMoney = yourMoney;
    }

    public User(String name, String login, String password, double yourMoney) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.yourMoney = yourMoney;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getYourMoney() {
        return yourMoney;
    }

    public void setYourMoney(double yourMoney2) {
        yourMoney = yourMoney2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(login, user.login) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, login, password);
    }

    @Override
    public String toString() {
        return "User: " +
                "name =" + name +
                ", login =" + login +
                ", password =" + password +
                ", yourMoney= " + yourMoney;
    }
}