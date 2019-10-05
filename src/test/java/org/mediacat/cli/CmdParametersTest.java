package org.mediacat.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CmdParametersTest {
    @Test
    public void simpleTest() {
        String[] args = new String[]{"\"Some movie name\"", "-smf", "10", "--best"};
        CommandLineInterface.main(args);
    }
}