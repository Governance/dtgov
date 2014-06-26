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

import java.util.ArrayList;
import java.util.List;

import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.dtgov.ui.client.shared.beans.TargetClassifier;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Custom table used in the Target page. It stores all the classifiers that
 * belongs to a Target
 *
 * @author David Virgil Naranjo
 */
public class ClassifiersTable extends TemplatedWidgetTable implements HasValue<List<TargetClassifier>> {

    private List<TargetClassifier> _classifiers;

    /* (non-Javadoc)
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<TargetClassifier>> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<TargetClassifier> getValue() {
        return _classifiers;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<TargetClassifier> value) {
        this.setValue(value, true);

    }

    /**
     * Instantiates a new classifiers table.
     */
    public ClassifiersTable() {
        this._classifiers = new ArrayList<TargetClassifier>();
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(List<TargetClassifier> value, boolean fireEvents) {
        this._classifiers = new ArrayList<TargetClassifier>(value);
        clear();
        if (value == null || value.isEmpty()) {
            // Put something here? "No Properties found..." ?
        } else {
            for (final TargetClassifier classifier : value) {
                addRow(classifier);
            }
        }

    }

    /**
     * Adds the row to the classifiers table.
     * 
     * @param classifier
     *            the classifier
     */
    private void addRow(final TargetClassifier classifier) {
        final int rowIdx = this.rowElements.size();
        FlowPanel actions = new FlowPanel();
        InlineLabel deleteAction = new InlineLabel();
        deleteAction.setStyleName("target-icon", true); //$NON-NLS-1$
        deleteAction.setStyleName("target-delete-icon", true); //$NON-NLS-1$
        actions.add(deleteAction);

        deleteAction.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                deleteRow(rowIdx);
                _classifiers.remove(rowIdx);
                setValue(_classifiers);
            }
        });
        final TextBox valueBox = new TextBox();
        valueBox.setText(classifier.getValue());
        valueBox.setStyleName("input-value"); //$NON-NLS-1$
        valueBox.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String val = event.getValue();
                classifier.setValue(val);
            }
        });




        add(rowIdx, 0, valueBox);
        Element row = add(rowIdx, 1, actions);
        setStyleName(row, "actions", true); //$NON-NLS-1$
    }

    /**
     * Adds the new row.
     */
    public void addNewRow() {
        TargetClassifier classifier = new TargetClassifier();
        _classifiers.add(classifier);
        addRow(classifier);
    }

    /**
     * Gets the classifiers.
     *
     * @return the classifiers
     */
    public List<TargetClassifier> getClassifiers() {
        return _classifiers;
    }

    /**
     * Sets the classifiers.
     *
     * @param classifiers
     *            the new classifiers
     */
    public void setClassifiers(List<TargetClassifier> classifiers) {
        this._classifiers = classifiers;
    }

}
