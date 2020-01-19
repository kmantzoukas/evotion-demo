package uk.ac.city.evotion;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import uk.ac.city.evotion.utilities.EvotionContollerUtilities;

import java.util.Properties;

@SpringBootApplication
public class EvotionAnalytics {

	public static void main(String[] args) {
		SpringApplication.run(EvotionAnalytics.class, args);
	}

	@Bean
	public VelocityEngine velocityEngine() {
		Properties properties = new Properties();
		properties.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		properties.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
		properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
		properties.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
		VelocityEngine engine = new VelocityEngine(properties);

		return engine;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public EvotionContollerUtilities getEvotionControllerUtilities(){
		return new EvotionContollerUtilities();
	}

}
