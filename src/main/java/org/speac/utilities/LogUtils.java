package org.speac.utilities;

import org.speac.Start;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LogUtils {
	private static final String LOG_FILE_PATH = Start.RESOURCES_DIRECTORY + ".log";
	public static final Logger LOGGER;
	static {
		LOGGER = Logger.getLogger("Speac Logger");

		FileHandler handler = null;
		try {
			handler = new FileHandler(LogUtils.LOG_FILE_PATH);
		} catch (IOException | SecurityException ignored) {}
		assert handler != null;
		LOGGER.addHandler(handler);

		SimpleFormatter formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
	}
}
