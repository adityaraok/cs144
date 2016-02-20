package edu.ucla.cs.cs144;

/**
 * User
 * Helper class to represent a single User
 */
public class User {
    public String u_name;
    public String u_location;
    public String u_rating;
    public String u_country;

    User(String name, String rating, String location, String country) {
        u_name = name;
        u_rating = rating;
        u_location = location;
        u_country = country;
    }
   public String getLocation()
    {
        return u_location;
    }
   public String getCountry()
    {
        return u_country;
    }
}
