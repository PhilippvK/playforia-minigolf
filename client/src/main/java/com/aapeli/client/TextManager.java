package com.aapeli.client;

import com.aapeli.tools.EncodedXmlReader;
import com.aapeli.tools.Tools;
import com.aapeli.tools.XmlUnit;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public final class TextManager implements Runnable {

    private Parameters aParameters1512;
    private Thread aThread1513;
    private String aString1514;
    private Hashtable aHashtable1515;
    private Hashtable aHashtable1516;
    private String aString1517;
    private boolean aBoolean1518;
    private boolean aBoolean1519;

    public TextManager(Applet var1, String var2) {
        this(var1, var2, false);
    }

    public TextManager(Applet var1, String var2, boolean var3) {
        this(var3);
        this.aString1514 = var2;
        this.aBoolean1518 = false;
        this.method1731(var1);
    }

    public TextManager(Parameters var1) {
        this(var1, false, false);
    }

    public TextManager(Parameters var1, boolean var2) {
        this(var1, false, var2);
    }

    public TextManager(Parameters var1, boolean var2, boolean var3) {
        this(var3);
        this.aParameters1512 = var1;
        String var4 = var1.getTranslationLang();
        if (var4 != null) {
            this.aString1514 = var4;
            this.aBoolean1518 = true;
        } else {
            this.aString1514 = var1.getLocale();
            this.aBoolean1518 = false;
        }

        if (var2) {
            this.aThread1513 = new Thread(this);
            this.aThread1513.start();
        } else {
            this.method1731(var1.getApplet());
        }

    }

    private TextManager(boolean var1) {
        this.aBoolean1519 = var1;
        this.aHashtable1515 = new Hashtable();
        this.aHashtable1516 = new Hashtable();
        this.aString1517 = null;
        this.aThread1513 = null;
    }

    public void run() {
        if (this.aBoolean1519) {
            System.out.println("TextManager.run(): Start loading texts");
        }

        this.method1731(this.aParameters1512.getApplet());
        this.aThread1513 = null;
        if (this.aBoolean1519) {
            System.out.println("TextManager.run(): Finished loading texts");
        }

    }

    public String getGame(String var1) {
        return this.getGame(var1, (String[]) null);
    }

    public boolean isAvailable(String var1) {
        return this.method1729(var1, 1) != null;
    }

    public String getIfAvailable(String var1) {
        return this.getIfAvailable(var1, (String) null);
    }

    public String getIfAvailable(String var1, String var2) {
        String var3 = this.method1729(var1, 1);
        return var3 != null ? var3 : var2;
    }

    public String getGame(String var1, String var2) {
        String[] var3 = new String[]{var2};
        return this.getGame(var1, var3);
    }

    public String getGame(String var1, String var2, String var3) {
        String[] var4 = new String[]{var2, var3};
        return this.getGame(var1, var4);
    }

    public String getGame(String var1, String var2, String var3, String var4) {
        String[] var5 = new String[]{var2, var3, var4};
        return this.getGame(var1, var5);
    }

    public String getGame(String var1, String var2, String var3, String var4, String var5) {
        String[] var6 = new String[]{var2, var3, var4, var5};
        return this.getGame(var1, var6);
    }

    public String getGame(String var1, String var2, String var3, String var4, String var5, String var6) {
        String[] var7 = new String[]{var2, var3, var4, var5, var6};
        return this.getGame(var1, var7);
    }

    public String getGame(String var1, int var2) {
        String[] var3 = new String[]{"" + var2};
        return this.getGame(var1, var3);
    }

    public String getGame(String var1, int var2, int var3) {
        String[] var4 = new String[]{"" + var2, "" + var3};
        return this.getGame(var1, var4);
    }

    public String getGame(String var1, int var2, int var3, int var4) {
        String[] var5 = new String[]{"" + var2, "" + var3, "" + var4};
        return this.getGame(var1, var5);
    }

    public String getGame(String var1, int var2, int var3, int var4, int var5) {
        String[] var6 = new String[]{"" + var2, "" + var3, "" + var4, "" + var5};
        return this.getGame(var1, var6);
    }

    public String getNumber(long var1) {
        return this.method1726(var1, true);
    }

    public String getDecimalNumber(double var1) {
        if (var1 == 0.0D) {
            return "0";
        } else {
            double var3 = var1 < 0.0D ? -var1 : var1;

            int var5;
            for (var5 = 0; var3 < 100.0D; ++var5) {
                var3 *= 10.0D;
            }

            return this.getNumber(var1, var5);
        }
    }

    public String getNumber(double var1, int var3) {
        return this.getNumber(var1, true, var3);
    }

    public String getNumber(double var1, boolean var3, int var4) {
        if (var4 <= 0) {
            return this.method1726((long) var1, var3);
        } else {
            boolean var5 = var1 < 0.0D;
            if (var5) {
                var1 = -var1;
            }

            long var6 = 1L;

            for (int var8 = 0; var8 < var4; ++var8) {
                var6 *= 10L;
            }

            long var17 = (long) (var1 * (double) var6 + 0.5D);
            long var10 = var17 / var6;
            long var12 = var17 % var6;
            String var14 = "" + var12;
            int var15 = var4 - var14.length();

            for (int var16 = 0; var16 < var15; ++var16) {
                var14 = "0" + var14;
            }

            String var18 = var5 ? "-" : "";
            var18 = var18 + this.method1726(var10, var3);
            var18 = var18 + this.getShared("SeparatorDecimal");
            var18 = var18 + var14;
            return var18;
        }
    }

    public String getTime(long var1) {
        return this.getTime(var1 * 1000L, false);
    }

    public String getTime(long var1, boolean var3) {
        boolean var4 = var1 < 0L;
        if (var4) {
            var1 = -var1;
        }

        int var5 = (int) ((var1 % 1000L + 5L) / 10L);
        if (!var3) {
            var1 += 500L;
        }

        var1 /= 1000L;
        int var6 = (int) (var1 % 60L);
        var1 /= 60L;
        int var7 = (int) (var1 % 60L);
        int var8 = (int) (var1 / 60L);
        boolean var9 = var8 > 0;
        boolean var10 = var9 || var7 > 0 || !var3;
        boolean var11 = var3 && var8 == 0;
        String var12 = var4 ? "-" : "";
        if (var9) {
            var12 = var12 + var8;
        }

        if (var9 && var10) {
            var12 = var12 + this.getShared("SeparatorHourMinute") + (var7 < 10 ? "0" : "");
        }

        if (var10) {
            var12 = var12 + var7 + this.getShared("SeparatorMinuteSecond") + (var6 < 10 ? "0" : "");
        }

        var12 = var12 + var6;
        if (var11) {
            var12 = var12 + this.getShared("SeparatorSecondFraction") + (var5 < 10 ? "0" : "") + var5;
        }

        return var12;
    }

    public String getDate(long var1, boolean var3) {
        return this.method1727(var1, var3 ? 1 : 0);
    }

    public String getClock(long var1, boolean var3) {
        return this.method1728(var1, var3 ? 1 : 0);
    }

    public String getCurrentDateAndClock(boolean var1) {
        long var2 = System.currentTimeMillis();
        String var4 = this.getDate(var2, var1) + " " + this.getClock(var2, var1);
        return var4;
    }

    public String getDateWithTodayYesterday(long var1) {
        return this.method1727(var1, 2);
    }

    public char getDecimalSeparator() {
        String var1 = this.getShared("SeparatorDecimal");
        return var1.charAt(0);
    }

    public String getShared(String var1) {
        return this.getShared(var1, (String[]) null);
    }

    public String getShared(String var1, String var2) {
        String[] var3 = new String[]{var2};
        return this.getShared(var1, var3);
    }

    public String getShared(String var1, String var2, String var3) {
        String[] var4 = new String[]{var2, var3};
        return this.getShared(var1, var4);
    }

    public String getShared(String var1, String var2, String var3, String var4) {
        String[] var5 = new String[]{var2, var3, var4};
        return this.getShared(var1, var5);
    }

    public String getShared(String var1, String var2, String var3, String var4, String var5) {
        String[] var6 = new String[]{var2, var3, var4, var5};
        return this.getShared(var1, var6);
    }

    public String getWithQuantity(String var1, int var2) {
        return this.getGame(var1, new String[]{"" + var2}, var2);
    }

    public String getWithQuantity(String var1, String[] var2, int var3) {
        return this.getGame(var1, var2, var3);
    }

    public boolean isLoadingFinished() {
        return this.aThread1513 == null;
    }

    public void waitLoadingFinished() {
        while (!this.isLoadingFinished()) {
            Tools.sleep(50L);
        }

    }

    public Parameters getParameters() {
        return this.aParameters1512;
    }

    public void destroy() {
        if (this.aThread1513 == null) {
            if (this.aHashtable1515 != null) {
                this.aHashtable1515.clear();
                this.aHashtable1515 = null;
            }

            if (this.aHashtable1516 != null) {
                this.aHashtable1516.clear();
                this.aHashtable1516 = null;
            }

            this.aParameters1512 = null;
            this.aString1514 = null;
            this.aString1517 = null;
        }
    }

    protected String method1719() {
        return this.aString1514;
    }

    private String getGame(String var1, String[] var2) {
        return this.getGame(var1, var2, 1);
    }

    private String getGame(String var1, String[] var2, int var3) {
        String var4 = this.method1722(var1, var2, var3);
        if (var4 != null) {
            return var4;
        } else {
            var4 = this.method1729(var1, var3);
            if (var2 != null) {
                int var5 = var2.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    var4 = Tools.replaceFirst(var4, "%" + (var6 + 1), var2[var6]);
                }
            }

            return var4;
        }
    }

    private String method1722(String var1, String[] var2, int var3) {
        if (this.aThread1513 != null) {
            return "[Loading texts...]";
        } else if (this.aHashtable1515 == null && this.aString1517 != null) {
            return "[" + this.aString1517 + "]";
        } else {
            String var4 = this.method1729(var1, var3);
            if (var4 == null) {
                if (this.aBoolean1519) {
                    System.out.println("TextManager.getText(\"" + var1 + "\"): Key not found");
                }

                return this.method1723(var1, var2);
            } else {
                return null;
            }
        }
    }

    private String method1723(String var1, String[] var2) {
        String var3 = "{" + var1 + "}";
        if (var2 != null) {
            int var4 = var2.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                var3 = var3 + " (" + var2[var5] + ")";
            }
        }

        return var3;
    }

    private String getShared(String var1, String[] var2) {
        return this.getShared(var1, var2, 1);
    }

    private String getShared(String var1, String[] var2, int var3) {
        if (this.aThread1513 != null) {
            return "[Loading texts...]";
        } else if (this.aHashtable1516 == null && this.aString1517 != null) {
            return "[" + this.aString1517 + "]";
        } else {
            String var4 = this.method1730(var1, var3);
            if (var4 == null) {
                return this.method1723(var1, var2);
            } else {
                if (var2 != null) {
                    int var5 = var2.length;

                    for (int var6 = 0; var6 < var5; ++var6) {
                        var4 = Tools.replaceFirst(var4, "%" + (var6 + 1), var2[var6]);
                    }
                }

                return var4;
            }
        }
    }

    private String method1726(long var1, boolean var3) {
        if ((var1 <= -1000L || var1 >= 1000L) && var3) {
            boolean var4 = var1 < 0L;
            if (var4) {
                var1 = -var1;
            }

            String var5 = "";
            String var6 = this.getShared("SeparatorThousand");

            do {
                int var7 = (int) (var1 % 1000L);
                var5 = var7 + var5;
                var1 /= 1000L;
                if (var1 > 0L) {
                    if (var7 < 10) {
                        var5 = "00" + var5;
                    } else if (var7 < 100) {
                        var5 = "0" + var5;
                    }

                    var5 = var6 + var5;
                }
            } while (var1 > 0L);

            if (var4) {
                var5 = "-" + var5;
            }

            return var5;
        } else {
            return "" + var1;
        }
    }

    private String method1727(long var1, int var3) {
        Calendar var4 = Calendar.getInstance();
        var4.setTime(new Date(var1));
        int var5 = var4.get(1);
        int var6 = var4.get(2) + 1;
        int var7 = var4.get(5);
        if (var3 == 0) {
            return var5 + "-" + (var6 < 10 ? "0" : "") + var6 + "-" + (var7 < 10 ? "0" : "") + var7;
        } else {
            String var8 = this.getShared("DateFormat");
            var8 = Tools.replaceFirst(var8, "%1", "" + var7);
            var8 = Tools.replaceFirst(var8, "%2", this.getShared("DateMonth" + var6));
            var8 = Tools.replaceFirst(var8, "%3", "" + var5);
            if (var3 == 1) {
                return var8;
            } else {
                try {
                    Calendar var9 = Calendar.getInstance();
                    var9.set(11, 0);
                    var9.set(12, 0);
                    var9.set(13, 0);
                    var9.set(14, 0);
                    long var10 = var9.getTime().getTime();
                    long var12 = var10 - 86400000L;
                    long var14 = var10 + 86400000L;
                    if (var1 >= var12 && var1 < var10) {
                        var8 = this.getShared("DateYesterday");
                    }

                    if (var1 >= var10 && var1 < var14) {
                        var8 = this.getShared("DateToday");
                    }
                } catch (Exception var16) {
                    ;
                }

                return var8;
            }
        }
    }

    private String method1728(long var1, int var3) {
        Calendar var4 = Calendar.getInstance();
        var4.setTime(new Date(var1));
        boolean var5 = true;
        if (var3 == 1 && this.getShared("ClockHours").equals("12")) {
            var5 = false;
        }

        int var6 = var4.get(var5 ? 11 : 10);
        int var7 = var4.get(12);
        String var8 = "";
        if (!var5) {
            if (var6 == 0) {
                var6 = 12;
            }

            int var9 = var4.get(9);
            if (var9 == 0) {
                var8 = this.getShared("ClockAM");
            } else if (var9 == 1) {
                var8 = this.getShared("ClockPM");
            }
        }

        if (var3 == 0) {
            return (var6 < 10 ? "0" : "") + var6 + "-" + (var7 < 10 ? "0" : "") + var7;
        } else {
            String var10 = this.getShared("ClockFormat");
            var10 = Tools.replaceFirst(var10, "%1", "" + var6);
            var10 = Tools.replaceFirst(var10, "%2", (var7 < 10 ? "0" : "") + var7);
            if (!var5) {
                var10 = Tools.replaceFirst(var10, "%3", var8);
            }

            return var10;
        }
    }

    protected String method1729(String var1, int var2) {
        var1 = var1.toLowerCase();
        if (this.aBoolean1518) {
            Class89 var3 = (Class89) ((Class89) this.aHashtable1515.get(var1));
            return var3 == null ? null : var3.method1737(var2);
        } else {
            return (String) ((String) this.aHashtable1515.get(var1));
        }
    }

    protected String method1730(String var1, int var2) {
        var1 = var1.toLowerCase();
        if (this.aBoolean1518) {
            Class89 var3 = (Class89) ((Class89) this.aHashtable1516.get(var1));
            return var3 == null ? null : var3.method1737(var2);
        } else {
            return (String) ((String) this.aHashtable1516.get(var1));
        }
    }

    private void method1731(Applet var1) {
        if (this.aBoolean1518) {
            this.method1734(var1);
        } else {
            this.method1732(var1);
        }

    }

    private void method1732(Applet var1) {
        URL var2 = var1.getCodeBase();
        this.aHashtable1515 = this.method1733(var2);

        try {
            System.out.println("FileUtil.isFileUrl: "+ var2);
            if (FileUtil.isFileUrl(var2)) {
                var2 = new URL(var2, FileUtil.RESOURCE_DIR);
            } else {
                var2 = new URL(var2, "../Shared/");
            }
        } catch (MalformedURLException var4) {
            ;
        }

        this.aHashtable1516 = this.method1733(var2);
    }

    private Hashtable method1733(URL var1) {
        Hashtable var2 = new Hashtable();
        BufferedReader var3 = null;
        String var4 = this.aString1514 + ".loc";

        try {
            URL var5 = new URL(var1, "locale/");
            var5 = new URL(var5, var4);
            System.out.println("URL: "+var5);
            InputStream var6 = var5.openStream();

            InputStreamReader var7;
            try {
                var7 = new InputStreamReader(var6, "Cp1252");
            } catch (UnsupportedEncodingException var12) {
                var7 = new InputStreamReader(var6);
            }

            var3 = new BufferedReader(var7);

            String var8;
            while ((var8 = var3.readLine()) != null) {
                var8 = var8.trim();
                if (var8.length() > 0 && var8.charAt(0) != 35) {
                    int var10 = var8.indexOf(61);
                    if (var10 <= 0) {
                        if (this.aBoolean1519) {
                            System.out.println("Missing \'=\'-character in \"" + this.aString1514 + "\"-locale file: \"" + var8 + "\"");
                            Thread.dumpStack();
                        }
                    } else {
                        String var9 = var8.substring(0, var10).trim();
                        if (var9.length() == 0) {
                            if (this.aBoolean1519) {
                                System.out.println("Empty key in \"" + this.aString1514 + "\"-locale file: \"" + var8 + "\"");
                                Thread.dumpStack();
                            }
                        } else {
                            var2.put(var9.toLowerCase(), var8.substring(var10 + 1).trim());
                        }
                    }
                }
            }
        } catch (FileNotFoundException var13) {
            if (this.aBoolean1519) {
                System.out.println("Missing localization file \"" + var4 + "\"");
            }

            this.aString1517 = "Texts for \'" + this.aString1514 + "\' not available";
            var2 = null;
        } catch (Exception var14) {
            if (this.aBoolean1519) {
                var14.printStackTrace();
            }

            this.aString1517 = var14.toString();
            var2 = null;
        }

        try {
            var3.close();
        } catch (Exception var11) {
            ;
        }

        return var2;
    }

    private void method1734(Applet var1) {
        URL var2 = var1.getCodeBase();
        String var5 = null;
        int var6 = this.aString1514.indexOf(47);
        if (var6 > 0) {
            var5 = this.aString1514.substring(var6 + 1);
            this.aString1514 = this.aString1514.substring(0, var6);
        }

        String var3;
        String var4;
        String var7;
        int var8;
        if (false) { //FileUtil.isFileUrl(var2)) {
            var7 = var2.toString();
            var8 = var7.indexOf(58, var7.indexOf(58) + 1) + 2;
            int var9 = var7.indexOf(47, var8);

            try {
                URL var10 = new URL(var2, FileUtil.LANGUAGE_DIR);
                var10 = new URL(var10, this.aString1514 + "/");
                var3 = var10.toExternalForm();
            } catch (MalformedURLException var11) {
                var3 = "file:" + FileUtil.LANGUAGE_DIR + this.aString1514 + "/";
            }

            //var4 = var7.substring(var8, var9);
            var4 = "AGolf";
            var3 = "file:/"+var3.substring(5,var3.length());
        } else {
            var7 = var2.toString();
            var8 = var7.length();
            if (var7.charAt(var8 - 1) == 47) {
                var7 = var7.substring(0, var8 - 1);
                --var8;
            }

            var6 = var7.lastIndexOf(47);
            var3 = var7.substring(0, var6 + 1) + "l10n/" + this.aString1514 + "/";
            var4 = var7.substring(var6 + 1);
        }

        if (var5 != null) {
            var4 = var5;
        }

        this.aHashtable1515 = this.method1735(var3 + var4 + ".xml");
        this.aHashtable1516 = this.method1735(var3 + "Shared.xml");
    }

    private Hashtable method1735(String var1) {
        EncodedXmlReader var2 = new EncodedXmlReader(var1, /*this.aBoolean1519*/true);
        XmlUnit var3 = var2.readXmlUnit();
        if (var3 == null) {
            System.out.println("Failed to read localization file \'" + var1 + "\'");
            this.aString1517 = "XML read error";
            return null;
        } else {
            XmlUnit[] var4 = var3.getChildren("str");
            int var5 = var4.length;
            Hashtable var6 = new Hashtable();

            for (int var7 = 0; var7 < var5; ++var7) {
                var6.put(var4[var7].getAttribute("key").toLowerCase(), new Class89(this, this.aString1514, var4[var7], Tools.getBoolean(var4[var7].getAttribute("reverse"))));
            }

            return var6;
        }
    }
}
