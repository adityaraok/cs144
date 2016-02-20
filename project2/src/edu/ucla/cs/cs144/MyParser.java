/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import java.lang.Object;

/**
 * MyParser
 * Parses .xml files provided via command line into .csv files
 * for loading into MySQL.
 */
class MyParser {
    /**
     * Category 
     * Helper class to represent a single entry in Category.csv
     */
    public static class Category {
        int c_categoryID;
        String c_name;

        public Category(int categoryID, String name) {
            c_categoryID = categoryID;
            c_name = name;
        }  
    }

    /**
     * Item Category
     * Helper class to represent a single entry in Item_Category.csv
     */
    public static class ItemCategory {
        String ic_itemID;
        int ic_categoryID;

        public ItemCategory(String itemID, int categoryID) {
            ic_itemID = itemID;
            ic_categoryID = categoryID;
        }
    }

    /**
     * User
     * Helper class to represent a single entry in User.csv
     */
    public static class User { 
        String u_name;
        String u_location;
        String u_rating;
        String u_country;

        User(String name, String rating, String location, String country) { 
            u_name = name; 
            u_rating = rating;
            u_location = location;
            u_country = country;
        } 
    }
 
    /**
     * Bid
     * Helper class to represent a single entry in Bid.csv
     */
    public static class Bid
    { 
        String b_userid;
        String b_time; 
        String b_item;
        String b_amount;


        Bid(String userID, String time, String itemID, String amount)
        { 
          
            b_userid = userID;
            b_time = time; 
            b_item = itemID; 
            b_amount = amount;
        } 
    }

    /**
     * Item
     * Helper class to represent a single entry in Item.csv
     */
    public static class Item { 
        String i_id;
        String i_name;
        String i_description;
        String i_started;
        String i_ends;
        String i_currently;
        String i_firstbid;
        String i_buyprice;
        String i_numberofbids;
        String i_seller;

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
    
    // Maps which will hold entries for .csv files while parsing
    static Map<String, User> userMap = new HashMap<String, User>();
    static Map<String, Bid> bidMap = new HashMap<String, Bid>();
    static Map<String, Item> itemMap = new HashMap<String, Item>();
    static Map<String, Category> categoryMap = new HashMap<String, Category>(); 
    static Map<String, ItemCategory> itemcategoriesMap = new HashMap<String, ItemCategory>(); 
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    static final String[] typeName = {
    "none",
    "Element",
    "Attr",
    "Text",
    "CDATA",
    "EntityRef",
    "Entity",
    "ProcInstr",
    "Comment",
    "Document",
    "DocType",
    "DocFragment",
    "Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

    /* Returns the date from the xml files' format (Dec-04-01 04:03:12)
     * converted to MySQL timestamp readable format ().
     */
    static String convertDate(String dateString) {
        String old_format = "MMM-dd-yy HH:mm:ss";
        String new_format = "yyyy-MM-dd HH:mm:ss";
        
        SimpleDateFormat sdf = new SimpleDateFormat(old_format);
        String out = "";
        try {
            Date d = sdf.parse(dateString);
            sdf.applyPattern(new_format);
            out = sdf.format(d);
        } catch (ParseException pe) {
            System.out.println("Could not format date");
        }
        
        return out;
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        

        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
    
 
        Element root = doc.getDocumentElement();
        Element[] items = getElementsByTagNameNR(root, "Item");


        int itemCount = items.length;
        for(int i=0; i<itemCount; i++) {

            String itemID = items[i].getAttribute("ItemID");
            String name =  getElementTextByTagNameNR(items[i], "Name");
            String start_time = convertDate(getElementTextByTagNameNR(items[i], "Started"));
            String end_time = convertDate(getElementTextByTagNameNR(items[i], "Ends"));
            String description = getElementTextByTagNameNR(items[i], "Description");

            if (description.length() > 4000)
                    description = description.substring(0, 4000);


            String currently = strip(getElementTextByTagNameNR(items[i], "Currently"));
            String buy_price = strip(getElementTextByTagNameNR(items[i], "Buy_Price"));

            if (buy_price.isEmpty())
                buy_price = "";

            String first_bid = strip(getElementTextByTagNameNR(items[i], "First_Bid"));
            String number_of_bids = getElementTextByTagNameNR(items[i], "Number_of_Bids");
            

            Element[] categories = getElementsByTagNameNR(items[i], "Category");
            

            int categoryCount = categories.length;
            for (int ind = 0; ind < categoryCount; ind++) {

                String categoryName = getElementText(categories[ind]);
                int categoryID;

                if (categoryMap.containsKey(categoryName)) {
                    Category category_object = categoryMap.get(categoryName);
                    categoryID = category_object.c_categoryID;
                } else {
                    // Create our own categoryID 
                    categoryID = categoryMap.size() + 1;
                    Category category_object = new Category(categoryID, categoryName);
                    categoryMap.put(categoryName, category_object);
                }


                ItemCategory item_cat = new ItemCategory(itemID, categoryID);
                itemcategoriesMap.put(itemID + categoryID, item_cat);
            }

            // Get the list of bids
            Element BidsParent = getElementByTagNameNR(items[i], "Bids");
            Element[] Bids = getElementsByTagNameNR(BidsParent, "Bid");

    
            int bidCount = Bids.length;
            for (int j=0; j<bidCount; j++) {
                // Get info about the bid for bidMap
                Element bid = Bids[j];
                String b_time = convertDate(getElementTextByTagNameNR(bid, "Time"));
                String b_amount = strip(getElementTextByTagNameNR(bid, "Amount"));

                // Get info about the bidder to add to userMap
                Element bidder = getElementByTagNameNR(bid, "Bidder");
                String b_userid =  bidder.getAttribute("UserID");
                String b_rating = bidder.getAttribute("Rating");
                String b_location = getElementTextByTagNameNR(bidder, "Location");
                if (b_location.isEmpty())
                    b_location = "";

                String b_country = getElementTextByTagNameNR(bidder, "Country");
                if (b_country.isEmpty())
                    b_country = "";


                Bid bid_object = new Bid(b_userid, b_time, itemID, b_amount);
                bidMap.put(b_userid + b_time, bid_object);


                if (!userMap.containsKey(b_userid)) {
                    User bidder_object = new User(b_userid, b_rating, b_location, b_country);
                    userMap.put(b_userid, bidder_object);
                }
            }


            Element seller = getElementByTagNameNR(items[i], "Seller");
            String s_userid = seller.getAttribute("UserID");
            String s_rating = seller.getAttribute("Rating");
            String s_location = getElementTextByTagNameNR(items[i], "Location");
            String s_country = getElementTextByTagNameNR(items[i], "Country");
          

            if (!userMap.containsKey(s_userid)) {
                User seller_object = new User(s_userid, s_rating, s_location, s_country);
                userMap.put(s_userid, seller_object);
            }

            Item item_object = new Item(itemID, name, description, start_time, end_time, currently, first_bid, buy_price, number_of_bids, s_userid); 
            itemMap.put(itemID, item_object);
        }        
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        

        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        try {
            FileWriter item = new FileWriter("Item.dat");
            BufferedWriter itemwriter= new BufferedWriter(item);
     
            for (Map.Entry<String, Item> entry : itemMap.entrySet()) {
                Item itemEntry = entry.getValue();
                  
                String entrycsv = String.format("%s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s\n", 
                       itemEntry.i_id,
                       itemEntry.i_name,
                       itemEntry.i_description,
                       itemEntry.i_started,
                       itemEntry.i_ends,
                       itemEntry.i_currently,
                       itemEntry.i_firstbid,
                       itemEntry.i_buyprice,
                       itemEntry.i_numberofbids,
                       itemEntry.i_seller);
                
                itemwriter.write(entrycsv); 
            }

            itemwriter.close();
            item.close();

        } catch (IOException e) {
                e.printStackTrace();
        }

        // Write userMap to User.dat
        try {
            FileWriter user = new FileWriter("User.dat");
            BufferedWriter userwriter= new BufferedWriter(user);
            for (Map.Entry<String, User> entry : userMap.entrySet()) {
                User userEntry = entry.getValue();

                String entrycsv = String.format("%s |*| %s |*| %s |*| %s\n", 
                        userEntry.u_name,
                        userEntry.u_rating,
                        userEntry.u_location,
                        userEntry.u_country);

                userwriter.write(entrycsv); 
            }

            userwriter.close();
            user.close();

        } catch (IOException e) {
                e.printStackTrace();
        }

        // Write categoryMap to Category.dat
        try {
            FileWriter category = new FileWriter("Category.dat");
            BufferedWriter categorywriter= new BufferedWriter(category);
            for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
                Category categoryEntry = entry.getValue();

                String entrycsv = String.format("%s |*| %s\n", 
                            String.valueOf(categoryEntry.c_categoryID),
                            categoryEntry.c_name);
                categorywriter.write(entrycsv); 
            }

            categorywriter.close();
            category.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write itemcategoriesMap to ItemCategory.dat
        try {
            FileWriter itemcategory = new FileWriter("Item_Category.dat");
            BufferedWriter itemcategorywriter= new BufferedWriter(itemcategory);
            for (Map.Entry<String, ItemCategory> entry : itemcategoriesMap.entrySet()) {
                ItemCategory icEntry = entry.getValue();
               
                String entrycsv = String.format("%s |*| %s\n", 
                        icEntry.ic_itemID,
                        String.valueOf(icEntry.ic_categoryID));

                itemcategorywriter.write(entrycsv); 
            }

            itemcategorywriter.close();
            itemcategory.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write bidMap to Bid.dat
        try {
            FileWriter bids = new FileWriter("Bid.dat");
            BufferedWriter bidswriter= new BufferedWriter(bids);
             for (Map.Entry<String, Bid> entry : bidMap.entrySet()) {

                Bid bidEntry = entry.getValue();
                   
                String entrycsv = String.format("%s |*| %s |*| %s |*| %s\n", 
                        bidEntry.b_userid,
                        bidEntry.b_time,
                        bidEntry.b_item,
                        bidEntry.b_amount);
                
                bidswriter.write(entrycsv); 
            }
            bidswriter.close();
            bids.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
