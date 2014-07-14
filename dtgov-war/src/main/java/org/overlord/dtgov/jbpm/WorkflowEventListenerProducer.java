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
package org.overlord.dtgov.jbpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.runtime.manager.api.qualifiers.Process;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.internal.runtime.manager.EventListenerProducer;

/**
 * The class that register all the jbmp process listeners
 *
 * @author David Virgil Naranjo
 */
@Process
public class WorkflowEventListenerProducer implements EventListenerProducer<ProcessEventListener> {

    /* (non-Javadoc)
     * @see org.kie.internal.runtime.manager.EventListenerProducer#getEventListeners(java.lang.String, java.util.Map)
     */
    @Override
    public List<ProcessEventListener> getEventListeners(String identifier, Map<String, Object> params) {
        List<ProcessEventListener> listeners = new ArrayList<ProcessEventListener>();
        listeners.add(new WorkflowEventListener());
        return listeners;
    }

}
