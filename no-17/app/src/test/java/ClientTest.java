/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.Test;

import memcached.client.MemcachedClient;
import memcached.commands.GetCommand;
import memcached.commands.Response;
import memcached.commands.SetCommand;

class ClientTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = ClientTest.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    private String randomKey(String prefix) {
        var rnd = ((int) Math.floor(Math.random() * 10000));
        return String.format("%s%d", prefix, rnd);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void getcmd_simple_expectsNoValue() throws URISyntaxException, IOException {

        // Arrange
        var client = new MemcachedClient("localhost:11212");
        try {
            var started = client.start();

            // Act
            var cmd = new GetCommand(randomKey("asdf"));
            Response response = client.sendCommand(cmd).get();

            // Assert
            assertTrue(started);
            assertEquals("END", response.finalNote);
        } finally {
            client.close();
        }

    }

    @Test
    void setcmd_simple_expectsValue() throws URISyntaxException, IOException {

        // Arrange
        var client = new MemcachedClient("localhost:11212");
        try {
            var started = client.start();
            var key = randomKey("asdf");
            var value = randomKey("testvalue");

            // Act
            var cmd = new SetCommand(key, value);
            Response response = client.sendCommand(cmd).get();

            var cmdGet = new GetCommand(key);
            var responseGet = client.sendCommand(cmdGet);

            // Assert
            assertTrue(started);
            assertEquals("STORED", response.finalNote);
            assertEquals(true, responseGet.isPresent());
        } finally {
            client.close();
        }

    }

    @Test
    void setcmd_simplenoreply_expectsValue() throws URISyntaxException, IOException {

        // Arrange
        var client = new MemcachedClient("localhost:11212");
        try {
            var started = client.start();
            var key = randomKey("asdf");
            var value = randomKey("testvalue");

            // Act
            var cmd = new SetCommand(key, value, 31, 300, true);
            Optional<Response> response = client.sendCommand(cmd);

            var cmdGet = new GetCommand(key);
            var responseGet = client.sendCommand(cmdGet);
            var responseCmd = responseGet.get().cmds.get(0);

            // Assert
            assertTrue(started);
            assertEquals(false, response.isPresent());
            assertEquals(true, responseGet.isPresent());
            assertEquals(31, responseCmd.flags());
            assertEquals(value, responseCmd.data.data);
        } finally {
            client.close();
        }

    }

    @Test
    void getcmd2_simple_expectsValue() throws URISyntaxException, IOException {

        // Arrange
        var client = new MemcachedClient("localhost:11212");
        try {
            var started = client.start();
            var key = randomKey("asdf");
            var value = randomKey("testvalue");

            // Act
            var cmd = new SetCommand(key, value, 31, 300, false);
            Optional<Response> response = client.sendCommand(cmd);

            var cmdGet = new GetCommand(key, key);
            Response responseGet = client.sendCommand(cmdGet).get();
            var responseCmd = responseGet.cmds.get(0);
            var responseCmd2 = responseGet.cmds.get(1);

            // Assert
            assertTrue(started);
            assertEquals(true, response.isPresent());
            assertEquals(31, responseCmd.flags());
            assertEquals(value, responseCmd.data.data);
            assertEquals(value, responseCmd2.data.data);
        } finally {
            client.close();
        }

    }
}
