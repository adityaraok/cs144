package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import edu.ucla.cs.cs144.*;

public class PaymentServlet extends HttpServlet implements Servlet {
	Item item;
	public PaymentServlet() 
	{}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (session.isNew()==true) 
		{
			//Bad session, set valid to false
			request.setAttribute("valid", "false");
			session.invalidate();
			//invalidate the session if it is new
		} else {
			String itemid = (String) request.getParameter("id");
			if (itemid != null) {
				//This means session is valid
				request.setAttribute("valid", "true");

				//Pass true to the next page.
				item = (Item) session.getAttribute(itemid);
				request.setAttribute("item", item);

				session.setAttribute("itemid", item);
			} else {
				//This means request isn't valid
				request.setAttribute("valid", "false");
			}

		}
		// send control to the purchase.jsp page with the request and response attributes
		request.getRequestDispatcher("./purchase.jsp").forward(request, response);
	}
}
