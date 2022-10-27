package org.daryl.apicascade.cascades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import static org.junit.jupiter.api.Assertions.*;

class CascadeManagerTest {

    PrintStream originalOutputStream = System.out;
    InputStream originalInputStream = System.in;

    ByteArrayOutputStream capturedOutput;
    ByteArrayInputStream simulatedInput;

    @BeforeEach
    void setUp() {
        simulatedInput = new ByteArrayInputStream("".getBytes());
        simulatedInput.reset();
        System.setIn(simulatedInput);

        capturedOutput = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(capturedOutput);
        System.setOut(ps);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalInputStream);
        System.setOut(originalOutputStream);
    }

    @Test
    void createCascade() {
        String name = "Test";

        // Create a thread to run the createCascade method while processing the input on the main thread.
        Future<Cascade> createdCascade = CompletableFuture.supplyAsync(() -> {
            try {
                return CascadeManager.createCascade(name);
            } catch (FileAlreadyExistsException e) {
                fail(e);
            }
            fail("Something went wrong.");
            return null;
        });
        LocalDateTime startTime = LocalDateTime.now();
        while(!createdCascade.isDone() && LocalDateTime.now().isBefore(startTime.plusMinutes(1))) {
            try {
//                Thread.currentThread().wait(500);
                capturedOutput.flush();
            } catch (IOException e) {
                fail(e);
            }
            String[] capturedOutputArray = capturedOutput.toString().split("\n");
            switch (capturedOutputArray[capturedOutputArray.length - 1]) {
                case "Parameters: ":
                    simulatedInput = new ByteArrayInputStream("serial, asset\n".getBytes());
                case "Please provide the an API endpoint for this cascade or type DONE to finish creating cascade.":
                    simulatedInput = new ByteArrayInputStream("http://example.com/api/v1/{$serial}/{$asset}\n".getBytes());
                    simulatedInput = new ByteArrayInputStream("serial\nasset\ndone\n".getBytes());
                default:
//                    fail("Some other String has been encountered " + capturedOutputArray[capturedOutputArray.length - 1]);
            }
        }

        Cascade generatedCascade = null;
        try {
            generatedCascade = createdCascade.get();
        } catch (InterruptedException e) {
            fail(e);
        } catch (ExecutionException e) {
            fail(e);
        }

        if(generatedCascade == null) {
            fail("Failed to create a Cascade object");
        }

        Cascade trueCascade = new Cascade();

        trueCascade.setName(name);

        Parameter[] parameters = new Parameter[] {new Parameter("serial"), new Parameter("asset")};
        trueCascade.setParameters(new ArrayList<>(List.of(parameters)));

        APIEndpoint[] apiEndpoints = new APIEndpoint[] {new APIEndpoint("http://example.com/api/v1/{$serial}/{$asset}",
                new ArrayList<>(List.of(new ParameterMapping[] {new ParameterMapping("serial", "serial"),
                new ParameterMapping("asset", "asset")})))};
        trueCascade.setApiEndpoints(new ArrayList<>(List.of(apiEndpoints)));

        assertEquals(trueCascade, generatedCascade);
    }
}