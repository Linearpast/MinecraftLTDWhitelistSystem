package com.linearpast.minecraftmanager.utils.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

public class ConfigLoader implements EnvironmentPostProcessor {
	public static final Map<String, String> config = new HashMap<>();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		try {
			PropertySource<?> source = environment.getPropertySources().stream().filter(propertySource ->
					propertySource.getName().contains("application.yml")
			).findFirst().orElseThrow();
			if(source instanceof MapPropertySource mapPropertySource) {
				for (String key : mapPropertySource.getPropertyNames()) {
					config.put(key, mapPropertySource.getProperty(key).toString());
					System.out.println(key + "=" + mapPropertySource.getProperty(key));
				}
			}
		}catch (Exception e){
			System.err.println("Failed to load config: " + e.getMessage());
		}
	}
}
