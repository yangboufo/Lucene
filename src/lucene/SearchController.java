package lucene;

import java.io.*;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

public class SearchController extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
		String searchWord = request.getParameter("searchWord");
		lucene searchManager = new lucene(searchWord);
		searchManager.search();
		List searchResult = null;
		searchResult = searchManager.searchResult;
		RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
        request.setAttribute("searchResult",searchResult);
        dispatcher.forward(request, response);
    }
		    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
    	/*
    	  response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          out.println("<html>");
          out.println("<head>");
          out.println("<title>Hello!</title>");
          out.println("</head>");
          out.println("<body>");
          out.println("<h1>Yang Bo!</h1>");
          out.println("</body>");
          out.println("</html>");
          */
         doPost(request,response);
    }
}
