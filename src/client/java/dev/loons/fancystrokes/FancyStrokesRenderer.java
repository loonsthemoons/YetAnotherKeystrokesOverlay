package dev.loons.fancystrokes;

import net.minecraft.client.gui.DrawContext;

public class FancyStrokesRenderer {

    /**
     * Zeichnet ein vollständig gefülltes Rechteck mit abgerundeten Ecken.
     *
     * @param context Der DrawContext zum Zeichnen.
     * @param x Die X-Koordinate.
     * @param y Die Y-Koordinate.
     * @param width Die Breite.
     * @param height Die Höhe.
     * @param radius Der gewünschte Radius der Ecken. Wird automatisch begrenzt.
     * @param color Die Füllfarbe.
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Der Radius wird auf die Hälfte der kürzeren Seite begrenzt, um Grafikfehler zu vermeiden.
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        if (radius < 0) radius = 0;

        int x_end = x + width;
        int y_end = y + height;

        // Zeichne die drei Hauptrechtecke, die den Kern bilden.
        context.fill(x + radius, y, x_end - radius, y_end, color);
        context.fill(x, y + radius, x + radius, y_end - radius, color);
        context.fill(x_end - radius, y + radius, x_end, y_end - radius, color);

        // Zeichne die vier gefüllten Ecken.
        drawFilledCorner(context, x + radius, y + radius, radius, 0, color); // Oben-Links
        drawFilledCorner(context, x_end - radius - 1, y + radius, radius, 1, color); // Oben-Rechts
        drawFilledCorner(context, x + radius, y_end - radius - 1, radius, 2, color); // Unten-Links
        drawFilledCorner(context, x_end - radius - 1, y_end - radius - 1, radius, 3, color); // Unten-Rechts
    }

    /**
     * Zeichnet eine saubere, nicht-pixelige Umrandung für ein abgerundetes Rechteck.
     *
     * @param context Der DrawContext.
     * @param x Die X-Koordinate.
     * @param y Die Y-Koordinate.
     * @param width Die Breite.
     * @param height Die Höhe.
     * @param radius Der gewünschte Radius der Ecken. Wird automatisch begrenzt.
     * @param color Die Farbe der Umrandung.
     */
    public static void drawRoundedOutline(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Der Radius wird auf die Hälfte der kürzeren Seite begrenzt, um Grafikfehler zu vermeiden.
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        if (radius < 0) radius = 0;

        int x_end = x + width;
        int y_end = y + height;

        // Zeichne die geraden Linien der Umrandung
        context.fill(x + radius, y, x_end - radius, y + 1, color); // Oben
        context.fill(x + radius, y_end - 1, x_end - radius, y_end, color); // Unten
        context.fill(x, y + radius, x + 1, y_end - radius, color); // Links
        context.fill(x_end - 1, y + radius, x_end, y_end - radius, color); // Rechts

        // Zeichne die vier sauberen Eckbögen.
        drawOutlineCorner(context, x + radius, y + radius, radius, 0, color); // Oben-Links
        drawOutlineCorner(context, x_end - radius - 1, y + radius, radius, 1, color); // Oben-Rechts
        drawOutlineCorner(context, x + radius, y_end - radius - 1, radius, 2, color); // Unten-Links
        drawOutlineCorner(context, x_end - radius - 1, y_end - radius - 1, radius, 3, color); // Unten-Rechts
    }

    /**
     * Hilfsmethode, die eine gefüllte Ecke (Viertelkreis) zeichnet.
     */
    private static void drawFilledCorner(DrawContext context, int centerX, int centerY, int radius, int quadrant, int color) {
        int x = radius;
        int y = 0;
        int err = 1 - radius; // Initialer Fehlerterm für den Midpoint-Algorithmus

        while (x >= y) {
            // Zeichnet horizontale Linien, um die Ecke zu füllen
            switch (quadrant) {
                case 0: // Oben-Links
                    context.fill(centerX - x, centerY - y, centerX, centerY - y + 1, color);
                    context.fill(centerX - y, centerY - x, centerX, centerY - x + 1, color);
                    break;
                case 1: // Oben-Rechts
                    context.fill(centerX, centerY - y, centerX + x + 1, centerY - y + 1, color);
                    context.fill(centerX, centerY - x, centerX + y + 1, centerY - x + 1, color);
                    break;
                case 2: // Unten-Links
                    context.fill(centerX - x, centerY + y, centerX, centerY + y + 1, color);
                    context.fill(centerX - y, centerY + x, centerX, centerY + x + 1, color);
                    break;
                case 3: // Unten-Rechts
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
     * Hilfsmethode, die einen sauberen Bogen (Umrandung) für eine Ecke zeichnet.
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
                case 0: // Oben-Links
                    context.fill(centerX - x, centerY - y, centerX - x + 1, centerY - y + 1, color);
                    context.fill(centerX - y, centerY - x, centerX - y + 1, centerY - x + 1, color);
                    break;
                case 1: // Oben-Rechts
                    context.fill(centerX + x, centerY - y, centerX + x + 1, centerY - y + 1, color);
                    context.fill(centerX + y, centerY - x, centerX + y + 1, centerY - x + 1, color);
                    break;
                case 2: // Unten-Links
                    context.fill(centerX - x, centerY + y, centerX - x + 1, centerY + y + 1, color);
                    context.fill(centerX - y, centerY + x, centerX - y + 1, centerY + x + 1, color);
                    break;
                case 3: // Unten-Rechts
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
