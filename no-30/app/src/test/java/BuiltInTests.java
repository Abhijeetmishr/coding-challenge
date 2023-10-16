/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import lisp.parser.ILispFunction;
import lisp.parser.LispRuntime;
import lisp.parser.TokenValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuiltInTests {

    private BufferedReader reader;
    private LispRuntime runtime;

    @BeforeEach
    public void initLibrary() {
        this.runtime = new LispRuntime();
    }

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = BuiltInTests.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new BufferedReader(new FileReader(file));
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }


    @Test
    void makeArray_1dim_zeros() throws URISyntaxException, IOException {
        // Assert
        var result = runtime.execute("(make-array '20)");

        // Assert
        assertEquals(TokenValue.NIL,result.get(0));
        assertEquals(TokenValue.NIL,result.get(19));
    }

    @Test
    void makeArray_2dim_random_zeros() throws URISyntaxException, IOException {
        // Arrange
        var result = runtime.execute("(make-array '(3 4) :element-type 'float :initial-element (random))");

        // Action Assert
        var val00 = result.get(0,0).getDouble();
        var val23 = result.get(2,3).getDouble();
        assertTrue(val00 >= 0 && val00 < 1.0);
        assertTrue(val23 >= 0 && val23 < 1.0);
    }

    @Test
    void makeArray_2dim_outofindex() throws URISyntaxException, IOException {
        // Arrange

        var result = runtime.execute("(make-array '(3 4) :element-type 'float :initial-element (random))");

        // Action Assert

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                result.get(3,2);
            }
        });

    }

    @Test
    void makeArray_2dim_outofindexdimension() throws URISyntaxException, IOException {
        // Arrange
        var result = runtime.execute("(make-array '(3 4) :element-type 'float :initial-element (random))");

        // Action Assert

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                result.get(2,2,1);
            }
        });

    }

    @Test
    void makeArray_content_ok() throws IOException {
        // Arrange
        var result = runtime.execute("(make-array '(7 20) :initial-contents (loop repeat 140 collect (random 1.0)))");
        assertNotNull(result);
        assertTrue(result.get(0, 0).getDouble() > 0.0);
        assertEquals(140, result.getExpression().size());
    }

    @Test
    void makeArray_setqAndmultipleArrays_ok() throws IOException {
        // Arrange
        var result = runtime.execute(
                "(setq hidden-weights (make-array '(7 20) :initial-contents (loop repeat 140 collect (random 1.0)))\n" +
                "hidden-biases (make-array '(20) :initial-contents (loop repeat 20 collect (random 1.0)))\n"+
                "output-weights (make-array '(20 1) :initial-contents (loop repeat 20 collect (random 1.0)))\n"+
                "output-bias (random 1.0))");
        assertNotNull(result);
    }
    @Test
    void makeArray_1dim20_ok() throws IOException {
        // Arrange
        var result = runtime.execute("(make-array '(20) :initial-element 0.000001)");
        assertNotNull(result);
        assertTrue(result.get(0).getDouble().equals(0.000001));
        assertTrue(result.get(19).getDouble().equals(0.000001));
        assertEquals(20, result.getExpression().size());
    }


    @Test
    void setf_dottimes_1dim20_ok() throws IOException {
        // Arrange
        runtime.execute("(setq hidden-weights (make-array '(7 20) :initial-element (random 1.0))");
        runtime.execute(
                "(dotimes (i 7)\n"+
                "  (dotimes (j 20)\n"+
                "   (setf (aref hidden-weights i j) (* (aref hidden-weights i j) (random 1.0))))");

        // Act
        var result = runtime.execute("hidden-weights");

        var list = (List<TokenValue>)result.getExpression();
        list.set(0, new TokenValue(1.1));
        assertNotNull(result);
        assertEquals(140, result.getExpression().size());
    }

    @Test
    void sigmoid_test_ok() throws IOException {
        // Arrange
        runtime.execute(
                "(defun sigmoid(x)\n" +
                "  (/ 1.0 (+ 1 (exp (- x)))))");
        runtime.execute("");

        // Act
        var result = runtime.execute("(sigmoid 1.0)");

        assertNotNull(result);
        assertTrue(result.getDouble() > 0.73);
    }

    @Test
    void let_ownscope_test_ok() throws IOException {
        // Arrange
        var result = runtime.execute(
                "(setq xtop (random 1.0) \n" +
                        "       xarr (make-array '20 :initial-contents (loop repeat 20 collect (random 10)))\n" +
                        ")\n" +
                        "(defun test()\n" +
                        "   (let* ((x 42))\n" +
                        "      (format t \"x=~A~%\" x)" +
                        "      (setf xtop x) \n" +
                        "      (setf (aref xarr 0) 4240) \n" +
                        "      (setf (aref xarr 1) 4241) \n" +
                        "      (setf (aref xarr 2) 4242) \n" +
                        "      (format t \"xtop=~A~%\" xtop)" +
                        "      (format t \"xarr=~A~%\" xarr)" +
                        "      x)" +
                        ")" +
                        "(test)" +
                        "(format t \"xtop=~A~%\" xtop)" +
                        "(format t \"xarr=~A~%\" xarr)"
        );

        // Act
        var testResult1 = runtime.executeAndPrint("xtop");
        var testResult2 = runtime.executeAndPrint("xarr");

        assertNotNull(result);
        assertNotNull(testResult1);
        assertNotNull(testResult2);
    }

}
