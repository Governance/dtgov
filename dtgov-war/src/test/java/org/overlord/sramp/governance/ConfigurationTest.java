/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.sramp.governance;

import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Tests the Configuration.
 *
 * @author kurt.stam@redhat.com
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigurationTest {

	/**
	 * @throws ConfigException
	 */
    @Test
	public void testAConfigure() throws ConfigException {
	    Governance governance = new Governance();
	    Map<String,Target> targets = governance.getTargets();
	    Assert.assertTrue(targets.size() > 0);
	    Set<Query> queries = governance.getQueries();
	    Assert.assertTrue(queries.size() > 0);
	    System.out.println(governance.validate());
	}

    /**
     * Add a bad query
     *
     * @throws ConfigException
     */
    @Test()
    public void testBad2QueryConfiguration() throws ConfigException {
        Governance governance = new Governance() {
            /**
             * @see org.overlord.sramp.governance.Governance#getConfiguration()
             */
            @Override
            protected Configuration getConfiguration() {
                try {
                    return new PropertiesConfiguration(ConfigurationTest.class.getClassLoader().getResource("bad2-governance.config.txt")); //$NON-NLS-1$
                } catch (ConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        try {
            governance.validate();
            Assert.fail("Expecting exception"); //$NON-NLS-1$
        } catch (ConfigException e) {
            Assert.assertTrue(e.getMessage().startsWith(Governance.QUERY_ERROR));
        }
    }

    /**
     * Add a bad target
     *
     * @throws ConfigException
     */
    @Test()
    public void testBad3TargetConfiguration() throws ConfigException {
        Governance governance = new Governance() {
            /**
             * @see org.overlord.sramp.governance.Governance#getConfiguration()
             */
            @Override
            protected Configuration getConfiguration() {
                try {
                    return new PropertiesConfiguration(ConfigurationTest.class.getClassLoader().getResource("bad3-governance.config.txt")); //$NON-NLS-1$
                } catch (ConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        try {
            governance.validate();
            Assert.fail("Expecting exception"); //$NON-NLS-1$
        } catch (ConfigException e) {
            Assert.assertTrue(e.getMessage().startsWith(Governance.TARGET_ERROR));
        }
    }
}
