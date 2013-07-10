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
package org.overlord.sramp.governance;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Governance {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    public static String QUERY_ERROR  = GovernanceConstants.GOVERNANCE_QUERIES + " should be of the format <query>|<processId>|<param::param>\nCheck\n";
    public static String TARGET_ERROR = GovernanceConstants.GOVERNANCE_TARGETS + " should be of the format <targetName>|<directory>\nCheck\n";
    public static String NOTIFICATION_ERROR  = GovernanceConstants.GOVERNANCE + ".<email|..> should be of the format <groupName>|<fromAddress>|<destination1>,<destination2>\nCheck\n";
    public static String DEFAULT_JNDI_EMAIL_REF = "java:jboss/mail/Default";
    public static String DEFAULT_EMAIL_DOMAIN = "example.com";
    public static String DEFAULT_EMAIL_FROM = "overlord@example.org";
    public static String DEFAULT_GOVERNANCE_WORKFLOW_GROUP   = "org.overlord.dtgov";
    public static String DEFAULT_GOVERNANCE_WORKFLOW_NAME    = "dtgov-workflows";
    public static String DEFAULT_GOVERNANCE_WORKFLOW_VERSION = "1.0.0";
    public static String DEFAULT_GOVERNANCE_WORKFLOW_PACKAGE = "SRAMPPackage";
    public static String DEFAULT_GOVERNANCE_USER = "admin";
    public static String DEFAULT_GOVERNANCE_PASSWORD = "overlord";
    public static String DEFAULT_RHQ_USER = "rhqadmin";
    public static String DEFAULT_RHQ_PASSWORD = "rhqadmin";
    public static String DEFAULT_RHQ_BASEURL = "http://localhost:7080";

    public Governance() {
        super();
        if (configuration == null) {
            read();
        }
    }

    private Configuration configuration = null;

    protected synchronized void read() {
        try {
            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new SystemConfiguration());
            //config.addConfiguration(new JNDIConfiguration("java:comp/env/overlord/s-ramp"));
            String configFile = config.getString(GovernanceConstants.GOVERNANCE_FILE_NAME, "governance.config.txt");
            Long refreshDelay = config.getLong(GovernanceConstants.GOVERNANCE_FILE_REFRESH, 5000l);
            URL url = Governance.class.getClassLoader().getResource(configFile);
            if (url==null) {
                log.warn("Cannot find " + configFile);
            } else {
                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(url);
                FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
                fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
                propertiesConfiguration.setReloadingStrategy(fileChangedReloadingStrategy);
                config.addConfiguration(propertiesConfiguration);
            }
            configuration = config;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String validate() throws ConfigException {
        StringBuffer configuration = new StringBuffer();
        try {
            configuration.append("Governance configuration:").append("\n");
            configuration.append(GovernanceConstants.GOVERNANCE_BPM_URL       + ": " + getBpmUrl()).append("\n");
            configuration.append(GovernanceConstants.GOVERNANCE_BPM_USER      + ": " + getBpmUser()).append("\n");
            configuration.append(GovernanceConstants.GOVERNANCE_BPM_PASSWORD  + ": " + getBpmPassword().replaceAll(".", "*")).append("\n");

            configuration.append(GovernanceConstants.SRAMP_REPO_URL           + ": " + getSrampUrl()).append("\n");
            configuration.append(GovernanceConstants.SRAMP_REPO_USER          + ": " + getSrampUser()).append("\n");
            configuration.append(GovernanceConstants.SRAMP_REPO_PASSWORD      + ": " + getSrampPassword()).append("\n");
            configuration.append(GovernanceConstants.SRAMP_REPO_VALIDATING    + ": " + getSrampValidating()).append("\n");
            configuration.append(GovernanceConstants.SRAMP_REPO_AUTH_PROVIDER + ": " + getSrampAuthProvider()).append("\n");
            configuration.append(GovernanceConstants.SRAMP_REPO_SAML_ISSUER   + ": " + getSrampAuthProvider()).append("\n");
            configuration.append(GovernanceConstants.SRAMP_REPO_SAML_SERVICE  + ": " + getSrampAuthProvider()).append("\n");

            int i=1;
            for (Query query : getQueries()) {
                configuration.append("Query ").append(i++).append("\n");
                configuration.append(query.toString()).append("\n\n");
            }
            i=1;
            for (String name : getTargets().keySet()) {
                configuration.append("Target ").append(i++).append("\n");
                configuration.append(getTargets().get(name).toString()).append("\n\n");
            }
            log.debug(configuration.toString());
            return configuration.toString();
        } catch (ConfigException e) {
            throw e;
        } catch (MalformedURLException e) {
            throw new ConfigException(e);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    public String getBpmUser() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_BPM_USER, DEFAULT_GOVERNANCE_USER);
    }

    public String getBpmPassword() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_BPM_PASSWORD, DEFAULT_GOVERNANCE_PASSWORD);
    }
    
    public String getOverlordUser() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_USER, DEFAULT_GOVERNANCE_USER);
    }

    public String getOverlordPassword() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_PASSWORD, DEFAULT_GOVERNANCE_PASSWORD);
    }
    
    public String getRhqUser() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_RHQ_USER, DEFAULT_RHQ_USER);
    }

    public String getRhqPassword() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_RHQ_PASSWORD, DEFAULT_GOVERNANCE_PASSWORD);
    }

    public URL getBpmUrl() throws MalformedURLException {
        return new URL(configuration.getString(GovernanceConstants.GOVERNANCE_BPM_URL, "http://localhost:8080/gwt-console-server"));
    }

    /**
     * This returns the baseURL, which by default is http://localhost:8080/s-ramp-server
     */
    public URL getSrampUrl() throws MalformedURLException {
        return new URL(configuration.getString(GovernanceConstants.SRAMP_REPO_URL, "http://localhost:8080/s-ramp-server"));
    }

    public String getSrampUser() {
        return configuration.getString(GovernanceConstants.SRAMP_REPO_USER, "admin");
    }

    public String getSrampPassword() {
        return configuration.getString(GovernanceConstants.SRAMP_REPO_PASSWORD, "overlord");
    }

    public Class<?> getSrampAuthProvider() throws Exception {
        String authProviderClassName = configuration.getString(
                GovernanceConstants.SRAMP_REPO_AUTH_PROVIDER,
                org.overlord.sramp.governance.auth.BasicAuthenticationProvider.class.getName());
        if (authProviderClassName == null)
            return null;
        return Class.forName(authProviderClassName);
    }

    public boolean getSrampValidating() throws Exception {
        return "true".equals(configuration.getString(GovernanceConstants.SRAMP_REPO_VALIDATING, "false"));
    }

    public String getSrampSamlIssuer() {
        return configuration.getString(GovernanceConstants.SRAMP_REPO_SAML_ISSUER, "/s-ramp-governance");
    }

    public String getSrampSamlService() {
        return configuration.getString(GovernanceConstants.SRAMP_REPO_SAML_SERVICE, "/s-ramp-server");
    }

    /**
     * This returns the governance baseURL, which by default is http://localhost:8080/s-ramp-server
     */
    public String getGovernanceUrl() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_URL, "http://localhost:8080/s-ramp-governance");
    }

    public Map<String,Target> getTargets() throws ConfigException {
        Map<String,Target> targets = new HashMap<String,Target>();
        String[] targetStrings = configuration.getStringArray(GovernanceConstants.GOVERNANCE_TARGETS);
        StringBuffer errors = new StringBuffer(TARGET_ERROR);
        boolean hasErrors = false;
        for (String targetString : targetStrings) {
            String[] info = targetString.split("\\|");
            if (info.length != 3) {
                hasErrors = true;
                errors.append(targetString).append("\n");
            }
            if (!hasErrors) {
            	if (Target.TYPE.COPY.toString().equalsIgnoreCase(info[1])) {
            		Target target = new Target(info[0],info[1]);
            		targets.put(target.getName(), target);
            	} else if (Target.TYPE.RHQ.toString().equalsIgnoreCase(info[1])) {
            		String rhqConfigStr = info[2].replaceAll("\\{rhq.user\\}",    DEFAULT_RHQ_USER)
            									 .replaceAll("\\{rhq.password\\}",DEFAULT_RHQ_PASSWORD)
            									 .replaceAll("\\{rhq.baseUrl\\}", DEFAULT_RHQ_BASEURL);
            				
            		String[] rhqConfig = rhqConfigStr.split("\\:\\:");
            		
            		Target target = new Target(info[0],rhqConfig[0], rhqConfig[1], rhqConfig[2]);
            		targets.put(target.getName(), target);
            	}
            }
        }
        if (hasErrors) {
            throw new ConfigException(errors.toString());
        }
        return targets;
    }

    public Set<Query> getQueries() throws ConfigException {
        Set<Query> queries = new HashSet<Query>();
        String[] queryStrings = configuration.getStringArray(GovernanceConstants.GOVERNANCE_QUERIES);
        StringBuffer errors = new StringBuffer(QUERY_ERROR);
        boolean hasErrors = false;
        for (String queryString : queryStrings) {
            String[] info = queryString.split("\\|");
            if (info.length != 3) {
                hasErrors = true;
                errors.append(queryString).append("\n");
            }
            if (!hasErrors) {
                String params = info[2];
                params = params.replaceAll("\\{governance.url\\}", getGovernanceUrl());
                Query query = new Query(info[0],info[1],params);
                queries.add(query);
            }
        }
        if (hasErrors) {
            throw new ConfigException(errors.toString());
        }
        return queries;
    }

    public Map<String,NotificationDestinations> getNotificationDestinations(String channel) throws ConfigException {
        Map<String,NotificationDestinations> destinationMap = new HashMap<String,NotificationDestinations>();
        String[] destinationStrings = configuration.getStringArray(GovernanceConstants.GOVERNANCE + channel);
        StringBuffer errors = new StringBuffer(NOTIFICATION_ERROR);
        boolean hasErrors = false;
        for (String destinationString : destinationStrings) {
            String[] info = destinationString.split("\\|");
            if (info.length != 3) {
                hasErrors = true;
                errors.append(destinationString).append("\n");
            }
            if (!hasErrors) {
                NotificationDestinations destination = new NotificationDestinations(info[0],info[1], info[2]);
                destinationMap.put(destination.getName(), destination);
            }
        }
        if (hasErrors) {
            throw new ConfigException(errors.toString());
        }
        return destinationMap;
    }

    public long getQueryInterval() {
        return configuration.getLong(GovernanceConstants.GOVERNANCE_QUERY_INTERVAL, 300000l); //5 min default
    }

    public long getAcceptableLagtime() {
        return configuration.getLong(GovernanceConstants.GOVERNANCE_ACCEPTABLE_LAG, 1000l); //1 s
    }

    public String getJNDIEmailName() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_JNDI_EMAIL_REF, DEFAULT_JNDI_EMAIL_REF);
    }

    public String getDefaultEmailDomain() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_EMAIL_DOMAIN, DEFAULT_EMAIL_DOMAIN);
    }

    public String getDefaultEmailFromAddress() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_EMAIL_FROM, DEFAULT_EMAIL_FROM);
    }
    
    public String getSrampWagonVersion() {
        return Release.getVersionFromManifest(GovernanceConstants.SRAMP_WAGON_JAR);
    }
    
    public Boolean getSrampWagonSnapshots() {
        return configuration.getBoolean(GovernanceConstants.SRAMP_WAGON_SNAPSHOTS, true);
    }
    
    public Boolean getSrampWagonReleases() {
        return configuration.getBoolean(GovernanceConstants.SRAMP_WAGON_RELEASES, true);
    }
    
    public String getGovernanceWorkflowGroup() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_WORKFLOW_GROUP, DEFAULT_GOVERNANCE_WORKFLOW_GROUP);
    }
    
    public String getGovernanceWorkflowName() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_WORKFLOW_NAME, DEFAULT_GOVERNANCE_WORKFLOW_NAME);
    }
    
    public String getGovernanceWorkflowVersion() {
    	String defaultDtGovVersion = Release.getVersionFromManifest(GovernanceConstants.GOVERNANCE_DTGOV);
    	if (defaultDtGovVersion==null || defaultDtGovVersion.equals("unknown")) defaultDtGovVersion = DEFAULT_GOVERNANCE_WORKFLOW_VERSION;
        return configuration.getString(GovernanceConstants.GOVERNANCE_WORKFLOW_VERSION, defaultDtGovVersion);
    }
    
    public String getGovernanceWorkflowPackage() {
        return configuration.getString(GovernanceConstants.GOVERNANCE_WORKFLOW_PACKAGE, DEFAULT_GOVERNANCE_WORKFLOW_PACKAGE);
    }
}
