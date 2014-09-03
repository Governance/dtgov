package org.overlord.sramp.demos.deployer;

import java.util.HashMap;
import java.util.Map;

import org.overlord.dtgov.services.deploy.Deployer;
import org.overlord.dtgov.services.deploy.DeployerProvider;

/**
 * Provider that is recognized during runtime andd add in the DeployerFactory a
 * CustomDeployer.
 *
 * @author David Virgil Naranjo
 */
public class CustomDeployerProvider implements DeployerProvider {

    public static final String CUSTOM_DEPLOYER_NAME = "custom_jboss"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.dtgov.services.deploy.DeployerProvider#createDeployers()
     */
    @Override
    public Map<String, Deployer> createDeployers() {
        Map<String, Deployer> deployers = new HashMap<String, Deployer>();
        deployers.put(CUSTOM_DEPLOYER_NAME, new CustomDeployer());
        return deployers;
    }

}

