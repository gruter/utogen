package com.gruter.generator.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class GeneratorServlet extends HttpServlet {
  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = null;
    try {
      out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);

      String action = request.getParameter("action");
      if(action == null) {
        out.write("{\"error\":\"no action param\"}");
        out.close();
        return;
      }
      
      String result = "";
      if(action.equals("Generate")) {
        result = generate(request);
      } else if(action.equals("Pause")){
//        result = generate();
      }
      
      response.setContentType("text/html;charset=UTF-8");

      out.write(result);
    } catch (Exception e) {
      e.printStackTrace();
      out.write(e.getMessage());
    } finally {
      out.close();
    }
  }

  private String generate(HttpServletRequest request) {
    String xmlCode = request.getParameter("xmlCode");
    System.out.println(">>>>>>>>" + xmlCode);
    return "{\"jobId\":\"1234\"}";
  }
}
