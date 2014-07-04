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
package org.overlord.dtgov.ui.server.services;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.common.target.TargetConstants;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetSummaryBean;
import org.overlord.dtgov.ui.client.shared.beans.ValidationError;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovFormValidationException;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovUiException;
import org.overlord.dtgov.ui.client.shared.services.ITargetService;
import org.overlord.dtgov.ui.server.services.sramp.SrampApiClientAccessor;
import org.overlord.dtgov.ui.server.services.targets.TargetFactory;
import org.overlord.dtgov.ui.server.services.targets.TargetValidator;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;

/**
 * Concrete implementation of the target service interface.
 *
 * @author David Virgil Naranjo
 */
@Service
public class TargetService implements ITargetService {

    @Inject
    private SrampApiClientAccessor _srampClientAccessor;

    @Inject
    private TargetValidator _targetValidator;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.client.shared.services.ITargetService#delete(java
     * .lang.String)
     */
    @Override
    public void delete(String uuid) throws DtgovUiException {
        try {
            _srampClientAccessor.getClient().deleteArtifact(uuid, ArtifactType.ExtendedArtifactType(TargetConstants.TARGET_EXTENDED_TYPE, false)); //$NON-NLS-1$
        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.client.shared.services.ITargetService#get(java.
     * lang.String)
     */
    @Override
    public TargetBean get(String uuid) throws DtgovUiException {
        try {
            BaseArtifactType artifact = _srampClientAccessor.getClient().getArtifactMetaData(uuid);
            TargetBean bean = TargetFactory.toTarget(artifact);
            return bean;

        } catch (SrampClientException e) {
            throw new DtgovUiException(e.getMessage());
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.ui.client.shared.services.ITargetService#save(org.
     * overlord.dtgov.ui.client.shared.beans.TargetBean)
     */
    @Override
    public String save(TargetBean target) throws DtgovUiException {
        List<ValidationError> errors = _targetValidator.validate(target);
        if (errors.size() == 0) {
            BaseArtifactType artifact = TargetFactory.toBaseArtifact(target);
            SrampAtomApiClient client = _srampClientAccessor.getClient();
            String uuid;
            if (StringUtils.isNotBlank(artifact.getUuid())) {
                uuid = artifact.getUuid();
                try {
                    client.updateArtifactMetaData(artifact);
                } catch (SrampClientException e) {
                    throw new DtgovUiException(e.getMessage());
                } catch (SrampAtomException e) {
                    throw new DtgovUiException(e.getMessage());
                }
            } else {
                BaseArtifactType art = null;
                try {
                    art = client.createArtifact(artifact);
                } catch (SrampClientException e) {
                    throw new DtgovUiException(e.getMessage());
                } catch (SrampAtomException e) {
                    throw new DtgovUiException(e.getMessage());
                }
                uuid = art.getUuid();
            }
            return uuid;
        } else {
            throw new DtgovFormValidationException(errors);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.overlord.dtgov.ui.client.shared.services.ITargetService#list()
     */
    @Override
    public List<TargetSummaryBean> list() throws DtgovUiException {

        SrampAtomApiClient client = _srampClientAccessor.getClient();

        SrampClientQuery query = client.buildQuery("/s-ramp/ext/" + TargetConstants.TARGET_EXTENDED_TYPE); //$NON-NLS-1$

        query = query.startIndex(0).orderBy("name"); //$NON-NLS-1$

        query.propertyName(TargetConstants.TARGET_TYPE);
        query = query.ascending();
        try {
            QueryResultSet resultSet = query.query();
            List<TargetSummaryBean> summary = TargetFactory.asList(resultSet);
            return summary;
        } catch (SrampClientException e) {
            throw new DtgovUiException(e);
        } catch (SrampAtomException e) {
            throw new DtgovUiException(e);
        }
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

    /**
     * Gets the target validator.
     *
     * @return the target validator
     */
    public TargetValidator getTargetValidator() {
        return _targetValidator;
    }

    /**
     * Sets the target validator.
     *
     * @param targetValidator
     *            the new target validator
     */
    public void setTargetValidator(TargetValidator targetValidator) {
        this._targetValidator = targetValidator;
    }

}
