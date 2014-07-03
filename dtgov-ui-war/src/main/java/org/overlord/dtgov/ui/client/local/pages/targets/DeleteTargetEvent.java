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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;


/**
 * Custom GWT Event created to pass a signal from the modal box to the main page
 * that the Target has been removed and the list of items need to be refreshed.
 *
 * @author David Virgil Naranjo
 *
 */
public class DeleteTargetEvent extends GwtEvent<DeleteTargetHandler> {

    /** The Constant TYPE. */
    public static final Type<DeleteTargetHandler> TYPE = new Type<DeleteTargetHandler>();

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeleteTargetHandler> getAssociatedType() {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared
     * .EventHandler)
     */
    @Override
    protected void dispatch(DeleteTargetHandler handler) {
        handler.onTargetDeleted(this);
    }

    /**
     * Register.
     *
     * @param eventBus
     *            the event bus
     * @param handler
     *            the handler
     * @return the handler registration
     */
    public static HandlerRegistration register(EventBus eventBus, DeleteTargetHandler handler) {
        return eventBus.addHandler(TYPE, handler);
    }

}
