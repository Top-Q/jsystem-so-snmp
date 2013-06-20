package systemobject.aqua.automation.utils.office.excel.constant;

import org.apache.poi.hssf.util.HSSFColor;

/**
 * @author Itzhak.Hovav
 */
public enum ExcelColor {

	AQUA(HSSFColor.AQUA.index), BLACK(HSSFColor.BLACK.index), BLUE(
			HSSFColor.BLUE.index), BLUE_GREY(HSSFColor.BLUE_GREY.index), BRIGHT_GREEN(
			HSSFColor.BRIGHT_GREEN.index), BROWN(HSSFColor.BROWN.index), CORAL(
			HSSFColor.CORAL.index), CORNFLOWER_BLUE(
			HSSFColor.CORNFLOWER_BLUE.index), DARK_BLUE(
			HSSFColor.DARK_BLUE.index), DARK_GREEN(HSSFColor.DARK_GREEN.index), DARK_TEAL(
			HSSFColor.DARK_TEAL.index), DARK_YELLOW(HSSFColor.DARK_TEAL.index), GOLD(
			HSSFColor.GOLD.index), GREY_40_PERCENT(
			HSSFColor.GREY_40_PERCENT.index), GREY_50_PERCENT(
			HSSFColor.GREY_50_PERCENT.index), GREY_80_PERCENT(
			HSSFColor.GREY_80_PERCENT.index), INDIGO(HSSFColor.INDIGO.index), LAVENDER(
			HSSFColor.LAVENDER.index), LEMON_CHIFFON(
			HSSFColor.LEMON_CHIFFON.index), LIGHT_BLUE(
			HSSFColor.LIGHT_BLUE.index), LIGHT_CORNFLOWER_BLUE(
			HSSFColor.LIGHT_CORNFLOWER_BLUE.index), LIGHT_GREEN(
			HSSFColor.LIGHT_GREEN.index), LIGHT_ORANGE(
			HSSFColor.LIGHT_ORANGE.index), LIGHT_TURQUOISE(
			HSSFColor.LIGHT_TURQUOISE.index), LIGHT_YELLOW(
			HSSFColor.LIGHT_YELLOW.index), LIME(HSSFColor.LIME.index), MAROON(
			HSSFColor.MAROON.index), OLIVE_GREEN(HSSFColor.OLIVE_GREEN.index), ORANGE(
			HSSFColor.ORANGE.index), ORCHID(HSSFColor.ORCHID.index), PALE_BLUE(
			HSSFColor.PALE_BLUE.index), PINK(HSSFColor.PINK.index), PLUM(
			HSSFColor.PLUM.index), RED(HSSFColor.RED.index), ROSE(
			HSSFColor.ROSE.index), ROYAL_BLUE(HSSFColor.ROYAL_BLUE.index), SEA_GREEN(
			HSSFColor.SEA_GREEN.index), SKY_BLUE(HSSFColor.SKY_BLUE.index), TAN(
			HSSFColor.TAN.index), TEAL(HSSFColor.TEAL.index), TURQUOISE(
			HSSFColor.AQUA.index), VIOLET(HSSFColor.VIOLET.index), WHITE(
			HSSFColor.WHITE.index), YELLOW(HSSFColor.YELLOW.index), AUTOMATIC(
			HSSFColor.AUTOMATIC.index);

	private short color;

	ExcelColor(short color) {
		this.color = color;
	}

	public short color() {
		return color;
	}

	public static ExcelColor get(short color) {
		for (ExcelColor c : values()) {
			if (c.color == color) {
				return c;
			}
		}
		return AUTOMATIC;
	}

	public static ExcelColor get(HSSFColor color) {
		return get(color.getIndex());
	}

}
