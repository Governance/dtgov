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

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.model.DtgovModel;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.overlord.sramp.governance.WorkflowAccesor.WorkflowStatusEnum;

/**
 * The dtgov workflow jbmp listener. It modifies values of the workflow artifact
 * when the process is aborted or completed.
 *
 * @author David Virgil Naranjo
 */
public class WorkflowEventListener implements ProcessEventListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#beforeProcessStarted(org
     * .kie.api.event.process.ProcessStartedEvent)
     */
    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#afterProcessStarted(org
     * .kie.api.event.process.ProcessStartedEvent)
     */
    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#beforeProcessCompleted
     * (org.kie.api.event.process.ProcessCompletedEvent)
     */
    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#afterProcessCompleted(
     * org.kie.api.event.process.ProcessCompletedEvent)
     */
    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        long processId = event.getProcessInstance().getId();
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        String queryStr = "/s-ramp/ext/" + DtgovModel.WorkflowInstanceType; //$NON-NLS-1$
        queryStr += "["; //$NON-NLS-1$
        queryStr += "@" + DtgovModel.CUSTOM_PROPERTY_PROCESS_ID + " = ? and "; //$NON-NLS-1$ //$NON-NLS-2$
        queryStr += "@" + DtgovModel.CUSTOM_PROPERTY_STATUS + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$
        queryStr += "]"; //$NON-NLS-1$
        SrampClientQuery query = client.buildQuery(queryStr);
        query.parameter(processId + ""); //$NON-NLS-1$
        query.parameter(WorkflowStatusEnum.RUNNING.name());
        QueryResultSet resultSet = null;
        try {
            resultSet = query.query();
        } catch (SrampClientException e) {
        } catch (SrampAtomException e) {
        }
        if (resultSet != null && resultSet.size() == 1) {
            ArtifactSummary summary = resultSet.get(0);
            BaseArtifactType artifact = null;
            try {
                artifact = client.getArtifactMetaData(summary.getUuid());
            } catch (SrampClientException e) {
            } catch (SrampAtomException e) {
            }
            if (artifact != null) {
                if (event.getProcessInstance().getState() == ProcessInstance.STATE_ABORTED) {
                    SrampModelUtils.setCustomProperty(artifact, DtgovModel.CUSTOM_PROPERTY_STATUS, WorkflowStatusEnum.ABORTED.name());
                } else if (event.getProcessInstance().getState() == ProcessInstance.STATE_COMPLETED) {
                    SrampModelUtils.setCustomProperty(artifact, DtgovModel.CUSTOM_PROPERTY_STATUS, WorkflowStatusEnum.COMPLETED.name());
                }

                try {
                    client.updateArtifactMetaData(artifact);
                } catch (SrampClientException e) {
                } catch (SrampAtomException e) {
                }
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#beforeNodeTriggered(org
     * .kie.api.event.process.ProcessNodeTriggeredEvent)
     */
    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#afterNodeTriggered(org
     * .kie.api.event.process.ProcessNodeTriggeredEvent)
     */
    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#beforeNodeLeft(org.kie
     * .api.event.process.ProcessNodeLeftEvent)
     */
    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#afterNodeLeft(org.kie.
     * api.event.process.ProcessNodeLeftEvent)
     */
    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#beforeVariableChanged(
     * org.kie.api.event.process.ProcessVariableChangedEvent)
     */
    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.kie.api.event.process.ProcessEventListener#afterVariableChanged(org
     * .kie.api.event.process.ProcessVariableChangedEvent)
     */
    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {

    }

}
