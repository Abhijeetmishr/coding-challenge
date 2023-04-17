package cut;
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.concurrent.Callable;

import cut.model.Result;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "Cut", mixinStandardHelpOptions = true, version = "cut 1.0",
description = "cut out selected portions of each line of a file")
public class Cut implements Callable<Result<String>>{

    public static void main(String[] args) {
        var cut = new Cut();
        var cmd = new CommandLine(cut);
        var exitCode = cmd.execute(args);
        Result<String> result = cmd.getExecutionResult();
        if (result != null) {
            System.out.println(result.printResult());
        }
        System.exit(exitCode);
}

    public Cut() {
    }

    public Cut(String bytePositionsWComma, String characterPositionsWComma, char delimiter, String fieldsWComma) {
        this.bytePositions = Cut.convertStringToIntArray(bytePositionsWComma);
        this.characterPositions = Cut.convertStringToIntArray(characterPositionsWComma);
        this.delimiter = delimiter;
        this.fieldsString = fieldsWComma;
        this.fields = Cut.convertStringToIntArray(fieldsWComma);
    }
    private static int[] convertStringToIntArray(String input) {
        String[] stringArray = input.trim().isEmpty() ? new String[0] : input.split("[, ]"); // Split the string by comma
        int[] intArray = new int[stringArray.length]; // Create a new int array
        
        // Convert each string element to int and store in int array
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }
        
        return intArray;
    }

    @Option(names = "-b", description = "-b specifies byte positions")
    int[] bytePositions = {};

    @Option(names = "-c", description = "-c specifies character positions")
    int[] characterPositions = {};

    @Option(names = "-d", description = "-d specifies delimiter used.")
    char delimiter = '\t';

    @Option(names = "-f", description = "-f specifies fields")
    String fieldsString;
    int[] fields = {};

    @Option(names = {"-n"}, description = "-n Do not split multi-byte characters.")
    private boolean switchNotSplitMultiByteChars; 

    @Option(names = {"-s"}, description = "-s Suppress lines with no field delimiter characters.")
    private boolean switchSuppressLinesWithNoFieldDelimiter; 

    @Option(names = {"-w"}, description = "-w Use whitespace (spaces and tabs) as the delimiter.")
    private boolean switchUseWhiteSpaceAsDelimiter;

    @Parameters(index = "0", arity = "0..", description = "The file to calculate for.")
    private File[] files;

    @Override
    public Result<String> call() throws Exception {
        fields = convertStringToIntArray(fieldsString);
        Arrays.sort(fields);

        Result<String> result = null;
        for (File file : files) {            
            var reader = new FileReader(file);
            try {
                var cutter = new Cutter(reader, this);
                Result<String>newResult = cutter.processLines();
                if (result == null) {
                    result = newResult;
                } else {
                    result = result.add(newResult);
                }
            } finally {
                reader.close();
            }
        }
        return result;
    }
}