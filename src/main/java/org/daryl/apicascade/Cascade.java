package org.daryl.apicascade;

public class Cascade {

    /*
    The main method to start the program. It is ran from the command line and provides a method
    to open a GUI (Under Construction).
     */
    public static void main(String[] args) {

        if(args[0].equals("-gui")) {
            HelloApplication.startGUI();
        } else {
            System.exit(0);
        }
    }
}
