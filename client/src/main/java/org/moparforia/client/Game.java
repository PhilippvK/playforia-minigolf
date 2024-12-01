package org.moparforia.client;

import agolf.GameApplet;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import org.moparforia.shared.Locale;

public class Game {
    private static final int WIDTH = 735;
    private static final int HEIGHT = 525;

    public Game(
            JFrame frame, String server, int port, Locale locale, String username, boolean verbose, boolean norandom) {
        Applet game = new GameApplet();

        game.setStub(new Stub(server, locale, username, port, verbose, norandom));
        game.setSize(WIDTH, HEIGHT);
        game.init();
        game.start();
        frame.add(game);
        frame.setSize(1280, 720);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    class Stub implements AppletStub {
        private final Map<String, String> params;
        private String server;

        public Stub(String server, Locale locale, String username, int port, boolean verbose, boolean norandom) {
            if (server.indexOf(':') == -1) { // is ipv4
                this.server = server;
            } else { // is ipv6
                this.server = "[" + server + "]";
            }
            params = new HashMap<>();
            params.put("initmessage", "Loading game...");
            params.put(
                    "ld_page",
                    "javascript:Playray.Notify.delegate({ jvm: { version: '%v', vendor: '%w', t1: '%r', t2: '%f' } })");
            params.put("server", server + ":" + port);
            params.put("locale", locale.toString());
            params.put("sitename", "playray");
            params.put("registerpage", "http://www.playforia.com/account/create/");
            params.put("creditpage", "http://www.playforia.com/shop/buy/");
            params.put("userinfopage", "http://www.playforia.com/community/user/");
            params.put("userinfotarget", "_blank");
            params.put("userlistpage", "javascript:Playray.GameFaceGallery('%n','#99FF99','agolf','%s')");
            params.put("json", "Playray.Notify.delegate(%o)");
            params.put("verbose", Boolean.toString(verbose));
            params.put("norandom", Boolean.toString(norandom));
            params.put("username", username);
        }

        public boolean isActive() {
            return true;
        }

        public URL getDocumentBase() {
            try {
                return new URL("http://" + this.server + "/AGolf/");
            } catch (Exception ex) {
                System.err.println("getdocumentbase exc eption");
                return null;
            }
        }

        public URL getCodeBase() {
            return getDocumentBase();
        }

        public String getParameter(String name) {
            if (!params.containsKey(name)) return "";
            return params.get(name);
        }

        public AppletContext getAppletContext() {
            return null;
        }

        public void appletResize(int width, int height) {}
    }
}
