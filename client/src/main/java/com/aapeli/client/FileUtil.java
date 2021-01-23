package com.aapeli.client;

import java.net.URL;

class FileUtil {

    //protected static final String RESOURCE_DIR = "/Shared/Client/src/";
    protected static final String RESOURCE_DIR = "src/";
    //protected static final String LANGUAGE_DIR = "/L10N/";
    protected static final String LANGUAGE_DIR = "src/main/resources/l10n/";


    protected static final boolean isFileUrl(URL url) {
        return url.getProtocol().equalsIgnoreCase("file") ? url.toString().indexOf("fs01") <= 0 : false;
    }
}
