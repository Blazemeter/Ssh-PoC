package com.abstracta.sshPoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  @Option(name = "-t", aliases = "--timeout", usage = "Sets the timeout of the connection and " 
      + "waits used in poc. It's expressed in milliseconds")
  public int defaultTimeoutMillis = 10000;
  @Option(name = "-u", aliases = "--username", usage = "Provide ssh user name for login",
      required = true)
  private String username;
  @Option(name = "-a", aliases = "--server-address", metaVar = "ip:port",
      usage = "This parameter specifies the ip address of the server", required = true)
  private String serverAddress;

  @Option(name = "-p", aliases = "--password", usage = "Provide ssh password for login", required =
      true)
  private String password;
  @Option(name = "-h", aliases = "--help", usage = "Show usage information", help = true)
  private boolean showHelp;

  //Visible for testing
  public App(String username, String serverAddress, String password, boolean showHelp) {
    this.username = username;
    this.serverAddress = serverAddress;
    this.password = password;
    this.showHelp = showHelp;
  }

  public App() {

  }

  private boolean isDisplayHelp() {
    return showHelp;
  }

  public static void main(String[] args) {
    App main = new App();
    CmdLineParser parser = new CmdLineParser(main);
    try {
      parser.parseArgument(args);

      if (main.isDisplayHelp()) {
        printHelp(parser, System.out);
      } else {
        main.runShhClient();
      }
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      printHelp(parser, System.err);
    }
  }

  private static void printHelp(CmdLineParser parser, PrintStream printStream) {
    String command = "java -jar ssh-poc-0.1.jar";
    parser.printUsage(printStream);
    printStream.println();
    printStream.println("  Examples: \n"
        + command + " -u testUserName -p testPassword -a 127.0.0.1:22\n"
        + command + " -u testUserName -p testPassword  -a some.rare.domain:23 ");
  }

  public void runShhClient() {
    String host = serverAddress.split(":")[0];
    int port = Integer.parseInt(serverAddress.split(":")[1]);

    SshClient client = SshClient.setUpDefaultClient();
    client.start();

    try (ClientSession session = client.connect(username, host, port)
        .verify(defaultTimeoutMillis).getSession()) {

      session.addPasswordIdentity(password);
      session.auth().verify(defaultTimeoutMillis, TimeUnit.MILLISECONDS);
      try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
          ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
        channel.setOut(responseStream);
        try {
          channel.open().verify(defaultTimeoutMillis, TimeUnit.SECONDS);
          channel.waitFor(Collections.singletonList(ClientChannelEvent.STDOUT_DATA),
              defaultTimeoutMillis);
          LOG.info("Data From Server:\n{}", responseStream);
        } finally {
          channel.close(false);
        }
      }
    } catch (IOException e) {
      LOG.error("There was an error when verifying and connecting to the service", e);
    } finally {
      client.stop();
    }
  }
}
