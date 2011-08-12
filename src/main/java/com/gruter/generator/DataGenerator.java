package com.gruter.generator;

import generator.misc.DataFileDefinition;
import generator.misc.Utils;

import java.util.Vector;

import com.gruter.generator.service.GeneratorService;

public class DataGenerator {
  public static void main(String[] args) throws Exception {
//    args = new String[] { "/Users/babokim/workspace/dgmaster_maven/conf/TextFileDefinitions.xml", "TestFile" };
    if (args.length < 2) {
      System.out.println("Usage: java DataGenerator <file definition xml> <file definition name>");
      System.exit(0);
    }

    String fileName = args[0];
    String dfName = args[1];

    DataFileDefinition dfd = getDataFileDefinitions(fileName, dfName);
    if (dfd == null) {
      System.out.println("No " + dfName + " in " + fileName);
      System.exit(0);
    }
    GeneratorService service = new GeneratorService();
    service.generateLog(dfd, false);
    service.stop();
  }

  public static DataFileDefinition getDataFileDefinitions(String filePath, String dfName) {
    Utils utils = new Utils();
    Vector<DataFileDefinition> vData = utils.loadDataFileDefinitions();

    for (DataFileDefinition eachDfd : vData) {
      if (eachDfd.getName().equals(dfName)) {
        return eachDfd;
      }
    }
    return null;
  }
}
