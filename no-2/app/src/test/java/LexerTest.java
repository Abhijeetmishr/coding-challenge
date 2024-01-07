/*
 * This Java source file was generated by the Gradle 'init' task.
 */


import json.Lexer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = LexerTest.class.getResource("tests/"+testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test void openobject_step1_expectsopen() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("step1/valid.json");
        Lexer classUnderTest = new Lexer(this.reader);
        
        //Action
        var token = classUnderTest.next();

        //Assert
        assertEquals(Lexer.Token.OPEN_OBJECT, token.token, "should be {");

    }


    @Test void simpelkeyvalue_step2_expectsopen() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("step2/valid.json");
        Lexer classUnderTest = new Lexer(this.reader);
        
        //Action
        var open = classUnderTest.next();
        var string1 = classUnderTest.next();
        var colon = classUnderTest.next();
        var string2 = classUnderTest.next();
        var close = classUnderTest.next();

        //Assert
        assertEquals(Lexer.Token.OPEN_OBJECT, open.token, "should be {");
        assertEquals(Lexer.Token.STRING, string1.token, "should be key");
        assertEquals(Lexer.Token.COLON, colon.token, "should be :");
        assertEquals(Lexer.Token.STRING, string2.token, "should be value");
        assertEquals(Lexer.Token.CLOSE_OBJECT, close.token, "should be }");

    }


    @Test void simpel2keyvalue_step2_expectsopen() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("step2/valid2.json");
        Lexer classUnderTest = new Lexer(this.reader);
        
        //Action
        var open = classUnderTest.next();
        var string1 = classUnderTest.next();
        var colon1 = classUnderTest.next();
        var string2 = classUnderTest.next();
        var comma = classUnderTest.next();
        var string3 = classUnderTest.next();
        var colon2 = classUnderTest.next();
        var string4 = classUnderTest.next();
        var close = classUnderTest.next();

        //Assert
        assertEquals(Lexer.Token.OPEN_OBJECT, open.token, "should be {");

        assertEquals(Lexer.Token.STRING, string1.token, "should be key");
        assertEquals(Lexer.Token.COLON, colon1.token, "should be :");
        assertEquals(Lexer.Token.STRING, string2.token, "should be value");

        assertEquals(Lexer.Token.COMMA, comma.token, "should be ,");

        assertEquals(Lexer.Token.STRING, string3.token, "should be key2");
        assertEquals(Lexer.Token.COLON, colon2.token, "should be :");
        assertEquals(Lexer.Token.STRING, string4.token, "should be value");

        assertEquals(Lexer.Token.CLOSE_OBJECT, close.token, "should be }");

    }
}
