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
package org.overlord.dtgov.ui.server.services;

import java.util.Date;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.dtgov.ui.client.shared.beans.ArtifactHistoryBean;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventBean;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventSummaryBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IHistoryService;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;

/**
 * Concrete implementation of the task inbox service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class HistoryService implements IHistoryService {

    @Inject
    private SrampApiClientAccessor srampClientAccessor;

    /**
     * Constructor.
     */
    public HistoryService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IHistoryService#listEvents(java.lang.String)
     */
    @Override
    public ArtifactHistoryBean listEvents(String artifactUuid) throws DtgovUiException {
        ArtifactHistoryBean bean = new ArtifactHistoryBean();
        bean.setArtifactName("mock-artifact-jar.jar");
        bean.setArtifactType("SwitchYardApplication");
        bean.setArtifactUuid(artifactUuid);
        bean.setArtifactVersion("1.0.3");

        HistoryEventSummaryBean event = new HistoryEventSummaryBean();
        event.setId("1");
        event.setSummary("Some random summary goes here.");
        event.setWhen(new Date());
        event.setWho("ewittman");
        bean.getEvents().add(event);

        event = new HistoryEventSummaryBean();
        event.setId("2");
        event.setSummary("Another random summary goes here.");
        event.setWhen(new Date());
        event.setWho("ewittman");
        bean.getEvents().add(event);

        event = new HistoryEventSummaryBean();
        event.setId("3");
        event.setSummary("Artifact was created!");
        event.setWhen(new Date());
        event.setWho("ewittman");
        bean.getEvents().add(event);
        return bean;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IHistoryService#getEventDetails(java.lang.String, java.lang.String)
     */
    @Override
    public HistoryEventBean getEventDetails(String artifactUuid, String eventId) throws DtgovUiException {
        HistoryEventBean bean = new HistoryEventBean();
        bean.setDetails("Consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa.");
        return bean;
    }

}
