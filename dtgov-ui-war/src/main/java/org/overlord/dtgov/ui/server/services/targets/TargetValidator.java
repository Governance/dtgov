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
package org.overlord.dtgov.ui.server.services.targets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.overlord.dtgov.common.targets.TargetConstants;
import org.overlord.dtgov.ui.client.shared.beans.CliTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CopyTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.CustomTargetProperty;
import org.overlord.dtgov.ui.client.shared.beans.MavenTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.RHQTargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetClassifier;
import org.overlord.dtgov.ui.client.shared.beans.ValidationError;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.QueryResultSet;

/**
 * Validates the target form before it is saved in the system. It has different
 * checks depending the Target Type
 *
 * @author David Virgil Naranjo
 */
@Dependent
public class TargetValidator  {

    /** The _sramp client accessor. */
    @Inject
    private SrampApiClientAccessor _srampClientAccessor;

    /** The Constant NOT_UNIQUE_LABEL. */
    private final static String NOT_UNIQUE_LABEL = "target.validation.error.not.unique.name"; //$NON-NLS-1$

    /** The Constant NAME_REQUIRED_LABEL. */
    private final static String NAME_REQUIRED_LABEL = "target.validation.error.name.required"; //$NON-NLS-1$

    /** The Constant WORKFLOW_REQUIRED_LABEL. */
    private final static String TYPE_REQUIRED_LABEL = "target.validation.error.type.required"; //$NON-NLS-1$


    private final static String CLASSIFIER_REQUIRED_LABEL = "target.validation.error.classifier.required"; //$NON-NLS-1$

    private final static String CLASSIFIERS_REPEATED_LABEL = "target.validation.error.repeated.classifiers"; //$NON-NLS-1$

    private final static String CLASSIFIER_EMPTY_LABEL = "target.validation.error.empty.classifier"; //$NON-NLS-1$

    // MAVEN CONSTANTS
    private final static String MAVEN_REPOSITORY_URL_REQUIRED_LABEL = "target.validation.error.maven.url.required"; //$NON-NLS-1$

    private final static String MAVEN_ENABLE_RELEASE_OR_SNAPSHOT_REQUIRED_LABEL = "target.validation.error.maven.release.or.snapshot.required"; //$NON-NLS-1$

    // COPY CONSTANTS
    private final static String COPY_DEPLOYMENTH_PATH_REQUIRED_LABEL = "target.validation.error.copy.deployment.path.required"; //$NON-NLS-1$

    // CLI CONSTANTS
    private final static String CLI_HOST_REQUIRED_LABEL = "target.validation.error.cli.host.required"; //$NON-NLS-1$

    private final static String CLI_PORT_REQUIRED_LABEL = "target.validation.error.cli.port.required"; //$NON-NLS-1$

    private final static String CLI_USER_REQUIRED_LABEL = "target.validation.error.cli.user.required"; //$NON-NLS-1$

    private final static String CLI_PASSWORD_REQUIRED_LABEL = "target.validation.error.cli.password.required"; //$NON-NLS-1$

    // RHQ CONSTANTS
    private final static String RHQ_URL_REQUIRED_LABEL = "target.validation.error.rhq.url.required"; //$NON-NLS-1$

    private final static String RHQ_USER_REQUIRED_LABEL = "target.validation.error.rhq.user.required"; //$NON-NLS-1$

    private final static String RHQ_PASSWORD_REQUIRED_LABEL = "target.validation.error.rhq.password.required"; //$NON-NLS-1$

    // CUSTOM CONSTANTS
    private final static String CUSTOM_TYPE_REQUIRED_LABEL = "target.validation.error.custom.type.required"; //$NON-NLS-1$

    private final static String CUSTOM_PROPERTIES_REQUIRED_LABEL = "target.validation.error.custom.properties.required"; //$NON-NLS-1$

    private final static String CUSTOM_PROPERTY_EMPTY_LABEL = "target.validation.error.custom.property.empty"; //$NON-NLS-1$

    private final static String CUSTOM_PROPERTY_REPEATED_LABEL = "target.validation.error.custom.property.repeated"; //$NON-NLS-1$
    /**
     * Instantiates a new Target validator.
     */
    public TargetValidator() {

    }

    /**
     * Validate the input targetBean
     *
     * @param bean
     *            the bean
     * @return the list
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    public List<ValidationError> validate(TargetBean bean) throws DtgovUiException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (StringUtils.isBlank(bean.getName())) {
            errors.add(new ValidationError(NAME_REQUIRED_LABEL));
        }
        if (!isUniqueName(bean)) {
            errors.add(new ValidationError(NOT_UNIQUE_LABEL));
        }

        if (bean.getClassifiers() == null || bean.getClassifiers().size() == 0) {
            errors.add(new ValidationError(CLASSIFIER_REQUIRED_LABEL));
        }
        if (repeatedClassifiers(bean)) {
            errors.add(new ValidationError(CLASSIFIERS_REPEATED_LABEL));
        }
        if (emptyClassifier(bean)) {
            errors.add(new ValidationError(CLASSIFIER_EMPTY_LABEL));
        }

        if (bean.getType() == null) {
            errors.add(new ValidationError(TYPE_REQUIRED_LABEL));
        } else {
            switch (bean.getType()) {
            case MAVEN:
                MavenTargetBean target = (MavenTargetBean) bean;
                if (StringUtils.isBlank(target.getRepositoryUrl())) {
                    errors.add(new ValidationError(MAVEN_REPOSITORY_URL_REQUIRED_LABEL));
                }
                if (!target.isReleaseEnabled() && !target.isSnapshotEnabled()) {
                    errors.add(new ValidationError(MAVEN_ENABLE_RELEASE_OR_SNAPSHOT_REQUIRED_LABEL));
                }
                break;
            case RHQ:
                RHQTargetBean rhq = (RHQTargetBean) bean;
                if (StringUtils.isBlank(rhq.getBaseUrl())) {
                    errors.add(new ValidationError(RHQ_URL_REQUIRED_LABEL));
                }
                if (StringUtils.isBlank(rhq.getUser())) {
                    errors.add(new ValidationError(RHQ_USER_REQUIRED_LABEL));
                }
                if (StringUtils.isBlank(rhq.getPassword())) {
                    errors.add(new ValidationError(RHQ_PASSWORD_REQUIRED_LABEL));
                }
                break;
            case COPY:
                CopyTargetBean copy = (CopyTargetBean) bean;
                if (StringUtils.isBlank(copy.getDeployDirectory())) {
                    errors.add(new ValidationError(COPY_DEPLOYMENTH_PATH_REQUIRED_LABEL));
                }
                break;
            case CLI:
                CliTargetBean cli = (CliTargetBean) bean;
                if (StringUtils.isBlank(cli.getHost())) {
                    errors.add(new ValidationError(CLI_HOST_REQUIRED_LABEL));
                }
                if (cli.getPort() == null) {
                    errors.add(new ValidationError(CLI_PORT_REQUIRED_LABEL));
                }
                if (StringUtils.isBlank(cli.getUser())) {
                    errors.add(new ValidationError(CLI_USER_REQUIRED_LABEL));
                }
                if (StringUtils.isBlank(cli.getPassword())) {
                    errors.add(new ValidationError(CLI_PASSWORD_REQUIRED_LABEL));
                }
                break;
            case CUSTOM:
                CustomTargetBean custom = (CustomTargetBean) bean;
                if (StringUtils.isBlank(custom.getCustomTypeName())) {
                    errors.add(new ValidationError(CUSTOM_TYPE_REQUIRED_LABEL));
                }
                if (custom.getProperties() == null || custom.getProperties().isEmpty()) {
                    errors.add(new ValidationError(CUSTOM_PROPERTIES_REQUIRED_LABEL));
                }
                if (emptyCustomProperty(custom)) {
                    errors.add(new ValidationError(CUSTOM_PROPERTY_EMPTY_LABEL));
                }
                if (repeatedCustomProperty(custom)) {
                    errors.add(new ValidationError(CUSTOM_PROPERTY_REPEATED_LABEL));
                }
                break;
            }

        }
        return errors;
    }

    /**
     * Check if there is any classifier empty in the TargetBean object.
     *
     * @param bean
     *            the bean
     * @return true, if successful
     */
    private boolean emptyClassifier(TargetBean bean) {
        if (bean.getClassifiers() != null) {
            for (TargetClassifier classifier : bean.getClassifiers()) {
                if (StringUtils.isBlank(classifier.getValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if there are repeated classifiers in the TargetBean object.
     *
     * @param bean
     *            the bean
     * @return true, if successful
     */
    private boolean repeatedClassifiers(TargetBean bean) {
        if (bean.getClassifiers() != null) {
            for (TargetClassifier classifier : bean.getClassifiers()) {
                int counter = 0;
                if (StringUtils.isNotBlank(classifier.getValue())) {
                    for (TargetClassifier second : bean.getClassifiers()) {
                        if (StringUtils.isNotBlank(second.getValue()) && classifier.getValue().equals(second.getValue())) {
                            counter++;
                        }
                        if (counter == 2) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if there is any classifier empty in the TargetBean object.
     * 
     * @param bean
     *            the bean
     * @return true, if successful
     */
    private boolean emptyCustomProperty(CustomTargetBean bean) {
        if (bean.getProperties() != null) {
            for (CustomTargetProperty property : bean.getProperties()) {
                if (StringUtils.isBlank(property.getValue()) || StringUtils.isBlank(property.getKey())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if there are repeated classifiers in the TargetBean object.
     * 
     * @param bean
     *            the bean
     * @return true, if successful
     */
    private boolean repeatedCustomProperty(CustomTargetBean bean) {
        if (bean.getProperties() != null) {
            for (CustomTargetProperty property : bean.getProperties()) {
                int counter = 0;
                if (StringUtils.isNotBlank(property.getKey())) {
                    for (CustomTargetProperty second : bean.getProperties()) {
                        if (StringUtils.isNotBlank(second.getKey()) && property.getKey().equals(second.getKey())) {
                            counter++;
                        }
                        if (counter == 2) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if the TargetBean name is an unique name in the system.
     *
     * @param target
     *            the target
     * @return true, if is unique name
     * @throws DtgovUiException
     *             the dtgov ui exception
     */
    private boolean isUniqueName(TargetBean target) throws DtgovUiException {
        StringBuilder queryBuilder = new StringBuilder();
        // Initial query

        queryBuilder.append("/s-ramp/ext/" + TargetConstants.TARGET_EXTENDED_TYPE); //$NON-NLS-1$

        List<String> criteria = new ArrayList<String>();
        List<Object> params = new ArrayList<Object>();

        criteria.add("fn:matches(@name, ?)"); //$NON-NLS-1$
        params.add(target.getName().replace("*", ".*")); //$NON-NLS-1$ //$NON-NLS-2$

        queryBuilder.append("["); //$NON-NLS-1$
        queryBuilder.append(StringUtils.join(criteria, " and ")); //$NON-NLS-1$
        queryBuilder.append("]"); //$NON-NLS-1$

        // Create the query, and parameterize it
        SrampAtomApiClient client = _srampClientAccessor.getClient();
        SrampClientQuery query = client.buildQuery(queryBuilder.toString());
        for (Object param : params) {
            if (param instanceof String) {
                query.parameter((String) param);
            }
            if (param instanceof Calendar) {
                query.parameter((Calendar) param);
            }
        }
        QueryResultSet resultSet = null;
        try {
            resultSet = query.count(1).query();
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
        if (StringUtils.isNotBlank(target.getUuid())) {
            if (resultSet.size() == 0 || (resultSet.size() == 1 && resultSet.get(0).getUuid().equals(target.getUuid()))) {
                return true;
            }
        } else {
            if (resultSet.size() == 0) {
                return true;
            }
        }
        return false;

    }

    /**
     * Gets the sramp client accessor.
     *
     * @return the sramp client accessor
     */
    public SrampApiClientAccessor getSrampClientAccessor() {
        return _srampClientAccessor;
    }

    /**
     * Sets the sramp client accessor.
     *
     * @param srampClientAccessor
     *            the new sramp client accessor
     */
    public void setSrampClientAccessor(SrampApiClientAccessor srampClientAccessor) {
        this._srampClientAccessor = srampClientAccessor;
    }
}