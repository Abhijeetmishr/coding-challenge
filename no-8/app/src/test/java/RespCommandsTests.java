
/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import redis.resp.RespException;
import redis.resp.RespInlineCommandScanner;
import redis.resp.RespScanner;
import redis.resp.types.RespType;

class RespCommandsTests {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = RespTypeTests.class.getResource("testcommands/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "valid, step1, 1",
            "valid, step1, 2",
            "valid, step1, 3"
    })
    void validX_stepX_expectok(String type, String step, int index)
            throws URISyntaxException, IOException, RespException {
        ReadReader(step + "/" + type + index + ".txt");

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                // Arrange
                var expectedCount = Integer.parseInt(br.readLine());
                var escapedResult = br.readLine();
                Optional<RespType> expectedArray = Optional.empty();
                if (!escapedResult.isEmpty()) {
                    expectedArray = RespScanner.fromEscapedString(escapedResult).next();
                }
                var scanner = new RespInlineCommandScanner(line);

                if (type.equals("valid")) {
                    // Act
                    var inlineCommand = scanner.nextInlineCommand();
                    if (inlineCommand.isEmpty()) {
                        fail("Command is empty");
                    }
                    var commandCount = inlineCommand.get().size();

                    // Assert
                    assertEquals(expectedCount, commandCount, "count should be the same");
                    if (expectedArray.isPresent()) {
                        var escapedArray = expectedArray.get().toRespEscapedString();
                        var command = inlineCommand.get().toCommand();
                        var realArray = command.array;
                        var realArrayEscapedString = realArray.toRespEscapedString();
                        assertEquals(escapedArray, realArrayEscapedString, "resp string is not the same");
                    }

                } else {
                    assertThrows(RespException.class, () -> {
                        scanner.nextCommand();
                    });
                }
            }
        }

    }

}