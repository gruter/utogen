package com.gruter.generator.http;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratorContextListener implements ServletContextListener {
	private static final Logger LOG = LoggerFactory.getLogger(GeneratorContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				LOG.info(String.format("deregistering jdbc driver: %s", driver));
			} catch (SQLException e) {
				LOG.info(String.format("Error deregistering driver %s", driver), e);
			}	
		}
	}
}