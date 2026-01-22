package Tests;

import Users.Drafter;

public class TestDrafter
{

    public static void main(String[] args)
    {
        System.out.println("Running Drafter Tests");

        testCalculateFormationRatingEmpty();
        testCalculateFormationRatingSimple();
        testCalculateFormationRatingFullSquadBonus();

        System.out.println("Tests made!");
    }

    private static void testCalculateFormationRatingEmpty()
    {
        System.out.print("Testing Empty Array");

        Drafter drafter = new Drafter(1, "Test", "User", "user", "email", "pass");
        int[] empty = {};

        double result = drafter.calculateFormationRating(empty);

        if (result != 0.0)
        {
            throw new RuntimeException("FAILED: Expected 0.0 but got " + result);
        }
        System.out.println("OK");
    }

    private static void testCalculateFormationRatingSimple()
    {
        System.out.print("Testing Simple Average");

        Drafter drafter = new Drafter(1, "Test", "User", "user", "email", "pass");

        int[] stats = {80, 90};

        double result = drafter.calculateFormationRating(stats);

        if (result != 85.0)
        {
            throw new RuntimeException("FAILED: Expected 85.0 but got " + result);
        }

        System.out.println("OK");
    }

    private static void testCalculateFormationRatingFullSquadBonus()
    {
        System.out.print("Testing Full Squad Bonus");

        Drafter drafter = new Drafter(1, "Test", "User", "user", "email", "pass");

        int[] stats = new int[11]; // 11 players
        for(int i=0; i<11; i++) stats[i] = 100;

        double result = drafter.calculateFormationRating(stats);

        if (Math.abs(result - 105.0) > 0.01)
        {
            throw new RuntimeException("FAILED: Expected 105.0 but got " + result);
        }
        System.out.println("OK");
    }
}