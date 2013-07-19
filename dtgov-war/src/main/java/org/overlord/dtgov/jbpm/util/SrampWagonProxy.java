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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.wagon.SrampWagon;

/**
 * A simple wrapper around the SrampWagon to fix a problem when using the wagon
 * from within jbpm.  In that use-case the logger isn't injected into the wagon
 * and results in an NPE.  This proxy simply performs that injection and delegates
 * all other calls to the normal s-ramp wagon.
 *
 * @author eric.wittmann@redhat.com
 */
public class SrampWagonProxy implements Wagon {

    private static final String MAVEN_META_DATA = "\r\n" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
            "<metadata modelVersion=\"1.1.0\">\r\n" +
            "  <groupId>GROUP_ID</groupId>\r\n" +
            "  <artifactId>ARTIFACT_ID</artifactId>\r\n" +
            "  <version>VERSION</version>\r\n" +
            "  <versioning>\r\n" +
            "    <snapshot>\r\n" +
            "      <timestamp>LAST_MODIFIED_TIME</timestamp>\r\n" +
            "      <buildNumber>1</buildNumber>\r\n" +
            "    </snapshot>\r\n" +
            "    <lastUpdated>20130718220725</lastUpdated>\r\n" +
            "    <snapshotVersions>\r\n" +
            "      <snapshotVersion>\r\n" +
            "        <extension>jar</extension>\r\n" +
            "        <value>VERSION-1</value>\r\n" +
            "        <updated>LAST_MODIFIED_TIME</updated>\r\n" +
            "      </snapshotVersion>\r\n" +
            "    </snapshotVersions>\r\n" +
            "  </versioning>\r\n" +
            "</metadata>\r\n" +
            "";

    private SrampWagon delegate;
    private Date wagonDate = new Date();

    /**
     * Constructor.
     */
    public SrampWagonProxy() {
        delegate = new SrampWagon();
        injectLoggerInto(delegate);
    }

    /**
     * Injects a logger into the SrampWagon instance.
     *
     * @param wagon
     */
    private void injectLoggerInto(SrampWagon wagon) {
        Logger logger = new ConsoleLogger();
        try {
            Field field = SrampWagon.class.getDeclaredField("logger");
            field.setAccessible(true);
            field.set(wagon, logger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.maven.wagon.Wagon#get(java.lang.String, java.io.File)
     */
    @Override
    public void get(String resourceName, File destination) throws TransferFailedException,
            ResourceDoesNotExistException, AuthorizationException {
        if (resourceName.endsWith("maven-metadata.xml")) {
            OutputStream out = null;
            try {
                out = new FileOutputStream(destination);
                String xml = generateMavenMetaData();
                StringReader in = new StringReader(xml);
                IOUtils.copy(in, out);
                return;
            } catch (Exception e) {
                throw new TransferFailedException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
        delegate.get(resourceName, destination);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#getIfNewer(java.lang.String, java.io.File, long)
     */
    @Override
    public boolean getIfNewer(String resourceName, File destination, long timestamp)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        if (resourceName.endsWith("maven-metadata.xml")) {
            OutputStream out = null;
            try {
                out = new FileOutputStream(destination);
                String xml = generateMavenMetaData();
                StringReader in = new StringReader(xml);
                IOUtils.copy(in, out);
                return true;
            } catch (Exception e) {
                throw new TransferFailedException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
        return delegate.getIfNewer(resourceName, destination, timestamp);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#put(java.io.File, java.lang.String)
     */
    @Override
    public void put(File source, String destination) throws TransferFailedException,
            ResourceDoesNotExistException, AuthorizationException {
        delegate.put(source, destination);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#putDirectory(java.io.File, java.lang.String)
     */
    @Override
    public void putDirectory(File sourceDirectory, String destinationDirectory)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        delegate.putDirectory(sourceDirectory, destinationDirectory);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#resourceExists(java.lang.String)
     */
    @Override
    public boolean resourceExists(String resourceName) throws TransferFailedException, AuthorizationException {
        return true;
    }

    /**
     * @see org.apache.maven.wagon.Wagon#getFileList(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getFileList(String destinationDirectory) throws TransferFailedException,
            ResourceDoesNotExistException, AuthorizationException {
        return delegate.getFileList(destinationDirectory);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#supportsDirectoryCopy()
     */
    @Override
    public boolean supportsDirectoryCopy() {
        return delegate.supportsDirectoryCopy();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#getRepository()
     */
    @Override
    public Repository getRepository() {
        return delegate.getRepository();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#connect(org.apache.maven.wagon.repository.Repository)
     */
    @Override
    public void connect(Repository source) throws ConnectionException, AuthenticationException {
        delegate.connect(source);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#connect(org.apache.maven.wagon.repository.Repository, org.apache.maven.wagon.proxy.ProxyInfo)
     */
    @Override
    public void connect(Repository source, ProxyInfo proxyInfo) throws ConnectionException,
            AuthenticationException {
        delegate.connect(source, proxyInfo);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#connect(org.apache.maven.wagon.repository.Repository, org.apache.maven.wagon.proxy.ProxyInfoProvider)
     */
    @Override
    public void connect(Repository source, ProxyInfoProvider proxyInfoProvider) throws ConnectionException,
            AuthenticationException {
        delegate.connect(source, proxyInfoProvider);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#connect(org.apache.maven.wagon.repository.Repository, org.apache.maven.wagon.authentication.AuthenticationInfo)
     */
    @Override
    public void connect(Repository source, AuthenticationInfo authenticationInfo) throws ConnectionException,
            AuthenticationException {
        delegate.connect(source, authenticationInfo);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#connect(org.apache.maven.wagon.repository.Repository, org.apache.maven.wagon.authentication.AuthenticationInfo, org.apache.maven.wagon.proxy.ProxyInfo)
     */
    @Override
    public void connect(Repository source, AuthenticationInfo authenticationInfo, ProxyInfo proxyInfo)
            throws ConnectionException, AuthenticationException {
        delegate.connect(source, authenticationInfo, proxyInfo);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#connect(org.apache.maven.wagon.repository.Repository, org.apache.maven.wagon.authentication.AuthenticationInfo, org.apache.maven.wagon.proxy.ProxyInfoProvider)
     */
    @Override
    public void connect(Repository source, AuthenticationInfo authenticationInfo,
            ProxyInfoProvider proxyInfoProvider) throws ConnectionException, AuthenticationException {
        delegate.connect(source, authenticationInfo, proxyInfoProvider);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#openConnection()
     */
    @Override
    public void openConnection() throws ConnectionException, AuthenticationException {
        delegate.openConnection();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#disconnect()
     */
    @Override
    public void disconnect() throws ConnectionException {
        delegate.disconnect();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#setTimeout(int)
     */
    @Override
    public void setTimeout(int timeoutValue) {
        delegate.setTimeout(timeoutValue);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#getTimeout()
     */
    @Override
    public int getTimeout() {
        return delegate.getTimeout();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#setReadTimeout(int)
     */
    @Override
    public void setReadTimeout(int timeoutValue) {
        delegate.setReadTimeout(timeoutValue);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#getReadTimeout()
     */
    @Override
    public int getReadTimeout() {
        return delegate.getReadTimeout();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#addSessionListener(org.apache.maven.wagon.events.SessionListener)
     */
    @Override
    public void addSessionListener(SessionListener listener) {
        delegate.addSessionListener(listener);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#removeSessionListener(org.apache.maven.wagon.events.SessionListener)
     */
    @Override
    public void removeSessionListener(SessionListener listener) {
        delegate.removeSessionListener(listener);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#hasSessionListener(org.apache.maven.wagon.events.SessionListener)
     */
    @Override
    public boolean hasSessionListener(SessionListener listener) {
        return delegate.hasSessionListener(listener);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#addTransferListener(org.apache.maven.wagon.events.TransferListener)
     */
    @Override
    public void addTransferListener(TransferListener listener) {
        delegate.addTransferListener(listener);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#removeTransferListener(org.apache.maven.wagon.events.TransferListener)
     */
    @Override
    public void removeTransferListener(TransferListener listener) {
        delegate.removeTransferListener(listener);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#hasTransferListener(org.apache.maven.wagon.events.TransferListener)
     */
    @Override
    public boolean hasTransferListener(TransferListener listener) {
        return delegate.hasTransferListener(listener);
    }

    /**
     * @see org.apache.maven.wagon.Wagon#isInteractive()
     */
    @Override
    public boolean isInteractive() {
        return delegate.isInteractive();
    }

    /**
     * @see org.apache.maven.wagon.Wagon#setInteractive(boolean)
     */
    @Override
    public void setInteractive(boolean interactive) {
        delegate.setInteractive(interactive);
    }

    /**
     * Generates a maven-metadata.xml.
     */
    protected String generateMavenMetaData() {
        Governance governance = new Governance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.hhmmss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tstamp = df.format(wagonDate);
        return MAVEN_META_DATA
                .replace("VERSION", governance.getGovernanceWorkflowVersion())
                .replace("ARTIFACT_ID", governance.getGovernanceWorkflowName())
                .replace("GROUP_ID", governance.getGovernanceWorkflowGroup())
                .replace("LAST_MODIFIED_TIME", tstamp);
    }
}
