package com.center.technology;

import com.center.technology.controllers.Controller;
import com.center.technology.model.Client;
import com.center.technology.model.Horse;
import com.center.technology.model.Parlay;
import com.center.technology.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;


public class Totalizator {

    private static String ADDRESS = "src/main/java/com/center/technology/clientsSheet.txt";
    public static final CyclicBarrier BARRIER = new CyclicBarrier(7);
    private static List<User> users = new ArrayList<>();
    private List<Horse> horses = new ArrayList<>();
    private static Parlay currentParlay;
    private static Client currentClient;
    private static boolean flagAdm = true;
    private static List<String> lines = new ArrayList<>();
    private static Set<String> newLines = new HashSet<>();

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String address) {
        ADDRESS = address;
    }

    public static List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        Totalizator.users = users;
    }

    public static void setLines(List<String> lines) {
        Totalizator.lines = lines;
    }

    public List<Horse> getHorses() {
        return horses;
    }

    public static Parlay getCurrentParlay() {
        return currentParlay;
    }

    public void setCurrentParlay(Parlay currentParlay) {
        this.currentParlay = currentParlay;
    }

    public static Client getCurrentClient() {
        return currentClient;
    }

    public void setCurrentClient(Client currentClient) {
        Totalizator.currentClient = currentClient;
    }

    public static boolean isFlagAdm() {
        return flagAdm;
    }

    public static void setFlagAdm(boolean flagAdm) {
        Totalizator.flagAdm = flagAdm;
    }

    public List<String> getLines() {
        return lines;
    }

    public Set<String> getNewLines() {
        return newLines;
    }

    public Totalizator() {
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.runProgram();
    }
}

