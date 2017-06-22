# icmp4j

# under construction !!#
please check
https://sourceforge.net/projects/icmp4j/?source=typ_redirect



Sample code:
  import org.icmp4j.IcmpPingUtil;
  import org.icmp4j.IcmpPingRequest;
  import org.icmp4j.IcmpPingResponse;

  // request - use IcmpPingUtil.createIcmpPingRequest () to create a request with defaults
  final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
  request.setHost ("www.google.com");

  // delegate
  final IcmpPingResponse response = IcmpPingUtil.executePingRequest (request);

  // log
  final String formattedResponse = IcmpPingUtil.formatResponse (response);
  System.out.println (formattedResponse);

See it in action:
  http://www.everyping.com/

Compilers:
  Built with Sun Java 1.7.0_55-b13, with a source/target combination of 1.6
  Apache Ant 1.9.2

Binaries:
  jna-3.5.1.jar (677 KB)
  platform-3.5.1.jar (931 KB)
  icmp4j.jar (23 KB)

Tested platforms:
  Windows 8.1 64-bit
  Windows Server 2008r2 64-bit
  Windows 7 Pro 64-bit (ver 6.1.7601)
  Windows XP Pro 32-bit
  Debian 6
  Mint17
  ArchLinux
  OSX 10.11.3
  Ubuntu 15.10 (kernel 4.2.0) 64 bit
  Ubuntu 15.10 (kernel 4.2.0) 32 bit   

Running from the command line:
  1. mkdir c:\temp\icmp4j
  2. cd c:\temp\icmp4j
  3. Place icmp4j-project.zip in c:\temp\icmp4j
  4. Expand icmp4j-project.zip in place
  5. cd c:\temp\icmp4j\trunk\icmp4j\output\tool
  4. java -cp * org.icmp4j.tool.Ping www.google.com
     -or, if the above does not work:
  4. java -cp jna-3.5.1.jar;platform-3.5.1.jar;icmp4j.jar org.icmp4j.tool.Ping www.google.com

Using icmp4j with native libraries on unix platforms:
  ICMP EchoReply native access can be done either using JNI or JNA calls.
  icmp4j-project.zip contains compiled dynamic libraries for OSX 10.11 and Linux (32 and 64 bit)

  1. Expand icmp4j-project.zip to the directory of your choice
  2. in the trunk/platform/linux/release, look for the library matching your architecture.
     - for linux distribution (32 bit) copy libicmp4jJNI_32bit.so and libicmp4jJNA_32bit.so to your deployment directory.
       Rename libicmp4jJNI_32bit.so to libicmp4jJNI.so and libicmp4jJNA_32bit.so to libicmp4jJNA.so

     - for linux distribution (64 bit) copy libicmp4jJNI_64bit.so and libicmp4jJNA_64bit.so to your deployment directory.
       Rename libicmp4jJNI_64bit.so to libicmp4jJNI.so and libicmp4jJNA_64bit.so to libicmp4jJNA.so

     - for OSX copy libicmp4jJNA.dylib to your deployment directory

  3.JNI mode
    java -cp icmp4j.jar -Djava.libraty.path=<path to your library> org.icmp4j.tool.Ping www.google.com

    JNA mode
    java -cp jna-3.5.1.jar;platform-3.5.1.jar;icmp4j.jar -Djna.library.path=<path to your library> org.icmp4j.tool.Ping www.google.com

Recompiling native librairies:
You can recompile the libraries for your own platform.
The source code and the makefile are located in trunk/platform/unix/source in the icmp4j-project.zip file.


Credits:
1. shortpasta-icmp, the predecessors of icmp4j
2. Haiming Zhang, 64-bit versions of the dll (most recent build)
3. Tiberius Pircalabu, 64-bit versions of the dll (initial builds)
4. Damian Fernandez, reported bug with shortpasta-icmp.dll and sping.exe that can generate the GPF when running in non-administrative mode
5. Jun Kwang, help with testing IcmpPingTool
6. Kevin Shih: Help with testing and integration
7. Nucly: add Mint17 and ArchLinux support
8. Dekker: cooperate on Android support
9. Daifeisg8: Icmp4jUtil.nativeBridge initialization bug
10. Laurent Buhler: *nix and mac platform native implementations
