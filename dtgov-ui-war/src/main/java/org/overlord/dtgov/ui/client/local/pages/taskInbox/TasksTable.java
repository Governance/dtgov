/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.ui.client.local.pages.taskInbox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.dtgov.ui.client.local.pages.TaskDetailsPage;
import org.overlord.dtgov.ui.client.shared.beans.TaskSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedWidgetTable;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of tasks.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class TasksTable extends TemplatedWidgetTable {

    @Inject
    protected TransitionAnchorFactory<TaskDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public TasksTable() {
    }

    /**
     * Adds a single row to the table.
     * @param taskSummaryBean
     */
    public void addRow(final TaskSummaryBean taskSummaryBean) {
        int rowIdx = this.rowElements.size();
        DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy");

        Anchor name = toDetailsPageLinkFactory.get("id", taskSummaryBean.getId());
        name.setText(taskSummaryBean.getName());
        InlineLabel priority = new InlineLabel(String.valueOf(taskSummaryBean.getPriority()));
        InlineLabel owner = new InlineLabel(taskSummaryBean.getOwner());
        InlineLabel status = new InlineLabel(taskSummaryBean.getStatus());
        InlineLabel dueOn = new InlineLabel(taskSummaryBean.getDueDate() != null ? format.format(taskSummaryBean.getDueDate()) : "");

        add(rowIdx, 0, name);
        add(rowIdx, 1, priority);
        add(rowIdx, 2, owner);
        add(rowIdx, 3, status);
        add(rowIdx, 4, dueOn);
    }

}
