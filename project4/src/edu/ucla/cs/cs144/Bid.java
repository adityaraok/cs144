package edu.ucla.cs.cs144;

/**
 * Bid
 * Helper class to represent a single Bid
 */
public class Bid
{
    public String b_userid;
    public String b_time;
    public String b_item;
    public String b_amount;

    Bid(String userID, String time, String itemID, String amount)
    {
      
        b_userid = userID;
        b_time = time;
        b_item = itemID;
        b_amount = amount;
    }
}