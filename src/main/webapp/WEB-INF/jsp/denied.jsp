<%-- 
    Document   : denied
    Created on : Sep 3, 2013, 7:45:15 AM
    Author     : Aaron
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
  request.setAttribute("code", response.getStatus());
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Request Denied</title>
    </head>
    <body>
        <div>Your request was denied.  It may be that the requested page is unavailable to you; or it may be that your request token has expired.</div>
        <div style="padding-top:10px;">This can happen if you remain inactive on a page for a long period of time, or if you navigate using bookmarks or restore a saved browser session.</div>
    </body>
</html>
