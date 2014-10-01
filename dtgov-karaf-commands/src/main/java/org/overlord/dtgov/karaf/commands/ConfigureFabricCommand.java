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
import java.io.InputStream;
import java.util.Properties;

import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureFabricCommand;

/**
 * Karaf console command for use within JBoss Fuse. It should be used before the
 * fabric is created. It configures s-ramp fabric profiles, including overlord
 * profile and sramp profile.
 *
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:fabric:dtgov", name = "configure")
public class ConfigureFabricCommand extends AbstractConfigureFabricCommand {


    /*
     * (non-Javadoc)
     *
     * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
     */
    @Override
    protected Object doExecute() throws Exception {
        super.doExecute();
        addHeaderProperties();

        return null;
    }


    /**
     * Adds the header properties.
     *
     * @throws Exception
     *             the exception
     */
    private void addHeaderProperties() throws Exception {

        String filePath = getOverlordPropertiesFilePath();

        Properties props = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
            props.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            props.setProperty(ConfigureConstants.DTGOV_HEADER_HREF, ConfigureConstants.DTGOV_HEADER_HREF_VALUE);
            props.setProperty(ConfigureConstants.DTGOV_HEADER_LABEL, ConfigureConstants.DTGOV_HEADER_LABEL_VALUE);
            props.setProperty(ConfigureConstants.DTGOV_HEADER_PRIMARY_BRAND, ConfigureConstants.DTGOV_HEADER_PRIMARY_BRAND_VALUE);
            props.setProperty(ConfigureConstants.DTGOV_HEADER_SECOND_BRAND, ConfigureConstants.DTGOV_HEADER_SECOND_BRAND_VALUE);
            props.store(out, null);

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
