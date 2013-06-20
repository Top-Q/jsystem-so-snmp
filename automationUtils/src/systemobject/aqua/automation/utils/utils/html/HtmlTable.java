package systemobject.aqua.automation.utils.utils.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class HtmlTable {

	private static final String css = "<style type=\"text/css\">"
			+ "table.stats" + "{"
			+ "font-family:Verdana,Geneva,Arial,Helvetica,sans-serif;"
			+ "font-weight:normal;" + "font-size:3px;" + "color:#fff;"
			+ "width:95%;" + "background-color:#666;" + "border:0px;"
			+ "border-collapse:collapse;" + "border-spacing:0px;" + "}"

			+ "table.stats td" + "{" + "background-color:#E8E8E8;"
			+ "color:#000;" + "padding:2px;" + "border:1px #fff solid;" + "}"

			+ "table.sofT td.hed" + "{" + "background-color:#BEC8D1;"
			+ "color:#404040;" + "padding:3px;"
			+ "border-bottom:2px #fff solid;" + "font-size:14px;"
			+ "font-weight:bold;" + "}"

			+ "table.sofT td.custom" + "{" + "background-color:#98AFC7;"
			+ "padding:3px;" + "border-bottom:2px #fff solid;"
			+ "font-size:14px;" + "font-weight:bold;" + "}"

			+ "tr.d1 td" + "{" + "color:white;" + "border:1px #fff solid;"
			+ "background-image:url('images/red-gradient3.jpg');"
			+ "opacity:0.8;" + "filter:alpha(opacity=80);" + "}"

			+ "tr.d2 td" + "{" + "opacity:1.0;" + "filter:alpha(opacity=100);"
			+ "background-image:none;" + "background-color:#fafafa;"
			+ "color:#404040;" + "border:1px #fff solid;" + "}"

			+ "table.sofT" + "{" + "text-align:left;" + "font-family:Verdana;"
			+ "font-weight:normal;" + "font-size:13px;" + "color:#404040;"
			+ "background-color:#fafafa;" + "border:1px #6699CC solid;"
			+ "border-collapse:collapse;" + "border-spacing:0px;"
			+ "width:90%;" + "}"

			+ "td.helpHed" + "{" + "border-bottom:1px solid #6699CC;"
			+ "border-left:1px solid #6699CC;" + "background-color:#BEC8D1;"
			+ "text-align:center;" + "text-indent:5px;"
			+ "font-family:Verdana;" + "font-size:14px;" + "color:#404040;"
			+ "font-weight:bold;" + "}"

			+ "</style>";

	public static final String HTML_SPACE = "&nbsp;";

	public static final String HTML_NEWLINE = "<br>";

	public static final String HTML_TAB = HTML_SPACE + HTML_SPACE + HTML_SPACE
			+ HTML_SPACE;

	public static EnumHtmlColor DEFAULT_HEADER_FONT_COLOR = EnumHtmlColor.DARK_BLUE;

	public static EnumHtmlColor DEFAULT_CELLS_FONT_COLOR = EnumHtmlColor.BLACK;

	public static EnumHtmlAlignment DEFAULT_HEADER_ALIGNMENT = EnumHtmlAlignment.CENTER;

	public static EnumHtmlAlignment DEFAULT_CELLS_ALIGNMENT = EnumHtmlAlignment.LEFT;

	public static EnumHtmlColor DEFAULT_HEADER_BG_COLOR = EnumHtmlColor.LIGHT_GREY;

	public static EnumHtmlColor DEFAULT_CELLS_BG_COLOR = EnumHtmlColor.WHITE;

	public static int DEFAULT_HEADER_FONT_SIZE = 3;

	public static int DEFAULT_CELLS_FONT_SIZE = 3;

	private ArrayList<String> header = new ArrayList<String>();

	private ArrayList<EnumHtmlColor> headerFontColor = new ArrayList<EnumHtmlColor>();

	private ArrayList<Integer> headerFontSize = new ArrayList<Integer>();

	private EnumHtmlColor headerBgColor = DEFAULT_HEADER_BG_COLOR;

	private EnumHtmlAlignment headerAlignment = DEFAULT_HEADER_ALIGNMENT;

	private ArrayList<String> currentLine = new ArrayList<String>();

	private ArrayList<EnumHtmlColor> currentLineFontColor = new ArrayList<EnumHtmlColor>();

	private ArrayList<Integer> currentLineFontSize = new ArrayList<Integer>();

	private ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();

	private ArrayList<ArrayList<EnumHtmlColor>> linesFontColor = new ArrayList<ArrayList<EnumHtmlColor>>();

	private ArrayList<EnumHtmlColor> linesBgColor = new ArrayList<EnumHtmlColor>();

	private ArrayList<EnumHtmlAlignment> linesAlignment = new ArrayList<EnumHtmlAlignment>();

	private ArrayList<ArrayList<Integer>> linesFontSize = new ArrayList<ArrayList<Integer>>();

	public void setHeader(String... header) {
		setHeader(DEFAULT_HEADER_FONT_COLOR, header);
	}

	public void setHeader(EnumHtmlColor color, String... header) {
		setHeader(color, DEFAULT_HEADER_FONT_SIZE, header);
	}

	public void setHeader(EnumHtmlAlignment alignment, String... header) {
		setHeader(DEFAULT_HEADER_FONT_COLOR, alignment, header);
	}

	public void setHeader(EnumHtmlColor color, EnumHtmlAlignment alignment,
			String... header) {
		setHeader(new EnumHtmlColor[] { color },
				new Integer[] { DEFAULT_HEADER_FONT_SIZE },
				DEFAULT_HEADER_ALIGNMENT, header);
	}

	public void setHeader(Integer fontSize, String... header) {
		setHeader(new EnumHtmlColor[] { DEFAULT_HEADER_FONT_COLOR },
				new Integer[] { fontSize }, DEFAULT_HEADER_ALIGNMENT, header);
	}

	public void setHeader(EnumHtmlColor color, Integer fontSize,
			String... header) {
		setHeader(new EnumHtmlColor[] { color }, new Integer[] { fontSize },
				DEFAULT_HEADER_ALIGNMENT, header);
	}

	public void setHeader(EnumHtmlColor color, EnumHtmlAlignment alignment,
			Integer fontSize, String... header) {
		setHeader(new EnumHtmlColor[] { color }, new Integer[] { fontSize },
				alignment, header);
	}

	public void setHeader(EnumHtmlColor[] color, Integer[] fontSize,
			EnumHtmlAlignment alignment, String... header) {
		this.header = new ArrayList<String>();
		this.headerFontColor = new ArrayList<EnumHtmlColor>();
		this.headerAlignment = alignment;
		int colIndexColor = 0;
		int colIndexSize = 0;
		for (int i = 0; header != null && i < header.length; i++) {
			this.header.add(header[i]);
			this.headerFontColor.add(color[colIndexColor]);
			this.headerFontSize.add(fontSize[colIndexSize]);
			if (colIndexColor < (color.length - 1)) {
				colIndexColor++;
			}
			if (colIndexSize < (color.length - 1)) {
				colIndexSize++;
			}
		}
	}

	public void setCellToHeader(int cellNumber, String cell) {
		setCellToHeader(cellNumber, cell, DEFAULT_HEADER_FONT_COLOR);
	}

	public void setCellToHeader(int cellNumber, String cell, EnumHtmlColor color) {
		setCellToHeader(cellNumber, cell, color, DEFAULT_HEADER_FONT_SIZE);
	}

	public void setCellToHeader(int cellNumber, String cell,
			EnumHtmlColor color, Integer fontSize) {
		for (int i = header.size(); i < cellNumber; i++) {
			header.add("");
			headerFontColor.add(DEFAULT_HEADER_FONT_COLOR);
			headerFontSize.add(DEFAULT_HEADER_FONT_SIZE);
		}
		header.set(cellNumber - 1, cell);
		headerFontColor.set(cellNumber - 1, color);
	}

	public void addMultipleLines(String[][] lines) {
		addMultipleLines(lines,
				new EnumHtmlColor[] { DEFAULT_CELLS_FONT_COLOR });
	}

	public void addMultipleLines(String[][] lines, EnumHtmlColor[] colors) {
		addMultipleLines(lines, colors,
				new Integer[] { DEFAULT_CELLS_FONT_SIZE });
	}

	public void addMultipleLines(String[][] lines, EnumHtmlColor[] colors,
			Integer[] fontSizes) {
		int lineIndexColor = 0;
		int lineIndexSize = 0;
		for (int i = 0; lines != null && i < lines.length; i++) {
			addLine(colors[lineIndexColor], fontSizes[lineIndexSize], lines[i]);
			if (lineIndexColor < (colors.length - 1)) {
				lineIndexColor++;
			}
			if (lineIndexSize < (colors.length - 1)) {
				lineIndexSize++;
			}
		}
	}

	public void addLine(String... line) {
		addLine(DEFAULT_CELLS_FONT_COLOR, DEFAULT_CELLS_FONT_SIZE, line);
	}

	public void addLine(EnumHtmlColor color, String... line) {
		addLine(color, DEFAULT_CELLS_FONT_SIZE, line);
	}

	public void addLineWithBgColor(EnumHtmlColor color, String... line) {
		addLine(DEFAULT_CELLS_FONT_COLOR, DEFAULT_CELLS_FONT_SIZE, color, line);
	}

	public void addLine(Integer fontSize, String... line) {
		addLine(DEFAULT_CELLS_FONT_COLOR, fontSize, line);
	}

	public void addLine(EnumHtmlColor color, Integer fontSize, String... line) {
		addLine(color, fontSize, DEFAULT_CELLS_BG_COLOR, line);
	}

	public void addLine(EnumHtmlColor color, EnumHtmlColor bgColor,
			String... line) {
		addLine(color, DEFAULT_CELLS_FONT_SIZE, bgColor, line);
	}

	public void addLine(EnumHtmlColor color, Integer fontSize,
			EnumHtmlColor bgColor, String... line) {
		addLine(color, fontSize, bgColor, DEFAULT_CELLS_ALIGNMENT, line);
	}

	public void addLine(EnumHtmlColor color, Integer fontSize,
			EnumHtmlColor bgColor, EnumHtmlAlignment alignment, String... line) {

		currentLine = new ArrayList<String>();
		currentLineFontColor = new ArrayList<EnumHtmlColor>();
		currentLineFontSize = new ArrayList<Integer>();

		for (int i = 0; line != null && i < line.length; i++) {
			currentLine.add(line[i]);
			currentLineFontColor.add(color);
			currentLineFontSize.add(fontSize);
		}

		lines.add(currentLine);
		linesFontColor.add(currentLineFontColor);
		linesFontSize.add(currentLineFontSize);
		linesBgColor.add(bgColor);
		linesAlignment.add(alignment);
	}

	public void setCell(int lineNumber, int cellNumber, String cell) {
		setCell(lineNumber, cellNumber, cell, DEFAULT_CELLS_FONT_COLOR);
	}

	public void setCell(int lineNumber, int cellNumber, String cell,
			EnumHtmlColor color) {
		setCell(lineNumber, cellNumber, cell, color, DEFAULT_CELLS_FONT_SIZE);
	}

	public void setCell(int lineNumber, int cellNumber, String cell,
			Integer fontSize) {
		setCell(lineNumber, cellNumber, cell, DEFAULT_CELLS_FONT_COLOR,
				fontSize);
	}

	public void setCell(int lineNumber, int cellNumber, String cell,
			EnumHtmlColor color, Integer fontSize) {
		for (int i = lines.size(); i < lineNumber; i++) {
			currentLine = new ArrayList<String>();
			currentLineFontColor = new ArrayList<EnumHtmlColor>();
			currentLineFontSize = new ArrayList<Integer>();
			lines.add(currentLine);
			linesFontColor.add(currentLineFontColor);
			linesFontSize.add(currentLineFontSize);
		}
		ArrayList<String> ArrayList = lines.get(lineNumber - 1);
		ArrayList<EnumHtmlColor> ArrayListColor = linesFontColor
				.get(lineNumber - 1);
		ArrayList<Integer> ArrayListSize = linesFontSize.get(lineNumber - 1);
		for (int i = ArrayList.size(); i < cellNumber; i++) {
			ArrayList.add("");
			ArrayListColor.add(DEFAULT_CELLS_FONT_COLOR);
			ArrayListSize.add(DEFAULT_CELLS_FONT_SIZE);
		}
		ArrayList.set(cellNumber - 1, cell);
		ArrayListColor.set(cellNumber - 1, color);
		ArrayListSize.set(cellNumber - 1, fontSize);
	}

	public String[] getHeader() {
		String[] hed = new String[header.size()];
		return header.toArray(hed);
	}

	public EnumHtmlColor[] getHeaderColor() {
		EnumHtmlColor[] hed = new EnumHtmlColor[headerFontColor.size()];
		return headerFontColor.toArray(hed);
	}

	public String[] getLine() {
		String[] line = new String[currentLine.size()];
		return currentLine.toArray(line);
	}

	public EnumHtmlColor[] getLineColor() {
		EnumHtmlColor[] line = new EnumHtmlColor[currentLineFontColor.size()];
		return currentLineFontColor.toArray(line);
	}

	public Integer[] getLineSize() {
		Integer[] line = new Integer[currentLineFontSize.size()];
		return currentLineFontSize.toArray(line);
	}

	public String[] getLine(int lineNumber) {
		ArrayList<String> ArrayList = lines.get(lineNumber - 1);
		String[] line = new String[ArrayList.size()];
		return ArrayList.toArray(line);
	}

	public int getNumberOfLines() {

		return lines.size();
	}

	public EnumHtmlColor[] getLineColor(int lineNumber) {
		ArrayList<EnumHtmlColor> ArrayList = linesFontColor.get(lineNumber - 1);
		EnumHtmlColor[] color = new EnumHtmlColor[ArrayList.size()];
		return ArrayList.toArray(color);
	}

	public Integer[] getLineSize(int lineNumber) {
		ArrayList<Integer> ArrayList = linesFontSize.get(lineNumber - 1);
		Integer[] color = new Integer[ArrayList.size()];
		return ArrayList.toArray(color);
	}

	public EnumHtmlAlignment getHeaderAlignment() {
		return headerAlignment;
	}

	public void setHeaderAlignment(EnumHtmlAlignment headerAlignment) {
		this.headerAlignment = headerAlignment;
	}

	public EnumHtmlAlignment[] getLinesAlignment() {
		EnumHtmlAlignment[] alignment = new EnumHtmlAlignment[linesAlignment
				.size()];
		return linesAlignment.toArray(alignment);
	}

	public EnumHtmlColor getHeaderBgColor() {
		return headerBgColor;
	}

	public void setHeaderBgColor(EnumHtmlColor headerBgColor) {
		this.headerBgColor = headerBgColor;
	}

	public String toString() {
		return toString(true, true);
	}

	public String getCss() {
		return css;
	}

	public String toString(boolean mainPage) {
		return toString(mainPage, true);
	}

	public String toString(boolean mainPage, boolean useCss) {

		StringBuffer sb = new StringBuffer();

		if (mainPage) {
			sb.append("!DOCTYPE HTML");
		} else {
			sb.append("<!DOCTYPE HTML>");
		}

		sb.append(((useCss) ? getCss() : "")
				+ "<table border='1' class=\"sofT\">");

		if (header != null && header.size() > 0) {
			sb.append("<tr>");
			for (int i = 0; i < header.size(); i++) {
				sb.append("<td class=\"helpHed\">");
				String headerStr = (String) header.get(i);
				headerStr = headerStr.replace("\n", HTML_NEWLINE);
				headerStr = headerStr.replace("\t", HTML_TAB);
				headerStr = headerStr.replace(" ", HTML_SPACE);
				headerStr = headerStr.replace("a" + HTML_SPACE + "href=",
						"a href=");
				headerStr = headerStr.replace("a" + HTML_SPACE + HTML_SPACE
						+ "href=", "a href=");
				sb.append(headerStr);
				sb.append("</td>");
			}

			sb.append("</tr>");
		}

		for (int i = 0; i < lines.size(); i++) {
			sb.append("<tr>");
			String[] line = new String[lines.get(i).size()];
			line = lines.get(i).toArray(line);
			if (line == null) {
				line = new String[] { "" };
			}
			String bgColor = linesBgColor.get(i).toString().toLowerCase();

			for (int j = 0; j < line.length; j++) {

				sb.append("<td bgcolor='");
				sb.append(bgColor);
				sb.append("'>");

				String lineStr = (String) line[j];

				if (lineStr == null) {
					lineStr = "";
				}

				lineStr = lineStr.replace("\n", HTML_NEWLINE);
				lineStr = lineStr.replace("\t", HTML_TAB);
				lineStr = lineStr.replace(" ", HTML_SPACE);
				lineStr = lineStr
						.replace("a" + HTML_SPACE + "href=", "a href=");
				lineStr = lineStr.replace("a" + HTML_SPACE + HTML_SPACE
						+ "href=", "a href=");
				sb.append("<font color='");
				sb.append(linesFontColor.get(i).get(j).color());
				sb.append("'>");
				sb.append(lineStr);
				sb.append("</font>");
				sb.append("</td>");
			}

			sb.append("</tr>");
		}

		sb.append("</table>");

		return sb.toString();
	}

	/**
	 * @author Itzhak.Hovav
	 */
	public enum EnumHtmlColor {

		ALICE_BLUE("AliceBlue"), ANTIQUE_WHITE("AntiqueWhite"), AQUA("Aqua"), AQUA_MARINE(
				"Aquamarine"), AZURE("Azure"), BEIGE("Beige"), BISQUE("Bisque"), BLACK(
				"Black"), BLANCHED_ALMOND("BlanchedAlmond"), BLUE("Blue"), BLUE_VIOLET(
				"BlueViolet"), BROWN("Brown"), BURLY_WOOD("BurlyWood"), CADET_BLUE(
				"CadetBlue"), CHARTREUSE("Chartreuse"), CHOCOLATE("Chocolate"), CORAL(
				"Coral"), CORNFLOWER_BLUE("CornflowerBlue"), CORNSILK(
				"Cornsilk"), CRIMSON("Crimson"), CYAN("Cyan"), DARK_BLUE(
				"DarkBlue"), DARK_CYAN("DarkCyan"), DARK_GOLDEN_ROD(
				"DarkGoldenRod"), DARK_GRAY("DarkGray"), DARK_GREEN("DarkGreen"), DARK_KHAKI(
				"DarkKhaki"), DARK_MAGENTA("DarkMagenta"), DARK_OLIVE_GREEN(
				"DarkOliveGreen"), DARK_ORANGE("Darkorange"), DARK_ORCHID(
				"DarkOrchid"), DARK_RED("DarkRed"), DARK_SALMON("DarkSalmon"), DARK_SEA_GREEN(
				"DarkSeaGreen"), DARK_SLATE_BLUE("DarkSlateBlue"), DARK_SLATE_GRAY(
				"DarkSlateGray"), DARK_TURQUISE("DarkTurquoise"), DARK_VIOLET(
				"DarkViolet"), DEEP_PINK("DeepPink"), DEEP_SKY_BLUE(
				"DeepSkyBlue"), DIM_GRAY("DimGray"), DODGER_BLUE("DodgerBlue"), FIRE_BRIKE(
				"FireBrick"), FLORAL_WHITE("FloralWhite"), FOREST_GREEN(
				"ForestGreen"), FUCHSIA("Fuchsia"), GAINSBORO("Gainsboro"), GHOST_WHITE(
				"GhostWhite"), GOLD("Gold"), GOLDEN_ROD("GoldenRod"), GRAY(
				"Gray"), GREEN("Green"), GREEN_YELLOW("GreenYellow"), HONEY_DEW(
				"HoneyDew"), HOT_PINK("HotPink"), INDIAN_RED("IndianRed"), INDIGO(
				"Indigo"), IVORY("Ivory"), KHAKI("Khaki"), LAVENDER("Lavender"), LAVENDER_BLUSH(
				"LavenderBlush"), LAWN_GREEN("LawnGreen"), LEMON_CHIFFON(
				"LemonChiffon"), LIGHT_BLUE("LightBlue"), LIGHT_CORAL(
				"LightCoral"), LIGHT_CYAN("LightCyan"), LIGHT_GOLDEN_ROD_YELLOW(
				"LightGoldenRodYellow"), LIGHT_GREY("LightGrey"), LIGHT_GREEN(
				"LightGreen"), LIGHT_PINK("LightPink"), LIGHT_SALMON(
				"LightSalmon"), LIGHT_SEA_GREEN("LightSeaGreen"), LIGHT_SKY_BLUE(
				"LightSkyBLUE"), LIGHT_SLATE_GRAY("LightSlateGray"), LIGHT_STEEL_BLUE(
				"LightSteelBlue"), LIGHT_YELLOW("LightYellow"), LIME("Lime"), LIME_GREEN(
				"LimeGreen"), LINEN("Linen"), MAGENTA("Magenta"), MAROON(
				"Maroon"), MEDIUM_AQUA_MARINE("MediumAquaMarine"), MEDIUM_BLUE(
				"MediumBlue"), MEDIUM_ORCHID("MediumOrchid"), MEDIUM_PURPLE(
				"MediumPurple"), MEDIUM_SEA_GREEN("MediumSeaGreen"), MEDIUM_SLATE_BLUE(
				"MediumSlateBlue"), MEDIUM_SPRING_GREEN("MediumSpringGreen"), MEDIUM_TURQUISE(
				"MediumTurquoise"), MEDIUM_VIOLET_RED("MediumVioletRed"), MIDNIGHT_BLUE(
				"MidnightBlue"), MINT_CREAM("MintCream"), MISTY_ROSE(
				"MistyRose"), MOCCASIN("Moccasin"), NAVAJO_WHITE("NavajoWhite"), NAVY(
				"Navy"), OLD_LACE("OldLace"), OLIVE("Olive"), OLIVE_DARB(
				"OliveDrab"), ORANGE("Orange"), ORANGE_RED("OrangeRed"), ORCHID(
				"Orchid"), PALE_GOLDEN_ROSE("PaleGoldenRod"), PALE_GREEN(
				"PaleGreen"), PALE_TURQUISE("PaleTurquoise"), PALE_VIOLET_RED(
				"PaleVioletRed"), PAPAYA_WHIP("PapayaWhip"), PEACH_PUFF(
				"PeachPuff"), PERU("Peru"), PINK("Pink"), PLUM("Plum"), POWDER_BLUE(
				"PowderBlue"), PURPLE("Purple"), RED("Red"), ROSY_BROWN(
				"RosyBrown"), ROYAL_BLUE("RoyalBlue"), SADDLE_BROWN(
				"SaddleBrown"), SALMON("Salmon"), SANDY_BROWN("SandyBrown"), SEA_GREEN(
				"SeaGreen"), SEA_SHELL("SeaShell"), SIENNA("Sienna"), SILVER(
				"Silver"), SKY_BLUE("SkyBlue"), SLATE_BLUE("SlateBlue"), SLATE_GRAY(
				"SlateGray"), SNOW("Snow"), SPRING_GREEN("SpringGreen"), STEEL_BLUE(
				"SteelBlue"), TAN("Tan"), TEAL("Teal"), THISTLE("Thistle"), TOMATO(
				"Tomato"), TURQUISE("Turquoise"), VIOLET("Violet"), WHEAT(
				"Wheat"), WHITE("White"), WHITE_SMOKE("WhiteSmoke"), YELLOW(
				"Yellow"), YELLOW_GREEN("YellowGreen");

		private String color;

		EnumHtmlColor(String color) {
			this.color = color;
		}

		public String color() {
			return color;
		}
	}

	/**
	 * @author Itzhak.Hovav
	 */
	public enum EnumHtmlAlignment {

		CENTER("center"), RIGHT("right"), LEFT("left"), JUSTIFY("justify"), CHAR(
				"char");

		private String alignment;

		EnumHtmlAlignment(String alignment) {
			this.alignment = alignment;
		}

		public String alignment() {
			return alignment;
		}
	}

	public void toFile(String fileName, String... titles) {

		File f = new File(fileName);

		FileWriter fw;

		try {

			StringBuffer sb = new StringBuffer();

			sb.append("<b>");

			for (int i = 0; i < titles.length; i++) {

				sb.append(titles[i] + "<br>");
			}

			sb.append("<\b>");

			fw = new FileWriter(f, f.exists());

			fw.write(sb.toString() + toString() + "<br>");

			fw.flush();
			fw.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HtmlTable t = new HtmlTable();
		t.setHeader("fff", "gfgfg");

		t.addLineWithBgColor(EnumHtmlColor.YELLOW, "gg", "yyy");

		System.out.println(t.toString(false, true));
	}

}
