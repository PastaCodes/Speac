package org.speac.ui.internal;

import org.json.simple.JSONObject;
import org.speac.Start;
import org.speac.core.types.internal.Token;
import org.speac.utilities.FixedList;
import org.speac.utilities.JsonUtils;
import org.speac.utilities.LogUtils;
import org.speac.utilities.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StylesheetLoader {
	public static final Stylesheet DEFAULT_STYLESHEET;
	static {
		Stylesheet defaultStylesheet;
		try {
			defaultStylesheet = new Stylesheet() {
				private final Color backgroundColor	= Color.decode("#2A2A2A");
				private final Color bordersColor	= Color.decode("#404040");
				private final Color foregroundColor	= Color.decode("#FFFFFF");
				private final Color notesColor		= Color.decode("#808080");

				@Override public Color backgroundColor()	{ return this.backgroundColor;	}
				@Override public Color bordersColor()		{ return this.bordersColor;		}
				@Override public Color foregroundColor()	{ return this.foregroundColor;	}
				@Override public Color notesColor()			{ return this.notesColor;		}

				private final Style errorsStyle				= new Style("#FF3000");
				private final Style errorHighlightsStyle	= new Style("#FF7F00");
				private final Style consoleStyle			= new Style("#FFFFFF");
				private final Style hudStyle				= new Style("#7F7F7F");

				@Override public Style errorsStyle()			{ return this.errorsStyle;			}
				@Override public Style errorHighlightsStyle()	{ return this.errorHighlightsStyle;	}
				@Override public Style consoleStyle()			{ return this.consoleStyle;			}
				@Override public Style hudStyle()				{ return this.hudStyle;				}

				private final Style functionNameTokenStyle		= new Style("#00FF00");
				private final Style immediateValueTokenStyle	= new Style("#FF7F00");
				private final Style stringArgumentTokenStyle	= new Style("#FFFF9F");
				private final Style stringTokenStyle			= new Style("#FFFF00");
				private final Style symbolTokenStyle			= new Style("#FFFFFF");
				private final Style variableNameTokenStyle		= new Style("#00BFFF");
				private final Style errorTokenStyle				= new Style("#FF3000");
				private final Style defaultTokenStyle			= new Style("#7F7F7F");

				@Override public Style tokenStyle(Token.Type type) {
					return switch (type) {
						case FUNCTION_NAME		-> this.functionNameTokenStyle;
						case IMMEDIATE_VALUE	-> this.immediateValueTokenStyle;
						case STRING_ARGUMENT	-> this.stringArgumentTokenStyle;
						case STRING				-> this.stringTokenStyle;
						case SYMBOL				-> this.symbolTokenStyle;
						case VARIABLE_NAME		-> this.variableNameTokenStyle;
						case ERROR				-> this.errorTokenStyle;
						default					-> this.defaultTokenStyle; // Should never happen
					};
				}

				@Override public String toString() {
					return "default";
				}
			};
		} catch (Exception ignored) {
			defaultStylesheet = null; // Won't happen
		}
		DEFAULT_STYLESHEET = defaultStylesheet;
	}

	private FixedList<Stylesheet> availableStylesheets; // Notice that although it is fixed it isn't final

	public StylesheetLoader() {
		this.rereadStylesheets();
	}

	public void rereadStylesheets() {
		LinkedList<Stylesheet> foundStylesheets = new LinkedList<>();
		foundStylesheets.add(StylesheetLoader.DEFAULT_STYLESHEET);

		try (Stream<Path> paths = Files.walk(Start.RESOURCES_DIRECTORY.resolve("stylesheets"))) {
			paths
					.filter(path -> path.toString().endsWith(".json"))
					.forEach(path -> {
						try {
							foundStylesheets.add(StylesheetLoader.load(path));
						} catch (JsonUtils.JsonException | Style.InvalidHexColorException exception) {
							StylesheetLoader.logDiscardedStylesheet(path, exception.getMessage());
						}
					});
		} catch (IOException ignored) {}

		this.availableStylesheets = new FixedList<>(foundStylesheets);
	}

	public FixedList<Stylesheet> getAvailableStylesheets() {
		return this.availableStylesheets;
	}

	public Stylesheet getStylesheetByName(String name) {
		for (Stylesheet stylesheet : this.availableStylesheets)
			if (stylesheet.toString().equals(name))
				return stylesheet;
		return null; // Not found
	}

	private static Stylesheet load(Path path) throws JsonUtils.JsonException, Style.InvalidHexColorException {
		JSONObject root = JsonUtils.getRootObject(path);

		// Read token-style object
		JSONObject tokenStyles = JsonUtils.getObject(root, "token-style");

		// All token styles will be stored in this dictionary
		HashMap<Token.Type, Style> tokenStylesTable = new HashMap<>();

		// Read the default token color string
		Style defaultTokenStyle = StylesheetLoader.loadStyle(JsonUtils.getObject(tokenStyles, "default"));

		// Default token style is denoted by a null key in the dictionary
		tokenStylesTable.put(null, defaultTokenStyle);

		// Read all token styles and store them in the dictionary
		for (Token.Type type : Token.Type.values()) {
			JSONObject tokenStyle = JsonUtils.getObject(tokenStyles, StringUtils.snakeCaseToKebabCase(type.toString()), true);
			if (tokenStyle == null)
				continue;

			tokenStylesTable.put(type, StylesheetLoader.loadStyle(tokenStyle));
		}

		return new Stylesheet() {
			private final Color backgroundColor = Color.decode(JsonUtils.getString(root, "background-color"	));
			private final Color bordersColor    = Color.decode(JsonUtils.getString(root, "borders-color"	));
			private final Color foregroundColor = Color.decode(JsonUtils.getString(root, "foreground-color"	));
			private final Color notesColor      = Color.decode(JsonUtils.getString(root, "notes-color"		));

			@Override public Color backgroundColor() { return this.backgroundColor;  }
			@Override public Color bordersColor()    { return this.bordersColor;     }
			@Override public Color foregroundColor() { return this.foregroundColor;  }
			@Override public Color notesColor()      { return this.notesColor;       }

			private final Style errorsStyle            = StylesheetLoader.loadStyle(JsonUtils.getObject(root, "errors-style"			));
			private final Style errorHighlightsStyle   = StylesheetLoader.loadStyle(JsonUtils.getObject(root, "error-highlights-style"	));
			private final Style consoleStyle           = StylesheetLoader.loadStyle(JsonUtils.getObject(root, "console-style"			));
			private final Style hudStyle               = StylesheetLoader.loadStyle(JsonUtils.getObject(root, "hud-style"				));

			@Override public Style errorsStyle()			{ return this.errorsStyle;			}
			@Override public Style errorHighlightsStyle()	{ return this.errorHighlightsStyle;	}
			@Override public Style consoleStyle()			{ return this.consoleStyle;			}
			@Override public Style hudStyle()				{ return this.hudStyle;				}

			@Override public Style tokenStyle(Token.Type type) {
				Style style = tokenStylesTable.get(type);
				if (style != null)
					return style;
				return tokenStylesTable.get(null); // Must work
			}

			@Override public String toString() {
				// Removes path and extension from the file name
				return path.getFileName().toString().replaceFirst("[.][^.]+$", "");
			}
		};
	}

	private static Style loadStyle(JSONObject object) throws JsonUtils.JsonException, Style.InvalidHexColorException {
		String color = JsonUtils.getString(object, "color");
		Boolean bold = JsonUtils.getBoolean(object, "bold", true);
		Boolean italic = JsonUtils.getBoolean(object, "italic", true);
		Boolean underlined = JsonUtils.getBoolean(object, "underlined", true);
		return new Style(
				color,
				bold != null && bold,
				italic != null && italic,
				underlined != null && underlined
		);
	}

	private static void logDiscardedStylesheet(Path path, String reason) {
		LogUtils.LOGGER.warning("Discarded the stylesheet at path '" + path + "', reason: " + reason + ".");
	}
}
