package com.center.technology.model;

import com.center.technology.Totalizator;

import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;

public class Horse extends Thread{
    private int horseId;
    private int speed;
    private static double coef;
    private static int aStat = 0;

    public Horse() {}

    public Horse(int id) {
        this.horseId = id;
        this.speed = 40 + (int) (Math.random() * 51);
        this.coef = 1.1 + (Math.random() * 10);
    }

    public int getHorseId() {
        return horseId;
    }

    public void setHorseId(int horseId) {
        this.horseId = horseId;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public static double getCoef() {
        return coef;
    }

    public static void setCoef(double coef) {
        Horse.coef = coef;
    }

    public static int getaStat() {
        return aStat;
    }

    public static void setaStat(int aStat) {
        Horse.aStat = aStat;
    }

    @Override
    public void run() {
        try {
            System.out.println(horseId + " на старте!");
            Totalizator.BARRIER.await();
            Thread.sleep(200000/speed);
            System.out.println("Лошадь №" + horseId + " финишировала!");
            if(aStat == 0) {
                System.out.println("Победитель лошадь № " + horseId + "!!!" );
                aStat = horseId;
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horse horse = (Horse) o;
        return horseId == horse.horseId && speed == horse.speed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(horseId, speed);
    }

    @Override
    public String toString() {
        return "Horse: " +
                "horseId= " + horseId +
                ", speed= " + speed +
                ", coef= " + coef;
    }
}
