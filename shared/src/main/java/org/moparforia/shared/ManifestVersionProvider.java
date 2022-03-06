package org.moparforia.shared;

import org.moparforia.shared.utils.MinigolfVersion;
import picocli.CommandLine;

public class ManifestVersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() {
        String version = MinigolfVersion.getVersion();
        return new String[] {"${COMMAND-FULL-NAME} " + version};
    }
}
