package org.overlord.sramp.governance;

import java.util.ArrayList;
import java.util.List;

import org.overlord.dtgov.common.Target;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.QueryResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetAccessor {
    private static Logger logger = LoggerFactory.getLogger(TargetAccessor.class);

    public static List<Target> getTargets() {
        SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
        // Initial query

        SrampClientQuery query = client.buildQuery("/s-ramp/ext/DeploymentTarget");
        query = query.startIndex(0);
        try {
            QueryResultSet resultSet = query.query();
            return TargetFactory.asList(resultSet);
        } catch (SrampClientException e) {
            logger.error(Messages.i18n.format("TargetAccessor.ExceptionFor", e.getMessage()), e); //$NON-NLS-1$
        } catch (SrampAtomException e) {
            logger.error(Messages.i18n.format("TargetAccessor.ExceptionFor", e.getMessage()), e); //$NON-NLS-1$
        }

        return new ArrayList<Target>();
    }

}
