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
package org.overlord.dtgov.ui.client.local.pages.deployments;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.services.HistoryRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingDateConverter;
import org.overlord.dtgov.ui.client.local.util.DataBindingTimeConverter;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventBean;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventSummaryBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A single item in the history UI for an artifact.
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentHistory.html#deployment-history-item")
@Dependent
public class HistoryEventItem extends Composite implements HasValue<HistoryEventSummaryBean> {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected HistoryRpcService historyService;
    @Inject
    protected NotificationService notificationService;

    @Inject @AutoBound
    protected DataBinder<HistoryEventSummaryBean> value;

    @Inject @DataField
    InlineLabel icon;
    @Inject @DataField @Bound
    InlineLabel who;
    @Inject @DataField @Bound(property="when", converter=DataBindingDateConverter.class)
    InlineLabel whenDay;
    @Inject @DataField @Bound(property="when", converter=DataBindingTimeConverter.class)
    InlineLabel whenTime;
    @Inject @DataField("btn-details")
    Button detailsButton;
    @Inject @DataField @Bound
    InlineLabel summary;
    @Inject @DataField("deployment-history-item-details")
    FlowPanel detailsPanel;

    @Inject
    Instance<HistoryEventDetailsLoading> spinnerFactory;

    /**
     * Constructor.
     */
    public HistoryEventItem() {
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void onPostConstruct() {
        detailsPanel.setVisible(false);
        DOMUtil.addClickHandlerToElement(getElement(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onDetails(event);
            }
        });
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<HistoryEventSummaryBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public HistoryEventSummaryBean getValue() {
        return value.getModel();
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(HistoryEventSummaryBean value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(HistoryEventSummaryBean value, boolean fireEvents) {
        this.value.setModel(value, InitialState.FROM_MODEL);
        setIcon(value);
    }

    /**
     * Sets the appropriate class to show the right icon (or none).
     * @param value
     */
    private void setIcon(HistoryEventSummaryBean value) {
        String type = value.getType();
        if (type != null) {
            if (type.contains("add")) { //$NON-NLS-1$
                this.icon.getElement().removeClassName("history-item-icon-none"); //$NON-NLS-1$
                this.icon.getElement().addClassName("history-item-icon-new"); //$NON-NLS-1$
            } else if (type.contains("update")) { //$NON-NLS-1$
                this.icon.getElement().removeClassName("history-item-icon-none"); //$NON-NLS-1$
                this.icon.getElement().addClassName("history-item-icon-edit"); //$NON-NLS-1$
            } else if (type.contains("delete")) { //$NON-NLS-1$
                this.icon.getElement().removeClassName("history-item-icon-none"); //$NON-NLS-1$
                this.icon.getElement().addClassName("history-item-icon-delete"); //$NON-NLS-1$
            }
        }
    }

    /**
     * Called when the user clicks the 'details' button.
     * @param event
     */
    @EventHandler("btn-details")
    protected void onDetails(ClickEvent event) {
        if (!detailsPanel.isVisible()) {
            HistoryEventSummaryBean bean = value.getModel();
            historyService.getEventDetails(bean.getArtifactUuid(), bean.getId(), new IRpcServiceInvocationHandler<HistoryEventBean>() {
                @Override
                public void onReturn(HistoryEventBean data) {
                    showEventDetails(data);
                }
                @Override
                public void onError(Throwable error) {
                    detailsPanel.clear();
                    notificationService.sendErrorNotification(i18n.format("history-event-item.error-loading"), error); //$NON-NLS-1$
                }
            });
            detailsPanel.clear();
            detailsPanel.add(spinnerFactory.get());
            detailsPanel.setVisible(true);
        } else {
            detailsPanel.setVisible(false);
            detailsPanel.clear();
        }

        if (event != null) {
            event.preventDefault();
            event.stopPropagation();
        }
    }

    /**
     * Shows the event details in the UI.
     * @param data
     */
    protected void showEventDetails(HistoryEventBean data) {
        String details = data.getDetails();
        HTML html = new HTML(details);
        detailsPanel.clear();
        detailsPanel.add(html);
    }

}
