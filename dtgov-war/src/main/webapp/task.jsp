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
<td><a href="task?user=<%= user %>&taskId=<%= task.getId() %>&cmd=approve">Approve</a></td>
</tr>
<% } %>
</table>
</body>
</html>