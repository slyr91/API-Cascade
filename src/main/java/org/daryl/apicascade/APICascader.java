package org.daryl.apicascade;

import org.apache.commons.cli.*;
import org.daryl.apicascade.cascades.CascadeManager;

import java.nio.file.FileAlreadyExistsException;

public class APICascader {

    /*
    The main method to start the program. It is ran from the command line and provides a method
    to open a GUI (Under Construction).
     */
    public static void main(String[] args) {
        // Create the commandline parser
        CommandLineParser parser = new DefaultParser();

        // Create the commandline options
        Options options = new Options();
//        options.addOption("g", "gui", false, "Opens the gui interface.");
        options.addOption("h", "help", false, "Display the help page.");
        options.addOption("c", "create", true, "Create a new cascade with the given arg.");
        options.addOption("e", "edit", true, "Edit the cascade with the given arg.");
        options.addOption("r", "run", true, "Run the given cascade. Parameters after the arg will be passed to the cascade in declared order.\n" +
                "For example, api-cascade -run example foo bar");

        // Parse the commandline for options.
        try {
            CommandLine line = parser.parse(options, args);

            //TODO Implement create, edit, and run options.
            if(line.hasOption("gui")) {
                HelloApplication.startGUI();
            } else if(line.hasOption("c")) {
                CascadeManager.createCascade(line.getOptionValue("c"));
            } else if(line.hasOption("e")) {
                CascadeManager.editCascade(line.getOptionValue("e"));
            } else if(line.hasOption("h") || !line.getArgList().isEmpty()) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("api-cascade [options]", options);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }
}
