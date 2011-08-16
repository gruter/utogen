/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.misc;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * Represents the information used to create a new text output file.
 * It also holds a vector of DataFileItem which define what to output in the
 * text file.
 */
public class DataFileDefinition
{
    private String name;
    private String description;
    private Vector<DataFileItem> outDataItems; //a place holder for DataFileItem
    private String dataItemXml;
    private String delimiter;
    private long totalRecords;
    private int tps;
    private String log4j;
    private String log4jName;
    
    public void setDataItemXml(String xml) throws Exception {
      String fullXml = "<datafile-definitions><File-output-definition delimiter=\",\" name=\"Test_" + System.currentTimeMillis() + "\" " +
      		"totalRecords=\"0\" tps=\"0\">" + xml;
      fullXml += "</File-output-definition></datafile-definitions>";
      
      Vector<DataFileDefinition> vData;
      RandomDefinitionsBuilder builder = new RandomDefinitionsBuilder();
      
      builder.setInputStreame(new ByteArrayInputStream(fullXml.getBytes()));
      builder.setSAXHandler(new DataFileOutputSAXHandler() );
      vData = builder.getElements();
      if(vData.size() == 0) {
        throw new IOException("XML Parsing error:" + xml);
      }
      outDataItems = vData.get(0).getOutDataItems();
      this.dataItemXml = xml;
    }
   
    public String getDataItemXml() {
      return dataItemXml;
    }
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getDescription() {
      return description;
    }
    public void setDescription(String description) {
      this.description = description;
    }
    public Vector<DataFileItem> getOutDataItems() {
      return outDataItems;
    }
    public void setOutDataItems(Vector<DataFileItem> outDataItems) {
      this.outDataItems = outDataItems;
      StringBuilder sb = new StringBuilder();
      for(DataFileItem eachItem: outDataItems) {
        sb.append(dataItemToXml(eachItem)).append("\n");
      }
      
      dataItemXml = sb.toString();
    }
    
    private String dataItemToXml(DataFileItem dataItem) {
      String result = "<data-item ";
      result += "fieldName=\"" + dataItem.getFieldName()  + "\" ";
      result += "randomiser-instance=\"" + dataItem.getRandomiserInstanceName()  + "\" ";
      result += "width=\"" + dataItem.getWidth()  + "\" ";
      result += "encloseChar=\"" + dataItem.getEncloseChar()  + "\" ";
      if(dataItem.getDummy() != null && dataItem.getDummy().length() > 0) {
        result += "dummy=\"" + dataItem.getDummy()  + "\" ";
      }
      if(dataItem.getExpression() != null && dataItem.getExpression().length() > 0) {
        result += "expression=\"" + dataItem.getExpression()  + "\" ";
      }
      result += "alignment=\"" + dataItem.getAlignment()  + "\" />";
      return result;
    }
    
    public String getDelimiter() {
      return delimiter;
    }
    public void setDelimiter(String delimiter) {
      this.delimiter = delimiter;
    }
    public long getTotalRecords() {
      return totalRecords;
    }
    public void setTotalRecords(long totalRecords) {
      this.totalRecords = totalRecords;
    }
    public int getTps() {
      return tps;
    }
    public void setTps(int tps) {
      this.tps = tps;
    }
    public String getLog4j() {
      return log4j;
    }
    public void setLog4j(String log4j) {
      this.log4j = log4j;
    }
    public String getLog4jName() {
      return log4jName;
    }
    public void setLog4jName(String log4jName) {
      this.log4jName = log4jName;
    }
    public String toString() {
      return name;
    }
    
    public static void main(String[] args) throws Exception {
      String data = "<data-item fieldName=\"f1\" alignment=\"1\" encloseChar=\"\" randomiser-instance=\"SimpleSequencer\" width=\"-1\"/>" + 
                    "<data-item fieldName=\"f2\" alignment=\"1\" encloseChar=\"\" randomiser-instance=\"EnglishFirstnames\" width=\"-1\"/>";

      data = "<datafile-definitions><File-output-definition delimiter=\",\" name=\"Test_" + System.currentTimeMillis() + "\" totalRecords=\"0\" tps=\"0\">" + data;
      data += "</File-output-definition></datafile-definitions>";
      
      Vector<DataFileDefinition> vData;
      RandomDefinitionsBuilder builder = new RandomDefinitionsBuilder();
      
      builder.setInputStreame(new ByteArrayInputStream(data.getBytes()));
      builder.setSAXHandler(new DataFileOutputSAXHandler() );
      vData = builder.getElements();
      System.out.println(">>>>" + vData.get(0).getOutDataItems().size());
    }
}
