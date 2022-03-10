package org.moparforia.shared.networking.packets.headers;

public enum ErrorHeaders implements HeaderProvider {
    VERSION_MISMATCH("versionMismatch"),
    USERNAME_TAKEN("usernameTaken"),
    CHEATING_DISABLED("cheatingNotAllowed");
    private final String text;

    /**
     * @param text
     */
    ErrorHeaders(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String getHeader() {
        return text;
    }
}
