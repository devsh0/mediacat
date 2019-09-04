package mediacat.torrent_engine;

import java.util.List;

public interface TorrentEngine {
    List<TorrentMeta> getTorrentMeta (String searchTerm) throws TorrentEngineFailedException;
}
