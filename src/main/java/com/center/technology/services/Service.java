package com.center.technology.services;

import com.center.technology.Totalizator;
import com.center.technology.controllers.Controller;
import com.center.technology.model.Client;
import com.center.technology.model.Horse;
import com.center.technology.model.Parlay;
import com.center.technology.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service {
    Totalizator totalizator = new Totalizator();

    public boolean enter(String login, String password, boolean isAdmin) {
        for (User user : Totalizator.getUsers()) {
            if (isAdmin && "admin".equals(login) && "admin".equals(password)) {
                totalizator.setCurrentClient(new Client(login, password, new User().getYourMoney()));
                return true;
            } else if (!isAdmin && login.equals(user.getLogin()) && password.equals(user.getPassword()) && !"admin".equals(login)) {
                totalizator.setCurrentClient(new Client(login, password, user.getYourMoney()));
                return true;
            }
        }
        System.out.println("Ошибка входа. Повторите попытку.");
        return false;
    }

    public User findUser(String login) {
        for (User user : Totalizator.getUsers()) {
            if (login.equals(user.getLogin())) {
                return user;
            }
        }
        System.out.println("Пользователь не найден");
        return new User();
    }

    public List<String> showClients() {
        List<String> clients = new ArrayList<>();

        try (FileReader reader = new FileReader(totalizator.getADDRESS());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.contains("Admin")) {
                    clients.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public void addParlay() {
        Totalizator.getCurrentClient().addParlay(Totalizator.getCurrentParlay());
    }

    public void addUser(String name, String login, String password) {
        try (FileWriter writer = new FileWriter(totalizator.getADDRESS(), true)) {
            User newUser = new User(name, login, password, new User().getYourMoney());
            saveSerializable();
            Totalizator.getUsers().add(newUser);
            writer.write("\n" + newUser + ",");
            System.out.println("Регистрация завершена. Вы можете сделать ставку");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAllParlay() {
        Totalizator.getCurrentClient().clearParlay();
    }

    public void addParlayInRegistrationSheet() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH:mm");
        String formattedDateTime = now.format(formatter);

        if (Totalizator.getCurrentClient() != null) {
            List<String> parlays = new ArrayList<>();
            for (Parlay parl : Totalizator.getCurrentClient().getParlays()) {
                parlays.add(String.valueOf(parl));
            }

            for (String line : totalizator.getLines()) {
                if (line.contains("login =" + Totalizator.getCurrentClient().getLogin()) && !line.contains("login =admin")) {
                    String regex = "yourMoney= ([\\d.]+),";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.find()) {
                        String matchedValue = matcher.group(1);
                        line = line.replaceAll(matchedValue, String.valueOf(Totalizator.getCurrentClient().getYourMoney()));
                        totalizator.getNewLines().add((line + " Дата: " +
                                formattedDateTime + " = " + parlays + ", "));
                    }
                } else {
                    totalizator.getNewLines().add(line);
                }
            }
        }

        totalizator.getLines().clear();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(totalizator.getADDRESS()))) {
            for (String line : totalizator.getNewLines()) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        totalizator.getNewLines().clear();
    }

    public void writingUsersByClientsSheetTxt() {
        try (BufferedReader reader = new BufferedReader(new FileReader(totalizator.getADDRESS()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                addUserToTotalizatorFromLine(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUserToTotalizatorFromLine(String line) {
        int count = 0;
        List<String> newParts = new ArrayList<>();
        totalizator.getLines().add(line);

        if (line.contains("Admin:")) {
            Totalizator.setFlagAdm(false);
        }

        String[] parts = line.split("(?<=,)");
        for (String par : parts) {
            if (count < 5) {
                String resultString = par.substring(par.indexOf("=") + 1, par.indexOf(","));
                newParts.add(resultString);
                count++;
            }
        }
        User user = new User(newParts.get(0), newParts.get(1),
                newParts.get(2), Double.parseDouble(newParts.get(3)));

        Totalizator.getUsers().add(user);
    }

    public void saveSerializable() {
        String serializationFile = "src\\main\\java\\com\\center\\technology\\serializable.txt";
        Path textFilePath = Paths.get(serializationFile);

        try {
            if (!Files.exists(textFilePath)) {
                Files.createFile(textFilePath);
            }

            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(serializationFile))) {
                objectOutputStream.writeObject(Totalizator.getUsers());
            } catch (IOException ex) {
                System.err.println("Ошибка сериализации: " + ex);
                ex.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Ошибка создания файла: " + e);
            e.printStackTrace();
        }
    }

    public void startingThreads() {
        for (int i = 1; i <= 7; i++) {
            Horse horse = new Horse(i);
            try {
                Thread thread = new Thread(horse);
                thread.start();
                totalizator.getHorses().add(horse);
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.err.println("Ошибка при запуске потоков: " + e);
                e.printStackTrace();
            }
        }
    }

    public void calculateMoney(Parlay currentParlay) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        DecimalFormat df = new DecimalFormat("#.00");
        if (currentParlay.getHorseId() == Horse.getaStat()) {
            double money = Horse.getCoef() * currentParlay.getSum();
            String formattedMoney = df.format(money);
            System.out.println("Вы выиграли " + formattedMoney + "руб");

            double currentMoney = Totalizator.getCurrentClient().getYourMoney() + money;
            String formatCurrentMoney = df.format(currentMoney);
            Totalizator.getCurrentClient().setYourMoney(currentMoney);
            System.out.println("Твой текущий счет: " + formatCurrentMoney + "руб");
        } else {
            double currentMoney = Totalizator.getCurrentClient().getYourMoney() - currentParlay.getSum();
            String formatCurrentMoney = df.format(currentMoney);
            Totalizator.getCurrentClient().setYourMoney(currentMoney);
            System.out.println("Вы проиграли. Ваш текущий остаток = " + formatCurrentMoney);
        }

        for (User user : Totalizator.getUsers()) {
            if (user.getLogin().equals(Totalizator.getCurrentClient().getLogin())) {
                user.setYourMoney(Totalizator.getCurrentClient().getYourMoney());
                break;
            }
        }

        Horse.setaStat(0);
//        new Controller().continueParlay();
    }
}