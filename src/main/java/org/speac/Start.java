package org.speac;

import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.StylesheetLoader;
import org.speac.ui.views.SpeacHub;
import org.speac.utilities.JsonUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class Start {
	public static final Path RESOURCES_DIRECTORY;
	static {
		// Check if we are in a development environment
		Path resourcesDirectory = Path.of("src\\main\\resources");
		if (Files.notExists(resourcesDirectory))
			resourcesDirectory = Path.of(System.getenv("ProgramFiles") + "\\Speac\\resources");

		RESOURCES_DIRECTORY = resourcesDirectory;
	}

	public static void main(String[] args) {
		StylesheetLoader loader = new StylesheetLoader();
		SettingsHandler settings;
		try {
			settings = SettingsHandler.initFromFile(loader);
		} catch (JsonUtils.JsonException exception) {
			settings = SettingsHandler.initDefault();
		}
		new SpeacHub(loader, settings);
	}
}
