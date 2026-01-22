package Interface;

import Draft.Player;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageService
{
    private static final String DIR_FACES = "/Faces/";
    private static final String DIR_TEAMS = "/Clubs/";
    private static final String DIR_FLAGS = "/Countries/";
    private static final String DIR_CARDS = "/Cards/";
    private static final String DIR_BACKGROUNDS = "/Backgrounds/";

    // Cache to prevent reloading the same image 100 times
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage getCardImage()
    {
        return loadResource(DIR_CARDS + "Card.png");
    }

    public static BufferedImage getFaceImage(Player p)
    {
        if (p == null) return loadResource(DIR_FACES + "default.png");

        String last = sanitize(p.getLastName());
        String first = sanitize(p.getFirstName());

        if (resourceExists(DIR_FACES + last + ".png"))
        {
            return loadResource(DIR_FACES + last + ".png");
        }

        return loadResource(DIR_FACES + "default.png");
    }

    public static BufferedImage getTeamImage(Player p)
    {
        if (p == null) return loadResource(DIR_TEAMS + "default.png");

        String path = DIR_TEAMS + sanitize(p.getTeamName()) + ".png";

        return resourceExists(path) ? loadResource(path) : loadResource(DIR_TEAMS + "default.png");
    }

    public static BufferedImage getFlagImage(Player p)
    {
        if (p == null) return loadResource(DIR_FLAGS + "default.png");

        String path = DIR_FLAGS + sanitize(p.getCountry()) + ".png";

        return resourceExists(path) ? loadResource(path) : loadResource(DIR_FLAGS + "default.png");
    }

    public static BufferedImage getBackground(String name)
    {
        if (name == null) return loadResource(DIR_BACKGROUNDS + "default.png");

        String path = DIR_BACKGROUNDS + sanitize(name) + ".png";

        return resourceExists(path) ? loadResource(path) : loadResource(DIR_BACKGROUNDS + "default.png");
    }

    public static BufferedImage getSelectedCard()
    {
        return loadResource(DIR_CARDS + "selected.png");
    }


    public static void preloadCard()
    {
        getCardImage();
    }

    private static String sanitize(String input)
    {
        if (input == null) return "";

        return input.toLowerCase()
                .replace(" ", "_")
                .replace(".", "")
                .replace("&", "and");
    }

    private static boolean resourceExists(String path)
    {
        return ImageService.class.getResource(path) != null;
    }

    private static BufferedImage loadResource(String path)
    {
        if (cache.containsKey(path))
        {
            return cache.get(path);
        }

        try
        {
            URL url = ImageService.class.getResource(path);

            if (url == null)
            {
                System.err.println("Resource not found: " + path);

                return createPlaceholder();
            }

            BufferedImage img = ImageIO.read(url);
            cache.put(path, img);
            return img;

        }
        catch (IOException e)
        {
            System.err.println("Error loading image: " + path);
            return createPlaceholder();
        }
    }

    private static BufferedImage createPlaceholder()
    {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 50, 50);
        g.setColor(Color.RED);
        g.drawLine(0, 0, 50, 50); // Draw an X
        g.drawLine(50, 0, 0, 50);
        g.dispose();

        return img;
    }
}