package com.aapeli.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;

public class EncodedXmlReader {

    private String resourcePath;
    private boolean isDebug;

    public EncodedXmlReader(String resourcePath, boolean debug) {
        this.resourcePath = resourcePath;
        this.isDebug = debug;
    }

    public XmlUnit readXmlUnit() {
        try {
            String xmlData = this.readFile();
            return XmlUnit.parseString(xmlData, true, true);
        } catch (Exception ex) {
            if (this.isDebug) {
                System.out.println("EncodedXmlReader.readXmlUnit(): Failed to read or parse xml-file \""
                        + this.resourcePath + "\"");
                ex.printStackTrace();
            }

            return null;
        }
    }

    private String readFile() throws Exception {
        String encoding = "UTF-8";
        InputStream instream = this.getClass().getResourceAsStream(resourcePath);

        PushbackInputStream in = new PushbackInputStream(instream, 3);
        int char1 = in.read();
        int char2 = in.read();
        int char3 = in.read();
        if (char1 == 239 && char2 == 187 && char3 == 191) { // ??? wtf //239=ï 187=» 191=¿
            encoding = "UTF-8";
        } else {
            in.unread(char3);
            in.unread(char2);
            in.unread(char1);
        }

        this.skipToWhiteSpace(in);
        char1 = in.read();
        char2 = in.read();
        if (char1 == '<' && char2 == '?') {
            String var8 = this.readDeclaration(in);
            if (var8 != null) {
                encoding = var8;
            }

            this.skipToWhiteSpace(in);
        } else {
            in.unread(char2);
            in.unread(char1);
        }

        InputStreamReader inReader = new InputStreamReader(in, encoding);
        String xmlData = this.readFully(inReader);
        inReader.close();
        return xmlData;
    }

    private void skipToWhiteSpace(PushbackInputStream in) throws Exception {
        int chr;
        do {
            chr = in.read();
            if (chr == -1) {
                throw new Exception("End of stream when reading out XML white spaces");
            }
        } while (chr <= ' ');

        in.unread(chr);
    }

    private String readDeclaration(PushbackInputStream in) throws Exception {
        StringBuffer sb = new StringBuffer(50);

        while (true) {
            int chrCurrent = in.read();
            if (chrCurrent == -1) {
                throw new Exception("End of stream before XML header was finished");
            }

            if (chrCurrent == '>') {
                String tag = sb.toString();
                int indexEnc = tag.toLowerCase().indexOf("encoding=");
                if (indexEnc == -1) {
                    return null;
                }

                int indexEncVal = indexEnc + 9;
                char chrQuote = tag.charAt(indexEncVal);
                int indexEndQuote = tag.indexOf(chrQuote, indexEncVal + 1);
                if (indexEndQuote == -1) {
                    return null;
                }

                return tag.substring(indexEncVal + 1, indexEndQuote);
            }

            sb.append((char) chrCurrent);
        }
    }

    private String readFully(InputStreamReader inReader) throws IOException {
        BufferedReader reader = new BufferedReader(inReader);
        StringBuffer sb = new StringBuffer(4096);

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }
}
