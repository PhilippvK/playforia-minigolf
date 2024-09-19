package org.moparforia.server;

import org.moparforia.shared.ManifestVersionProvider;
import picocli.CommandLine;

import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(
        description = "Starts Minigolf Server",
        name = "server",
        mixinStandardHelpOptions = true,
        versionProvider = ManifestVersionProvider.class,
        subcommands = {
                Converter.class
        }
)
public class Launcher implements Callable<Integer> {

    public static final String DEFAULT_HOST = "0.0.0.0";
    public static final String DEFAULT_PORT = "4242";

    @CommandLine.Option(
            names = {"--hostname", "-ip"},
            description = "Sets server hostname",
            defaultValue = DEFAULT_HOST
    )
    private String host;

    @CommandLine.Option(
            names = {"--port", "-p"},
            description = "Sets server port",
            defaultValue = DEFAULT_PORT
    )
    private int port;

    @CommandLine.Option(
            names = {"--verbose", "-v"},
            description = "Sets server to log more verbosely"
    )
    private boolean verbose = false;

    @CommandLine.Option(
            names = {"--tracks-dir", "-t"},
            description = "Sets where to look for tracks and track sets"
    )
    private String tracksDirectory;

    public static void main(String... args) {
        Launcher launcher = new Launcher();
        new CommandLine(launcher)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    @Override
    public Integer call() {
        getServer(this.host, this.port, this.verbose, this.tracksDirectory).start();
        return 0;
    }

    public Server getServer(String host, int port, boolean verbose, String tracksDirectory) {
        return new Server(host, port, verbose, Optional.ofNullable(tracksDirectory));
    }
}
