package redis;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "redis", mixinStandardHelpOptions = true, version = "redis 1.0", description = "This challenge is to build your own redis orginal server")
public class Redis implements Callable<Result> {

    public static void main(String[] args) {
        var compress = new Redis();
        var cmd = new CommandLine(compress);
        var exitCode = cmd.execute(args);
        cmd.getExecutionResult();
        System.exit(exitCode);
    }

    @Option(names = "-p", description = "-p to specify the port default 6379")
    int port = 6379;

    @Option(names = "-t", description = "-t to specify the max number of threads used default 10")
    int maxThreads = 10;

    @Override
    public Result call() throws Exception {
        new RedisServer(this.port, this.maxThreads);
        return new Result();
    }
}