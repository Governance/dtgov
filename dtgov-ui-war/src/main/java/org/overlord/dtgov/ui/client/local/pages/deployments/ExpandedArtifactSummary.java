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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.dtgov.ui.client.local.util.DataBindingListCountConverter;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactsBean;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Summary/detail information about an expanded artifact.
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/deploymentContents.html#deployment-contents-item-summary")
@Dependent
public class ExpandedArtifactSummary extends Composite implements HasValue<DerivedArtifactsBean> {

    @Inject @AutoBound
    protected DataBinder<DerivedArtifactsBean> value;

    @Inject @DataField("artifact-name-2") @Bound(property="artifactName")
    InlineLabel name2;
    @Inject @DataField("artifact-name-3") @Bound(property="artifactName")
    InlineLabel name3;
    @Inject @DataField("derived-artifact-count") @Bound(property="derivedArtifacts", converter=DataBindingListCountConverter.class)
    InlineLabel count;

    /**
     * Constructor.
     */
    public ExpandedArtifactSummary() {
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DerivedArtifactsBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public DerivedArtifactsBean getValue() {
        return value.getModel();
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(DerivedArtifactsBean value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(DerivedArtifactsBean value, boolean fireEvents) {
        this.value.setModel(value, InitialState.FROM_MODEL);
    }

}
