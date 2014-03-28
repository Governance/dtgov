/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.dtgov.ui.client.local.pages.workflowQuery;

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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.commons.gwt.client.local.widgets.SortableTemplatedWidgetTable;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.pages.WorkflowQueryPage;
import org.overlord.dtgov.ui.client.shared.beans.Constants;
import org.overlord.dtgov.ui.client.shared.beans.WorkflowQuerySummaryBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

// TODO: Auto-generated Javadoc
/**
 * A table of workflow queries.
 * 
 * @author dvirgiln@redhat.com
 */
@Dependent
public class WorkflowQueryTable extends SortableTemplatedWidgetTable {

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _edit query link factory. */
    @Inject
    private TransitionAnchorFactory<WorkflowQueryPage> _editQueryLinkFactory;

    /** The _delete workflow query dialog. */
    private Instance<DeleteWorkflowQueryDialog> _deleteWorkflowQueryDialog;

    /**
     * Constructor.
     */
    public WorkflowQueryTable() {
    }

    /**
     * Sets the delete workflow query dialog.
     * 
     * @param deleteWorkflowQueryDialog
     *            the new delete workflow query dialog
     */
    public void setDeleteWorkflowQueryDialog(Instance<DeleteWorkflowQueryDialog> deleteWorkflowQueryDialog) {
        this._deleteWorkflowQueryDialog = deleteWorkflowQueryDialog;
    }

    /**
     * Gets the default sort column.
     * 
     * @return the default sort column
     * @see org.overlord.sramp.ui.client.local.widgets.common.SortableTemplatedWidgetTable#getDefaultSortColumn()
     */
    @Override
    public SortColumn getDefaultSortColumn() {
        SortColumn sortColumn = new SortColumn();
        sortColumn.columnId = Constants.SORT_COLID_WORKFLOW_NAME;
        sortColumn.ascending = false;
        return sortColumn;
    }

    /**
     * Configure column sorting.
     * 
     * @see org.overlord.monitoring.ui.client.local.widgets.common.SortableTemplatedWidgetTable#configureColumnSorting()
     */
    @Override
    protected void configureColumnSorting() {
        setColumnSortable(0, Constants.SORT_COLID_WORKFLOW_NAME);
        setColumnSortable(1, Constants.SORT_COLID_WORKFLOW_TYPE);
        setColumnSortable(2, Constants.SORT_COLID_WORKFLOW_QUERY);
        sortBy(Constants.SORT_COLID_WORKFLOW_NAME, false);
    }

    /**
     * Adds a single row to the table.
     * 
     * @param workFlowQuerySummaryBean
     *            the work flow query summary bean
     */
    public void addRow(final WorkflowQuerySummaryBean workFlowQuerySummaryBean) {
        int rowIdx = this.rowElements.size();

        //Anchor name = editQueryLinkFactory.get("uuid", deploymentSummaryBean.getUuid()); //$NON-NLS-1$
        Anchor name_link = _editQueryLinkFactory.get("uuid", workFlowQuerySummaryBean.getUuid()); //$NON-NLS-1$
        name_link.setText(workFlowQuerySummaryBean.getName());

        InlineLabel query = new InlineLabel(workFlowQuerySummaryBean.getQuery());

        InlineLabel workflow = new InlineLabel(workFlowQuerySummaryBean.getWorkflow());

        FlowPanel actions = new FlowPanel();
        Anchor editQuery = _editQueryLinkFactory.get("uuid", workFlowQuerySummaryBean.getUuid());

        InlineLabel editAction = new InlineLabel();
        editAction.setStyleName("workflow-icon", true);
        editAction.setStyleName("workflow-edit-icon", true);
        editAction.setStyleName("firstAction", true);

        editQuery.getElement().appendChild(editAction.getElement());
        actions.add(editQuery);

        InlineLabel deleteAction = new InlineLabel();
        deleteAction.setStyleName("workflow-icon", true);
        deleteAction.setStyleName("workflow-delete-icon", true);
        actions.add(deleteAction);

        deleteAction.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DeleteWorkflowQueryDialog dialog = _deleteWorkflowQueryDialog.get();
                dialog.setWorkflowQuery(workFlowQuerySummaryBean);
                dialog.show();
            }
        });
        add(rowIdx, 0, name_link);
        add(rowIdx, 1, workflow);
        add(rowIdx, 2, query);
        Element row = add(rowIdx, 3, actions);
        setStyleName(row, "actions", true);
        // add(rowIdx, 2, initiatedOn);
    }

    /**
     * Gets the i18n.
     * 
     * @return the i18n
     */
    public ClientMessages getI18n() {
        return _i18n;
    }

    /**
     * Sets the i18n.
     * 
     * @param i18n
     *            the new i18n
     */
    public void setI18n(ClientMessages i18n) {
        this._i18n = i18n;
    }

    /**
     * Gets the edits the query link factory.
     * 
     * @return the edits the query link factory
     */
    public TransitionAnchorFactory<WorkflowQueryPage> getEditQueryLinkFactory() {
        return _editQueryLinkFactory;
    }

    /**
     * Sets the edits the query link factory.
     * 
     * @param editQueryLinkFactory
     *            the new edits the query link factory
     */
    public void setEditQueryLinkFactory(TransitionAnchorFactory<WorkflowQueryPage> editQueryLinkFactory) {
        this._editQueryLinkFactory = editQueryLinkFactory;
    }

    /**
     * Gets the delete workflow query dialog.
     * 
     * @return the delete workflow query dialog
     */
    public Instance<DeleteWorkflowQueryDialog> getDeleteWorkflowQueryDialog() {
        return _deleteWorkflowQueryDialog;
    }

}
