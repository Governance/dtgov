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
package org.overlord.dtgov.ui.client.local.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;


/**
 * Custom GWT Event created to pass a signal from the modal box to the main page
 * that the process(workflow) has been stopped and the list of processes need to
 * be refreshed.
 * 
 * @author David Virgil Naranjo
 * 
 */
public class StopProcessEvent extends GwtEvent<StopProcessEvent.Handler> {

    /** The Constant TYPE. */
    public static Type<Handler> TYPE;

    private Object item;

    public StopProcessEvent(Object item) {
        this.setItem(item);
    }

    /**
     * Handler for {@link StopProcessEvent}.
     */
    public static interface Handler extends EventHandler {

        /**
         * Called when {@link StopProcessEvent} is fired.
         *
         * @param event
         *            the {@link StopProcessEvent} that was fired
         */
        public void onProcessStopped(StopProcessEvent event);
    }

    /**
     * Indicates if a widget supports ok/cancel.
     */
    public static interface HasStopProcessHandlers extends HasHandlers {

        /**
         * Adds a handler to the widget.
         *
         * @param handler
         */
        public HandlerRegistration addStopProcessHandler(Handler handler);

    }

    /**
     * Fires the event.
     *
     * @param source
     * @param item
     */
    public static StopProcessEvent fire(HasHandlers source, Object item) {
        StopProcessEvent event = new StopProcessEvent(item);
        if (TYPE != null)
            source.fireEvent(event);
        return event;
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<Handler> getAssociatedType() {
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
    protected void dispatch(Handler handler) {
        handler.onProcessStopped(this);
    }

    /**
     * @return the item
     */
    public Object getItem() {
        return item;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(Object item) {
        this.item = item;
    }

}
