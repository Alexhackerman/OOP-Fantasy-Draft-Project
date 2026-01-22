package Tests;

import Users.Admin;
import Users.Drafter;

public class TestUser
{

    public static void main(String[] args)
    {
        System.out.println("Running User Logic Tests");

        testAdminRole();
        testDrafterRole();
        testHasAnyRole();
        testCaseInsensitivity();

        System.out.println("Tests made!");
    }

    private static void testAdminRole()
    {
        System.out.print("Testing Admin is Admin");

        Admin admin = new Admin(1, "Super", "Admin", "admin", "admin@test.com", "pass");

        if (!admin.isAdmin())
        {
            throw new RuntimeException("FAILED: Admin object should return true for isAdmin()");
        }
        if (admin.isDrafter())
        {
            throw new RuntimeException("FAILED: Admin object should return false for isDrafter()");
        }
        System.out.println("OK");
    }

    private static void testDrafterRole()
    {
        System.out.print("Testing Drafter is User");

        Drafter drafter = new Drafter(1, "Simple", "User", "user", "user@test.com", "pass");

        if (drafter.isAdmin())
        {
            throw new RuntimeException("FAILED: Drafter object should return false for isAdmin()");
        }
        if (!drafter.isDrafter())
        {
            throw new RuntimeException("FAILED: Drafter object should return true for isDrafter()");
        }
        System.out.println("OK");
    }

    private static void testHasAnyRole()
    {
        System.out.print("Testing hasAnyRole()");

        Drafter drafter = new Drafter(1, "Test", "User", "u", "e", "p"); // Role is "user"

        if (!drafter.hasAnyRole("admin", "manager", "user"))
        {
            throw new RuntimeException("FAILED: Should find 'user' in the list");
        }

        if (drafter.hasAnyRole("admin", "manager"))
        {
            throw new RuntimeException("FAILED: Should NOT find 'user' in admin/manager list");
        }
        System.out.println("OK");
    }

    private static void testCaseInsensitivity()
    {
        System.out.print("Testing Role Case Insensitivity");

        Admin admin = new Admin(1, "A", "A", "a", "e", "p"); // Role is "admin"

        if (!admin.hasRole("ADMIN"))
        {
            throw new RuntimeException("FAILED: hasRole should ignore case (ADMIN == admin)");
        }
        System.out.println("OK");
    }
}