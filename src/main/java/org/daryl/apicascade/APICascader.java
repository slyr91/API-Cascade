package org.daryl.apicascade;

import org.apache.commons.cli.*;
import org.daryl.apicascade.cascades.CascadeManager;

import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class APICascader {

    /*
    The main method to start the program. It is run from the command line and provides a method
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
        options.addOption("r", "run", true, "Run the given cascade. Parameters after the arg will be passed to the cascade. Everything is case-sensitive\n" +
                "For example, api-cascade -run example foo=foo bar=bar");
        options.addOption("i", true, "Output the contents of the named cascade.");
        options.addOption("p", true, "Output the parameters of the named cascade.");

        // Parse the commandline for options
        try {
            CommandLine line = parser.parse(options, args);

            //TODO Implement create, edit, and run options.
            if(line.hasOption("gui")) {
                HelloApplication.startGUI();
            } else if(line.hasOption("c")) {
                CascadeManager.createCascade(line.getOptionValue("c"));
            } else if(line.hasOption("e")) {
                CascadeManager.editCascade(line.getOptionValue("e"));
            } else if(line.hasOption("p")) {
                System.out.println(CascadeManager.printParameters(line.getOptionValue("p")));
            } else if(line.hasOption("i")) {
                System.out.println(CascadeManager.printCascade(line.getOptionValue("i")));
            } else if(line.hasOption("r")) {
                List<String> parameters = line.getArgList();
                if(parameters.size() == 0) {
                    CascadeManager.runCascade(line.getOptionValue("r"));
                } else {
                    Map<String, String> parammap = new HashMap<>();
                    for (String parameter :
                            parameters) {
                        System.out.println(parameter);
                        String[] parameterSplit = parameter.split("=");
                        parammap.put(parameterSplit[0], parameterSplit[1]);
                    }

                    CascadeManager.runCascade(line.getOptionValue("r"), parammap);

                }
            } else if(line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("api-cascade [options]", options);
            } else {
                System.out.println("Invalid operation. Use -h for help.");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }
}
