package org.mediacat.torrentengine;

public class TorrentEngineFailedException extends Exception {
    public TorrentEngineFailedException (Throwable cause) {
        this.initCause(cause);
    }

}
