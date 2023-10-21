package qr;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.util.UUID;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(name = "qr", mixinStandardHelpOptions = true, version = "qr 1.0", description = "This challenge is to build your own QR Code Generator")
public class QrCode implements Callable<Result> {

    public static void main(String[] args) {
        var compress = new QrCode();
        var cmd = new CommandLine(compress);
        var exitCode = cmd.execute(args);
        Result result = cmd.getExecutionResult();
        if (result != null && result.toString() != null) {
            System.out.println(result.toString());
            System.exit(exitCode);
        }
    }

    @Parameters(index = "0", description = "passes a string to create a QR code")
    String data = null;

    @Option(names = "-o", description = "-o specifies an optional output file for the generated code")
    String outputFileName = "qr-"+ UUID.randomUUID().toString()+".png";

    @Override
    public Result call() throws Exception {
        if (data == null) {
            return null;
        }
        return new Result(this.data, this.outputFileName);
    }
}