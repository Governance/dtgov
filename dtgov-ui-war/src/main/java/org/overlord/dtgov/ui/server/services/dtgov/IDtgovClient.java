package org.overlord.dtgov.ui.server.services.dtgov;

import java.util.List;
import java.util.Locale;

import org.overlord.dtgov.common.model.Deployer;
import org.overlord.dtgov.common.model.Workflow;

/**
 * A client used to access a dtgov server
 *
 * @author David Virgil Naranjo
 */
public interface IDtgovClient {
    public void stopProcess(String targetUUID, long processId) throws Exception;

    public List<Deployer> getCustomDeployerNames() throws Exception;

    public void setLocale(Locale locale);

    public List<Workflow> getWorkflows() throws Exception;
}
