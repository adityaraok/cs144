import java.awt.Rectangle;
import java.io.*;
import java.security.*;

public class ComputeSHA {

    public static String getHash(String filename) throws Exception
    {
        // Open a filestream to stream data from the file.
        InputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];

        // Create a MessageDigest object to handle hashing.
        MessageDigest hasher = MessageDigest.getInstance("SHA-1");

        // Read bytes from the file and pass them to the MessageDigest.
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0)
            {
                hasher.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
       
        // Close the file and tell the hasher to compute.      
        fis.close(); 
        byte[] b = hasher.digest();

        // Convert the hashed bytes to hex values and append to a StringBuffer for efficiency.
        StringBuffer result = new StringBuffer();
        for (int i=0; i < b.length; i++)
        {
           result.append(Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 ));
        }

        // Return the final hex string of the hash.
        return result.toString();
    }

    public static void main(String args[])
    {
        try 
        {
            // Get the file name from the command line.
            String inFile = "";
            if (args.length == 1)
            {
                inFile = args[0];
            }
            else
            {
               System.err.println("Invalid arguments count:" + args.length);
            }

            // Get the SHA-1 hash of the file's content and print it.
            System.out.println(getHash(inFile));        
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
    }
}
