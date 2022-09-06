package org.speac.utilities;

import org.speac.Start;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LogUtils {
	private static final Path LOG_FILE_PATH = Start.RESOURCES_DIRECTORY.resolve(".log");
	public static final Logger LOGGER;
	static {
		LOGGER = Logger.getLogger("Speac Logger");

		FileHandler handler = null;
		try {
			handler = new FileHandler(LogUtils.LOG_FILE_PATH.toString());
		} catch (IOException | SecurityException ignored) {}
		assert handler != null;
		LOGGER.addHandler(handler);

		SimpleFormatter formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
	}
}
