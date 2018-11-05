
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>File Upload Example in JSP and Servlet - Java web application</title>
    </head>
 
    <body> 
        <div>
        	<form action="upload" method="post" enctype="multipart/form-data">
                <br> <br>
                <input type="submit" value="Generate key"/>
            </form>
            <h3> Choose File to Upload in Server </h3>
            <form action="upload" method="post" enctype="multipart/form-data">
                File: <input type="file" name="file" />
                <br> <br>
                <input type="submit" value="upload" />
            </form> 
            <br> <br>          
            <form action="decrypt" method="post" enctype="multipart/form-data">
            	File: <input type="file" name="file" />
                <br> <br>
                <input type="submit" value="decrypt" />
            </form>
        </div>
      
    </body>
</html>
