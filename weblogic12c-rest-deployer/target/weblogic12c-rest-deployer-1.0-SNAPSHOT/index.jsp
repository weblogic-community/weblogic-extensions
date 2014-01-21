<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WebLogic REST Deployer</title>
    </head>
    <body>
        <h1>WebLogic REST Deployer</h1>
        <form action="rest/deployer" method="post" enctype="multipart/form-data">
            <p>
                Select your deployable artifact (.war, .ear, .jar, etc): <input type="file" name="file" size="45" />
            </p>
            <p>
                Enter the command line for weblogic.Deployer: <input type="text" name="args" size="90" /> <br/>
                * remember to use the same filename
            </p>
            <p>
                For more information, read the <a href="http://docs.oracle.com/middleware/1212/wls/DEPGD/wldeployer.htm">Deployer Command-Line Reference</a>
            </p>
            <input type="submit" value="Deploy" />
        </form>
    </body>
</html>