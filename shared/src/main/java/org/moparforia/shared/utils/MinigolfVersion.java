package org.moparforia.shared.utils;

public final class MinigolfVersion {
    public static String getVersion() {
        String version = MinigolfVersion.class.getPackage().getImplementationVersion();
        return version != null ? version : "development";
    }
}
