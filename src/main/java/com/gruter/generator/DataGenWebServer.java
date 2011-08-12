package com.gruter.generator;

import java.io.File;

import com.gruter.generator.http.CommonHttpServer;


public class DataGenWebServer {
  CommonHttpServer webServer;
  
  public DataGenWebServer(String[] args) throws Exception {
    if(args.length < 1) {
      System.out.println("Usage: java DataGenWebServer <port>");
      System.exit(0);
    }
    File userDir = new File(System.getProperty("user.dir"));

    webServer = new CommonHttpServer("/", 
        userDir.getCanonicalPath() + "/webapp", "webdefault.xml",
        "0.0.0.0", Integer.parseInt(args[0]), false);

    System.out.println("WebServer started: " + args[0]);
    webServer.start();
  }
  
  public static void main(String[] args) throws Exception {
//    args = new String[]{"8990"};
    new DataGenWebServer(args);
  }
}
