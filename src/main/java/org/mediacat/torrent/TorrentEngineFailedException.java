package org.mediacat.torrent;

public class TorrentEngineFailedException extends Exception {
    public TorrentEngineFailedException (Throwable cause) {
        this.initCause(cause);
    }

    public TorrentEngineFailedException(String message) {
        super(message);
    }
}
