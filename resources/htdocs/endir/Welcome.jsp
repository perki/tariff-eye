
<%@ include file="../BCControlerDefinition.jsp" %> 
<%
if (paramsExists("menu_file")) {
	if (params("menu_file").equals("open")) {
		menu_file_open(params("app_type"));
	}
}
if (paramsExists("show_help")) {
	showHelp();
}
if (paramsExists("open_url")) {
	open_url_in_browser(params("open_url"));
}
%>

<HTML><HEAD>
<%= base_href() %>
<link rel="stylesheet" href="StyleSheet.css" type="text/css">
<style type="text/css"> 
@import url("StyleSheet.css"); 
</style>
</HEAD> 
<BODY BGCOLOR=<%= color_bg() %> >

<!--
// Do not remove this table which is here for spacing reasons...
// have the content a little far from the edges
-->

<table width=100%>
<tr><td>

<table width="100%" cellspacing="3">
<tr><td bgcolor="#FFFFFF" align="center"><img src="images/WEB_Banner_Small_PNG.png"></td></tr>
</table>
<table width="100%"  >
	<tr><td colspan="3" bgcolor="#D2D2D2"><font color="#000000"><strong>Tariff simulation</strong></font>
	</td></tr>
    <tr align="left" valign="middle">
      <td width="26%"><a href="<%= ACTD %><%= DIRECT_menu_file_open %>?<%= SIMULATOR_NEW %>"><strong>New portfolio...</strong></a> </td>
      <td valign="middle" width="3%"><a href="<%= ACTD %><%= DIRECT_menu_file_open %>?<%= SIMULATOR_NEW %>"><img src="images/run.gif" width="16" height="16" border="0"></a></td>
      <td width="71%"><font size="-2" color="#000033">Choose a Tariff from the <i>tarification library</i> and create an empty portfolio to simulate your costs.</font></td>
	</tr>
    <tr align="left" valign="middle">
      <td><a href="<%= ACTD %><%= DIRECT_menu_file_open %>?<%= SIMULATOR_OPEN %>"><strong>Open portfolio...</strong></a> </td>
      <td valign="middle"><a href="<%= ACTD %><%= DIRECT_menu_file_open %>?<%= SIMULATOR_OPEN %>"><img src="images/run.gif" width="16" height="16" border="0"></a></td>
   	  <td><font size="-2" color="#000033">Open an user defined portfolio, from your own files.</font></td>
    </tr>
    
	<tr>
		<td colspan="3" bgcolor="#D2D2D2"><font color="#000000">
	<strong>Support</strong></font>
		</td>
	</tr>
    <tr align="left" valign="middle">
    	  <td>
    	  <a href="<%= ACTD %><%= DIRECT_showHelp %>"><strong>TariffEye Help...</strong></a> 
    	  </td>
    	  <td>
    	  <a href="<%= ACTD %><%= DIRECT_showHelp %>"><img src="images/run.gif" width="16" height="16" border="0"></a>
    	  </td>
		  <td><font size="-2" color="#000033">Browse the helper system to learn more about Tariff Eye features.</font></td>
    </tr>
   
  <TR>
  	<td colspan="3" bgcolor="#D2D2D2"><font color="#000000">
  <strong>TariffEye.com</strong></font>
		</td>
	</tr>
    <tr align="left" valign="middle">
    	  <td>
    	  <A HREF="<%= ACT %>Welcome.jsp?open_url=http://www.TariffEye.com/te"><strong>WebSite...</strong></A>
    	  </td>
    	  <td>
    	  <A HREF="<%= ACT %>Welcome.jsp?open_url=http://www.TariffEye.com/te"><img src="images/run.gif" width="16" height="16" border="0"></a>
    	  </td>
		  <td><font size="-2" color="#000033">Visit TariffEye.com for news and updates.</font></td>
    </tr>
 
  </table>	  
  
  <br>
<!--
<hr>
<A HREF="<%= ACT %>tests.jsp">tests</A>
<HR>
<A HREF="http://google.com">Google</A>
<HR>
debug:
<%
debug_print_params(); 
%>
-->

</td></tr>
</table>
</BODY>
</HTML>

<!--
 $Id: Welcome.jsp,v 1.1 2006/12/03 12:48:44 perki Exp $

 Revision 1.4  2004/06/16 09:58:27  plegris
 *** empty log message ***

 Revision 1.3  2004/06/07 15:12:18  plegris
 *** empty log message ***

 -->
