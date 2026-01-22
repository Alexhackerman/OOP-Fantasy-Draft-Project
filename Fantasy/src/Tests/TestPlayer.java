package Tests;

import Draft.Player;

public class TestPlayer
{

    public static void main(String[] args)
    {
        System.out.println("Running Player Tests");

        testConstructorAssignment();
        testSetOverallValid();
        testSetOverallTooHigh();
        testSetOverallNegative();

        System.out.println("Tests made!");
    }

    private static void testConstructorAssignment()
    {
        System.out.print("Testing Constructor");

        Player p = new Player("Cristiano", "Ronaldo", 36, "Portugal", 92, 5, 10);

        if (!p.getFirstName().equals("Cristiano")) throw new RuntimeException("First Name mismatch");
        if (!p.getLastName().equals("Ronaldo")) throw new RuntimeException("Last Name mismatch");
        if (p.getAge() != 36) throw new RuntimeException("Age mismatch");
        if (!p.getCountry().equals("Portugal")) throw new RuntimeException("Country mismatch");
        if (p.getOverall() != 92) throw new RuntimeException("Overall mismatch");

        System.out.println("OK");
    }

    private static void testSetOverallValid()
    {
        System.out.print("Testing Valid Rating");
        Player p = new Player();
        p.setOverall(85);
        if (p.getOverall() != 85)
        {
            throw new RuntimeException("Expected 85 but got " + p.getOverall());
        }
        System.out.println("OK");
    }

    private static void testSetOverallTooHigh()
    {
        System.out.print("Testing Invalid Rating");
        Player p = new Player();
        try
        {
            p.setOverall(150);
            throw new RuntimeException("Rating not acceptable");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("OK");
        }
    }

    private static void testSetOverallNegative()
    {
        System.out.print("Testing Negative Rating");
        Player p = new Player();
        try
        {
            p.setOverall(-10);
            throw new RuntimeException("Negative rating not acceptable");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("OK");
        }
    }
}