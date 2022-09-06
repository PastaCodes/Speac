package org.speac.ui.internal;

import org.speac.core.SpeacParser;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.internal.Token;
import org.speac.utilities.FixedList;
import org.speac.utilities.Reference;
import org.speac.utilities.StringUtils;

import java.util.LinkedList;

public class HtmlHighlighter {
	public static final class StylePlaceholders {
		public static final String ERRORS_STYLE = "&errors-style;";
		public static final String ERROR_HIGHLIGHTS_STYLE = "&error-highlights-style;";
		public static final String CONSOLE_STYLE = "&console-style;";
		public static final String HUD_STYLE = "&hud-style;";

		public static String generateForTokenType(Token.Type tokenType) {
			return "&" + StringUtils.snakeCaseToKebabCase(tokenType.toString()) + "-token-style;";
		}
	}

	public static String applyStylesheet(String highlighted, Stylesheet selectedStylesheet) {
		highlighted = highlighted	.replaceAll(StylePlaceholders.ERRORS_STYLE,				selectedStylesheet.errorsStyle()				.asCss())
									.replaceAll(StylePlaceholders.ERROR_HIGHLIGHTS_STYLE,	selectedStylesheet.errorHighlightsStyle()	.asCss())
									.replaceAll(StylePlaceholders.CONSOLE_STYLE,			selectedStylesheet.consoleStyle()			.asCss())
									.replaceAll(StylePlaceholders.HUD_STYLE,				selectedStylesheet.hudStyle()				.asCss());

		for (Token.Type tokenType : Token.Type.values())
			highlighted = highlighted.replaceAll(
					StylePlaceholders.generateForTokenType(tokenType),
					selectedStylesheet.tokenStyle(tokenType).asCss());

		return highlighted;
	}

	public static String highlightLine(String line) {
		Reference<FixedList<Token>> tokens = new Reference<>();
		if (SpeacParser.parseSyntax(line, tokens) != null)
			return ""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">"
					+	HtmlHighlighter.escapeHtmlEntities(line)
					+	"</span>";

		if (tokens.get() == null)
			return "";

		StringBuilder html = new StringBuilder();
		int tokenIndex = 0;
		Token currentToken = tokens.get().get(0);

		while (tokenIndex < tokens.get().size()) {
			html    .append("<span style = \"")
					.append(StylePlaceholders.generateForTokenType(currentToken.type))
					.append("\">");

			int beginIndex = currentToken.beginIndex;
			int endIndex;

			tokenIndex++;
			if (tokenIndex < tokens.get().size()) {
				currentToken = tokens.get().get(tokenIndex);
				endIndex = currentToken.beginIndex - 1;
			} else {
				endIndex = line.length() - 1;
			}

			html    .append(HtmlHighlighter.escapeHtmlEntities(line.substring(beginIndex, endIndex + 1)))
					.append("</span>");
		}

		return html.toString();
	}

	public static FixedList<String> highlightScript(FixedList<String> lines) {
		LinkedList<String> highlightedLines = new LinkedList<>();
		for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++)
			highlightedLines.add(""
					+	"<span style = \""
					+	StylePlaceholders.HUD_STYLE
					+	"\">"
					+	String.format("%5d", lineIndex + 1)
					+	"&#9;</span>"
					+	HtmlHighlighter.highlightLine(lines.get(lineIndex)));
		return new FixedList<>(highlightedLines);
	}

	public static String highlightConsoleOut(String line) {
		return ""
				+	"<span style = \""
				+	StylePlaceholders.HUD_STYLE
				+	"\">    &gt;&#9;</span><span style = \""
				+	StylePlaceholders.CONSOLE_STYLE
				+	"\">"
				+	HtmlHighlighter.escapeHtmlEntities(line)
				+	"</span>";
	}

	public static String highlightConsoleIn(String input) {
		return ""
				+	"<span style = \""
				+	StylePlaceholders.HUD_STYLE
				+	"\">    &lt;&#9;"
				+	HtmlHighlighter.escapeHtmlEntities(input)
				+	"</span>";
	}

	public static String highlightInstructionIn(String input, int lineNumber) {
		return ""
				+	"<span style = \""
				+	StylePlaceholders.HUD_STYLE
				+	"\">"
				+	String.format("%5d", lineNumber)
				+	"&#9;</span>"
				+	HtmlHighlighter.highlightLine(input);
	}

	public static FixedList<String> highlightError(
			String name,        String description,
			String solution,    int lineNumber,
			String source,      String incriminatedToken) {
		LinkedList<String> highlightedLines = new LinkedList<>();

		String errorTitle = ""
				+	"<span style = \""
				+	StylePlaceholders.ERRORS_STYLE
				+	"\">    !&#9;Error </span><span style = \""
				+	StylePlaceholders.ERROR_HIGHLIGHTS_STYLE
				+	"\">["
				+	name
				+	"]</span>";

		if (lineNumber != SpeacError.NO_LINE_NUMBER)
			errorTitle += ""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\"> in </span><span style = \""
					+	StylePlaceholders.ERROR_HIGHLIGHTS_STYLE
					+	"\">[line "
					+	lineNumber
					+	"]</span>";

		if (source != SpeacError.TERMINAL_SOURCE)
			errorTitle += ""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\"> from </span><span style = \""
					+	StylePlaceholders.ERROR_HIGHLIGHTS_STYLE
					+	"\">["
					+	source
					+	"]</span>";

		errorTitle += ""
				+	"<span style = \""
				+	StylePlaceholders.ERRORS_STYLE
				+	"\">:</span>";

		highlightedLines.add(errorTitle);

		if (incriminatedToken != SpeacError.NO_TOKEN)
			highlightedLines.add(""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">&#9;Incriminated token: </span><span style = \""
					+	StylePlaceholders.ERROR_HIGHLIGHTS_STYLE
					+	"\">["
					+	HtmlHighlighter.escapeHtmlEntities(incriminatedToken)
					+	"]</span><span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">.</span>");

		if (description != SpeacError.NO_DESCRIPTION)
			highlightedLines.add(""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">&#9;Description: "
					+	description
					+	"</span>");
		else
			highlightedLines.add(""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">&#9;No further description.</span>");

		if (solution != SpeacError.NO_SOLUTION)
			highlightedLines.add(""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">&#9;Suggested Solution: "
					+	solution
					+	"</span>");
		else
			highlightedLines.add(""
					+	"<span style = \""
					+	StylePlaceholders.ERRORS_STYLE
					+	"\">&#9;No suggested solution available.</span>");

		return new FixedList<>(highlightedLines);
	}

	public static String joinLines(FixedList<String> highlightedLines) {
		StringBuilder joined = new StringBuilder();
		for (int lineIndex = 0; lineIndex < highlightedLines.size(); lineIndex++) {
			joined.append(highlightedLines.get(lineIndex));
			if (lineIndex < highlightedLines.size() - 1)
				joined.append("<br>");
		}
		return joined.toString();
	}

	private static String escapeHtmlEntities(String input) {
		return input	.replaceAll("\t", "&#9;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;");
	}
}
