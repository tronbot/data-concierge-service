package io.tronbot.dc.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

@Configuration
// @EnableJpaRepositories("io.tronbot.dc.repository")
// @EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

	/**
	 * Open the TCP port for the H2 database, so it is available remotely.
	 *
	 * @return the H2 database TCP server
	 * @throws SQLException
	 *             if the server failed to start
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
	public Server h2TCPServer() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers");
	}

	@Bean
	public Hibernate5Module hibernate5Module() {
		return new Hibernate5Module();
	}
}
