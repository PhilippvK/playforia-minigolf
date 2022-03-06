package org.moparforia.server;

import org.moparforia.new_server.GolfServer;
import org.moparforia.shared.ManifestVersionProvider;
import picocli.CommandLine;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
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
            names = {"--allow-cheating"},
            description = "Sets server port",
            defaultValue = "false"
    )
    private boolean allowCheating;

    public static void main(String... args) {
        Launcher launcher = new Launcher();
        new CommandLine(launcher)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    @Override
    public Integer call() {
        try {
            getServer(host, port, allowCheating).start();
        } catch (CertificateException | SSLException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public GolfServer getServer(String host, int port, boolean allowCheating) {
        return new GolfServer(host, port, allowCheating);
    }
}
