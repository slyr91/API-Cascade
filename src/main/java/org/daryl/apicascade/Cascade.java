package org.daryl.apicascade;

import org.apache.commons.cli.*;

public class Cascade {

    /*
    The main method to start the program. It is ran from the command line and provides a method
    to open a GUI (Under Construction).
     */
    public static void main(String[] args) {
        // Create the commandline parser
        CommandLineParser parser = new DefaultParser();

        // Create the commandline options
        Options options = new Options();
        options.addOption("gui", false, "Opens the gui interface.");
        options.addOption("h", "help", false, "Display the help page.");

        // Parse the commandline for options.
        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("gui")) {
                HelloApplication.startGUI();
            } else if(line.hasOption("h") || !line.getArgList().isEmpty()) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("api-cascade [options]", options);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
