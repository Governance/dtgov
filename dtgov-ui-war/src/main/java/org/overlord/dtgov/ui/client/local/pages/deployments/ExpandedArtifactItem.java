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
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.local.services.DeploymentsRpcService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.local.util.DataBindingParentheticalConverter;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactsBean;
import org.overlord.dtgov.ui.client.shared.beans.ExpandedArtifactSummaryBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A single item in the list of expanded artifacts for a deployment.
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentContents.html#deployment-contents-item")
@Dependent
public class ExpandedArtifactItem extends Composite implements HasValue<ExpandedArtifactSummaryBean> {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected DeploymentsRpcService deploymentsService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected ConfigurationService configService;

    @Inject @AutoBound
    protected DataBinder<ExpandedArtifactSummaryBean> value;

    @Inject @DataField("artifact-name") @Bound
    InlineLabel name;
    @Inject @DataField("artifact-type") @Bound(converter=DataBindingParentheticalConverter.class)
    InlineLabel type;

    @Inject @DataField("btn-browse")
    Anchor browseButton;
    @Inject @DataField("btn-details")
    Button detailsButton;
    @Inject @DataField("deployment-contents-item-details")
    FlowPanel detailsPanel;

    @Inject
    Instance<ExpandedArtifactDetailsLoading> spinnerFactory;

    @Inject
    Instance<ExpandedArtifactSummary> summaryFactory;
    @Inject
    Instance<DerivedArtifactsTable> derivedArtifactsTableFactory;

    /**
     * Constructor.
     */
    public ExpandedArtifactItem() {
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
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ExpandedArtifactSummaryBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public ExpandedArtifactSummaryBean getValue() {
        return value.getModel();
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ExpandedArtifactSummaryBean value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(ExpandedArtifactSummaryBean value, boolean fireEvents) {
        this.value.setModel(value, InitialState.FROM_MODEL);
        this.browseButton.setHref(configService.getUiConfig().createSrampUiUrl("details", "uuid", value.getUuid())); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Called when the user clicks the 'details' button.
     * @param event
     */
    @EventHandler("btn-details")
    protected void onDetails(ClickEvent event) {
        if (!detailsPanel.isVisible()) {
            ExpandedArtifactSummaryBean bean = value.getModel();
            deploymentsService.listDerivedArtifacts(bean.getUuid(), new IRpcServiceInvocationHandler<DerivedArtifactsBean>() {
                @Override
                public void onReturn(DerivedArtifactsBean data) {
                    showDetails(data);
                }
                @Override
                public void onError(Throwable error) {
                    detailsPanel.clear();
                    notificationService.sendErrorNotification(i18n.format("expanded-artifact-item.error-fetching"), error); //$NON-NLS-1$
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
     * Called when the user clicks the 'Browse' button.
     * @param event
     */
    @EventHandler("btn-browse")
    protected void onBrowse(ClickEvent event) {
        event.stopPropagation();
    }

    /**
     * Shows the details in the UI.
     * @param data
     */
    protected void showDetails(DerivedArtifactsBean data) {
        detailsPanel.clear();
        if (data.getDerivedArtifacts().isEmpty()) {
            InlineLabel label = new InlineLabel(i18n.format("expanded-artifact-item.none-found")); //$NON-NLS-1$
            detailsPanel.add(label);
        } else {
            ExpandedArtifactSummary summaryInfo = summaryFactory.get();
            summaryInfo.setValue(data);
            detailsPanel.add(summaryInfo);
            DerivedArtifactsTable table = derivedArtifactsTableFactory.get();
            for (DerivedArtifactSummaryBean row : data.getDerivedArtifacts()) {
                table.addRow(row);
            }
            detailsPanel.add(table);
        }
    }

}
