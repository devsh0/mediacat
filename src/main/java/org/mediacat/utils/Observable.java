package org.mediacat.utils;

public interface Observable {
    void register(Observer observer);

    void broadcast();
}
