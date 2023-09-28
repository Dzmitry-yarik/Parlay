package com.center.technology.model;

import java.util.Objects;

public class Parlay {

    private int horseId;
    private double sum;

    public Parlay(int horse, double sum) {
        this.horseId = horse;
        this.sum = sum;
    }

    public int getHorseId() {
        return horseId;
    }

    public void setHorseId(int horseId) {
        this.horseId = horseId;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parlay parlay = (Parlay) o;
        return horseId == parlay.horseId && Double.compare(parlay.sum, sum) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(horseId, sum);
    }

    @Override
    public String toString() {
        return "Parlay: " +
                "horse = " + horseId +
                ", sum = " + sum;
    }
}
