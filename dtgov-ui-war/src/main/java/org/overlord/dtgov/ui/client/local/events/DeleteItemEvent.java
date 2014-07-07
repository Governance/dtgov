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

package org.overlord.dtgov.ui.client.local.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Event fired when the user clicks the Delete action for an item in a list (e.g.
 * the Targets list).
 *
 * @author eric.wittmann@redhat.com
 */
public class DeleteItemEvent extends GwtEvent<DeleteItemEvent.Handler> {

    /**
     * Handler for {@link DeleteItemEvent}.
     */
    public static interface Handler extends EventHandler {

        /**
         * Called when {@link DeleteItemEvent} is fired.
         *
         * @param event the {@link DeleteItemEvent} that was fired
         */
        public void onDeleteItem(DeleteItemEvent event);
    }

    /**
     * Indicates if a widget supports ok/cancel.
     */
    public static interface HasDeleteItemHandlers extends HasHandlers {

        /**
         * Adds a handler to the widget.
         * @param handler
         */
        public HandlerRegistration addDeleteItemHandler(Handler handler);

    }

    private static Type<Handler> TYPE;

    /**
     * Fires the event.
     * @param source
     * @param item
     */
    public static DeleteItemEvent fire(HasHandlers source, Object item) {
        DeleteItemEvent event = new DeleteItemEvent(item);
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

    private Object item;

    /**
     * Constructor.
     * @param item
     */
    public DeleteItemEvent(Object item) {
        this.setItem(item);
    }

    /**
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(Handler handler) {
        handler.onDeleteItem(this);
    }

    /**
     * @return the item
     */
    public Object getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(Object item) {
        this.item = item;
    }
}
