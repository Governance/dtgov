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
package org.overlord.sramp.governance;

import java.util.HashSet;
import java.util.Set;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.SrampModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class QueryAccesor.
 *
 * @author David Virgil Naranjo
 */
public class QueryAccessor {


    /** The Constant ARTIFACT_TYPE. */
    private final static String ARTIFACT_TYPE="/s-ramp/ext/DtgovWorkflowQuery";

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(QueryAccessor.class);

    /** The Constant PROPERTY_PREFIX. */
    private final static String PROPERTY_PREFIX = "prop.";

    /** The Constant PROPERTY_QUERY. */
    private final static String PROPERTY_QUERY = "query";


    /** The Constant PROPERTY_WORKLOW. */
    private final static String PROPERTY_WORKLOW = "workflow";

    /**
     * Gets the queries.
     *
     * @return the queries
     * @throws ConfigException
     *             the config exception
     */
    public Set<Query> getQueries() throws ConfigException {
        Set<Query> queries = new HashSet<Query>();

        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query

        queryBuilder.append(ARTIFACT_TYPE); //$NON-NLS-1$
        SrampClientQuery query = client.buildQuery(queryBuilder.toString());
        SrampClientQuery scq = query.startIndex(0);
        try {
            QueryResultSet resultSet = scq.query();
            for (ArtifactSummary artifactSummary : resultSet) {
                BaseArtifactType artifact = client.getArtifactMetaData(artifactSummary.getUuid());

                Query q = new Query(SrampModelUtils.getCustomProperty(artifact, PROPERTY_QUERY),
                        SrampModelUtils.getCustomProperty(artifact, PROPERTY_WORKLOW));
                q.setParameters(SrampModelUtils.getCustomPropertiesByPrefix(artifact, PROPERTY_PREFIX));
                queries.add(q);
            }
        } catch (SrampClientException e) {
            logger.error(
                    Messages.i18n.format(
                            "QueryExecutor.ExceptionFor", "Reading query from s-ramp", e.getMessage()), e); //$NON-NLS-1$
        } catch (SrampAtomException e) {
            logger.error(
                    Messages.i18n.format(
                            "QueryExecutor.ExceptionFor", " Reading query from s-ramp", e.getMessage()), e); //$NON-NLS-1$
        }
        return queries;
    }
}
