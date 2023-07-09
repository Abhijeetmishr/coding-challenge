package memcached;

import java.util.Arrays;
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "mem", mixinStandardHelpOptions = true, version = "mem 1.0", description = "This challenge is to build your own memcached server")
public class Mem implements Callable<Result> {
    private static Logger _logger = Logger.getLogger(Mem.class.getName());

    public static void main(String[] args) {
        var mem = new Mem();
        var cmd = new CommandLine(mem);
        var exitCode = cmd.execute(args);
        cmd.getExecutionResult();
        System.exit(exitCode);
    }

    @Option(names = "-p", description = "-p specifies the port. default 11211")
    int port = 11211;
    @Option(names = "-n", description = "-n specifies a constant server name. default localhost")
    String serverName = "localhost";

    @Option(names = "-servers", description = "-servers specifies the list of server ids separated by , and only used if client mode. default localhost-11211")
    String serverIds = "localhost-11211";

    @Option(names = "-s", arity = "0..", description = "-s specifies if its a server or a client. default false means client")
    boolean isServer = false;

    private String[] serverIdList() {
        return Arrays.asList(serverIds.split(",")).stream().filter((x) -> !x.isEmpty() && !x.isBlank())
                .toArray(String[]::new);
    }

    @Override
    public Result call() throws Exception {
        if (isServer) {
            _logger.info(String.format("Starting memcached server '%s' and port %d ", this.serverName, this.port));
            new MemcachedServer(this.serverName, this.port).start();
        } else {
            _logger.info(String.format("Client connected to '%s'", this.serverIds));
            new MemcachedClient(this.serverIdList());
        }
        return new Result();
    }
}