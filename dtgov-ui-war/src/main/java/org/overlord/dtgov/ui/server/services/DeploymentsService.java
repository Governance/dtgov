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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentResultSetBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.DerivedArtifactsBean;
import org.overlord.dtgov.ui.client.shared.beans.ExpandedArtifactSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.ExpandedArtifactsBean;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.IDeploymentsService;
import org.overlord.dtgov.ui.server.DtgovUIConfig;
import org.overlord.dtgov.ui.server.DtgovUIConfig.DeploymentStage;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;

/**
 * Concrete implementation of the task inbox service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class DeploymentsService implements IDeploymentsService {

    private static final int PAGE_SIZE = 20;

    @Inject
    private SrampApiClientAccessor srampClientAccessor;
    @Inject
    private DtgovUIConfig config;

    /**
     * Constructor.
     */
    public DeploymentsService() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#search(org.overlord.dtgov.ui.client.shared.beans.DeploymentsFilterBean, java.lang.String, int)
     */
    @Override
    public DeploymentResultSetBean search(DeploymentsFilterBean filters, String searchText, int page)
            throws DtgovUiException {
        int pageSize = PAGE_SIZE;
        try {
            int req_startIndex = (page - 1) * pageSize;
            SrampClientQuery query = null;
            query = createQuery(filters, searchText);
            QueryResultSet resultSet = query.startIndex(req_startIndex).orderBy("name").ascending().count(pageSize + 1).query();

            DeploymentResultSetBean rval = new DeploymentResultSetBean();
            ArrayList<DeploymentSummaryBean> deployments = new ArrayList<DeploymentSummaryBean>();
            for (ArtifactSummary artifactSummary : resultSet) {
                ArtifactType artifactType = artifactSummary.getType();
                DeploymentSummaryBean bean = new DeploymentSummaryBean();
                bean.setInitiatedDate(artifactSummary.getCreatedTimestamp());
                bean.setName(artifactSummary.getName());
                bean.setModel(artifactType.getArtifactType().getModel());
                bean.setType(artifactType.getType());
                bean.setRawType(artifactType.getArtifactType().getType());
                bean.setUuid(artifactSummary.getUuid());
                deployments.add(bean);
            }
            boolean hasMorePages = false;
            if (deployments.size() > pageSize) {
                deployments.remove(deployments.get(deployments.size()-1));
                hasMorePages = true;
            }
            // Does the server support opensearch style attributes?  If so,
            // use that information.  Else figure it out from the request params.
            if (resultSet.getTotalResults() != -1) {
                rval.setItemsPerPage(pageSize);
                rval.setStartIndex(resultSet.getStartIndex());
                rval.setTotalResults(resultSet.getTotalResults());
            } else {
                rval.setItemsPerPage(pageSize);
                rval.setTotalResults(hasMorePages ? pageSize + 1 : deployments.size());
                rval.setStartIndex(req_startIndex);
            }

            rval.setDeployments(deployments);
            return rval;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e);
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e);
        }
    }

    /**
     * Creates a query given the selected filters and search text.
     */
    protected SrampClientQuery createQuery(DeploymentsFilterBean filters, String searchText) {
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query
        if (filters.getType() == null) {
            queryBuilder.append("/s-ramp");
        } else {
            queryBuilder.append("/s-ramp/" + filters.getType());
        }
        List<String> criteria = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();

        // Search Text
        if (searchText != null && searchText.trim().length() > 0) {
            criteria.add("fn:matches(@name, ?)");
            params.add(searchText.replace("*", ".*"));
        }

        // Stage
        criteria.add("classifiedByAnyOf(., ?)");
        if (filters.getStage() == null) {
            params.add(config.getConfiguration().getString(DtgovUIConfig.DEPLOYMENT_ALL_CLASSIFIER,
                    "http://www.jboss.org/overlord/deployment-status.owl#Lifecycle"));
        } else {
            params.add(filters.getStage());
        }

        // Created on
        if (filters.getDateInitiatedFrom() != null) {
            criteria.add("@createdTimestamp >= ?");
            Calendar cal = Calendar.getInstance();
            cal.setTime(filters.getDateInitiatedFrom());
            zeroOutTime(cal);
            params.add(cal);
        }
        if (filters.getDateInitiatedTo() != null) {
            criteria.add("@createdTimestamp < ?");
            Calendar cal = Calendar.getInstance();
            cal.setTime(filters.getDateInitiatedTo());
            zeroOutTime(cal);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            params.add(cal);
        }
        // Bundle name
        if (filters.getBundleName() != null && filters.getBundleName().trim().length() > 0) {
            // TODO implement query by bundle name here
        }

        // Now create the query predicate from the generated criteria
        if (criteria.size() > 0) {
            queryBuilder.append("[");
            queryBuilder.append(StringUtils.join(criteria, " and "));
            queryBuilder.append("]");
        }

        // Create the query, and parameterize it
        SrampAtomApiClient client = srampClientAccessor.getClient();
        SrampClientQuery query = client.buildQuery(queryBuilder.toString());
        for (Object param : params) {
            if (param instanceof String) {
                query.parameter((String) param);
            }
            if (param instanceof Calendar) {
                query.parameter((Calendar) param);
            }
        }
        return query;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#get(java.lang.String)
     */
    @Override
    public DeploymentBean get(String uuid) throws DtgovUiException {
        try {
            BaseArtifactType artifact = srampClientAccessor.getClient().getArtifactMetaData(uuid);
            ArtifactType artifactType = ArtifactType.valueOf(artifact);

            DeploymentBean bean = new DeploymentBean();
            bean.setInitiatedDate(artifact.getCreatedTimestamp().toGregorianCalendar().getTime());
            bean.setName(artifact.getName());
            bean.setStage(getStage(artifact));
            bean.setModel(artifactType.getArtifactType().getModel());
            bean.setType(artifactType.getType());
            bean.setRawType(artifactType.getArtifactType().getType());
            bean.setUuid(artifact.getUuid());
            bean.setVersion(artifact.getVersion());
            bean.setInitiatedBy(artifact.getCreatedBy());
            bean.setMavenId(SrampModelUtils.getCustomProperty(artifact, "maven.artifactId"));
            bean.setMavenGroup(SrampModelUtils.getCustomProperty(artifact, "maven.groupId"));
            bean.setMavenVersion(SrampModelUtils.getCustomProperty(artifact, "maven.version"));
            bean.setDescription(artifact.getDescription());
            return bean;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /**
     * Gets the stage (comma separated list of classifiers) from the artifact meta-data.
     * @param artifact
     */
    private String getStage(BaseArtifactType artifact) {
        StringBuilder buff = new StringBuilder();
        List<DeploymentStage> stages = config.getStages();
        List<String> classifiedBy = artifact.getClassifiedBy();
        boolean first = true;
        boolean found = false;
        for (DeploymentStage stage : stages) {
            String classifier = stage.getClassifier();
            if (classifiedBy.contains(classifier)) {
                int sharpIdx = classifier.lastIndexOf('#');
                if (sharpIdx > 0) {
                    if (first) {
                        first = false;
                    } else {
                        buff.append(", ");
                    }
                    buff.append(classifier.substring(sharpIdx+1));
                    found = true;
                }
            }
        }
        if (!found) {
            buff.append("[Not Deployed]");
        }
        return buff.toString();
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#update(org.overlord.dtgov.ui.client.shared.beans.DeploymentBean)
     */
    @Override
    public void update(DeploymentBean bean) throws DtgovUiException {
        try {
            ArtifactType artifactType = ArtifactType.valueOf(bean.getModel(), bean.getRawType());
            // Grab the latest from the server
            BaseArtifactType artifact = srampClientAccessor.getClient().getArtifactMetaData(artifactType, bean.getUuid());
            // Update it with new data from the bean
            artifact.setDescription(bean.getDescription());
            // Push the changes back to the server
            srampClientAccessor.getClient().updateArtifactMetaData(artifact);
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#listExpandedArtifacts(java.lang.String)
     */
    @Override
    public ExpandedArtifactsBean listExpandedArtifacts(String uuid) throws DtgovUiException {
        try {
            ExpandedArtifactsBean rval = new ExpandedArtifactsBean();
            BaseArtifactType artifact = srampClientAccessor.getClient().getArtifactMetaData(uuid);
            ArtifactType type = ArtifactType.valueOf(artifact);
            rval.setArtifactName(artifact.getName());
            rval.setArtifactType(type.getType());
            rval.setArtifactUuid(uuid);
            rval.setArtifactVersion(artifact.getVersion());

            SrampClientQuery query = srampClientAccessor.getClient().buildQuery("/s-ramp[expandedFromDocument[@uuid = ?]]");
            QueryResultSet results = query.parameter(uuid).orderBy("name").ascending().query();
            for (ArtifactSummary artifactSummary : results) {
                ArtifactType at = artifactSummary.getType();
                ExpandedArtifactSummaryBean easBean = new ExpandedArtifactSummaryBean();
                easBean.setName(artifactSummary.getName());
                easBean.setType(at.getType());
                easBean.setUuid(artifactSummary.getUuid());
                rval.getExpandedArtifacts().add(easBean);
            }

            return rval;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IDeploymentsService#listDerivedArtifacts(java.lang.String)
     */
    @Override
    public DerivedArtifactsBean listDerivedArtifacts(String uuid) throws DtgovUiException {
        try {
            DerivedArtifactsBean rval = new DerivedArtifactsBean();
            BaseArtifactType artifact = srampClientAccessor.getClient().getArtifactMetaData(uuid);
            rval.setArtifactName(artifact.getName());
            rval.setArtifactUuid(uuid);

            SrampClientQuery query = srampClientAccessor.getClient().buildQuery("/s-ramp[relatedDocument[@uuid = ?]]");
            QueryResultSet results = query.parameter(uuid).orderBy("name").ascending().query();
            for (ArtifactSummary artifactSummary : results) {
                ArtifactType at = artifactSummary.getType();
                DerivedArtifactSummaryBean dasBean = new DerivedArtifactSummaryBean();
                dasBean.setName(artifactSummary.getName());
                dasBean.setType(at.getType());
                dasBean.setUuid(artifactSummary.getUuid());
                rval.getDerivedArtifacts().add(dasBean);
            }

            return rval;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /**
     * Set the time components of the given {@link Calendar} to 0's.
     * @param cal
     */
    protected void zeroOutTime(Calendar cal) {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

}
