package com.center.technology.controllers;

import com.center.technology.Totalizator;
import com.center.technology.model.Client;
import com.center.technology.model.Parlay;
import com.center.technology.model.User;
import com.center.technology.services.Service;

import java.util.Scanner;

public class Controller {

    private Service service = new Service();
    private Scanner scanner = new Scanner(System.in);

    public void runProgram() {
        boolean flag = true;

        while (flag) {
            int ans = 0;
            do {
                System.out.print("""
                        Вы зарегистрированы?
                        1. Да
                        2. Нет
                        3. Войти как администратор
                        4. Завершить программу
                        """);
                try {
                    ans = Integer.parseInt(scanner.nextLine());
                    if (ans < 1 || ans > 4) {
                        System.out.println("Введены некорректные данные");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Вы ввели не цифру");
                }
            } while (ans < 1 || ans > 4);

            switch (ans) {
                case 1 -> {
                    enterUser(false);
                    flag = false;
                }
                case 2 -> {
                    registration();
                    flag = false;
                }
                case 3 -> {
                    enterUser(true);
                    flag = false;
                }
                case 4 -> flag = false;

                default -> throw new IllegalStateException("Unexpected value: " + ans);
            }
        }
    }

    public void enterUser(boolean isAdmin) {
        service.writingUsersByClientsSheetTxt();
        boolean userEntered = false;

        while (!userEntered) {
            System.out.println("ВХОД В СИСТЕМУ");
            System.out.println("Введите login:");
            String login = scanner.nextLine();
            System.out.println("Введите password:");
            String password = scanner.nextLine();
            userEntered = service.enter(login, password, isAdmin);
        }

        if (isAdmin) {
            System.out.println("Вы вошли от имени администратора");
            adminSettings();
        } else {
            System.out.println("Вы вошли и можете сделать ставку");
            runFacade();
        }
    }

    public void registration() {
        String name;
        String login;
        String password;
        boolean findUserFlag;
        do {
            System.out.println("РЕГИСТРАЦИЯ");
            System.out.println("Введите name:");
            name = scanner.nextLine().replaceAll("\\s", "");
            System.out.println("Введите login:");
            login = scanner.nextLine().replaceAll("\\s", "");
            System.out.println("Введите password:");
            password = scanner.nextLine().replaceAll("\\s", "");
            if (name.isEmpty() || login.isEmpty() || password.isEmpty()) {
                System.out.println("Ошибка: все поля должны быть заполнены.");
            }

            if ("null".equals(String.valueOf(service.findUser(login).getLogin()))) {
                findUserFlag = true;
            } else {
                findUserFlag = false;
                System.out.println("Пользователь с таким логином уже существует. Повторите ввод");
            }

        } while (name.isEmpty() || login.isEmpty() || password.isEmpty() || !findUserFlag);

        new Totalizator().setCurrentClient(new Client(login, password, new User().getYourMoney()));
        service.addUser(name, login, password);
        service.writingUsersByClientsSheetTxt();

        runFacade();
    }

    public void runFacade() {
        createParlay();
        service.startingThreads();
        service.calculateMoney(Totalizator.getCurrentParlay());
    }

    public void adminSettings() {
        System.out.print("""
                1. Найти UserA
                2. Очистить лист ставок
                3. Вывести список клиентов в консоль
                4. Вернуться в меню
                """);
        int adm = Integer.parseInt(scanner.nextLine());

        switch (adm) {
            case 1 -> {
                String logS;
                do {
                    System.out.println("Введите login: ");
                    logS = scanner.nextLine();
                    System.out.println(service.findUser(logS));
                    adminSettings();
                } while (logS == null || logS.isEmpty());
            }
            case 2 -> {
                service.clearAllParlay();
                adminSettings();
            }
            case 3 -> {
                System.out.println(service.showClients());
                adminSettings();
            }
            case 4 -> runProgram();
            default -> {
                System.out.println("выбран неправильный пункт меню, повторите ввод.");
                adminSettings();
            }
        }
    }

    public void createParlay() {
        Totalizator totalizator = new Totalizator();
        int horseId = 0;
        do {
            System.out.print("Выберите номер лошади от 1 до 7: ");
            try {
                horseId = Integer.parseInt(scanner.nextLine());
                if (horseId < 1 || horseId > 7) {
                    System.out.println("Выбран неверный номер, повторите ввод.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели не цифру. Повторите ввод");
            }
        } while (horseId < 1 || horseId > 7);

        int sum = 0;
        double controlCheck;
        do {
            System.out.print("Введите сумму ставки: ");
            try {
                sum = Integer.parseInt(scanner.nextLine());
                controlCheck = Totalizator.getCurrentClient().getYourMoney() - sum;

                if (controlCheck < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели не цифру или у вас не достаточно средств. Проверьте данные и попробуйте снова");
                controlCheck = -1;
            }
        } while (sum <= 0 || controlCheck < 0);

        totalizator.setCurrentParlay(new Parlay(horseId, sum));
        new Service().addParlay();
    }

    public void continueParlay() {
        int ans = 0;
        do {
            System.out.print("""
                    Желаете испытать удачу ещё раз?
                    1. Да
                    2. Нет. Вернуться в меню
                    """);
            try {
                ans = Integer.parseInt(scanner.nextLine());
                if (ans < 1 || ans > 2) {
                    System.out.println("Выбран неверный номер, повторите ввод.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели не цифру. Повторите ввод");
            }
        } while (ans < 1 || ans > 2);

        switch (ans) {
            case 1 -> runFacade();
            case 2 -> {
                service.addParlayInRegistrationSheet();
                service.clearAllParlay();
                runProgram();
            }
        }
    }
}
