package com.center.technology.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends User{

    private static List<Parlay> parlays = new ArrayList<>();

    public Client() {
    }

    public Client(String login, String password, double yourMoney) {
        super(login, password, yourMoney);
    }

    public Client(String name, String login, String password, double yourMoney) {
        super(name, login, password, yourMoney);
    }

    public Client(String name, String login, String password, double yourMoney, List<Parlay> parlays) {
        super(name, login, password, yourMoney);
        this.parlays = parlays;
    }

    public List<Parlay> getParlays() {
        return parlays;
    }

    public void addParlay(Parlay parlay) {
        parlays.add(parlay);
    }

    public void clearParlay(){
        parlays.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(parlays, client.parlays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parlays);
    }

    @Override
    public String toString() {
        return "Client: " +
                "parlays = " + parlays;
    }
}
