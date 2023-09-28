package com.center.technology.services;

import com.center.technology.Totalizator;
import com.center.technology.model.Client;
import com.center.technology.model.Horse;
import com.center.technology.model.Parlay;
import com.center.technology.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ServiceTest {

    private Service service;
    private Totalizator totalizator;
    private  Client currentClient;
    private Horse horse;

    private String TEST_ADDRESS = "C:\\Users\\Professional\\IdeaProjects\\UUU222\\homework_tester\\Parlay\\" +
            "src\\main\\java\\com\\center\\technology\\clientsSheetTest.txt";

    private String TEST_SERIALIZABLE = "C:\\Users\\Professional\\IdeaProjects\\UUU222\\homework_tester\\Parlay\\" +
            "src\\main\\java\\com\\center\\technology\\serializable.txt";
    private static List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        service = new Service();
        totalizator = new Totalizator();
        currentClient  = new Client("login1", "password1", new User().getYourMoney());
        totalizator.setCurrentClient(currentClient);
        horse = new Horse();
        totalizator.setADDRESS(TEST_ADDRESS);
        totalizator.getUsers().add(new User("user1", "login1", "password1", 500));
    }

    @Test
    void enterSuccess() {
        assertTrue(service.enter("login1", "password1"));
    }

    @Test
    void enterFailure() {
        assertFalse(service.enter("lonig123", "password123"));
    }

    @Test
    void enterAsAdmin() {
        assertFalse(service.enter("admin", "admin"));
    }

    @Test
    void enterAdmSuccess() {
        assertTrue(service.enterAdm("admin", "admin"));
    }

    @Test
    void enterAdmFailure() {
        assertFalse(service.enterAdm("lonig123", "password123"));
    }

    @Test
    void findUserSuccess() {
        User user = service.findUser("login1");
        assertNotNull(user);
        assertEquals("login1", user.getLogin());
    }

    @Test
    void findUserFailure() {
        User user = service.findUser("lonig123");
        assertNotNull(user);
        assertEquals(null, user.getLogin());
    }

    @Test
    void testShowClients() {

        try (FileWriter writer = new FileWriter(TEST_ADDRESS)) {
            writer.write("User: nane =user1, login =login1, password =password1, yourMoney= 500.0,\n");
            writer.write("User: nane =user2, login =login2, password =password2, yourMoney= 500.0,\n");
            writer.write("Admin: nane =admin, login =admin, password =admin, yourMoney =0.0,\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> clients = service.showClients();

        assertNotNull(clients);
        assertEquals(2, clients.size());
        assertEquals("User: nane =user1, login =login1, password =password1, yourMoney= 500.0,", clients.get(0));
        assertEquals("User: nane =user2, login =login2, password =password2, yourMoney= 500.0,", clients.get(1));
    }

    @Test
    void addParlayTest() {
        Parlay newParlay = new Parlay(3, 100);
        totalizator.setCurrentParlay(newParlay);
        service.addParlay();

        List<Parlay> parlays = totalizator.getCurrentClient().getParlays();
        assertTrue(parlays.contains(newParlay));
    }

    @Test
    void addUserNewUserTest() {
        String testName = "user1";
        String testLogin = "login1";
        String testPassword = "password1";
        service.addUser(testName, testLogin, testPassword);

        User newUser = totalizator.getUsers().stream()
                .filter(user -> testLogin.equals(user.getLogin()))
                .findFirst()
                .orElse(null);

        assertNotNull(newUser);
        assertEquals(testName, newUser.getName());
        assertEquals(testLogin, newUser.getLogin());
        assertEquals(testPassword, newUser.getPassword());
    }

    @Test
    void clearAllParlayTest() {
        Parlay newParlay1 = new Parlay(3, 100);
        Parlay newParlay2 = new Parlay(4, 200);

        totalizator.setCurrentParlay(newParlay1);
        service.addParlay();

        totalizator.setCurrentParlay(newParlay2);
        service.addParlay();

        service.clearAllParlay();

        List<Parlay> parlays = totalizator.getCurrentClient().getParlays();
        assertEquals(0, parlays.size());
    }

    @Test
    void addParlayInRegistrationSheetTest() {
        Parlay parlay = new Parlay(5, 55);
        currentClient.addParlay(parlay);
        totalizator.setCurrentClient(currentClient);

        List<String> lines = new ArrayList<>();
        lines.add("User: nane =user1, login =login1, password =password1, yourMoney= 500.0,");
        totalizator.setLines(lines);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH:mm");
        String formattedDateTime = now.format(formatter);
        service.addParlayInRegistrationSheet();

        assertEquals(1, totalizator.getNewLines().size());
        String lastLine = String.valueOf(totalizator.getNewLines());
        assertTrue(lastLine.contains("Дата: " + formattedDateTime));
        assertTrue(lastLine.contains("login1"));
        assertTrue(lastLine.contains("500.0"));
        assertTrue(lastLine.contains(parlay.toString()));
    }

    @Test
    void writingUsersByClientsSheetTxtTest() {
        Totalizator.getUsers().clear();
        try (FileWriter writer = new FileWriter(TEST_ADDRESS)) {
            writer.write("User: nane =user1, login =login1, password =password1, yourMoney= 500.0,\n");
            writer.write("User: nane =user2, login =login2, password =password2, yourMoney= 500.0,\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        service.writingUsersByClientsSheetTxt();

        List<User> users = Totalizator.getUsers();
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getName());
        assertEquals("user2", users.get(1).getName());
    }

    @Test
    void saveSerializableTest() {
        service.saveSerializable();

        Path testSerializationFilePath = Paths.get(TEST_SERIALIZABLE);

        assertTrue(Files.exists(testSerializationFilePath), "Файл сериализации не найден");

        List<User> deserializedUsers = null;

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(testSerializationFilePath.toFile()))) {
            deserializedUsers = (List<User>) objectInputStream.readObject();
            for (var user : deserializedUsers)
                System.out.printf("name = %s, logim = %s, password = %s, \n",
                       user.getName(), user.getLogin(), user.getPassword());

        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Ошибка десериализации: " + ex);
            ex.printStackTrace();
        }

        assertNotNull(deserializedUsers, "Некорректная десериализация данных");
    }

/**
 * для теста следующих двух методов нужно закомментировать строку №250( new Controller().continueParlay(); )
 * метода calculateMoney, класса Service
 */
//    @Test
//    void testCalculateMoneyWin() {
//        horse.setaStat(2);
//        horse.setCoef(5.0);
//        Parlay currentParlay = new Parlay(2, 50);
//
//        double initialMoney = 100.0;
//        Totalizator.getCurrentClient().setYourMoney(initialMoney);
//
//        service.calculateMoney(currentParlay);
//
//        assertEquals(100.0 + (5.0 * 50.0), Totalizator.getCurrentClient().getYourMoney());
//    }
//
//    @Test
//    void testCalculateMoneyLose() {
//        horse.setaStat(2);
//        Parlay currentParlay = new Parlay(1, 50);
//
//        double initialMoney = 100.0;
//        Totalizator.getCurrentClient().setYourMoney(initialMoney);
//
//        service.calculateMoney(currentParlay);
//
//        assertEquals(100.0 - 50.0, Totalizator.getCurrentClient().getYourMoney());
//    }
}