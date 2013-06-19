<html>
<head>
<title>Start a process</title>
</head>
<body>
<p>Select a recipient</p>
<form method="post" action="process">
<select name="recipient"><option selected="selected">krisv</option></select>
<select name="processId">
	<option>com.sample.rewards-basic</option>
	<option selected="selected">overlord.demo.SimpleReleaseProcess</option>
</select>
UUID: <input type="text" name="uuid"><br>
<input type="submit" value="Start a process">
</form>
</body>
</html>