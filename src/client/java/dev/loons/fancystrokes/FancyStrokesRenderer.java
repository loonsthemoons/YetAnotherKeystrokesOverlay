package dev.loons.fancystrokes;

import net.minecraft.client.gui.DrawContext;

/**
 * Provides utility methods for rendering custom graphical elements,
 * specifically rounded rectangles and their outlines, using Minecraft's {@link DrawContext}.
 */
public class FancyStrokesRenderer {

    /**
     * Draws a fully filled rectangle with rounded corners.
     * The radius is automatically clamped to half of the shorter side to prevent graphical issues.
     *
     * @param context The {@link DrawContext} used for drawing.
     * @param x The X-coordinate of the top-left corner of the rectangle.
     * @param y The Y-coordinate of the top-left corner of the rectangle.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param radius The desired radius of the corners. This value will be clamped automatically.
     * @param color The ARGB integer color for filling the rectangle.
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // The radius is clamped to half of the shorter side to prevent graphical artifacts.
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        if (radius < 0) radius = 0;

        int x_end = x + width;
        int y_end = y + height;

        // Draw the three main rectangles that form the core of the rounded rectangle.
        context.fill(x + radius, y, x_end - radius, y_end, color);
        context.fill(x, y + radius, x + radius, y_end - radius, color);
        context.fill(x_end - radius, y + radius, x_end, y_end - radius, color);

        // Draw the four filled corners using a specialized helper method.
        drawFilledCorner(context, x + radius, y + radius, radius, 0, color); // Top-Left
        drawFilledCorner(context, x_end - radius - 1, y + radius, radius, 1, color); // Top-Right
        drawFilledCorner(context, x + radius, y_end - radius - 1, radius, 2, color); // Bottom-Left
        drawFilledCorner(context, x_end - radius - 1, y_end - radius - 1, radius, 3, color); // Bottom-Right
    }

    /**
     * Draws a clean, anti-aliased outline for a rounded rectangle.
     * The radius is automatically clamped to half of the shorter side to prevent graphical issues.
     *
     * @param context The {@link DrawContext} used for drawing.
     * @param x The X-coordinate of the top-left corner of the rectangle.
     * @param y The Y-coordinate of the top-left corner of the rectangle.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param radius The desired radius of the corners. This value will be clamped automatically.
     * @param color The ARGB integer color for the outline.
     */
    public static void drawRoundedOutline(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // The radius is clamped to half of the shorter side to prevent graphical artifacts.
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        if (radius < 0) radius = 0;

        int x_end = x + width;
        int y_end = y + height;

        // Draw the straight lines of the outline.
        context.fill(x + radius, y, x_end - radius, y + 1, color); // Top
        context.fill(x + radius, y_end - 1, x_end - radius, y_end, color); // Bottom
        context.fill(x, y + radius, x + 1, y_end - radius, color); // Left
        context.fill(x_end - 1, y + radius, x_end, y_end - radius, color); // Right

        // Draw the four clean corner arcs using a specialized helper method.
        drawOutlineCorner(context, x + radius, y + radius, radius, 0, color); // Top-Left
        drawOutlineCorner(context, x_end - radius - 1, y + radius, radius, 1, color); // Top-Right
        drawOutlineCorner(context, x + radius, y_end - radius - 1, radius, 2, color); // Bottom-Left
        drawOutlineCorner(context, x_end - radius - 1, y_end - radius - 1, radius, 3, color); // Bottom-Right
    }

    /**
     * Helper method that draws a filled quarter-circle (corner) using a modified Midpoint Circle Algorithm.
     * It draws horizontal lines to fill the corner area.
     *
     * @param context The {@link DrawContext} used for drawing.
     * @param centerX The X-coordinate of the center of the full circle from which this corner is derived.
     * @param centerY The Y-coordinate of the center of the full circle from which this corner is derived.
     * @param radius The radius of the corner.
     * @param quadrant The quadrant to draw (0 for top-left, 1 for top-right, 2 for bottom-left, 3 for bottom-right).
     * @param color The ARGB integer color for the filled corner.
     */
    private static void drawFilledCorner(DrawContext context, int centerX, int centerY, int radius, int quadrant, int color) {
        int x = radius;
        int y = 0;
        int err = 1 - radius;

        while (x >= y) {
            // Draw horizontal lines to fill the corner
            switch (quadrant) {
                case 0: // Top-Left
                    context.fill(centerX - x, centerY - y, centerX, centerY - y + 1, color);
                    context.fill(centerX - y, centerY - x, centerX, centerY - x + 1, color);
                    break;
                case 1: // Top-Right
                    context.fill(centerX, centerY - y, centerX + x + 1, centerY - y + 1, color);
                    context.fill(centerX, centerY - x, centerX + y + 1, centerY - x + 1, color);
                    break;
                case 2: // Bottom-Left
                    context.fill(centerX - x, centerY + y, centerX, centerY + y + 1, color);
                    context.fill(centerX - y, centerY + x, centerX, centerY + x + 1, color);
                    break;
                case 3: // Bottom-Right
                    context.fill(centerX, centerY + y, centerX + x + 1, centerY + y + 1, color);
                    context.fill(centerX, centerY + x, centerX + y + 1, centerY + x + 1, color);
                    break;
            }

            y++;
            if (err <= 0) {
                err += 2 * y + 1;
            } else {
                x--;
                err += 2 * (y - x) + 1;
            }
        }
    }

    /**
     * Helper method that draws a clean arc (outline) for a corner using a modified Midpoint Circle Algorithm.
     * It draws individual pixels to form the arc.
     *
     * @param context The {@link DrawContext} used for drawing.
     * @param centerX The X-coordinate of the center of the full circle from which this corner is derived.
     * @param centerY The Y-coordinate of the center of the full circle from which this corner is derived.
     * @param radius The radius of the corner.
     * @param quadrant The quadrant to draw (0 for top-left, 1 for top-right, 2 for bottom-left, 3 for bottom-right).
     * @param color The ARGB integer color for the outline.
     */
    private static void drawOutlineCorner(DrawContext context, int centerX, int centerY, int radius, int quadrant, int color) {
        int x = radius;
        int y = 0;
        // KORREKTUR: Der Fehlerterm wird auf den gleichen Wert wie bei der Füllung gesetzt.
        // Dies stellt sicher, dass die Outline exakt auf dem Rand der Füllung gezeichnet wird.
        int err = 1 - radius;

        while (x >= y) {
            // Zeichnet einzelne Pixel für die Umrandung
            switch (quadrant) {
                case 0: // Top-Left
                    context.fill(centerX - x, centerY - y, centerX - x + 1, centerY - y + 1, color);
                    context.fill(centerX - y, centerY - x, centerX - y + 1, centerY - x + 1, color);
                    break;
                case 1: // Top-Right
                    context.fill(centerX + x, centerY - y, centerX + x + 1, centerY - y + 1, color);
                    context.fill(centerX + y, centerY - x, centerX + y + 1, centerY - x + 1, color);
                    break;
                case 2: // Bottom-Left
                    context.fill(centerX - x, centerY + y, centerX - x + 1, centerY + y + 1, color);
                    context.fill(centerX - y, centerY + x, centerX - y + 1, centerY + x + 1, color);
                    break;
                case 3: // Bottom-Right
                    context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                    context.fill(centerX + y, centerY + x, centerX + y + 1, centerY + x + 1, color);
                    break;
            }

            y++;
            if (err <= 0) {
                err += 2 * y + 1;
            } else {
                x--;
                err += 2 * (y - x) + 1;
            }
        }
    }
}
