package Draft;

public class Player
{

    private int playerId;
    private String firstName;
    private String lastName;
    private int overall;
    private String country;
    private int age;
    private int positionId;
    private int teamId;

    private transient String teamName;
    private transient String positionName;

    public Player()
    {

    }

    public Player(String firstName, String lastName, int age, String country, int overall, int teamId, int positionId)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.overall = overall;
        this.teamId = teamId;
        this.age = age;
        this.positionId = positionId;
        this.country = country;
    }

    public Player(int playerId, String firstName, String lastName, int overall, int age, int teamId, int positionId,
                  String country,  String teamName, String positionName)
    {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.overall = overall;
        this.teamId = teamId;
        this.age = age;
        this.positionId = positionId;
        this.country = country;
        this.positionName = positionName;
        this.teamName = teamName;
    }

    // getters

    public int  getPlayerId()
    {
        return playerId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public int  getOverall()
    {
        return overall;
    }

    public int getAge()
    {
        return age;
    }

    public int getTeamId()
    {
        return teamId;
    }

    public String  getTeamName()
    {
       return Teams.getNameById(this.teamId);
    }

    public int getPositionId()
    {
        return positionId;
    }

    public String getPositionName()
    {
        return Positions.getNameById(this.positionId);
    }

    public String getPositionAbrev()
    {
        return Positions.getAbbreviationById(this.positionId);
    }

    public String getCountry()
    {
        return country;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    // setters

    public void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setOverall(int overall)
    {
        if (overall >= 0 && overall <= 99)
        {
            this.overall = overall;
        }
        else
        {
            throw new IllegalArgumentException("Overall must be between 0 and 99");
        }
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setTeamId(int teamId)
    {
        this.teamId = teamId;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public void setPositionId(int positionId)
    {
        this.positionId = positionId;
    }

    public void setPositionName(String positionName)
    {
        this.positionName = positionName;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    @Override
    public String toString()
    {
        String posName = (positionName != null) ? positionName : "Position#" + positionId;
        String tmName = (teamName != null) ? teamName : "Team#" + teamId;

        return getFullName() + " (" + posName + ") - " + overall + " - " + tmName;
    }
}
