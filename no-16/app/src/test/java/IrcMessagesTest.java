/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import irc.handler.IrcProtocolHandler;
import irc.message.IrcGeneralMessage;
import irc.message.IrcPingMessage;
import irc.message.IrcPrivMessage;

class IrcMessagesTest {

    private BufferedReader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = IrcMessagesTest.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new BufferedReader(new FileReader(file));
    }

    List<String> getTestFileLines(String testfile) throws URISyntaxException, IOException {
        this.ReadReader(testfile);
        var line = this.reader.readLine();
        var list = new ArrayList<String>();
        while (line != null) {
            if (!line.isEmpty() && !line.isBlank()) {
                list.add(line);
            }
            line = this.reader.readLine();
        }
        return list;

    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void parse_ping_expectsok() throws URISyntaxException, IOException {
        // Arrange
        var list = this.getTestFileLines("ping-valid.txt");

        // Action
        for (String msg : list) {
            var ping = IrcPingMessage.parsePing(msg);
            assertTrue(ping.isPresent());
        }

    }

    @Test
    void parse_ping_expectsnotok() throws URISyntaxException, IOException {
        // Arrange
        var list = this.getTestFileLines("ping-invalid.txt");

        // Action
        for (String msg : list) {
            var ping = IrcPingMessage.parsePing(msg);
            assertTrue(ping.isEmpty(), msg);
        }

    }

    @Test
    void parse_privmsg_expectsok() throws URISyntaxException, IOException {
        // Arrange
        var list = this.getTestFileLines("privmsg-valid.txt");

        // Action
        for (String msg : list) {
            var ping = IrcPrivMessage.parsePrivMsg(msg);
            assertTrue(ping.isPresent(), msg);
        }

    }

    @Test
    void parse_privmsg_expectsnotok() throws URISyntaxException, IOException {
        // Arrange
        var list = this.getTestFileLines("privmsg-invalid.txt");

        // Action
        for (String msg : list) {
            var ping = IrcPrivMessage.parsePrivMsg(msg);
            assertTrue(ping.isEmpty(), msg);
        }

    }

    @Test
    void parse_genmsg1_expectsok() throws URISyntaxException, IOException {
        // Arrange
        var list = this.getTestFileLines("generalmsg-valid-1.txt");

        // Action
        for (String msg : list) {
            var anyMsg = IrcGeneralMessage.parseGeneralMsg(msg);
            assertTrue(anyMsg.isPresent(), msg);
        }
    }

    @Test
    void parse_anymsg_expectsok() throws URISyntaxException, IOException {
        // Arrange
        var allLists = new ArrayList<String>();
        allLists.addAll(this.getTestFileLines("ping-valid.txt"));
        allLists.addAll(this.getTestFileLines("privmsg-valid.txt"));
        allLists.addAll(this.getTestFileLines("generalmsg-valid-1.txt"));

        // Action
        var handler = new IrcProtocolHandler(null);
        for (String msg : allLists) {
            var anyMessage = handler.parseMessage(msg);
            assertTrue(anyMessage.isPresent(), msg);
        }
    }

}
