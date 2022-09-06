package org.speac.ui.internal;

import org.json.simple.JSONObject;
import org.speac.Start;
import org.speac.utilities.JsonUtils;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

/**
 * Loads and stores and manages settings
 */
public class SettingsHandler {
	public static record Settings(Stylesheet selectedStylesheet) {
		private static final Comparator<Settings> COMPARATOR = (first, second) -> first.selectedStylesheet == second.selectedStylesheet ? 0 : 1;
	}

	private static final Path SETTINGS_PATH = Start.RESOURCES_DIRECTORY.resolve("settings.json");

	private Settings currentSettings;

	protected SettingsHandler(Settings settings) {
		this.currentSettings = settings;
	}

	public static SettingsHandler initDefault() {
		return new SettingsHandler(new Settings(StylesheetLoader.DEFAULT_STYLESHEET));
	}

	public static SettingsHandler initFromFile(StylesheetLoader loader) throws JsonUtils.JsonException {
		JSONObject root = JsonUtils.getRootObject(SettingsHandler.SETTINGS_PATH);

		String selectedStylesheetName = JsonUtils.getString(root, "selected-stylesheet");
		Stylesheet selectedStylesheet = loader.getStylesheetByName(selectedStylesheetName);

		if (selectedStylesheet == null)
			return SettingsHandler.initDefault();

		return new SettingsHandler(new Settings(selectedStylesheet));
	}

	public Settings currentSettings() {
		return this.currentSettings;
	}

	public void applySettings(Settings newSettings) {
		this.currentSettings = newSettings;
	}

	public boolean compareSettings(Settings newSettings) {
		return Objects.compare(this.currentSettings, newSettings, Settings.COMPARATOR) == 0;
	}

	@SuppressWarnings("unchecked") // Outdated library code is actually safe
	public void dumpToFile() throws JsonUtils.JsonException {
		JSONObject root = new JSONObject();
		root.put("selected-stylesheet", this.currentSettings.selectedStylesheet.toString());

		JsonUtils.dumpObject(root, SettingsHandler.SETTINGS_PATH);
	}
}
