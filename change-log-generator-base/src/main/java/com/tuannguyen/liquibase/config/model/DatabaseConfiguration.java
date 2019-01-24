package com.tuannguyen.liquibase.config.model;

import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class DatabaseConfiguration {
	@PromptConfig(config = "db.username", prompt = "Username")
	private String username;

	@PromptConfig(config = "db.password", prompt = "Password")
	private String password;

	@PromptConfig(config = "db.connection_url", prompt = "Connection Url")
	private String connectionUrl;

	@PromptConfig(config = "db.driver_class", prompt = "Driver")
	private String driverClass;
}
