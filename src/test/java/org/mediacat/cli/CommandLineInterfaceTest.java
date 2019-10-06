package org.mediacat.cli;

import org.junit.jupiter.api.Test;
import org.mediacat.torrent.TorrentEngineFailedException;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineInterfaceTest {
    @Test
    public void getTheatreTorrentsTest() {
        try {
            String[] args = {"Article 15", "--best", "--hd"};
            CommandLineInterface.main(args);
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail();
        }
    }

}