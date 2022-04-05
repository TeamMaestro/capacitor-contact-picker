package com.teamhive.capacitor.utils;

public interface Visitable<T> {
    void accept(Visitor<T> visitor);
}
