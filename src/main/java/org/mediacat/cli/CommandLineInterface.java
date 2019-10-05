package org.mediacat.cli;

import picocli.CommandLine;

public class CommandLineInterface {
    public static void main(String[] args) {
        CmdParameters params = new CmdParameters();
        new CommandLine(params).parseArgs(args);
        params.wrapUp();

        System.out.println("search term: " + params.searchTerm);
        System.out.println("fetch count: " + params.fetchCount);
        System.out.println("includes theatre: " + params.includeTheatre);
        System.out.println("includes hd: " + params.includeHd);
        System.out.println("includes bluray: " + params.includeBluray);
        System.out.println("magnet only: " + params.magnetOnly);
        System.out.println("best only: " + params.best);
        System.out.println("save as default: " + params.saveAsDefault);
    }
}
