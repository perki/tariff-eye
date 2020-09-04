<%@ include file="BCControlerDefinition.jsp" %> 
<HTML>
<HEAD><%= base_href() %><HEAD>
<BODY BGCOLOR=<%= color_bg() %>>
text tex"t text" 2"" 1\ 2\\<BR>
<%
	System.out.println("Oupla") ;
	p("Zobi la mouche n'a pas de couche"); 
	String a = "Salut ma poule";
	int b = 0;
	boolean c = true;
%> 
<HR>
	<%= a %><BR>
	<%= b %><BR>
	<%= c %><BR>
</HR>

<% 
	if(paramsExists("BOBBY")) { 
		%><BR><B>BOBBY EXISTS : </B><%= params("BOBBY")%>
	<%;
	} else {
		%><BR><B>NO BOBBY :(</B><% 
	} 
%>
<HR>
<FORM ACTION="" METHOD="GET">
Set Bobby:<INPUT TYPE="TEXT" NAME="BOBBY"><INPUT TYPE=SUBMIT>
</FORM>
<HR>
<%
	parse("testCompo.jsp");
%>
<HR>
<%
	debug_print_params();
%>
<HR>
<A HREF="<%= ACT %>Welcome.jsp">Welcome</A>
<HR>
</HTML>
<!--
 $Id: tests.jsp,v 1.1 2006/12/03 12:48:43 perki Exp $

 $Log: tests.jsp,v $
 Revision 1.1  2006/12/03 12:48:43  perki
 First commit on sourceforge

 Revision 1.7  2004/09/24 10:08:27  plegris
 *** empty log message ***

 Revision 1.6  2004/09/23 16:29:03  plegris
 Pfuuuu--- got the web interfac to work

 Revision 1.5  2004/06/16 09:58:27  plegris
 *** empty log message ***

 Revision 1.4  2004/06/07 15:51:54  plegris
 aiye aye baby

 Revision 1.3  2004/06/07 15:11:17  plegris
 *** empty log message ***

 -->