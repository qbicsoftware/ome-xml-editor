package de.qbic.omeedit.views;

public interface UserInterface {
    void start();
    void loadImage(String path) throws Exception;
    void loadChangeHistory(String path) throws Exception;
    void applyChangeHistory(String path) throws Exception;
    void saveImage(String path) throws Exception;
}