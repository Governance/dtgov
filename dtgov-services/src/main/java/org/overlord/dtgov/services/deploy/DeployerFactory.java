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
package org.overlord.dtgov.services.deploy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.dtgov.common.DTGovConstants;

/**
 * Factory used to create an {@link Deployer} for a particular type of artifact.
 *
 * @author David Virgil Naranjo
 */
public class DeployerFactory {
    private static Map<String, Deployer> deployers = new HashMap<String, Deployer>();

    static {
        loadDeployers();
    }

    /**
     * Loads any dtgov deployers. These can be contributed via the standard Java
     * service loading mechanism.
     */
    private static void loadDeployers() {
        // First load via the standard ServiceRegistry mechanism.
        Set<DeployerProvider> providers = ServiceRegistryUtil.getServices(DeployerProvider.class);
        for (DeployerProvider provider : providers) {
            Map<String, Deployer> deployers_provider = provider.createDeployers();
            if (deployers_provider != null && !deployers_provider.isEmpty()) {
                deployers.putAll(deployers_provider);
            }
        }

        // Allow users to provide a directory path where we will check for JARs
        // that
        // contain Deployer implementations.
        Collection<ClassLoader> loaders = new LinkedList<ClassLoader>();
        String customDeployerDirPath = System.getProperty(DTGovConstants.DTGOV_CUSTOM_DEPLOYERS_DIR);
        if (customDeployerDirPath != null && customDeployerDirPath.trim().length() > 0) {
            File directory = new File(customDeployerDirPath);
            if (directory.isDirectory()) {
                List<URL> jarURLs = new ArrayList<URL>();
                Collection<File> jarFiles = FileUtils.listFiles(directory, new String[] { "jar" }, false); //$NON-NLS-1$
                for (File jarFile : jarFiles) {
                    try {
                        URL jarUrl = jarFile.toURI().toURL();
                        jarURLs.add(jarUrl);
                    } catch (MalformedURLException e) {
                    }
                }
                URL[] urls = jarURLs.toArray(new URL[jarURLs.size()]);
                ClassLoader jarCL = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
                loaders.add(jarCL);
            }
        }
        // Now load all of these contributed DeployerProvider implementations
        for (ClassLoader loader : loaders) {
            for (DeployerProvider provider : ServiceLoader.load(DeployerProvider.class, loader)) {
                Map<String, Deployer> deployers_provider = provider.createDeployers();
                if (deployers_provider != null && !deployers_provider.isEmpty()) {
                    deployers.putAll(deployers_provider);
                }
            }
        }
    }


    /**
     * Creates a new Deployer object.
     *
     * @param deployerType
     *            the deployer type
     * @return the deployer
     */
    public final static Deployer createDeployer(String deployerType) {
        Deployer deployer = deployers.get(deployerType);
        return deployer;
    }
}
