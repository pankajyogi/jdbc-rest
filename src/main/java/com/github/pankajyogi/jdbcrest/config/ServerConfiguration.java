package com.github.pankajyogi.jdbcrest.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration {

  @Bean
  public DataSource dataSource() {
    return null;
  }

  @Bean
  public String[] tableTypes() {
    return new String[] {"TABLE", "VIEW"};
  }

}
