package edu.ucla.cs.cs144;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.io.InputStream;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle the request and pick out the query
        String query = request.getQueryString();

        // Issue the GET request to google suggest
        //String url = URLEncoder.encode("http://google.com/complete/search?output=toolbar&q="+query);
        URL googleUrl = new URL("http://google.com/complete/search?output=toolbar&"+query);
        HttpURLConnection googleRequest = (HttpURLConnection) googleUrl.openConnection();

        if (googleRequest.getResponseCode() == HttpURLConnection.HTTP_OK) {

            response.setContentType("text/xml");

            // Return google's XML response by writing it
            InputStream input = googleRequest.getInputStream();

            byte[] buffer = new byte[1024]; // Adjust if you want
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1)
            {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            
            // Close the output
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }        
    }
}
