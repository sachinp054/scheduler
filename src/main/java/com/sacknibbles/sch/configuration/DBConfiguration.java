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

	private @Value("${db.default.driver}") String driverClassName;
	private @Value("${db.default.url}") String dataSourceURL;
	private @Value("${db.default.username}") String dbUserName;
	private @Value("${db.default.password}") String dbPassword;

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
