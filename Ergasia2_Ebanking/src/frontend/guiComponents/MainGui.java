package frontend.guiComponents;

import frontend.guiComponents.MainWindow;

import javax.swing.*;

public class MainGui {

    public static void main(String[] args) {


        //run the graphic user interface
        SwingUtilities.invokeLater(() -> new MainWindow());
    }

}