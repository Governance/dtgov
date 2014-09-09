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
package org.overlord.sramp.governance.services.notification;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.overlord.dtgov.common.model.DtgovModel;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class used in the notification resource service.
 *
 * @author David Virgil Naranjo
 *
 */
public class NotificationResourceUtil {

    private static final String GET_TEMPLATE_QUERY = "/s-ramp/ext/" + DtgovModel.EmailTemplateType + "[@template = ? and @template-type = ?]"; //$NON-NLS-1$ //$NON-NLS-2$

    private static Logger logger = LoggerFactory.getLogger(NotificationResourceUtil.class);

    public static final String CLASSPATH_FOLDER = "/governance-email-templates/"; //$NON-NLS-1$

    /**
     * Gets the notification subject.
     *
     * @param template
     *            the template
     * @return the notification subject
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static String getNotificationSubject(String template) throws IOException {
        return getTemplate(template, NotificationTemplateTypeEnum.SUBJECT.value(), CLASSPATH_FOLDER);
    }

    /**
     * Gets the notification body.
     *
     * @param template
     *            the template
     * @return the notification body
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static String getNotificationBody(String template) throws IOException {
        return getTemplate(template, NotificationTemplateTypeEnum.BODY.value(), CLASSPATH_FOLDER);
    }

    /**
     * Gets the template.
     *
     * @param template
     *            the template
     * @param templateType
     *            the template type
     * @param classpathFolder
     *            the classpath folder
     * @return the template
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static String getTemplate(String template, String templateType, String classpathFolder) throws IOException {
        String toReturn = ""; //$NON-NLS-1$

        toReturn = getTemplateFromQuery(template, templateType);
        if (StringUtils.isBlank(toReturn)) {
            String resource = classpathFolder + template + "." + templateType + ".tmpl"; //$NON-NLS-1$ //$NON-NLS-2$
            URL resourceUrl = Governance.class.getClassLoader().getResource(resource);
            if (resourceUrl != null) {
                toReturn = IOUtils.toString(resourceUrl);
            }
        }
        return toReturn;
    }



    /**
     * Gets the template from query.
     *
     * @param template
     *            the template
     * @param templateType
     *            the template type
     * @return the template from query
     */
    private static String getTemplateFromQuery(String template, String templateType) {
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        SrampClientQuery query = client.buildQuery(GET_TEMPLATE_QUERY).parameter(template)
                .parameter(templateType);

        QueryResultSet resultSet = null;
        try {
            resultSet = query.query();
        } catch (SrampClientException e) {
            logger.warn(
                    Messages.i18n.format("NotificationResourceUtil.query.client.error", query.toString()), e); //$NON-NLS-1$
        } catch (SrampAtomException e) {
            logger.warn(Messages.i18n.format("NotificationResourceUtil.query.atom.error", query.toString()), //$NON-NLS-1$
                    e);
        }
        if (resultSet != null && resultSet.size() == 1) {
            ArtifactSummary artifactSummary = resultSet.get(0);
            InputStream is = null;
            try {
                is = client.getArtifactContent(artifactSummary);
            } catch (SrampClientException e) {
                logger.warn(Messages.i18n.format(
                        "NotificationResourceUtil.query.client.getArtifactContent.error", artifactSummary), e); //$NON-NLS-1$
            } catch (SrampAtomException e) {
                logger.warn(Messages.i18n.format(
                        "NotificationResourceUtil.query.atom.getArtifactContent.error", artifactSummary), e); //$NON-NLS-1$
            }
            if (is != null) {
                try {
                    return IOUtils.toString(is);
                } catch (IOException e) {
                    logger.warn(Messages.i18n.format("NotificationResourceUtil.query.ioutils.to.string"), e); //$NON-NLS-1$
                }
            }
        } else {
            logger.warn(Messages.i18n.format("NotificationResourceUtil.query.no.result", template, //$NON-NLS-1$
                    templateType));
        }
        return null;
    }
}
