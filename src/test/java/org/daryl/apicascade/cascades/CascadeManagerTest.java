package org.daryl.apicascade.cascades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
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
    void createCascadeWithNormalInput() {
        String name = "Test";

        String simulatedInputString = """
                serial,asset\n
                http://example.com/api/v1/devices?serial={$serial}&asset={$asset}\n
                serial\n
                asset\n
                http://example.com/api/v1/devices?serial={$serial}&asset={$asset}&delete={$delete}serial\n
                asset\n
                delete\n
                done\n
                """;
        simulatedInput = new ByteArrayInputStream(simulatedInputString.getBytes());
        System.setIn(simulatedInput);

        Cascade createdCascade = null;
        try {
            createdCascade = CascadeManager.createCascade(name);
        } catch (FileAlreadyExistsException e) {
            fail(e);
        }

        if(createdCascade == null) {
            fail("Failed to create a Cascade object");
        }

        Cascade trueCascade = new Cascade();

        trueCascade.setName(name);

        Parameter[] parameters = new Parameter[] {new Parameter("serial"), new Parameter("asset"), new Parameter("delete")};
        trueCascade.setParameters(new ArrayList<>(List.of(parameters)));

        APIEndpoint[] apiEndpoints = new APIEndpoint[] {new APIEndpoint("http://example.com/api/v1/devices?serial={$serial}&asset={$asset}",
                new ArrayList<>(List.of(new ParameterMapping[] {new ParameterMapping("serial", "serial"),
                new ParameterMapping("asset", "asset")}))),
                new APIEndpoint("http://example.com/api/v1/devices?serial={$serial}&asset={$asset}&delete={$delete}serial",
                        new ArrayList<>(List.of(new ParameterMapping[] {new ParameterMapping("serial", "serial"),
                                new ParameterMapping("asset", "asset"),
                                new ParameterMapping("delete", "delete")})))};
        trueCascade.setApiEndpoints(new ArrayList<>(List.of(apiEndpoints)));

        assertEquals(trueCascade.toString(), trueCascade.toString());

        File cascadeFile = Path.of("./Cascades/" + name + ".yaml").toFile();
        cascadeFile.delete();
    }
}