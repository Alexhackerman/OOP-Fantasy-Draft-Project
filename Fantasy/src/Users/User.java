package Users;

public abstract class User
{
    protected int userId;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String email;
    protected String password;
    protected String role;

    protected User(int userId, String firstName, String lastName, String username, String email, String password)
    {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "user";
    }

    public User()
    {

    }

    public int getUserId()
    {
        return userId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getUsername()
    {
        return username;
    }

    public String getEmail()
    {
        return email;
    }

    public String getRole()
    {
        return role;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public boolean isAdmin()
    {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isDrafter()
    {
        return "user".equalsIgnoreCase(role);
    }

    public boolean hasRole(String roleToCheck)
    {
        return role != null && role.equalsIgnoreCase(roleToCheck);
    }

    public boolean hasAnyRole(String... rolesToCheck)
    {
        if (role == null) return false;

        for (String r : rolesToCheck)
        {
            if (role.equalsIgnoreCase(r))
            {
                return true;
            }
        }
        return false;
    }

    public abstract String getDashboardTitle();

    public abstract void openDashboard();

    public abstract String getPermissionsDescription();

    @Override
    public String toString() {
        return getFullName() + " (" + username + ") - " + role;
    }

}
