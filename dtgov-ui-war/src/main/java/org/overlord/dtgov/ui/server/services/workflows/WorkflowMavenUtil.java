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
package org.overlord.dtgov.ui.server.services.workflows;

import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;

/**
 * Utility class used to include all the utility methods related the sramp maven
 * artifacts used in dtgov.
 *
 * @author David Virgil Naranjo
 */
public class WorkflowMavenUtil {
    private static final String MAVEN_VERSION_LATEST = "LATEST"; //$NON-NLS-1$
    private static final String MAVEN_VERSION_RELEASE = "RELEASE"; //$NON-NLS-1$

    private static final String MAVEN_QUERY_PARAMETERS = "@maven.groupId=? and @maven.artifactId = ? and xp2:not(@maven.classifier)"; //$NON-NLS-1$

    /**
     * Gets the version of an artifact stored in sramp.
     *
     * @param queryModel
     *            the query model
     * @param mavenGroupId
     *            the maven group id
     * @param mavenArtifactId
     *            the maven artifact id
     * @param mavenVersion
     *            the maven version
     * @return the version
     * @throws SrampClientException
     *             the sramp client exception
     * @throws SrampAtomException
     *             the sramp atom exception
     */
    public static String getVersion(String queryModel, String mavenGroupId, String mavenArtifactId, String mavenVersion, SrampAtomApiClient client)
            throws SrampClientException,
            SrampAtomException {
        if (mavenVersion.equals(MAVEN_VERSION_LATEST) || mavenVersion.equals(MAVEN_VERSION_RELEASE)) {
            QueryResultSet results = null;
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(queryModel).append("[").append(MAVEN_QUERY_PARAMETERS).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
            if (mavenVersion.equals(MAVEN_VERSION_LATEST)) {
                results = client.buildQuery(queryBuilder.toString()).parameter(mavenGroupId).parameter(mavenArtifactId)
                        .propertyName("maven.version").orderBy("createdTimestamp").descending() //$NON-NLS-1$ //$NON-NLS-2$
                        .count(1).query();
                if (results.size() > 0) {
                    return results.get(0).getCustomPropertyValue("maven.version"); //$NON-NLS-1$
                } else {
                    return null;
                }

            } else if (mavenVersion.equals(MAVEN_VERSION_RELEASE)) {
                int page = 0;
                int page_size = 50;
                boolean moreItems = true;

                while (moreItems) {
                    results = client.buildQuery(queryBuilder.toString()).parameter(mavenGroupId).parameter(mavenArtifactId)
                            .propertyName("maven.version").orderBy("createdTimestamp").descending() //$NON-NLS-1$ //$NON-NLS-2$
                            .count(page_size).startIndex(page * page_size).query();
                    for (ArtifactSummary artSumm : results) {
                        String version = artSumm.getCustomPropertyValue("maven.version"); //$NON-NLS-1$
                        if (!version.endsWith("-SNAPSHOT")) { //$NON-NLS-1$
                            return version;
                        }
                    }
                    page++;
                    if (results.getTotalResults() < page * page_size) {
                        moreItems = false;
                    }
                }
                return null;
            }
        } else {
            return mavenVersion;
        }
        return null;
    }
}
