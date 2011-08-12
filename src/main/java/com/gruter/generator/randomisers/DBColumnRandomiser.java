package com.gruter.generator.randomisers;

import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class DBColumnRandomiser implements IRandomiserFunctionality {
  Logger logger = Logger.getLogger(DBColumnRandomiser.class);
  private String driver;
  private String connUrl;
  private String userId;
  private String password;
  private String query;
  private int maxItems;
  
  List<String> items = new ArrayList<String>();
  
  int itemSize;
  
  Random random;
  
  @Override
  public void destroy() {
  }

  @Override
  public Object generate() {
    return items.get(random.nextInt(itemSize));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setRandomiserInstance(RandomiserInstance ri) {
    LinkedHashMap<String, String> props = ri.getProperties();
    
    driver = props.get("driver");
    connUrl = props.get("connUrl");
    userId = props.get("userId");
    password = props.get("password");
    query = props.get("query");
    maxItems = Integer.parseInt(props.get("maxItems"));
    
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(connUrl, userId, password);
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      int count = 0;
      while(rs.next()) {
        items.add(rs.getString(1));
        count++;
        if(count > maxItems) {
          logger.warn("exceed maxItems[" + maxItems + "]");
          break;
        }
      }
      
      itemSize = items.size();
      random = new Random();
      
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e1) {
        }
      }
      if(stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e1) {
        }
      }
      if(rs != null) {
        try {
          rs.close();
        } catch (SQLException e1) {
        }
      }
    }
  }
}
