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
package org.overlord.dtgov.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.overlord.dtgov.seed.DataSeeder;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.governance.Governance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepare the arguments for the Seeder and call it to seed dtgov with the
 * initialization data
 *
 * @author David Virgil Naranjo
 */
@Singleton
public class InitService {

    @Inject
    private ServletContext context;

    private final Governance governance = new Governance();

    boolean init = false;

    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    /**
     * Inits the.
     */
    public void init() {
        if (!init) {
            init = true;
            try {
                if (!governance.isInitialized()) {
                    logger.info(Messages.i18n.format("init.service.init.start"));

                    String workflowArtifact = governance.getGovernanceWorkflowName().concat("-");
                    StringBuilder relativePathWorkflows = new StringBuilder();
                    relativePathWorkflows.append("WEB-INF").append(File.separatorChar).append("lib").append(File.separatorChar);

                    String absolutePath = null;
                    Set<String> paths = context.getResourcePaths("/" + relativePathWorkflows.toString());
                    boolean found = false;
                    if (paths != null && paths.size() > 0) {
                        for (String path : paths) {
                            if (path.contains(workflowArtifact)) {
                                workflowArtifact = path.substring(path.lastIndexOf("/") + 1);
                                found = true;
                                break;
                            }
                        }
                    }

                    if (found) {
                        relativePathWorkflows.append(workflowArtifact);
                        try {
                            absolutePath = context.getRealPath(relativePathWorkflows.toString());
                        } catch (Exception ee) {
                            InputStream is = this.getClass().getClassLoader().getResourceAsStream(workflowArtifact);
                            OutputStream os = null;
                            if (is != null) {
                                try {
                                    File f = File.createTempFile(workflowArtifact, null);
                                    f.deleteOnExit();
                                    os = new FileOutputStream(f);
                                    IOUtils.copy(is, os);
                                    absolutePath = f.getAbsolutePath();
                                } finally {
                                    is.close();
                                    if (os != null) {
                                        os.close();
                                    }
                                }

                            } else {
                                throw new RuntimeException(ee);
                            }

                        }

                        DataSeeder seeder = new DataSeeder(governance.getSrampUrl().toString(), governance.getSrampUser(),
                                governance.getSrampPassword(), absolutePath);

                        boolean seed = seeder.seed();
                        if (seed) {
                            governance.initialize();
                        }
                    }
 else {
                        logger.info(Messages.i18n.format("init.service.init.error"));
                    }

                    logger.info(Messages.i18n.format("init.service.init.end"));
                } else {
                    logger.info(Messages.i18n.format("init.service.already.init"));
                }
            } catch (Exception e) {
                logger.info(Messages.i18n.format("init.service.init.error"));
            }

        }

    }


}
