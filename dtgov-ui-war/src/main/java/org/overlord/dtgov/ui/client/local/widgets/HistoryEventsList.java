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
package org.overlord.dtgov.ui.client.local.widgets;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.overlord.dtgov.ui.client.shared.beans.HistoryEventSummaryBean;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;

/**
 * A widget that displays a list of history events for an artifact.
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class HistoryEventsList extends FlowPanel implements HasValue<List<HistoryEventSummaryBean>> {

    private List<HistoryEventSummaryBean> value;
    @Inject
    protected Instance<HistoryEventItem> eventItemFactory;

    /**
     * Constructor.
     */
    public HistoryEventsList() {
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<HistoryEventSummaryBean>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<HistoryEventSummaryBean> getValue() {
        return value;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<HistoryEventSummaryBean> value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(List<HistoryEventSummaryBean> value, boolean fireEvents) {
        this.value = value;
        render();
    }

    /**
     * Renders the
     */
    private void render() {
        clear();
        if (this.value != null) {
            for (HistoryEventSummaryBean event : this.value) {
                HistoryEventItem eventItem = eventItemFactory.get();
                eventItem.setValue(event);
                add(eventItem);
            }
        }
    }

}
