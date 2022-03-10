package org.moparforia.shared.networking.packets.headers;

public enum ChatMessageHeaders implements HeaderProvider {
    PLAYER_JOINED("playerJoined"),
    PLAYER_LEFT("playerLeft");
    private final String text;

    /**
     * @param text
     */
    ChatMessageHeaders(final String text) {
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
