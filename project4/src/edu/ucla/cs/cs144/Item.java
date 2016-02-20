package edu.ucla.cs.cs144;

/**
 * Item
 * Helper class to represent a single item
 */
public class Item {

    public String i_id;
    public String i_name;
    public String i_description;
    public String i_started;
    public String i_ends;
    public String i_currently;
    public String i_firstbid;
    public String i_buyprice;
    public String i_numberofbids;
    public String i_seller;

    Item(String id, String name, String description, String started, String ends, String currently,
            String firstbid, String buyprice, String numberofbids, String seller) {

        i_id = id;
        i_name = name;
        i_description = description;
        i_started = started;
        i_ends = ends;
        i_currently = currently;
        i_firstbid = firstbid;
        i_buyprice = buyprice;
        i_numberofbids = numberofbids;
        i_seller = seller;
    }
}