/**
 * 
 */
package com.sacknibbles.sch.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * @author Sachin
 *
 */
@Configuration
public class DBConfiguration {

	private @Value("${org.quartz.dataSource.qtzDS.driver}") String driverClassName;
	private @Value("${org.quartz.dataSource.qtzDS.URL}") String dataSourceURL;
	private @Value("$org.quartz.dataSource.qtzDS.user") String dbUserName;
	private @Value("${org.quartz.dataSource.qtzDS.password}") String dbPassword;

	@Bean(name = "quartzDataSource")
	@Primary
	public DataSource createMainDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(dataSourceURL);
		dataSource.setUsername(dbUserName);
		dataSource.setPassword(dbPassword);
		return dataSource;
	}
		
}
