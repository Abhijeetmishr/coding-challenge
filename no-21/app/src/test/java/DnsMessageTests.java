/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dns.DnsMessage;
import dns.DnsMessage.Flags;
import dns.DnsMessage.HeaderFlags;
import dns.DnsQuestion;

class DnsMessageTests {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = DnsMessageTests.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void header_22rec_expectsok() {
        // Arrange
        var msg = new DnsMessage(22, Flags.RECURSION_DESIRED);
        var builder = new StringBuilder();

        // Act
        msg.buildHeader(builder);

        // Assert
        assertEquals("001601000000000000000000", builder.toString());

    }

    @Test
    void header_65535rec_expectsok() {
        // Arrange
        var msg = new DnsMessage(65535, 65535);
        var builder = new StringBuilder();

        // Act
        msg.buildHeader(builder);

        // Assert
        assertEquals("FFFFFFFF0000000000000000", builder.toString());

    }

    @Test
    void flag_recursiveReq_expectsok() {
        // Arrange

        // Act
        var flag = Flags.RECURSION_DESIRED;

        // Assert
        assertEquals(256, flag);

    }

    @Test
    void question_default_expectsok() {
        // Arrange

        // Act
        var q = new DnsQuestion("www.google.com");

        // Assert
        assertEquals(HeaderFlags.QCLASS_INTERNET, q.getClazz());
        assertEquals(HeaderFlags.QTYPE_A, q.getType());
        assertEquals("3www6google3com0", q.encodedName());

    }

    @Test
    void questionencoded_default_expectsok() {
        // Arrange
        var q = new DnsQuestion("www.google.com");
        var builder = new StringBuilder();

        // Act
        q.buildHeader(builder);

        // Assert
        assertEquals("3377777736676F6F676C6533636F6D3000010001", builder.toString());

    }

    @Test
    void fullquestion_default_expectsok() {
        // Arrange
        var msg = new DnsMessage(65535, 65535);
        var q = new DnsQuestion("www.google.com");
        msg.setQuestion(q);
        var builder = new StringBuilder();

        // Act
        q.buildHeader(builder);

        // Assert
        assertEquals("3377777736676F6F676C6533636F6D3000010001", builder.toString());

    }

    @Test
    void fullmsg_default_expectsok() {
        // Arrange
        var msg = new DnsMessage(22, Flags.QR_QUERY);
        var q = new DnsQuestion("www.google.com");
        msg.setQuestion(q);
        var builder = new StringBuilder();

        // Act
        msg.build(builder);

        // Assert
        assertEquals("00160000000100000000000003646e7306676f6f676c6503636f6d0000010001", builder.toString());

    }
}
