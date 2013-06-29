<html>
<head>
<title>Start a process for testing and debugging purposes only</title>
</head>
<body>
<p><%= request.getAttribute("message") == null ? "" : request.getAttribute("message") %></p>
<p>Select a process</p>
<form method="post" action="process">
<select name="processId">
	<option selected="selected">overlord.demo.SimpleReleaseProcess</option>
</select>
UUID: <input type="text" name="uuid"><br>
<input type="submit" value="Start a process">
</form>
</body>
</html>