package org.mediacat.utils;

public interface Object {
    void register(Observer observer);

    void broadcast();
}
