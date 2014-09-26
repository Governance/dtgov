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
package org.overlord.dtgov.karaf.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureCommand;
import org.overlord.dtgov.karaf.commands.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:dtgov", name = "configure")
public class ConfigureCommand extends AbstractConfigureCommand {

    private static final Logger logger = LoggerFactory.getLogger(ConfigureCommand.class);

    @Override
    protected Object doExecute() throws Exception {
        logger.info(Messages.getString("configure.command.executed")); //$NON-NLS-1$

        super.doExecute();

        logger.debug(Messages.getString("configure.command.copying.files")); //$NON-NLS-1$
        copyFile("dtgov-ui.properties"); //$NON-NLS-1$
        copyFile("dtgov.properties"); //$NON-NLS-1$
        File dir = new File(karafConfigPath + "overlord-apps"); //$NON-NLS-1$
        if (!dir.exists()) {
            dir.mkdir();
        }
        copyFile("dtgovui-overlordapp.properties", "overlord-apps/dtgovui-overlordapp.properties"); //$NON-NLS-1$

        logger.debug(Messages.getString("configure.command.copying.files.end")); //$NON-NLS-1$
        String randomWorkflowUserPassword = UUID.randomUUID().toString();

        logger.debug(Messages.getString("configure.command.adding.jms.user")); //$NON-NLS-1$
        Properties usersProperties = new Properties();
        File srcFile = new File(karafConfigPath + "users.properties"); //$NON-NLS-1$
        usersProperties.load(new FileInputStream(srcFile));
        // Adding the jms user to the users.properties
        String encryptedPassword = "{CRYPT}" + DigestUtils.sha256Hex(randomWorkflowUserPassword) + "{CRYPT}"; //$NON-NLS-1$ //$NON-NLS-2$
        StringBuilder workflowUserValue = new StringBuilder();
        workflowUserValue.append(encryptedPassword).append(",").append(ConfigureConstants.DTGOV_WORKFLOW_USER_GRANTS);
        usersProperties.setProperty(ConfigureConstants.DTGOV_WORKFLOW_USER, workflowUserValue.toString()); //$NON-NLS-1$
        logger.debug(Messages.getString("configure.command.adding.user.end")); //$NON-NLS-1$

        // Adding to the admin user the sramp grants:
        String adminUser = (String) usersProperties.get("admin");
        adminUser += ",admin.sramp"; //$NON-NLS-1$
        usersProperties.setProperty("admin", adminUser); //$NON-NLS-1$

        logger.debug(Messages.getString("configure.command.modify.admin.roles")); //$NON-NLS-1$
        // Storing the users.properties changes
        usersProperties.store(new FileOutputStream(srcFile), ""); //$NON-NLS-1$


        logger.info(Messages.getString("configure.command.end.execution")); //$NON-NLS-1$
        return null;
    }
}
