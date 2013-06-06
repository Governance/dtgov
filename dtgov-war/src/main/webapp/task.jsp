<%@ page import="org.kie.api.task.model.TaskSummary" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Task management</title>
</head>
<body>
<% String user = request.getParameter("user"); %>
<p><%= user %>'s Task</p>
<table border="1">
<tr>
<th>Task Id</th>
<th>Task Name</th>
<th>Priority</th>
<th>Owner</th>
<th>Status</th>
<th>Due On</th>
<th>ProcessInstance Id</th>
<th>Action</th>
</tr>
<% for (TaskSummary task : (List<TaskSummary>)request.getAttribute("taskList")) { %>
<tr>
<td><a href="rest/tasks/get/<%= task.getId() %>"><%= task.getId() %></a></td>
<td><%= task.getName() %></td>
<td><%= task.getPriority() %></td>
<td><%= task.getActualOwner() %></td>
<td><%= task.getStatus() %></td>
<td><%= task.getExpirationTime() %></td>
<td><%= task.getProcessInstanceId() %></td>
<td>
  <a href="task?user=<%= user %>&taskId=<%= task.getId() %>&cmd=approve">Approve</a> - 
  <a href="rest/tasks/claim/<%= task.getId() %>">Claim</a> | 
  <a href="rest/tasks/release/<%= task.getId() %>">Release</a> -
  <a href="rest/tasks/start/<%= task.getId() %>">Start</a> |
  <a href="rest/tasks/stop/<%= task.getId() %>">Stop</a> -
  <a href="rest/tasks/complete/<%= task.getId() %>">Complete</a> |
  <a href="rest/tasks/fail/<%= task.getId() %>">Fail</a>
</td>
</tr>
<% } %>
</table>
</body>
</html>