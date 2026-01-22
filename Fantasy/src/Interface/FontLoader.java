package Interface;

import java.awt.*;
import java.io.*;

public class FontLoader
{
    private static Font daydreamFont = null;
    private static final String FONT_PATH = "C:\\Users\\alext\\Desktop\\LAB OOP\\Project\\Fantasy\\Daydream.otf";

    public static Font getDaydreamFont(float size)
    {
        if (daydreamFont == null
        ) {
            try
            {
                daydreamFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH));
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(daydreamFont);
                System.out.println("Daydream font loaded successfully!");
            }
            catch (FontFormatException | IOException e)
            {
                System.err.println("Error loading Daydream font: " + e.getMessage());

                daydreamFont = new Font("Arial", Font.PLAIN, (int) size);
            }
        }

        return daydreamFont.deriveFont(size);
    }
}