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

import java.util.List;

import javax.inject.Inject;

import org.jboss.downloads.overlord.sramp._2013.auditing.AuditEntry;
import org.jboss.downloads.overlord.sramp._2013.auditing.AuditItemType;
import org.jboss.downloads.overlord.sramp._2013.auditing.AuditItemType.Property;
import org.jboss.errai.bus.server.annotations.Service;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.ui.client.shared.beans.ArtifactHistoryBean;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventBean;
import org.overlord.dtgov.ui.client.shared.beans.HistoryEventSummaryBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IHistoryService;
import org.overlord.dtgov.ui.server.i18n.Messages;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.audit.AuditEntrySummary;
import org.overlord.sramp.client.audit.AuditResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.audit.AuditEntryTypes;
import org.overlord.sramp.common.audit.AuditItemTypes;

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
        try {
            // Get the artifact from s-ramp
            BaseArtifactType artifact = srampClientAccessor.getClient().getArtifactMetaData(artifactUuid);
            ArtifactType artifactType = ArtifactType.valueOf(artifact);

            // Create the return bean with some artifact info
            ArtifactHistoryBean bean = new ArtifactHistoryBean();
            bean.setArtifactName(artifact.getName());
            bean.setArtifactType(artifactType.getType());
            bean.setArtifactUuid(artifactUuid);
            bean.setArtifactVersion(artifact.getVersion());

            // TODO this will stop at 100 audit entries - the UI should be updated to handle this limitation (bring in a page at a time and only fetch more as the user scrolls)
            AuditResultSet auditTrail = srampClientAccessor.getClient().getAuditTrailForArtifact(artifactUuid);
            for (AuditEntrySummary auditEntry : auditTrail) {
                HistoryEventSummaryBean event = new HistoryEventSummaryBean();
                event.setArtifactUuid(artifactUuid);
                event.setId(auditEntry.getUuid());
                event.setType(auditEntry.getType());
                event.setSummary(generateEventSummary(auditEntry));
                event.setWhen(auditEntry.getWhen());
                event.setWho(auditEntry.getWho());
                bean.getEvents().add(event);
            }

            return bean;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e);
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * Generates a human readable summary from the information in the audit entry summary.  This basically
     * means interpreting the 'type' of the entry and outputing something sensible for a person to read.
     * @param event
     */
    private String generateEventSummary(AuditEntrySummary entry) {
        if (AuditEntryTypes.ARTIFACT_ADD.equals(entry.getType())) {
            return Messages.i18n.format("HistoryService.AddedArtifactToRepo"); //$NON-NLS-1$
        } else if (AuditEntryTypes.ARTIFACT_UPDATE.equals(entry.getType())) {
            return Messages.i18n.format("HistoryService.UpdatedInfoOnArty"); //$NON-NLS-1$
        } else if (AuditEntryTypes.ARTIFACT_DELETE.equals(entry.getType())) {
            return Messages.i18n.format("HistoryService.DeletedArty"); //$NON-NLS-1$
        }
        return Messages.i18n.format("HistoryService.UnrecognizedAction", entry.getType()); //$NON-NLS-1$
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IHistoryService#getEventDetails(java.lang.String, java.lang.String)
     */
    @Override
    public HistoryEventBean getEventDetails(String artifactUuid, String eventId) throws DtgovUiException {
        try {
            AuditEntry auditEntry = srampClientAccessor.getClient().getAuditEntry(artifactUuid, eventId);
            HistoryEventBean bean = new HistoryEventBean();
            bean.setDetails(generateEventDetails(auditEntry));
            return bean;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e);
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * Generates a human readable but detailed account of what this event is all about.  This means
     * analyzing the type and audit items in the entry and producing a bit of text explaining what
     * they are and what they mean.
     * @param auditEntry
     */
    private String generateEventDetails(AuditEntry auditEntry) {
        if (AuditEntryTypes.ARTIFACT_ADD.equals(auditEntry.getType()) || AuditEntryTypes.ARTIFACT_DELETE.equals(auditEntry.getType())) {
            return Messages.i18n.format("HistoryService.NoAdditionalInfo"); //$NON-NLS-1$
        } else if (AuditEntryTypes.ARTIFACT_UPDATE.equals(auditEntry.getType())) {
            List<AuditItemType> auditItem = auditEntry.getAuditItem();
            if (auditItem.isEmpty()) {
                return Messages.i18n.format("HistoryService.NoAdditionalInfo"); //$NON-NLS-1$
            }
            StringBuilder buffer = new StringBuilder();
            for (AuditItemType auditItemType : auditItem) {
                generateAuditItemSummary(auditItemType, buffer);
            }
            if (buffer.length() == 0) {
                return Messages.i18n.format("HistoryService.NoAdditionalInfo"); //$NON-NLS-1$
            }
            return buffer.toString();
        }
        return Messages.i18n.format("HistoryService.UnrecognizedAction", auditEntry.getType()); //$NON-NLS-1$
    }

    /**
     * @param auditItemType
     * @param buffer
     */
    private void generateAuditItemSummary(AuditItemType auditItemType, StringBuilder buffer) {
        // TODO lots of injection possible here!
        // TODO this needs to be re-thought.  should the server generate the HTML?
        String type = auditItemType.getType();
        if (AuditItemTypes.PROPERTY_ADDED.equals(type)) {
            List<Property> properties = auditItemType.getProperty();
                if (!properties.isEmpty()) {
                buffer.append("<p>"); //$NON-NLS-1$
                buffer.append(Messages.i18n.format("HistoryService.PropertiesWereAdded")); //$NON-NLS-1$
                buffer.append(":</p>"); //$NON-NLS-1$
                buffer.append("<ul>"); //$NON-NLS-1$
                for (Property property : properties) {
                    buffer.append("<li>"); //$NON-NLS-1$
                    buffer.append(
                            Messages.i18n.format("HistoryService.PropertyWithValue", //$NON-NLS-1$
                                    property.getName(), property.getValue()));
                    buffer.append("</li>"); //$NON-NLS-1$
                }
                buffer.append("</ul>"); //$NON-NLS-1$
            }
        } else if (AuditItemTypes.PROPERTY_CHANGED.equals(type)) {
            List<Property> properties = auditItemType.getProperty();
            if (!properties.isEmpty()) {
                buffer.append("<p>"); //$NON-NLS-1$
                buffer.append(Messages.i18n.format("HistoryService.PropertiesWereModified")); //$NON-NLS-1$
                buffer.append(":</p>"); //$NON-NLS-1$
                buffer.append("<ul>"); //$NON-NLS-1$
                for (Property property : properties) {
                    buffer.append("<li>"); //$NON-NLS-1$
                    buffer.append(
                            Messages.i18n.format("HistoryService.PropertyWithNewValue", //$NON-NLS-1$
                            property.getName(), property.getValue()));
                    buffer.append("</li>"); //$NON-NLS-1$
                }
                buffer.append("</ul>"); //$NON-NLS-1$
            }
        } else if (AuditItemTypes.PROPERTY_REMOVED.equals(type)) {
            List<Property> properties = auditItemType.getProperty();
            if (!properties.isEmpty()) {
                buffer.append("<p>"); //$NON-NLS-1$
                buffer.append(Messages.i18n.format("HistoryService.PropertiesWereRemoved")); //$NON-NLS-1$
                buffer.append(":</p>"); //$NON-NLS-1$
                buffer.append("<ul>"); //$NON-NLS-1$
                for (Property property : properties) {
                    buffer.append("<li>"); //$NON-NLS-1$
                    buffer.append(Messages.i18n.format("HistoryService.Property", property.getName())); //$NON-NLS-1$
                    buffer.append("</li>"); //$NON-NLS-1$
                }
                buffer.append("</ul>"); //$NON-NLS-1$
            }
        } else if (AuditItemTypes.CLASSIFIERS_ADDED.equals(type)) {
            List<Property> properties = auditItemType.getProperty();
            if (!properties.isEmpty()) {
                buffer.append("<p>"); //$NON-NLS-1$
                buffer.append(Messages.i18n.format("HistoryService.ClassifiersWereAdded")); //$NON-NLS-1$
                buffer.append(":</p>"); //$NON-NLS-1$
                buffer.append("<ul>"); //$NON-NLS-1$
                for (Property property : properties) {
                    buffer.append("<li>"); //$NON-NLS-1$
                    buffer.append(Messages.i18n.format("HistoryService.Classifier", property.getValue())); //$NON-NLS-1$
                    buffer.append("</li>"); //$NON-NLS-1$
                }
                buffer.append("</ul>"); //$NON-NLS-1$
            }
        } else if (AuditItemTypes.CLASSIFIERS_REMOVED.equals(type)) {
            List<Property> properties = auditItemType.getProperty();
            if (!properties.isEmpty()) {
                buffer.append("<p>"); //$NON-NLS-1$
                buffer.append(Messages.i18n.format("HistoryService.ClassifiersWereRemoved")); //$NON-NLS-1$
                buffer.append(":</p>"); //$NON-NLS-1$
                buffer.append("<ul>"); //$NON-NLS-1$
                for (Property property : properties) {
                    buffer.append("<li>"); //$NON-NLS-1$
                    buffer.append(Messages.i18n.format("HistoryService.Classifier", property.getValue())); //$NON-NLS-1$
                    buffer.append("</li>"); //$NON-NLS-1$
                }
                buffer.append("</ul>"); //$NON-NLS-1$
            }
        }
    }

}
