package org.moparforia.client;

import picocli.CommandLine;

import org.moparforia.shared.ManifestVersionProvider;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;
import java.util.Date;
import java.text.SimpleDateFormat;


@CommandLine.Command(
        description = "Starts Minigolf Client",
        name = "client",
        mixinStandardHelpOptions = true,
        versionProvider = ManifestVersionProvider.class
)
public class Launcher implements Callable<Void> {

    public static final String DEFAULT_SERVER = "127.0.0.1";
    public static final int DEFAULT_PORT = 4242;

    // CLI options
    @CommandLine.Option(names = {"--hostname", "-ip"},
            description = "Sets server hostname",
            defaultValue = "")
    private String hostname;

    @CommandLine.Option(names = {"--port", "-p"},
            description = "Sets server port",
            defaultValue = "0")
    private int port;

    @CommandLine.Option(names = {"--lang", "-l"},
            description = "Sets language of the game, available values:\n ${COMPLETION-CANDIDATES}",
            defaultValue = "en_us")
    private Language lang;

    @CommandLine.Option(names = {"--verbose", "-v"}, description = "Set if you want verbose information")
    private static boolean verbose = false;

    @CommandLine.Option(names = {"--reset", "-r"}, description = "Reset user preferences at launch")
    private static boolean reset = false;

    public static boolean debug() {
        return verbose;
    }

    public static boolean isUsingCustomServer() {
        return true;//instance.serverBox.isSelected();
    }

    public void loadPreferences() {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        long last_time = prefs.getLong("timestamp", 0);
        String last_version = prefs.get("version", "unknown"); // TODO: check version
        String last_hostname = prefs.get("hostname", "");
        int last_port = prefs.getInt("port", 0);
        if (!last_hostname.isEmpty() && last_port != 0) {
            hostname = last_hostname;
            port = last_port;
            System.out.printf("Successfully loaded old user preferences written with v%s at %s\n",
                last_version, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(last_time));
        } else {
            System.err.println("No old user preferences found. Using the default settings...");
        }
    }

    public void savePreferences() {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        System.out.println("this.getClass().getName()=="+this.getClass().getName());

        prefs.putLong("timestamp", new Date().getTime());
        prefs.put("version", Launcher.class.getPackage().getImplementationVersion());
        prefs.put("hostname", hostname);
        prefs.putInt("port", port);
        System.out.println("User preferences have been saved successfully");
    }

    public void resetPreferences() {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.remove("timestamp");
        prefs.remove("version");
        prefs.remove("hostname");
        prefs.remove("port");
        System.out.println("Removed old preferences as requested by user");
    }

    public static void main(String... args) {
        Launcher launcher = new Launcher();
        new CommandLine(launcher)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    public boolean showSettingDialog(JFrame frame, String server, int port) throws ParseException {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(4, 1));

        final JTextField serverField = new JTextField();
        JLabel serverLabel = new JLabel("Hostname:");
        serverField.setText(server);
        pane.add(serverLabel);
        pane.add(serverField);

        JSpinner portSpinner = new JSpinner();
        portSpinner.setModel(new SpinnerNumberModel(port,1, Integer.MAX_VALUE,1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(portSpinner,"#");
        // Align spinner values to the left
        editor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        portSpinner.setEditor(editor);
        JLabel portLabel = new JLabel("Port:");
        pane.add(portLabel);
        pane.add(portSpinner);

        int result = JOptionPane.showConfirmDialog(frame, pane, "Choose a server", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            this.hostname = serverField.getText();
            portSpinner.commitEdit();
            this.port = (Integer) portSpinner.getValue();
            return true;
        } else {
            return false;
        }
    }
    private String[] login(JFrame frame) {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(2, 2));
        JLabel userLabel = new JLabel("user");
        JLabel passLabel = new JLabel("pass");
        final JTextField userField = new JTextField();
        final JPasswordField passField = new JPasswordField();

        pane.add(userLabel);
        pane.add(userField);
        pane.add(passLabel);
        pane.add(passField);

        int option = JOptionPane.showConfirmDialog(frame, pane, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            return new String[]{user, pass};
        } else {
            return new String[]{null, null};
        }

    }

    @Override
    public Void call() throws Exception{
        JFrame frame = createFrame();
        // Remove preferences if requested
        if (reset) {
            resetPreferences();
        }
        if (hostname.isEmpty() || port == 0) {
            // Determine which of these was actually false
            loadPreferences();
            String temp_hostname = hostname.isEmpty() ? DEFAULT_SERVER : hostname;
            int temp_port = port == 0 ? DEFAULT_PORT : port;
            if (!showSettingDialog(frame, temp_hostname, temp_port)) {
                System.err.println("Server needs to be specified for minigolf to run");
                System.exit(1);
                return null;
            }
        }
        savePreferences();

        launchGame(frame, hostname, port, lang, verbose);
        return null;
    }

    public JFrame createFrame() throws IOException {
        JFrame frame = new JFrame();
        frame.setTitle("Minigolf");
        Image img = loadIcon();
        frame.setIconImage(img);
        return frame;
    }

    public Game launchGame(JFrame frame, String hostname, int port, Language lang, boolean verbose) {
        return new Game(frame, hostname, port, lang.toString(), verbose);
    }

    public Image loadIcon() throws IOException {
        return ImageIO.read(getClass().getResource("/icons/playforia.png"));
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    enum Language {
        EN_US("en_US"),
        FI_FI("fi_FI"),
        SV_SE("sv_SE");

        private final String custom_name;

        Language(String name) {
            this.custom_name = name;
        }

        @Override
        public String toString() {
            return this.custom_name;
        }
    }
}
