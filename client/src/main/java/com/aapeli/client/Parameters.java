package com.aapeli.client;

import com.aapeli.applet.AApplet;
import com.aapeli.tools.Tools;

import java.applet.Applet;
import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public final class Parameters {

    private static final String aString1416 = "127.0.0.1";
    //private static final String aString1416 = "192.168.1.23";
    private static final String aString1417 = "en";
    private static final String aString1418 = "playforia";
    private static final String aString1419 = "http://www.playforia.com/";
    private static final String aString1420 = "_top";
    private Applet anApplet1421;
    private AApplet anAApplet1422;
    private String aString1423;
    private String aString1424;
    private String serverIp;
    private String aString1426;
    private String aString1427;
    private String aString1428;
    private String aString1429;
    private String sessionLocale;
    private String session;
    private String welcomeMessage;
    private String aString1433;
    private String urlRegisterPage;
    private String urlVipPage;
    private String urlUserInfoPage;
    private String urlTargetUserInfo;
    private String urlUserListPage;
    private String urlTargetUserList;
    private String urlTellFriendPage;
    private String urlTargetTellFriend;
    private String characterImageDir;
    private String tournamentRound;
    private String subgame;
    private String ticket;
    private String json;
    private boolean tellFriend;
    private boolean guestAutoLogin;
    private boolean disableGuestLobbyChat;
    private int serverPort;
    private URL urlCreditPage;
    private URL anURL1452;
    private String[][] imageAliases;
    private int[] anIntArray1454;
    private int anInt1455;
    private String[] aStringArray1456;
    private String aString1457;
    private boolean debug;
    private static final String[] aStringArray1459 = new String[73];


    public Parameters(Applet var1) {
        this(var1, false);
    }

    public Parameters(Applet var1, boolean var2) {
        this.anApplet1421 = var1;
        if (var1 instanceof AApplet) {
            this.anAApplet1422 = (AApplet) ((AApplet) var1);
        }

        this.aString1423 = var1.getCodeBase().getHost().toLowerCase();
        this.aString1424 = var1.getDocumentBase().getHost().toLowerCase();
        this.anInt1455 = 0;
        this.debug = var2;
        this.method1660();
    }

    public static boolean getBooleanValue(String var0) {
        return var0 == null ? false : var0.equalsIgnoreCase("true") || var0.equalsIgnoreCase("t") || var0.equalsIgnoreCase("yes") || var0.equalsIgnoreCase("y") || var0.equals("1") || var0.equals("1.0") || var0.equals("1,0");
    }

    public String getParameter(String var1) {
        String var2 = this.anApplet1421.getParameter(var1);
        if (var2 == null) {
            var2 = this.anApplet1421.getParameter(var1.toLowerCase());
        }

        if (var2 == null) {
            var2 = this.anApplet1421.getParameter(var1.toUpperCase());
        }

        if (var2 == null) {
            return null;
        } else {
            var2 = var2.trim();
            return var2.length() == 0 ? null : var2;
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public String getLocale() {
        return this.aString1426;
    }

    public String getTranslationLang() {
        return this.aString1427;
    }

    public String getChatLang() {
        return this.aString1428 != null ? this.aString1428 : this.aString1427;
    }

    public String getLang() {
        return this.getChatLang();
    }

    public String getSiteName() {
        return this.aString1429;
    }

    public String getSessionLocale() {
        return this.sessionLocale;
    }

    public String getSession() {
        return this.session;
    }

    public void removeSession() {
        this.session = null;
    }

    public String getWelcomeMessage() {
        return this.welcomeMessage;
    }

    public void removeWelcomeMessage() {
        this.welcomeMessage = null;
    }

    public String getRegisterPage() {
        return this.urlRegisterPage;
    }

    public String getVipPage() {
        return this.urlVipPage;
    }

    public boolean showPlayerCard(String var1) {
        if (this.debug) {
            System.out.println("Parameters.showPlayerCard(\"" + var1 + "\")");
        }

        try {
            if (this.urlUserInfoPage == null) {
                return false;
            }

            if (var1.charAt(0) == '~') {
                return false;
            }

            String var2 = this.urlUserInfoPage.toLowerCase();
            if (var2.startsWith("http:")) {
                if (this.urlTargetUserInfo == null) {
                    return false;
                }

                this.method1676(this.toURL(this.urlUserInfoPage + var1), this.urlTargetUserInfo);
                return true;
            }

            if (var2.startsWith("javascript:")) {
                URL var3 = this.toURL(Tools.replaceFirst(this.urlUserInfoPage, "%n", var1));
                if (var3 == null) {
                    return false;
                }

                this.method1676(var3, this.urlTargetUserInfo);
                return true;
            }
        } catch (Exception var4) {
            ;
        }

        return false;
    }

    public void showPlayerList(String[] var1) {
        this.showPlayerList(var1, (String) null);
    }

    public void showPlayerList(String[] var1, String var2) {
        try {
            if (var1 == null) {
                if (this.debug) {
                    System.out.println("Parameters.showPlayerList(null,...)");
                }

                this.removePlayerList();
                return;
            }

            if (this.debug) {
                System.out.println("Parameters.showPlayerList(...): nicks.length=" + var1.length);
            }

            if (this.method1675(var1, var2)) {
                return;
            }

            this.showPlayerList(var1, (boolean[]) null, var2);
            this.aStringArray1456 = var1;
            this.aString1457 = var2;
            this.anInt1455 = 1;
        } catch (Exception var4) {
            ;
        }

    }

    public void showPlayerListWinners(boolean[] var1) {
        try {
            if (var1 == null) {
                if (this.debug) {
                    System.out.println("Parameters.showPlayerListWinners(null)");
                }

                this.removePlayerListWinners();
                return;
            }

            if (this.debug) {
                System.out.println("Parameters.showPlayerListWinners(...): winners.length=" + var1.length);
            }

            int var2 = var1.length;
            boolean var3 = false;

            for (int var4 = 0; var4 < var2; ++var4) {
                if (var1[var4]) {
                    var3 = true;
                }
            }

            if (!var3) {
                this.removePlayerListWinners();
                return;
            }

            if (this.anInt1455 == 0) {
                return;
            }

            this.showPlayerList(this.aStringArray1456, var1, this.aString1457);
            this.anInt1455 = 2;
        } catch (Exception var5) {
            ;
        }

    }

    public void removePlayerListWinners() {
        this.showPlayerList(this.aStringArray1456, this.aString1457);
    }

    public void removePlayerList() {
        try {
            if (this.anInt1455 == 0) {
                return;
            }

            this.showPlayerList((String[]) null, (boolean[]) null, (String) null);
            this.anInt1455 = 0;
        } catch (Exception var2) {
            ;
        }

    }

    public boolean showRegisterPage() {
        return this.method1676(this.toURL(this.urlRegisterPage), (String) null);
    }

    public void showCreditPurchasePage() {
        this.showCreditPurchasePage(true);
    }

    public void showCreditPurchasePage(boolean var1) {
        this.method1676(this.urlCreditPage, var1 ? "_blank" : null);
    }

    public boolean isCreditPurchasePageAvailable() {
        return this.urlCreditPage != null;
    }

    public void showQuitPage() {
        this.method1676(this.anURL1452, this.aString1433);
    }

    public String[][] getImageAliases() {
        return this.imageAliases;
    }

    public boolean isGuestAutoLogin() {
        return this.guestAutoLogin;
    }

    public void noGuestAutoLogin() {
        this.guestAutoLogin = false;
    }

    public boolean isGuestLobbyChattingDisabled() {
        return this.disableGuestLobbyChat;
    }

    public String getTicket() {
        return this.ticket;
    }

    public boolean callJavaScriptJSON(String var1) {
        if (this.debug) {
            System.out.println("Parameters.callJavaScriptJSON(\"" + var1 + "\")");
        }

        if (this.json == null) {
            return false;
        } else {
            try {
                var1 = Tools.replaceAll(var1, "\'", "\\\'");
                String var2 = Tools.replaceFirst(this.json, "%o", "\'" + var1 + "\'");
                URL var3 = this.toURL(var2);
                if (var3 == null) {
                    return false;
                } else {
                    this.method1676(var3, (String) null);
                    return true;
                }
            } catch (Exception var4) {
                return false;
            }
        }
    }

    public void updateWebPageInfoBox(int var1, int var2, int var3) {
        if (this.json != null) {
            if (var1 >= 0 || var2 >= 0 || var3 >= 0) {
                String var4 = "{info:{";
                if (var1 >= 0) {
                    var4 = var4 + "credits:" + var1;
                }

                if (var2 >= 0) {
                    if (var1 >= 0) {
                        var4 = var4 + ',';
                    }

                    var4 = var4 + "chips:" + var2;
                }

                if (var3 >= 0) {
                    if (var1 >= 0 || var2 >= 0) {
                        var4 = var4 + ',';
                    }

                    var4 = var4 + "ranking:" + var3;
                }

                var4 = var4 + "}}";
                this.callJavaScriptJSON(var4);
            }
        }
    }

    public Applet getApplet() {
        return this.anApplet1421;
    }

    public AApplet getAApplet() {
        return this.anAApplet1422;
    }

    public void destroy() {
        this.serverIp = null;
        this.aString1426 = null;
        this.aString1427 = null;
        this.aString1428 = null;
        this.aString1429 = null;
        this.sessionLocale = null;
        this.session = null;
        this.welcomeMessage = null;
        this.aString1433 = null;
        this.urlRegisterPage = null;
        this.urlVipPage = null;
        this.urlUserInfoPage = null;
        this.urlTargetUserInfo = null;
        this.urlUserListPage = null;
        this.urlTargetUserList = null;
        this.urlTellFriendPage = null;
        this.urlTargetTellFriend = null;
        this.characterImageDir = null;
        this.tournamentRound = null;
        this.subgame = null;
        this.ticket = null;
        this.json = null;
        this.urlCreditPage = null;
        this.imageAliases = (String[][]) null;
        this.anIntArray1454 = null;
        this.aStringArray1456 = null;
        this.aString1457 = null;
        this.aString1424 = null;
        this.aString1423 = null;
    }

    protected AppletContext method1654() {
        return this.anApplet1421.getAppletContext();
    }

    protected boolean method1655() {
        return this.tellFriend;
    }

    protected String method1656() {
        return this.urlTellFriendPage;
    }

    protected String method1657() {
        return this.urlTargetTellFriend;
    }

    protected String method1658() {
        return this.tournamentRound;
    }

    protected String method1659() {
        return this.subgame;
    }

    private void method1660() {
        this.serverIp = this.getParamServer();
        this.serverPort = this.getParamPort();
        this.aString1426 = this.getParamLocale();
        this.aString1427 = this.getParamLanguage();
        this.aString1428 = this.getParamChatLanguage();
        this.aString1429 = this.getParamSiteName();
        this.sessionLocale = this.getParameter("sessionlocale");
        this.session = this.getParameter("session");
        this.welcomeMessage = this.getParameter("welcomemessage");
        if (this.welcomeMessage == null) {
            this.welcomeMessage = this.getParameter("gamewelcome");
        }

        this.anURL1452 = this.getParamQuitPage();
        this.aString1433 = this.getParamQuitTarget();
        this.urlRegisterPage = this.getParameter("registerpage");
        this.urlVipPage = this.getParameter("vippage");
        this.urlCreditPage = this.toURL(this.getParameter("creditpage"));
        this.urlUserInfoPage = this.getParameter("userinfopage");
        this.urlTargetUserInfo = this.getParameter("userinfotarget");
        this.urlUserListPage = this.getParameter("userlistpage");
        this.urlTargetUserList = this.getParameter("userlisttarget");
        this.tellFriend = Tools.getBoolean(this.getParameter("tellfriend"));
        this.urlTellFriendPage = this.getParameter("tellfriendpage");
        this.urlTargetTellFriend = this.getParameter("tellfriendtarget");
        this.anIntArray1454 = this.getParamRegRemindShowTime();
        this.characterImageDir = this.getParameter("characterimagedir");
        this.imageAliases = this.getParamImageAliases();
        this.guestAutoLogin = Tools.getBoolean(this.getParameter("guestautologin"));
        this.disableGuestLobbyChat = Tools.getBoolean(this.getParameter("disableguestlobbychat"));
        this.tournamentRound = this.getParameter("tournamentround");
        this.subgame = this.getParameter("subgame");
        this.ticket = this.getParameter("ticket");
        this.json = this.getParameter("json");
        if (this.json != null) {
            this.json = Tools.replaceFirst(this.json, "\'%o\'", "%o");
            if (!this.json.toLowerCase().startsWith("javascript:")) {
                this.json = "javascript:" + this.json;
            }
        }
    }

    private String getParamServer() {
        try {
            String var1 = this.getParameter("server");
            int var2 = var1.lastIndexOf(':');
            return var1.substring(0, var2);
        } catch (Exception var3) {
            return this.aString1423.length() > 0 ? this.aString1423 : aString1416;
        }
    }

    private int getParamPort() {
        try {
            String var1 = this.getParameter("server");
            int var2 = var1.lastIndexOf(':');
            return Integer.parseInt(var1.substring(var2 + 1));
        } catch (Exception var4) {
            try {
                return Integer.parseInt(this.getParameter("port"));
            } catch (Exception var3) {
                return 4200;
            }
        }
    }

    private String getParamLocale() {
        String var1;
        try {
            var1 = this.getParameter("locale");
            if (var1 != null) {
                return var1;
            }
        } catch (Exception var4) {
            ;
        }

        if (this.aString1423.endsWith("aapeli.com")) {
            return "fi";
        } else if (this.aString1423.endsWith("playray.com")) {
            return aString1417;
        } else {
            if (this.aString1423.endsWith(".playforia.com")) {
                try {
                    var1 = this.aString1423.substring(0, this.aString1423.indexOf(46));
                    if (var1.length() > 0 && !var1.equals("www")) {
                        return var1;
                    }
                } catch (Exception var3) {
                    ;
                }
            }

            if (this.aString1423.indexOf("playray") >= 0) {
                try {
                    var1 = this.aString1423.substring(this.aString1423.lastIndexOf(46) + 1);
                    if (var1.length() > 0) {
                        return var1;
                    }
                } catch (Exception var2) {
                    ;
                }
            }

            return aString1417;
        }
    }

    private String getParamLanguage() {
        try {
            String var1 = this.getParameter("lang");
            if (var1 != null) {
                return var1;
            }

            var1 = this.getParameter("language");
            if (var1 != null) {
                return var1;
            }
        } catch (Exception var2) {
            ;
        }

        return null;
    }

    private String getParamChatLanguage() {
        try {
            String var1 = this.getParameter("chatlang");
            if (var1 != null) {
                return var1;
            }

            var1 = this.getParameter("serverlang");
            if (var1 != null) {
                return var1;
            }
        } catch (Exception var2) {
            ;
        }

        return null;
    }

    private String getParamSiteName() {
        try {
            String var1 = this.getParameter("sitename");
            if (var1 != null) {
                return var1;
            }
        } catch (Exception var2) {
            ;
        }

        return this.aString1424.indexOf("aapeli.") >= 0 ? "aapeli" : (this.aString1424.indexOf("playforia.") >= 0 ? aString1418 : (this.aString1424.indexOf("playray.") >= 0 ? "playray" : aString1418));
    }

    private URL getParamQuitPage() {
        URL var1 = this.toURL(this.getParameter("quitpage"));
        if (var1 != null) {
            return var1;
        } else {
            var1 = this.toURL(this.aString1424);
            return var1 != null ? var1 : this.toURL(aString1419);
        }
    }

    private String getParamQuitTarget() {
        String var1 = this.getParameter("quittarget");
        return var1 != null ? var1 : aString1420;
    }

    private String[][] getParamImageAliases() {
        String var1 = this.getParameter("imagealias");
        if (var1 == null) {
            return (String[][]) null;
        } else {
            StringTokenizer var2 = new StringTokenizer(var1, " ");
            int var3 = var2.countTokens();
            if (var3 == 0) {
                return (String[][]) null;
            } else {
                String[][] var4 = new String[var3][2];

                for (int var7 = 0; var7 < var3; ++var7) {
                    String var5 = var2.nextToken();
                    int var6 = var5.indexOf(58);
                    if (var6 <= 0 || var6 == var5.length() - 1) {
                        return (String[][]) null;
                    }

                    var4[var7][0] = var5.substring(0, var6);
                    var4[var7][1] = this.method1670(var5.substring(var6 + 1));
                }

                return var4;
            }
        }
    }

    private String method1670(String var1) {
        StringTokenizer var2 = new StringTokenizer(var1, ",");
        int var3 = var2.countTokens();
        if (var3 <= 1) {
            return var1;
        } else {
            int var4 = (int) (Math.random() * (double) var3);

            while (true) {
                var1 = var2.nextToken();
                if (var4 == 0) {
                    return var1;
                }

                --var4;
            }
        }
    }

    private int[] getParamRegRemindShowTime() {
        String var1 = this.getParameter("regremindshowtime");
        if (var1 == null) {
            return null;
        } else {
            StringTokenizer var2 = new StringTokenizer(var1, ",");
            int var3 = var2.countTokens();
            if (var3 == 0) {
                return null;
            } else {
                int[] var4 = new int[var3];

                for (int var5 = 0; var5 < var3; ++var5) {
                    try {
                        var4[var5] = Integer.parseInt(var2.nextToken());
                    } catch (NumberFormatException var7) {
                        return null;
                    }

                    if (var4[var5] <= 0) {
                        return null;
                    }

                    if (var5 > 0 && var4[var5] <= var4[var5 - 1]) {
                        return null;
                    }
                }

                return var4;
            }
        }
    }

    private URL toURL(String var1) {
        try {
            return new URL(var1);
        } catch (MalformedURLException var3) {
            return null;
        }
    }

    private void showPlayerList(String[] nicks, boolean[] winners, String subgame) {
        if (this.debug) {
            System.out.println("Parameters.showPlayerList(...): " + (nicks != null ? "nicks.length=" + nicks.length : "null") + ", " + (winners != null ? "winners.length=" + winners.length : "null"));
        }

        if (this.urlUserListPage != null) {
            String var4 = this.urlUserListPage.toLowerCase();
            String var5 = null;
            if (nicks != null) {
                var5 = "";
                int var6 = nicks.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    var5 = var5 + nicks[var7];
                    if (winners != null) {
                        var5 = var5 + (winners[var7] ? "*" : "");
                    }

                    if (var7 < var6 - 1) {
                        var5 = var5 + ',';
                    }
                }

                var5 = this.method1674(var5);
            }

            String var8;
            if (var4.startsWith("http:")) {
                if (this.urlTargetUserList != null) {
                    var8 = this.urlUserListPage;
                    if (var5 != null) {
                        var8 = var8 + var5;
                        if (subgame != null) {
                            var8 = var8 + "&subgame=" + subgame;
                        }
                    }

                    this.method1676(this.toURL(var8), this.urlTargetUserList);
                }
            } else {
                if (var4.startsWith("javascript:")) {
                    var8 = this.urlUserListPage;
                    var8 = Tools.replaceFirst(var8, "%n", var5 != null ? var5 : "");
                    var8 = Tools.replaceFirst(var8, "%s", subgame != null ? subgame : "");
                    URL var9 = this.toURL(var8);
                    if (var9 == null) {
                        return;
                    }

                    this.method1676(var9, this.urlTargetUserList);
                }

            }
        }
    }

    private String method1674(String var1) {
        int var2 = var1.length();
        StringBuffer var3 = new StringBuffer(var2 * 3);

        for (int var6 = 0; var6 < var2; ++var6) {
            char var4 = var1.charAt(var6);
            if ((var4 < 65 || var4 > 90) && (var4 < 97 || var4 > 122) && (var4 < 48 || var4 > 57) && var4 != 45 && var4 != 126 && var4 != 44) {
                String var5 = Integer.toHexString(var4 & 255);
                var3.append('%');
                if (var5.length() < 2) {
                    var3.append(0);
                }

                var3.append(var5);
            } else {
                var3.append(var4);
            }
        }

        return var3.toString();
    }

    private boolean method1675(String[] var1, String var2) {
        if (this.anInt1455 != 1) {
            return false;
        } else if (var2 == null && this.aString1457 != null) {
            return false;
        } else if (var2 != null && this.aString1457 == null) {
            return false;
        } else if (var2 != null && this.aString1457 != null && !var2.equals(this.aString1457)) {
            return false;
        } else {
            int var3 = var1.length;
            if (var3 != this.aStringArray1456.length) {
                return false;
            } else {
                for (int var4 = 0; var4 < var3; ++var4) {
                    if (!var1[var4].equals(this.aStringArray1456[var4])) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private boolean method1676(URL var1, String var2) {
        if (var1 == null) {
            return false;
        } else {
            AppletContext var3 = this.anApplet1421.getAppletContext();
            if (var2 != null) {
                var3.showDocument(var1, var2);
            } else {
                var3.showDocument(var1);
            }

            return true;
        }
    }
}
