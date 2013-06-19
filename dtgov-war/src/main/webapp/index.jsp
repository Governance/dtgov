<html>
<head>
<title>Workflow Processes</title>
</head>
<body>
<p>Rewards Basic example</p>
<p><%= request.getAttribute("message") == null ? "" : request.getAttribute("message") %></p>
<ul>
<li><a href="startProcess.jsp">Start Reward Process</a></li>
<li><a href="task?user=eric&cmd=list">Eric's Task</a></li>
<li><a href="task?user=kurt&cmd=list">Kurt's Task</a></li>
</ul>
</body>
</html>