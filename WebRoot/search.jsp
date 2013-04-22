<%@ page import="java.util.List"%>
<%@ page import="lucene.SearchController"%>
<%@ page import="lucene.lucene"%>
<%@ page import="lucene.ExtractText"%>

<FORM id=searchFrom action=sample.dw.paper.lucene/SearchController>
	<TABLE>
		<TBODY>
			<TR>
				<TD colspan="3"><INPUT name=searchWord id=searchWord type=text
					size="40"> <INPUT id=doSearch type=submit value=search>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
</FORM>
<table class = "result">
	<tbody>
		<%
			List searchResult = (List)request.getAttribute("searchResult");
			int resultCount = 0;
			if(null != searchResult)
			{
				resultCount  =searchResult.size();
			}
			for(int i=0;i<resultCount;i++)
			{
				String title = searchResult.get(i).toString();
		 %>
		 <tr>
		 	<td class="title"><h3><a href><%=title %>></a></h3></td>
		 </tr>
		 <tr><td><hr /></td></tr>
		 <%
		 	} 
		 %>
	</tbody>
</table>