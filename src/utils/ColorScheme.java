package utils;

import java.awt.*;

public class ColorScheme {
    // Main theme - Modern minimalist colors
    public static final Color PRIMARY = new Color(33, 150, 243); // Material Blue
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    public static final Color SECONDARY = new Color(236, 239, 241); // Blue Grey 50
    public static final Color BACKGROUND = new Color(255, 255, 255);

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    // Button colors
    public static final Color BUTTON_DEFAULT = PRIMARY;
    public static final Color BUTTON_HOVER = PRIMARY_DARK;
    public static final Color BUTTON_TEXT = Color.WHITE;
    public static final Color BUTTON_SECONDARY = SECONDARY;
    public static final Color BUTTON_SECONDARY_TEXT = TEXT_PRIMARY;

    // Table colors
    public static final Color TABLE_HEADER = new Color(236, 239, 241);
    public static final Color TABLE_HEADER_TEXT = TEXT_PRIMARY;
    public static final Color TABLE_EVEN_ROW = BACKGROUND;
    public static final Color TABLE_ODD_ROW = new Color(245, 245, 245);
    public static final Color TABLE_TEXT = TEXT_PRIMARY;
    public static final Color TABLE_SELECTION = new Color(197, 220, 247);

    // Border and shadow colors
    public static final Color BORDER = new Color(224, 224, 224);
    public static final Color SHADOW = new Color(0, 0, 0, 20);
    public static final Color TEXT_FIELD_BACKGROUND = null;
    public static final Color BUTTON_TEXT_PRIMARY = null;
    public static final Color BUTTON_TEXT_SECONDARY = null;
    public static final Color BUTTON_BACKGROUND_PRIMARY = null;
    public static final Color BUTTON_BACKGROUND_SECONDARY = null;
}