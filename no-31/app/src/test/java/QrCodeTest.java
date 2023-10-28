/*
 * This Java source file was generated by the Gradle 'init' task.
 */


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import qr.EncodingMode;
import qr.QrCode;
import qr.Quality;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QrCodeTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = QrCodeTest.class.getResource("tests/"+testfile);
        if (resource != null) {
            File file = Paths.get(resource.toURI()).toFile();
            reader = new FileReader(file);
        }
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    public void detectmode_valid_ok() throws URISyntaxException, IOException {
        // Arrange
        var qr = new QrCode("HELLO CC WORLD");

        // Act
        var bits = qr.appendBits(new StringBuilder()).toString();

        // Assert
        var compare = "0010" // Mode
                +"000001110" // Character Count
                +"01100001011011110001101000101110001000101000110011101001000101001101110111110"; // Data
        assertEquals(compare, bits);

    }

    @Test
    public void fullbits_HELLOWORLD_ok() throws URISyntaxException, IOException {
        // Arrange
        var qr = new QrCode("HELLO WORLD", Quality.L);

        // Act
        var bits = qr.encode();

        // Assert
        var mine =
        "001000000101101100001011011110001101000101110010110111000100110101000011010000001110110000010001111011000001000111101100000100011110110000010001111011001010101111000001101101001100100111000011111000100110000100000000";
        var compare =
        "001000000101101100001011011110001101000101110010110111000100110101000011010000001110110000010001111011000001000111101100000100011110110000010001111011001101000111101111110001001100111101001110110000110110110100000000"; // Data
        assertEquals(compare, bits);

    }

    @Test
    public void fullbits_loremIpsum_ok() throws URISyntaxException, IOException {
        // Arrange
        var qr = new QrCode("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus maximus ipsum vel faucibus dictum. Vestibulum vitae mattis est, non accumsan felis. Pellentesque leo arcu, pharetra non ante sed, faucibus dapibus odio. Duis sodales vestibulum lacus, in rutrum ipsum ornare sit amet. Phasellus facilisis tortor ac diam molestie, vel commodo massa cursus. Donec finibus enim ac erat porttitor volutpat. Aliquam eu ligula ac mauris auctor interdum id id urna. In scelerisque libero vel massa ultricies sagittis. Curabitur ut pharetra nunc, et lacinia est. Sed scelerisque arcu in euismod venenatis. Proin nec tempor est. Aliquam euismod dignissim felis id sodales. Nulla fermentum nulla non massa dictum, tincidunt hendrerit lacus maximus. Quisque eget tellus magna. Aliquam erat volutpat. Praesent hendrerit facilisis nulla, viverra pharetra velit. In lorem est, venenatis sit amet nulla at, sollicitudin ullamcorper dui. Donec ultricies elementum eros tincidunt vehicula. Aliquam sem nunc, tincidunt quis hendrerit at, lacinia at ligula. Sed vel suscipit turpis. Aliquam erat volutpat. Ut et nisi eget nunc ullamcorper feugiat. Maecenas ut lobortis lorem. Sed a blandit sapien. Nulla sit amet velit eget turpis viverra tincidunt quis vel lacus."
                , Quality.L, EncodingMode.BYTE);

        // Act
        var bits = qr.encode();

        // Assert
        var compare =
                bitsInBlocks("0100000001000110010101111101001011010110001001100100011000110111000001001110011011100010010000100100110110010110001100100000011000010111000100101001011000010111100101100011011000000100000001111010010000100111000001101101011001010111000001100111011001010110111000101001011000010110001101101100011001010110111101101111011000100110111001111110011001010010000001100100011111000110000101111111011111000111010001101100011010010111010101101001011100000110110001100101011010010111000001100010011001010110100101100101011100110010111001100011011101010110111101111110011100010111100101100101011011010010111100100011011100000110001100100011011001110110001001100100001001010110010101101101001000000111111000100100011000010111110000101001011001010111010101100000011100010110111000100000011001100110000001001001011001010110000001101101001001000010110100100110011011010010111000101001011110010111010001110101001000110111010101110000011000000111000001100101011000000110000001000000011101000110010101101100001001000110010000100110011001000110010101111000011001010111111001110011011100010110100101110000011111110111000001100101011001010110001101111001011000100110010101100101011001010010001100100110011000100010110001101100011011000110010000100011011100010111110001101101001000000110000001110101011000000110000101101001011111000111110000100101011001000010110001100000011011010110001101101100001010010110001101100011001001010111000001111100011000000111000100100100011000010111111101100000011011100111100101100000011000110010011001100001001001100110000001111111011001000111010001100011011001000110111001101001011000000110010101101110001011110110001101101100011001000110000101101111011001010111100101100100001011010110111001100000010011000111100101111111011110010111110001101101011000100110000100100000011100010110010101100001011001010111010000100010001000110010010101111101011001000111000001100011011001110110111001101100011001000111000001100000011100000110001100101111011001010110010101111111011011100110000101111001011100000110000101100011011001010111000001110100011011010010001101110100011000010010010001100001011100010111110101101001011100110111011001101111001000000110010000100001011011100010100101110101011001000010010101110100001001000010010101110000011010010110111000101100011000000100001100100001011011100010010000100000011011000010001101111101011001000010000001010101011100010110000001111101001000000101000001110001011000000110010001100001011100000110001101100011001011000110001101100000011101010111011001101101011011100110100101100011011110010110010101101110001010010111100101110011011001000010010101100101011111110110001001110011011001000010010000100000010000010111010000100101011000000110110001100100001011100010010101100001001000000111000001111110011101010110000001101101001001010111100101111100001000000110110001110000011001010111001101100101011000010110000101100000011001000010010000100000011000010110010101100011011100100110001101101100011011010010110101101110011100000110000001100011011000110110110100100101011111100110010101101100011000000110010101110101011011100110010101101111011000110111000001100010011100010010110001100001001001010111010000101110011010010111011101101110011101010110110001100011011111100010010101110000011000100110000001100011001000110110010101110011011011010111000101100101011100000100001001100110011000010111111001111100001010010010010000100101011000110110001101110011001010010110100101110101011101000010010101100000011100000110000001110011011100010110010101111110001011100010001101110010011000000111110001100100011001010110010001110100011011100010001100100000010000000111000101111101011001100110110001101001011001110110010101110101011100000110110000100100011000110110010101100101011011110110000100101110011001010111001001110100011101100110000001101111011000110110010100101110011111000111000001100011011001000010000001100101011101010110100101101110011001010110000001100100011101010111000101111001011000000110100101110010001011000110111000100101011011000110000101110101011001000111010000100100011111100111001100100000011010010111000001110011001001010111001001101101001000000110110000100101011001010110000001110001011000110010001001110000011000100110001101110000011000010111000001111110011111100110011001100100011011100010010101110110011010010111010100101110011101000010001101100100001000110010100101111001011100000101010001111001011000110111000001100101011011100010111101100000011100000111011001100000011000000110001001111110011000010111100101101100011000000101110001100001011101010110010101111001011101010110010101101001011001010110111000101100011000000111110001100101011011000110001001110011011011000110110100100010011101010010000001100001001000100110100101101001011111000110001001100011011011000110000001100101011100000110010101110000011000010110001101100011001000010110000100101001011001010110100101110011001011000110010101101110011001010111100101110000011011010110000001111110011011100111000001110000011010010110100101111111011000110110010001111000011000110110010001100111001001000110001101110101011000100110001101101110001001010110010101100101011011110111100101100000011001010111010101101110011001010111110101100000011011100111010001101110011000100111111001100101011000110111110100101001011000100110111101101101011001000010100101100100011100000110001101101100011000010111000001101101001011110010010000100001011100000110111000100010011001010111100101101001011101010110111101110000011000000111000001110011011110000110000001110101011100100010010001110100001001010010001001100001011001100110011001100011011001010110010101100010011000000110010101101110001000000110111001100011001001010110010101100001001011100110110001101001011101100110111001110000010111000110000101110000011011000010111001100000011001000111110001100100001001010111010000100110011001010110001001100101011100000110010101100100011000100110000101100000011001010110000001111001011111110010010100100010011011010110111001101001011001010111110101100001011101110110000101110110011000000110000001110001011100010111000101110011011100100110001101100100001010010110010101100001011000010111001101100100001000110111010001100100011110010111111101111100001000010111100101111101011100100110100101110000011100110110100101110101011001000010001001110000011001000010001100100101011100110111010000100000011000010010001100101101001000000110000001101100011011100010000001110011001001010010000001101111011100000111111000101100001001100110010101110001011000000100011001100000011011000010000101100010011101010110000001010000011100010110001000100011011011010110010101101101011000000111110101100100011111000111000001110100011000110110000001101001011000010110110000100001011100000110010101110100011001000111001001101001011010010110010001111110011001010110000001101000011010000110010000101001011100100110111101101110011011000110010101101001011000110110110001101001011000010111111000100100011010010110100101100011011010010111100100100001001001010110000101101101011100100110000001011111011100110110111000101001011000110110111000100000011011100110001101110101011101010111000001100010001010010110000001100100011110010111000001000001011100010111010101110011001001000111100001100000011101010111111001100101011000110010010001100100001000110010001100100000011000100110000101110110011000110010010101101110011100000110111101100000011000000111111000001001011100010010001101101111011000000111001100100100001011100111111001101100011001010111111011000000011100000110010101101100011100110110000001110000011001010110010101101001011001000010000100010011011111100110110001100101011100010110010001101000011011000110001100100111011100000110111011000101011011110110110001110100011101110110010101100101011011000110000001110101011011000110000100011101001011100010010101110000011010010111110101111110011000010010010101101100011011110110111011000000011100000110001100100001011101000111000001100100011111000010110001110001001000100110000100010110011000010110000001100100001001000110111101110010011000000111010001111110001011110111111011000101011011100111011001101110001010010111001000100101011101100110001001100000010100100111000100011100001001000110000101100000010000110010000001100010011010010111100101100011011001000110111011000000011001010010001101100001011011100010010101111001011101100110001101100101011010010111000100010110011000000111100101101100011000000100001101110100001001010111100101100100001000110010111011000001011100110110110001101001011100110111010000100000011000100111010101110000011100000110000100010101011001010110100101110001011101010111111000101100011000100110001100100110011011000110111011000011011001000010001101100101011000100110000001000001011000010010000001100101011011110111000100011001011011000010100101110001011000010110000101100011011100000111010101101100001000100110111011000010011100000110001100101101001000100110110001100101011100000110110001100000011101010110000100010101011101100110000001110000011010010111100101110011001010000110010101100011011111010010111011000011001000010111010001100101011101000111000101110000011000010111110101100101011111100010000100010000011001010110111101110101001001010111010101101101011000100110010101100011011000000101111011000100011000110110001001110000011000100010000101100001011101010111111001110011011000110110000100011001011010010110010001101100011000000111110100101000011001000111010001111001011101010110111011000011011100100111111101111001011001010111000001101001011000100110010101100000011001000010000100010100011101010111001000100111011101000010010101111101011100010010110100101001011100000110111011000101011000110010000001100101011000000111010101100101011100000111000001100100001000010010000100011101001000000110000101101100011000000110100101110011001001100110010101110000011100000110111011001110001001000110001100100001001010000110001101101110001001010110001001100100011100100110000100010000010100010111000001100000011000010111110101100000010111000110111101110101011111000110111011000110011000000110010001100001011000100110111101100001011110010111001100100010011100010110000100010101011110010110100101100011001001010111010000100101011001000010000001110000011011100110111011000011011100100111000101100000011001000111000001101001011111100010010001101001011101000110000100011001011000110010100101111110110010100100001111111011011001011001110011011110101001001001001000010001010000001111011100101101100100101011111111101101000001111110011000010001001100001010011111001001011100101111011000001101110101011010000000000111101011010111100111110010111001000101001011001010100100101110001000111011001011010001111100010000111010000000101010100101001001001011000001111011011011111010000110110000100001110100100011000110100110111111011001100110111110011000111110000110110101111000111100100001011111110011011100101010000100000011010101111000100000001111001100111111000100111010111110100001101010001111001110011010101101101001100001110001011101000000101101101101010010011010101101011001100101010110011111100100100110111110011110101011001100001011110011010000101011110110111101111011000000110001001000000010010101010110011000011001111110001111100101011011100110011100110010100011010110111101001101001110100100000101001101101011010110110010100000010111010110110011011110111110111100111000010110100001100001101011010001110000000010011010000111001111111000001100011010010101101111010010111001111000001100010011101011001100110111110011011010011110010001100001101100111000011010001110111100001011011111011101110110000111110001000001010110011100110000011011001101011010111100100111001011010111101101111001001111111110010010011001101011011101011110010110011100000001000100100111110111110010111100101011101011011010101001001001001011000011010010101101110001001000101111011110000000100011101111010101100011000011110010110101100010100000000010101001000010010010001101111111110101110101001001100100100110000000000011010000111100101111110000011000111011001011111000101100111110011011000101000010011100011011111011010001101000110011011010011001011111011000011110001101101000010101010101111101001011010001011101011011010000111110110000010110011001110100000000011111101001001101101101110100101111110100000101010100100111000110010000000011111101000110000111100001011101101111110100100001110111101010101111001011010101101100111011111110011110000100001001010001101110010011100011111010010000110110010111000001001110101011101000111111101010011110011010100000111001110000110010011011001110001001001101101110010001101011100110110110001001000011011000011010101101101011101111011101011100011011001010000110110001001010100001111001100011101001101000111100010010001011100111110001011111110010010111010001100100010110011111011000100011111110001111110001001110110100010000001000010110100100101101101000011101011101000111000101101100110101110000000000"); // Data
        assertEquals(compare, bitsInBlocks(bits));

    }

    private String bitsInBlocks(String bits) {
        var bitsInBlocks = new String[bits.length() / 8];
        var index = 0;
        for (int i = 0; i < bits.length() / 8; ) {
            bitsInBlocks[i] = bits.substring(index, index+8);
            i++;
            index += 8;
        }
        return String.join("\n", bitsInBlocks);
    }


}