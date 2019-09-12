package org.mediacat.torrent_engine;

public class TorrentEngineFailedException extends Exception {
    public TorrentEngineFailedException (Throwable cause) {
        this.initCause(cause);
    }

}
