package org.mediacat.torrentengine;

class TorrentEngineState {
    private final TorrentEngine engine;
    private volatile boolean isFailing;

    TorrentEngineState(TorrentEngine engine) {
        this.engine = engine;
        this.isFailing = false;
    }

    public void setFailing() {
        this.isFailing = true;
    }

    public void unsetFailing() {
        this.isFailing = false;
    }

    public TorrentEngine getEngine() {
        return this.engine;
    }
}
