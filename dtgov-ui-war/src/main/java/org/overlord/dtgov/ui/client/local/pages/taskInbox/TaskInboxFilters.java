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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.shared.beans.TaskInboxFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.TaskOwnerEnum;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.DateBox;
import org.overlord.sramp.ui.client.local.widgets.common.RadioButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The tasks filtersPanel sidebar.  Whenever the user changes any of the settings in
 * the filter sidebar, a ValueChangeEvent will be fired.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/taskInbox.html#tasks-filter-sidebar")
@Dependent
public class TaskInboxFilters extends Composite implements HasValue<TaskInboxFilterBean> {

    private TaskInboxFilterBean currentState = new TaskInboxFilterBean();

    // Owner
    @Inject @DataField
    protected RadioButton ownerAny;
    @Inject @DataField
    protected RadioButton ownerMine;
    @Inject @DataField
    protected RadioButton ownerActive;
    @Inject @DataField
    protected RadioButton ownerGroup;

    // Create By
    @Inject @DataField
    protected TextBox priority;

    // Date Created
    @Inject @DataField
    protected DateBox dateDueFrom;
    @Inject @DataField
    protected DateBox dateDueTo;

    @Inject @DataField
    protected Button clearFilters;

    /**
     * Constructor.
     */
    public TaskInboxFilters() {
    }

    /**
     * Called after construction and injection.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void postConstruct() {
        ownerAny.setValue(true);
        ClickHandler clearFilterHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setValue(new TaskInboxFilterBean(), true);
            }
        };
        clearFilters.addClickHandler(clearFilterHandler);
        @SuppressWarnings("rawtypes")
        ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                onFilterValueChange();
            }
        };
        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onFilterValueChange();
            }
        };
        ownerAny.addClickHandler(clickHandler);
        ownerMine.addClickHandler(clickHandler);
        ownerGroup.addClickHandler(clickHandler);
        ownerActive.addClickHandler(clickHandler);
        priority.addValueChangeHandler(valueChangeHandler);
        dateDueFrom.addValueChangeHandler(valueChangeHandler);
        dateDueTo.addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        TaskInboxFilterBean newState = new TaskInboxFilterBean();
        int priorityVal = -1;
        if (priority.getValue().trim().length() > 0) {
            try { priorityVal = Integer.parseInt(priority.getValue()); } catch (NumberFormatException nfe) {}
        }
        newState.setOwner(TaskOwnerEnum.valueOf(ownerAny.getValue(), ownerMine.getValue(), ownerActive.getValue(), ownerGroup.getValue()))
            .setPriority(priorityVal)
            .setDateDueFrom(dateDueFrom.getDateValue())
            .setDateDueTo(dateDueTo.getDateValue())
        ;

        TaskInboxFilterBean oldState = this.currentState;
        this.currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
    }

    /**
     * @return the current filter settings
     */
    public TaskInboxFilterBean getValue() {
        return this.currentState;
    }

    /**
     * @param value the new filter settings
     */
    public void setValue(TaskInboxFilterBean value) {
        setValue(value, false);
    }
    
    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(TaskInboxFilterBean value, boolean fireEvents) {
        if (value.getOwner() == TaskOwnerEnum.any) {
            ownerAny.setValue(true);
        } else if (value.getOwner() == TaskOwnerEnum.mine) {
            ownerMine.setValue(true);
        } else if (value.getOwner() == TaskOwnerEnum.active) {
            ownerActive.setValue(true);
        } else if (value.getOwner() == TaskOwnerEnum.group) {
            ownerGroup.setValue(true);
        }
        priority.setValue(value.getPriority() == -1 ? "" : String.valueOf(value.getPriority())); //$NON-NLS-1$
        dateDueFrom.setDateValue(value.getDateDueFrom() == null ? null : value.getDateDueFrom());
        dateDueTo.setDateValue(value.getDateDueTo() == null ? null : value.getDateDueTo());
        TaskInboxFilterBean oldState = this.currentState;
        currentState = value;
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
        }
    }

    /**
     * Refresh any data in the filter panel.
     */
    public void refresh() {
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TaskInboxFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
