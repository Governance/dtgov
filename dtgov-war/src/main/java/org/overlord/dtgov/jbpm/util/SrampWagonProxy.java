/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.dtgov.jbpm.util;

import java.lang.reflect.Field;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.overlord.sramp.wagon.SrampWagon;

/**
 * A simple wrapper around the SrampWagon to ensure that a logger is properly
 * injected/available.
 *
 * @author eric.wittmann@redhat.com
 */
public class SrampWagonProxy extends SrampWagon {

    /**
     * Constructor.
     */
    public SrampWagonProxy() {
        injectLoggerInto(this);
    }

    /**
     * Injects a logger into the SrampWagon instance.
     *
     * @param wagon
     */
    private static void injectLoggerInto(SrampWagon wagon) {
        Logger logger = new ConsoleLogger();
        try {
            Field field = SrampWagon.class.getDeclaredField("logger"); //$NON-NLS-1$
            field.setAccessible(true);
            field.set(wagon, logger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
