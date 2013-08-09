/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.sramp.governance;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 *
 */
public class Release {

	private static final String JAR_NAME = "dtgov-task-api"; //$NON-NLS-1$
	private static String governanceVersion = null;

	private Release () {
	}

	public static String getGovernanceVersion() {
		if (governanceVersion == null) {
			governanceVersion = getVersionFromManifest(JAR_NAME);
		}
		return governanceVersion;

	}

	public static String getVersionFromManifest(String jarName) {
		Enumeration<URL> resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = resEnum.nextElement();
                    if (url.toString().toLowerCase().contains(jarName)) {
                        InputStream is = url.openStream();
                        if (is != null) {
                            Manifest manifest = new Manifest(is);
                            Attributes mainAttribs = manifest.getMainAttributes();
                            String version = mainAttribs.getValue("Implementation-Version"); //$NON-NLS-1$
                            if (version != null) {
                                return (version);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
         } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
         }
         return "unknown"; //$NON-NLS-1$
	}
}
