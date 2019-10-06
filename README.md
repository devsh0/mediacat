# Mediacat
Simple tool to extract torrent magnets from the web.
```
mcat "Best Movie in The World" --hd -f=3
```

### Usage
`mcat --help` for usage instructions.

### Build
**Make sure you have a working copy of Java 11 installed on your machine.**

Clone this repository:
```
git clone https://github.com/theaveragedude/mediacat.git
```
You can browse the source code and make changes as per your requirements. To ensure compatibility
with you environment, do a `mvn test` and make sure all the test cases pass.

### Using as A Library
If you wish, you can use Mediacat as a library and extend it to include custom torrent engines to
lookout magnets. Do a `mvn package` to get the jar and include it as a dependency in your
project. You will need Java 11 for compilation, though.

### Adding custom TorrentEngines
Internally, Mediacat uses custom implementations of `TorrentEngine`s to search, parse, and fetch
a list of torrent files and magnet URLs. One implementation is readily available (see `org.mediacat.torrent.Kat`)
and used as the default engine. Each engine implements `org.mediacat.torrent.TorrentEngine`.
To add your own implementation to the list of available engines, 

##### 1. Add an entry to `configs/torrent.settings.properties`:
Add the fully qualified name of your class as an entry for `org.mediacat.torrent.impls`. Multiple implementation
names must be separated by a comma. Then add three more properties to this file in this format:

***<fully-qualified-class-name>.url=<http(s)://baseurl.com>***

***<fully-qualified-class-name>.searchPath=<path/to/search>***

***<fully-qualified-class-name>.proxy.isSet=false***

`*.url` specifies the base URL of the website that will be crawled for magnets. With `*.searchPath`, you
specify the endpoint to perform searches in that website. `*.proxy.*` properties are there to allow proxy
settings. Halfway implementing proxy support for individual engines, I realized that something in Java
platform is broken which prevents us from using SOCKSv4 and SOCKSv5 proxies. Hence, **it is advised to disable
proxies for all implementations by setting `*.proxy.isSet=false`**.

##### 2. Define a class implementing TorrentEngine
Each implementation encapsulates the entire routine to grab magnets from a website. Implementations are
generally straight forward. Documentations will soon be added for further guidance. As of now, you can peek
the source code of `org.mediacat.torrent.Kat` to get a hint.
