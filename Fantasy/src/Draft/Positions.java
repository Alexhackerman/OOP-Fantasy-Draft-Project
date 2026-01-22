package Draft;

import java.util.HashMap;
import java.util.Map;

public class Positions
{
    private static final Map<Integer, String> idToName = new HashMap<>();
    private static final Map<String, String> nameToAbbrev = new HashMap<>();

    static
    {
        idToName.put(1, "Goalkeeper");
        idToName.put(2, "Right Back");
        idToName.put(3, "Center Back");
        idToName.put(4, "Left Back");
        idToName.put(5, "Defensive Midfielder");
        idToName.put(6, "Central Midfielder");
        idToName.put(7, "Attacking Midfielder");
        idToName.put(8, "Right Winger");
        idToName.put(9, "Left Winger");
        idToName.put(10, "Striker");

        nameToAbbrev.put("Goalkeeper", "GK");
        nameToAbbrev.put("Right Back", "RB");
        nameToAbbrev.put("Center Back", "CB");
        nameToAbbrev.put("Left Back", "LB");
        nameToAbbrev.put("Defensive Midfielder", "CDM");
        nameToAbbrev.put("Central Midfielder", "CM");
        nameToAbbrev.put("Right Winger", "RW");
        nameToAbbrev.put("Left Winger", "LW");
        nameToAbbrev.put("Attacking Midfielder", "CAM");
        nameToAbbrev.put("Striker", "ST");
    }

    public static String getNameById(int id)
    {
        return idToName.getOrDefault(id, "Unknown");
    }

    public static String getAbbreviationByName(String name)
    {
        return nameToAbbrev.getOrDefault(name, "??");
    }

    public static String getAbbreviationById(int id)
    {
        String name = getNameById(id);
        return getAbbreviationByName(name);
    }
}

