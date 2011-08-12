/**
 * 
 */
package com.gruter.generator.web;

import generator.misc.DataFileDefinition;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gruter.generator.service.GeneratorService;
import com.gruter.generator.service.IGeneratorService;

/**
 * @author kimjh
 * 
 */
@Controller("mainController")
public class MainController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

  @Resource(type = GeneratorService.class)
  private IGeneratorService generatorService;

  @RequestMapping("main.do")
  protected ModelAndView handlerIndex() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("generator");
    return mav;
  }

  @RequestMapping(value = "get_file_definitions.do", method = RequestMethod.GET)
  protected @ResponseBody 
  Object getFileDefinitions() throws Exception {
    try {
      return generatorService.getFileDefinitions();
    } catch (Exception e) {
      return this.createFailureResponse(e.getMessage(), e);
    }
  }

  @RequestMapping(value = "get_jobs.do", method = RequestMethod.GET)
  protected @ResponseBody 
  Object getGeneratorJobs() throws Exception {
    try {
      return generatorService.getGeneratorJobs();
    } catch (Exception e) {
      return this.createFailureResponse(e.getMessage(), e);
    }
  }
  
  @RequestMapping(value = "generate.do", method = RequestMethod.POST)
  protected @ResponseBody 
  Object generate(
      @RequestParam(value = "name", required=true) String name,
      @RequestParam(value = "totalRecords", required=false) long totalRecords,
      @RequestParam(value = "tps", required=false) int tps,
      @RequestParam(value = "delimiter", required=true) String delimiter,
      @RequestParam(value = "dataItemXml", required=true) String dataItemXml,
      @RequestParam(value = "log4jName", required=true) String log4jName,
      @RequestParam(value = "log4j", required=true) String log4j) throws Exception {
    try {
      DataFileDefinition dfd = new DataFileDefinition();
      dfd.setName(name);
      dfd.setLog4jName(log4jName);
      dfd.setLog4j(log4j);
      dfd.setDataItemXml(dataItemXml);
      dfd.setDelimiter(delimiter);
      dfd.setTps(tps);
      dfd.setTotalRecords(totalRecords);
      
      String jobId = generatorService.generateLog(dfd, true);
      return this.createSuccessResponse("Successfully launched[jobid=" + jobId + "]", jobId);
      
    } catch (Exception e) {
      e.printStackTrace();
      return this.createFailureResponse(e.getMessage(), e);
    }
  }
  
  @RequestMapping(value = "handle_job.do", method = RequestMethod.GET)
  protected @ResponseBody Object stopJob(
      @RequestParam(value = "jobId", required=true) String jobId, 
      @RequestParam(value="mode", required=true) String mode) throws Exception {
    if("stop".equals(mode)) {
      generatorService.stopGenerator(jobId);
    } else if("pause".equals(mode)) {
      generatorService.pauseGenerator(jobId);
    } else if("resume".equals(mode)) {
      generatorService.resumeGenerator(jobId);
    }
    return this.createSuccessResponse("Successfully " + mode  + "[jobid=" + jobId + "]", jobId);
  }
}
