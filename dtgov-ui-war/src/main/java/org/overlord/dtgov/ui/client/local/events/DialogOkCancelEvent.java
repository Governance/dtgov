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
 * Event fired when the user clicks either the OK or Cancel buttons (or equivalent) on
 * a modal dialog.
 *
 * @author eric.wittmann@redhat.com
 */
public class DialogOkCancelEvent extends GwtEvent<DialogOkCancelEvent.Handler> {

    /**
     * Handler for {@link DialogOkCancelEvent}.
     */
    public static interface Handler extends EventHandler {

        /**
         * Called when {@link DialogOkCancelEvent} is fired.
         *
         * @param event the {@link DialogOkCancelEvent} that was fired
         */
        public void onDialogOkCancel(DialogOkCancelEvent event);
    }

    /**
     * Indicates if a widget supports ok/cancel.
     */
    public static interface HasDialogOkCancelHandlers extends HasHandlers {

        /**
         * Adds a handler to the widget.
         * @param handler
         */
        public HandlerRegistration addDialogOkCancelHandler(Handler handler);

    }

    private static Type<Handler> TYPE;

    /**
     * Fires the event.
     * @param source
     * @param ok
     */
    public static DialogOkCancelEvent fire(HasHandlers source, boolean ok) {
        DialogOkCancelEvent event = new DialogOkCancelEvent(ok);
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
    
    private boolean ok;

    /**
     * Constructor.
     * @param ok
     */
    public DialogOkCancelEvent(boolean ok) {
        this.ok = ok;
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
        handler.onDialogOkCancel(this);
    }

    /**
     * @return the ok
     */
    public boolean isOk() {
        return ok;
    }
    
    /**
     * @return !ok
     */
    public boolean isCancel() {
        return !isOk();
    }
}
