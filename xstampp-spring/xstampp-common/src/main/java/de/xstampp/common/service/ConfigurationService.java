package de.xstampp.common.service;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationService {

	@Autowired
	private Environment environment;

	private String path;

	private Logger logger;
	private ObjectMapper mapper;
	private HashMap<String, String> configs = new HashMap<>();

	private static final String GLOBAL_CONF_FILENAME = "config.json";
	private static final String DEV_CONF_FILENAME = "config.dev.json";
	private static final String LOCAL_CONF_FILENAME = "config.dev.local.json";

	public ConfigurationService() {
		logger = LoggerFactory.getLogger(this.getClass());
		mapper = new ObjectMapper();
	}

	@PostConstruct
	private void init() {
		path = environment.getProperty("config.path");
		logger.info("load configuration from: {}", path);

		// load global configs
		if (checkIfPresent(GLOBAL_CONF_FILENAME)) {
			logger.info("global configs found");
			loadConfigsInMap(loadFileContent(path + GLOBAL_CONF_FILENAME));
		} else {
			logger.warn("no global configs present");
		}

		// load development configs
		if (checkIfPresent(DEV_CONF_FILENAME)) {
			logger.info("development configs found");
			loadConfigsInMap(loadFileContent(path + DEV_CONF_FILENAME));
		} else {
			logger.debug("no development configs present");
		}

		// load local configs
		if (checkIfPresent(LOCAL_CONF_FILENAME)) {
			logger.info("local configs found");
			loadConfigsInMap(loadFileContent(path + LOCAL_CONF_FILENAME));
		} else {
			logger.debug("no local configs present");
		}
	}

	private boolean checkIfPresent(String filename) {
		return Files.exists(Paths.get(path + filename));
	}

//	private String loadFileContent(String path) {
//		return super.loadFileContent(path);
//	}
	
	private String loadFileContent(String path) {
		String result = "";

		try (FileInputStream fileIn = new FileInputStream(path);
		     DataInputStream reader = new DataInputStream(fileIn)) {
			result = new String(reader.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ioe) {
			logger.error("unable to read file: {}", path, ioe);
		}

		return result;
	}

	private void loadConfigsInMap(String json) {
		try (JsonParser parser = new JsonFactory().createParser(json)) {
			parser.setCodec(mapper);
			JsonNode node = parser.readValueAsTree();

			recursiveConfigTraversal(node, null);

		} catch (JsonParseException e) {
			logger.error("failed to parse json", e);
		} catch (IOException e) {
			logger.error("failed to read json", e);
		}
	}

	private void recursiveConfigTraversal(JsonNode node, String path) {
		// check if node is null (eg. invalid config json)
		if (node == null) {
			logger.error("Invalid JSON structure");
			return;
		}

		if (node.size() == 0) {
			configs.put(path, node.textValue());
		} else {
			// skip if this node is array
			if (node.isArray()) {
				return;
			}

			Iterator<Entry<String, JsonNode>> fields = node.fields();

			// iterate over child elements
			while (fields.hasNext()) {
				Entry<String, JsonNode> entry = fields.next();
				recursiveConfigTraversal(entry.getValue(),
						path != null ? String.join(".", path, entry.getKey()) : entry.getKey());
			}
		}
	}

	/**
	 * returns a config value by its key
	 * 
	 * @param key the traversed key from the json tree
	 * @return returns the property. Null if no such property exist
	 */
	public String getStringProperty(String key) {
		Object obj = this.configs.get(key);

		if (obj != null) {
			return (String) obj;
		} else {
			return null;
		}
	}

	/**
	 * returns a config value by its key. If the key does not exists it returns the
	 * defined default value
	 * 
	 * @param key          the traversed key from, the json tree
	 * @param defaultValue the default value which will be return if the key does
	 *                     not exist
	 * @return returns the property
	 */
	public String getStringProperty(String key, String defaultValue) {
		String result = getStringProperty(key);

		return result != null ? result : defaultValue;
	}
}
