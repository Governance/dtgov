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
package org.overlord.dtgov.ui.client.local.pages.targets;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.events.DeleteItemEvent;
import org.overlord.dtgov.ui.client.local.events.DeleteItemEvent.Handler;
import org.overlord.dtgov.ui.client.local.events.DeleteItemEvent.HasDeleteItemHandlers;
import org.overlord.dtgov.ui.client.local.pages.TargetPage;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Custom GWT widget table that stores the Targets into a gwt row components.The
 * row contains 3 items, name, description and actions.
 *
 * @author David Virgil Naranjo
 */
@Dependent
public class TargetsTable extends TemplatedWidgetTable implements HasValue<List<TargetSummaryBean>>, HasDeleteItemHandlers {

    public static final String PREFIX_I18_TARGET_TYPE = "targets.type."; //$NON-NLS-1$

    /** The _i18n. */
    @Inject
    private ClientMessages i18n;

    /** The _edit query link factory. */
    @Inject
    private TransitionAnchorFactory<TargetPage> _editTargetLinkFactory;

    private List<TargetSummaryBean> targets;


    /**
     * Constructor.
     */
    public TargetsTable() {
    }

    /**
     * Adds a single row to the table.
     *
     * @param target
     *            the target
     */
    public void addRow(final TargetSummaryBean target) {
        int rowIdx = this.rowElements.size();

        //Anchor name = editQueryLinkFactory.get("uuid", deploymentSummaryBean.getUuid()); //$NON-NLS-1$
        Anchor name_link = _editTargetLinkFactory.get("uuid", target.getUuid()); //$NON-NLS-1$
        name_link.setText(target.getName());
        String type_text = ""; //$NON-NLS-1$
        if (target.getType() != null) {
            type_text = i18n.format(PREFIX_I18_TARGET_TYPE + target.getType().getValue());
        }
        InlineLabel type = new InlineLabel(type_text);

        InlineLabel description = new InlineLabel(target.getDescription());

        FlowPanel actions = new FlowPanel();
        Anchor editTarget = _editTargetLinkFactory.get("uuid", target.getUuid()); //$NON-NLS-1$

        InlineLabel editAction = new InlineLabel();
        editAction.setStyleName("target-icon", true); //$NON-NLS-1$
        editAction.setStyleName("target-edit-icon", true); //$NON-NLS-1$
        editAction.setStyleName("firstAction", true); //$NON-NLS-1$
        editAction.setTitle(i18n.format("edit")); //$NON-NLS-1$

        editTarget.getElement().appendChild(editAction.getElement());
        actions.add(editTarget);

        InlineLabel deleteAction = new InlineLabel();
        deleteAction.setStyleName("target-icon", true); //$NON-NLS-1$
        deleteAction.setStyleName("target-delete-icon", true); //$NON-NLS-1$
        deleteAction.setTitle(i18n.format("delete")); //$NON-NLS-1$
        actions.add(deleteAction);

        deleteAction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DeleteItemEvent.fire(TargetsTable.this, target);
            }
        });
        add(rowIdx, 0, name_link);
        add(rowIdx, 1, type);
        add(rowIdx, 2, description);
        Element row = add(rowIdx, 3, actions);
        setStyleName(row, "actions", true); //$NON-NLS-1$
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<TargetSummaryBean>> handler) {
        return super.addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see org.overlord.dtgov.ui.client.local.events.DeleteItemEvent.HasDeleteItemHandlers#addDeleteItemHandler(org.overlord.dtgov.ui.client.local.events.DeleteItemEvent.Handler)
     */
    @Override
    public HandlerRegistration addDeleteItemHandler(Handler handler) {
        return super.addHandler(handler, DeleteItemEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<TargetSummaryBean> getValue() {
        return targets;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<TargetSummaryBean> value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(List<TargetSummaryBean> value, boolean fireEvents) {
        targets = value;
        clear();
        refresh();
    }

    /**
     * Refresh the display with the current value.
     */
    public void refresh() {
        if (targets != null && !targets.isEmpty()) {
            for (TargetSummaryBean targetSummaryBean : targets) {
                addRow(targetSummaryBean);
            }
        }
    }

}
