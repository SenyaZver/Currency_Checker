package com.example.currencychecker;

public class Currency {
    String Name;
    double Value;

    public Currency(String Name, double Value) {
        this.Name = Name;
        this.Value = Value;
    }
    public String getName() {
        return this.Name;
    }
    public double getValue() {
        return this.Value;
    }
}
