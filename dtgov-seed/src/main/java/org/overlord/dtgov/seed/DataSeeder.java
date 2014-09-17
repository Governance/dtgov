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
package org.overlord.dtgov.seed;

import org.overlord.dtgov.seed.i18n.Messages;
import org.overlord.sramp.shell.SrampShellEmbedded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prepare the arguments for the SrampShellEmbedded and call it to seed dtgov
 * with the initialization data
 *
 * @author David Virgil Naranjo
 */
public class DataSeeder {

    private final static String CLI_COMMANDS_FILE = "cli-commands.txt";

    private final static String S_RAMP_ENDPOINT_PROPERTY = "s-ramp.endpoint";

    private final static String S_RAMP_ENDPOINT_USERNAME = "s-ramp.username";

    private final static String S_RAMP_ENDPOINT_PASSWORD = "s-ramp.password";

    private final static String DTGOV_WORKFLOWS_PATH = "dtgov-workflow-jar";

    private final static String SNAPSHOT_ALLOWED = "sramp.config.maven.allow-snapshots";
    private final String srampEndPoint;

    private final String srampUsername;

    private final String srampPassword;

    private final String dtgovWorkflowsPath;

    private static Logger logger = LoggerFactory.getLogger(DataSeeder.class);


    /**
     * Instantiates a new data seeder.
     *
     * @param srampEndPoint
     *            the sramp end point
     * @param srampUsername
     *            the sramp username
     * @param srampPassword
     *            the sramp password
     * @param dtgovWorkflowsPath
     *            the dtgov workflows path
     */
    public DataSeeder(String srampEndPoint, String srampUsername, String srampPassword, String dtgovWorkflowsPath) {
        super();
        this.srampEndPoint = srampEndPoint;
        this.srampUsername = srampUsername;
        this.srampPassword = srampPassword;
        this.dtgovWorkflowsPath = dtgovWorkflowsPath;
    }


    /**
     * Seed.
     *
     * @return true, if successful
     */
    public boolean seed() {
        logger.debug(Messages.i18n.format("data.seeder.seed.start"));
        SrampShellEmbedded shell = new SrampShellEmbedded();
        StringBuilder args = new StringBuilder();
        args.append("-f ").append(CLI_COMMANDS_FILE);
        args.append(" -b ").append("false");
        String[] argsArray = args.toString().split(" ");
        System.setProperty(S_RAMP_ENDPOINT_PROPERTY, srampEndPoint);
        System.setProperty(S_RAMP_ENDPOINT_USERNAME, srampUsername);
        System.setProperty(S_RAMP_ENDPOINT_PASSWORD, srampPassword);
        System.setProperty(DTGOV_WORKFLOWS_PATH, dtgovWorkflowsPath);
        System.setProperty(SNAPSHOT_ALLOWED, "true");
        try {
            shell.run(argsArray);
        } catch (Exception ee) {
            logger.warn(Messages.i18n.format("data.seeder.seed.error"));

            return false;
        }
        logger.debug(Messages.i18n.format("data.seeder.seed.end"));
        return true;
    }



}
