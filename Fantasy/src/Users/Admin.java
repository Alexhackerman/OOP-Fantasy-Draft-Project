package Users;

import javax.swing.*;
import Interface.AdminPage;

public class Admin extends User
{

    public Admin(int userId, String firstName, String lastName, String username, String email, String password)
    {
        super(userId, firstName, lastName, username, email, password);
        this.setRole("admin");
    }

    @Override
    public String getDashboardTitle()
    {
        return "Admin Panel - " + getFullName();
    }

    @Override
    public void openDashboard()
    {
        SwingUtilities.invokeLater(() ->
        {
            AdminPage adminPage = new AdminPage(this);
        });
    }

    @Override
    public String getPermissionsDescription()
    {
        return "Full system access: manage users, players, and view all data";
    }

    public boolean canManageUsers()
    {
        return true;  // Admin can always manage users
    }

    public boolean canManagePlayers()
    {
        return true;  // Admin can always manage players
    }

    public boolean canViewAllDrafts()
    {
        return true;  // Admin can view all drafts
    }

}