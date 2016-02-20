package edu.ucla.cs.cs144;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import java.io.IOException;

public class Indexer {

    private IndexWriter indexWriter = null;    

    /** Creates a new instance of Indexer */
    public Indexer() 
    {

    }

    /**
     * Returns the indexWriter for the class.
     * Creates a new one if necessary.
     */
    public IndexWriter getIndexWriter(boolean create)  {
        if (indexWriter == null) {
            try {
                indexWriter = new IndexWriter(System.getenv("LUCENE_INDEX"), new StandardAnalyzer(), create); 
            } catch(IOException e) {
                System.out.println(e);
            } 
        }
        return indexWriter;
    }

    /**
     * Closes the indexWriter if it was opened.
     */
    public void closeIndexWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch(IOException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Adds an index for a single item.
     */
     
    public void indexItem(int id, String name, String description, String categories) throws IOException  {
        IndexWriter writer = getIndexWriter(false);
        
        Document doc = new Document();

        doc.add(new Field("id", String.valueOf(id), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("name", name, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("category", categories, Field.Store.NO, Field.Index.TOKENIZED));
        doc.add(new Field("description", description, Field.Store.NO, Field.Index.TOKENIZED));
        String fullSearchableText = String.valueOf(id) + " " + name +  " " + categories + " " + description;
        doc.add(new Field("content", fullSearchableText, Field.Store.NO, Field.Index.TOKENIZED));
        writer.addDocument(doc);
    }
 
    /**
     * Removes current indexes and rebuilds indexes on items.
     */
    public void rebuildIndexes() throws SQLException {
        try
        {
        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
        try {
            conn = DbManager.getConnection(true);
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        /*
         * Add your code here to retrieve Items using the connection
         * and add corresponding entries to your Lucene inverted indexes.
             *
             * You will have to use JDBC API to retrieve MySQL data from Java.
             * Read our tutorial on JDBC if you do not know how to use JDBC.
             *
             * You will also have to use Lucene IndexWriter and Document
             * classes to create an index and populate it with Items data.
             * Read our tutorial on Lucene as well if you don't know how.
             *
             * As part of this development, you may want to add 
             * new methods and create additional Java classes. 
             * If you create new classes, make sure that
             * the classes become part of "edu.ucla.cs.cs144" package
             * and place your class source files at src/edu/ucla/cs/cs144/.
         * 
         */

        // Initialize the index writer to overwrite the indexes.
        getIndexWriter(true);

        // Query the database for the items to build the index on.
        // Create a statement to query DB
        Statement stmt = conn.createStatement();

        String sql = "SELECT Item.ItemID, Item.Name, Item.Description, C.Categories "
                   + "FROM (SELECT ItemID, group_concat(Category.Category SEPARATOR ' ') AS Categories "
                               + "FROM Item_Category  "
                               + "INNER JOIN Category "
                               + "ON Item_Category.CategoryID = Category.CategoryID "
                               + "GROUP BY ItemID) AS C "
                   + "INNER JOIN Item "
                   + "ON Item.ItemID = C.ItemID ";

        // Fetch all the items
        ResultSet items = stmt.executeQuery(sql);

        // Add an index on each item
        while(items.next()) {
            indexItem(items.getInt("ItemID"), items.getString("Name"),
                      items.getString("Description"), items.getString("Categories"));
        }

        // Close the index writer
        closeIndexWriter();

        // close the database connection
        try {
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    catch(IOException e)
    {
        System.out.print("ere");
    }
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        try {
            idx.rebuildIndexes();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }   
}
