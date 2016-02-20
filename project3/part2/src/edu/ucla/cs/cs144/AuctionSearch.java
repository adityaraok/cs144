package edu.ucla.cs.cs144;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.util.*;
import java.text.SimpleDateFormat;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchConstraint;
import edu.ucla.cs.cs144.SearchResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
public class AuctionSearch implements IAuctionSearch {
    IndexSearcher searcher;
    QueryParser parser;

    /* 
     * You will probably have to use JDBC to access MySQL data
     * Lucene IndexSearcher class to lookup Lucene index.
     * Read the corresponding tutorial to learn about how to use these.
     *
     * Your code will need to reference the directory which contains your
     * Lucene index files.  Make sure to read the environment variable 
     * $LUCENE_INDEX with System.getenv() to build the appropriate path.
     *
     * You may create helper functions or classes to simplify writing these
     * methods. Make sure that your helper functions are not public,
     * so that they are not exposed to outside of this class.
     *
     * Any new classes that you create should be part of
     * edu.ucla.cs.cs144 package and their source files should be
     * placed at src/edu/ucla/cs/cs144.
     *
     */
    public SearchResult[] basicSearch(String query, int numResultsToSkip, int numResultsToReturn) {
        SearchResult[] resultstore = new SearchResult[0];
        try 
        {
            // Access the lucene index
            searcher = new IndexSearcher(System.getenv("LUCENE_INDEX"));
            parser = new QueryParser("content", new StandardAnalyzer());

            Query q = parser.parse(query);        
            Hits hits = searcher.search(q);

            // Allocate the results array
            if (numResultsToReturn != 0 && numResultsToReturn - numResultsToSkip < hits.length())
                resultstore = new SearchResult[numResultsToReturn];
            else
                resultstore = new SearchResult[hits.length()];
            
            // Iterators for the hits(matches found)
            // And for resultsStore to return
            Iterator<Hit> it =  hits.iterator();
            int i = 0;
            int resultIndex = 0;
            
            // Following Code will try to find the hits.
            // stores hits in resultstore
            while(it.hasNext() && (numResultsToReturn == 0 || (i < hits.length() && i - numResultsToSkip < numResultsToReturn)))
            {
                Hit hit = it.next();
                if (i > numResultsToSkip - 1) {
                    Document doc = hit.getDocument();
                    resultstore[resultIndex] = new SearchResult(doc.get("id"), doc.get("name"));
                    resultIndex++;
                }

                // Update hit counter
                i++;
            }
        } 
        catch (Exception e) 
        {
            System.out.println(e);    
        }
        
        return resultstore;
    }

    /* Returns the date from the xml files' format (Dec-04-01 04:03:12)
     * converted to MySQL timestamp readable format.
     */
    static String convertDate(String dateString, String oldFormat, String newFormat) {


        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        String out = "";
        try {
            Date d = sdf.parse(dateString);
            sdf.applyPattern(newFormat);
            out = sdf.format(d);
        } catch (Exception e) {
            System.out.println("Could not format date");
        }

        return out;
    }

    // Build the lucene query from the given constraints
    public String buildLuceneQuery(SearchConstraint[] constraints) {
        // Add queries to a list
        Stack<String> fieldQueries = new Stack();

        // Iterate over constraints and push if 
        // on a field that is indexed by lucene
        int numConstraints = constraints.length;
        for(int i = 0; i < numConstraints; i++) {
            // Pull out Lucene indexed constraints
            if(constraints[i].getFieldName().equals(FieldName.ItemName))
                fieldQueries.push("name:" + constraints[i].getValue());
           
            else if(constraints[i].getFieldName().equals(FieldName.Category))
                fieldQueries.push("category:" + constraints[i].getValue());
                    
            else if(constraints[i].getFieldName().equals(FieldName.Description))
                fieldQueries.push("description:" + constraints[i].getValue());
        }

        // Build final query String
        StringBuilder query = new StringBuilder();
        if(!fieldQueries.empty()) {
            query.append(fieldQueries.pop());
            while(!fieldQueries.empty()) {
                String next = fieldQueries.pop();
                query.append(" AND ");
                query.append(next);
            }
        }

        return query.toString();
    }

    public String buildSqlQuery(SearchConstraint[] constraints) {
            
        Stack<String> whereClauses = new Stack();
        // Iterate over constraints and push if
        // on a field that are indexed by lucene
        int numConstraints = constraints.length;
        boolean bidJoin = false;
        for(int i = 0; i < numConstraints; i++) {

            // Pull out MySQL indexed constraints
            if(constraints[i].getFieldName().equals(FieldName.SellerId))
                whereClauses.push("Seller = '" + constraints[i].getValue() + "'");
           
            else if(constraints[i].getFieldName().equals(FieldName.BuyPrice))
                whereClauses.push("Buy_Price = " + constraints[i].getValue());
                    
            else if(constraints[i].getFieldName().equals(FieldName.EndTime))
                whereClauses.push("Ends = \"" + convertDate(constraints[i].getValue(), "MMM-dd-yy HH:mm:ss", "yyyy-MM-dd HH:mm:ss") + "\"");

            else if(constraints[i].getFieldName().equals(FieldName.BidderId)) {
                bidJoin = true;
                whereClauses.push("UserID = '" + constraints[i].getValue() + "'");
            }
                
        }

        StringBuilder query = new StringBuilder();      
        if(!whereClauses.empty()) {
            query.append("SELECT ItemID, Name FROM Item ");
            if (bidJoin)
                query.append("INNER JOIN Bid ON Item.ItemID = Bid.ItemID ");

            query.append("WHERE " + whereClauses.pop() + " ");

            while(!whereClauses.empty()) {
                query.append("AND " + whereClauses.pop() + " ");
            }
        }
        return query.toString();
    }

    public SearchResult[] advancedSearch(SearchConstraint[] constraints, 
        int numResultsToSkip, int numResultsToReturn) {
            
        // Handle Lucene querying
        Map<String, String> luceneResult = new HashMap<String, String>();
        try {
            String luceneQuery = buildLuceneQuery(constraints);

            if(!luceneQuery.isEmpty()) {
                // Access the lucene index 
                // Query Parser has no default field because 
                // query will specify fields.
                searcher = new IndexSearcher(System.getenv("LUCENE_INDEX"));
                parser = new QueryParser("content", new StandardAnalyzer());

                // Execute Query
                Query q = parser.parse(luceneQuery);        
                Hits hits = searcher.search(q);

                Iterator<Hit> it = hits.iterator();

                // Place all the itemIDs a set for quick intersection later
                while(it.hasNext()) {
                    Hit hit = it.next();
                    Document doc = hit.getDocument();
                    luceneResult.put(doc.get("id"), doc.get("name"));
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        
        // Handle SQL querying
        Map<String, String> sqlResult = new HashMap<String, String>();
        Connection conn = null;
        try {
            String sqlQuery = buildSqlQuery(constraints);

            conn = DbManager.getConnection(true);
            Statement stmt = conn.createStatement();

            // Execute query
            ResultSet items = stmt.executeQuery(sqlQuery);

            // If there are results place them in a map
            if(!sqlQuery.isEmpty()) {
                while(items.next()) {
                    sqlResult.put(items.getString("ItemID"), items.getString("Name"));         
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

        // Create the final SearchResult
        // Determine if we must do an intersection 
        Set<String> itemIds;
        Map<String, String> result;
        if(sqlResult.isEmpty()) {
            itemIds = luceneResult.keySet();
            result = luceneResult;
        } else if(luceneResult.isEmpty()) {
            itemIds = sqlResult.keySet();
            result = sqlResult;
        } else {
            // In this case do an intersection of the keys
            itemIds = luceneResult.keySet();
            itemIds.retainAll(sqlResult.keySet());
            result = luceneResult;
        }

        // Add the keys and itemIds to the SearchResult array
        SearchResult[] resultstore = new SearchResult[itemIds.size()];

        Iterator<String> i = itemIds.iterator();
        int resultIndex = 0;

        while(i.hasNext()) {
            String itemId = i.next();
            resultstore[resultIndex] = new SearchResult(itemId, result.get(itemId));
            resultIndex++;
        }

        return resultstore;
    }

    public String getXMLDataForItemId(String itemId) {
        String xmlstore = "";

        Connection conn = null;

        // Create a connection to the database
        try 
        {
            // Connection to db manager
            conn = DbManager.getConnection(true);
            Statement statement = conn.createStatement();

            // Geting the items
            ResultSet result = statement.executeQuery("SELECT * FROM Item "
                                                    + "WHERE Item.ItemID = " + itemId);
                
            result.first();
            // Somethings in it
            if (result.getRow() != 0) 
            {                             
                  
                DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
                DocumentBuilder b          = fac.newDocumentBuilder();
                org.w3c.dom.Document doc   = b.newDocument();

                // root element
                Element root = doc.createElement("Item");
                root.setAttribute("ItemID", itemId);
                doc.appendChild(root);

                Element element_name = doc.createElement("Name");
                element_name.appendChild(doc.createTextNode(replacespecial(result.getString("Name"))));
                root.appendChild(element_name);

                
                // Build Category Elements
                // Get the Categories
                Statement catstatement = conn.createStatement();
                ResultSet catresult = catstatement.executeQuery("SELECT Category "
                                                              + "FROM Category,Item_Category "
                                                              + "WHERE Item_Category.ItemID = " + itemId + " "
                                                              + "AND Item_Category.CategoryID = Category.CategoryID");

                Element category_element;
                while (catresult.next()) {
                    category_element = doc.createElement("Category");
                    category_element.appendChild(doc.createTextNode(replacespecial(catresult.getString("Category"))));
                    root.appendChild(category_element);
                }

                catresult.close();
                catstatement.close();

                // Build Item price elements
                Element currently_element = doc.createElement("Currently");
                currently_element.appendChild(doc.createTextNode("$" + result.getString("Currently")));
                root.appendChild(currently_element);
    
                if (result.getString("Buy_Price") != null) {
                    Element buyprice_element = doc.createElement("Buy_Price");
                    buyprice_element.appendChild(doc.createTextNode("$" + result.getString("Buy_Price")));
                    root.appendChild(buyprice_element);
                }

                Element start_element = doc.createElement("First_Bid");
                start_element.appendChild(doc.createTextNode("$" + result.getString("First_Bid")));
                root.appendChild(start_element);

                // num bids
                Element numberbids_elements = doc.createElement("Number_of_Bids");
                numberbids_elements.appendChild(doc.createTextNode(result.getString("Number_of_Bids")));
                root.appendChild(numberbids_elements);

                // Build Bid Elements
                Statement bidstatement = conn.createStatement();
                ResultSet bidresult = bidstatement.executeQuery("SELECT * " 
                                                              + "FROM Bid, User "  
                                                              + "WHERE Bid.ItemId = " + itemId + " "
                                                              + "AND Bid.UserID = User.UserID");

                Element bids_element = doc.createElement("Bids");

                while (bidresult.next()) {
                    try {
                        Element bid_element = doc.createElement("Bid");
                        Element bidder_element = doc.createElement("Bidder");
                        bidder_element.setAttribute("UserID", replacespecial(bidresult.getString("UserID")));
                        bidder_element.setAttribute("Rating", bidresult.getString("Rating"));

                        // Add Location and Country elements if they aren't NULL
                        if (!bidresult.getString("Location").equals("")) {
                            Element location_element = doc.createElement("Location");
                            location_element.appendChild(doc.createTextNode((replacespecial(bidresult.getString("Location")))));
                            bidder_element.appendChild(location_element);
                        }
                        if (!bidresult.getString("Country").equals("")) {
                            Element country_element = doc.createElement("Country");
                            country_element.appendChild(doc.createTextNode((replacespecial(bidresult.getString("Country")))));
                            bidder_element.appendChild(country_element);
                        }
                        bid_element.appendChild(bidder_element);

                        // time
                        Element time_element = doc.createElement("Time");
                        time_element.appendChild(doc.createTextNode(convertDate(bidresult.getString("Time"), "yyyy-MM-dd HH:mm:ss", "MMM-dd-yy HH:mm:ss")));
                        bid_element.appendChild(time_element);

                        // amount
                        Element amount_element = doc.createElement("Amount"); 
                        amount_element.appendChild(doc.createTextNode(bidresult.getString("Amount")));
                        bid_element.appendChild(amount_element);

                        bids_element.appendChild(bid_element);
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                }

                root.appendChild(bids_element);

                bidresult.close();
                bidstatement.close();
                
                // Get the Seller data
                Statement sellstatement = conn.createStatement();
                ResultSet sellres = sellstatement.executeQuery("SELECT UserID, Rating, Location, Country "
                                                             + "FROM Item, User " 
                                                             + "WHERE Item.ItemID" + " = " + itemId + " "
                                                             + "AND Item.Seller = User.UserID");
                sellres.first();
             
                Element location_element = doc.createElement("Location");
                location_element.appendChild(doc.createTextNode((replacespecial(sellres.getString("Location")))));
                root.appendChild(location_element);

                // country
                Element country_element = doc.createElement("Country");
                country_element.appendChild(doc.createTextNode((replacespecial(sellres.getString("Country")))));
                root.appendChild(country_element);

                // started
                Element started_elem = doc.createElement("Started");
                started_elem.appendChild(doc.createTextNode(convertDate(result.getString("Started"), "yyyy-MM-dd HH:mm:ss", "MMM-dd-yy HH:mm:ss")));
                root.appendChild(started_elem);

                // ends
                Element ends_element = doc.createElement("Ends");
                ends_element.appendChild(doc.createTextNode(convertDate(result.getString("Ends"), "yyyy-MM-dd HH:mm:ss", "MMM-dd-yy HH:mm:ss")));
                root.appendChild(ends_element);
                // seller
                Element sellerElem = doc.createElement("Seller");
                sellerElem.setAttribute("UserID", (replacespecial(sellres.getString("UserID"))));
                sellerElem.setAttribute("Rating", sellres.getString("Rating"));
                root.appendChild(sellerElem);

                // description
                Element description_element = doc.createElement("Description");
                description_element.appendChild(doc.createTextNode(replacespecial(result.getString("Description"))));
                root.appendChild(description_element);

                sellres.close();
                sellstatement.close();

                // Write the XML
                TransformerFactory newfactory = TransformerFactory.newInstance();
                Transformer transform = newfactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult res = new StreamResult(writer);
                transform.setOutputProperty(OutputKeys.INDENT, "yes");
                transform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transform.transform(source, res);
                xmlstore = writer.toString();
                xmlstore = replacespecial1(xmlstore);
            }

            result.close();
            statement.close();

            conn.close();

        } catch (SQLException e) {
            System.out.println(e);            
        } catch (ParserConfigurationException e) {
            System.out.println("oops");
        } catch (TransformerException e) {
            System.out.println("oops");
        }

        return xmlstore;
    
    }
       private String replacespecial(String s) {
       	return s.replaceAll("\"", "thisisaquotethatwehavetohandle")
                                .replaceAll("\'", "thisisanaposweneedtohandle")
                                
                                ;
                
              
        }
         private String replacespecial1(String s) {
       	return s.replaceAll( "thisisaquotethatwehavetohandle", "&quot;")
                                .replaceAll( "thisisanaposweneedtohandle","&apos;")
                                
                               ;
                
              
        }
    
    public String echo(String message) {
        return message;
    }
}
