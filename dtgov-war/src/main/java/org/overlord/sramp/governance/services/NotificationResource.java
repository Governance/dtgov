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
package org.overlord.sramp.governance.services;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.governance.Governance;
import org.overlord.sramp.governance.GovernanceConstants;
import org.overlord.sramp.governance.NotificationDestinations;
import org.overlord.sramp.governance.SlashDecoder;
import org.overlord.sramp.governance.SrampAtomApiClientFactory;
import org.overlord.sramp.governance.ValueEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JAX-RS resource that handles notification specific tasks.
 *
 */
@Path("/notify")
public class NotificationResource {

    private Session mailSession;

    private static Logger logger = LoggerFactory.getLogger(NotificationResource.class);
    private Governance governance = new Governance();

    //https://issues.jboss.org/browse/DTGOV-107 also support sending mail on tomcat
    /**
     * Constructor.
     * @throws NamingException
     */
    public NotificationResource() {
//        InitialContext context;
//        try {
//            String jndiEmailRef = governance.getJNDIEmailName();
//            context = new InitialContext();
//            mailSession = (Session) context.lookup(jndiEmailRef);
//            if (mailSession==null) {
//                logger.error(Messages.i18n.format("NotificationResource.JndiLookupFailed", jndiEmailRef)); //$NON-NLS-1$
//            }
//        } catch (NamingException e) {
//            logger.error(e.getMessage(),e);
//        }

    }

    /**
     * POST to email a notification about an artifact.
     *
     * @param environment
     * @param uuid
     * @throws SrampAtomException
     */
    @POST
    @Path("email/{group}/{template}/{target}/{uuid}")
    @Produces("application/xml")
    public Map<String,ValueEntity> emailNotification(@Context HttpServletRequest request,
            @PathParam("group") String group,
            @PathParam("template") String template,
            @PathParam("target") String target,
            @PathParam("uuid") String uuid) throws Exception {
    	
    	
    	Map<String, ValueEntity> results = new HashMap<String,ValueEntity>();
//        try {
//            // 0. run the decoder on the arguments, after replacing * by % (this so parameters can
//            //    contain slashes (%2F)
//            group = SlashDecoder.decode(group);
//            template = SlashDecoder.decode(template);
//            target = SlashDecoder.decode(target);
//            uuid = SlashDecoder.decode(uuid);
//
//            // 1. get the artifact from the repo
//            SrampAtomApiClient client = SrampAtomApiClientFactory.createAtomApiClient();
//            String query = String.format("/s-ramp[@uuid='%s']", uuid); //$NON-NLS-1$
//            QueryResultSet queryResultSet = client.query(query);
//            if (queryResultSet.size() == 0) {
//            	results.put(GovernanceConstants.STATUS, new ValueEntity("fail")); //$NON-NLS-1$
//            	results.put(GovernanceConstants.MESSAGE, new ValueEntity("Could not obtain artifact from repository.")); //$NON-NLS-1$
//                return results;
//            }
//            ArtifactSummary artifactSummary = queryResultSet.iterator().next();
//
//            // 2. get the destinations for this group
//            NotificationDestinations destinations = governance.getNotificationDestinations("email").get(group); //$NON-NLS-1$
//            if (destinations==null) {
//                destinations = new NotificationDestinations(group,
//                        governance.getDefaultEmailFromAddress(),
//                        group + "@" + governance.getDefaultEmailDomain()); //$NON-NLS-1$
//            }
//
//            // 3. send the email notification
//            try {
//                MimeMessage m = new MimeMessage(mailSession);
//                Address from = new InternetAddress(destinations.getFromAddress());
//                Address[] to = new InternetAddress[destinations.getToAddresses().length];
//                for (int i=0; i<destinations.getToAddresses().length;i++) {
//                    to[i] = new InternetAddress(destinations.getToAddresses()[i]);
//                }
//                m.setFrom(from);
//                m.setRecipients(Message.RecipientType.TO, to);
//
//                String subject = "/governance-email-templates/" + template  + ".subject.tmpl"; //$NON-NLS-1$ //$NON-NLS-2$
//                URL subjectUrl = Governance.class.getClassLoader().getResource(subject);
//                if (subjectUrl!=null) subject=IOUtils.toString(subjectUrl);
//                subject = subject.replaceAll("\\$\\{uuid}", uuid); //$NON-NLS-1$
//                subject = subject.replaceAll("\\$\\{name}", artifactSummary.getName()); //$NON-NLS-1$
//                subject = subject.replaceAll("\\$\\{target}", target); //$NON-NLS-1$
//                m.setSubject(subject);
//
//                m.setSentDate(new java.util.Date());
//                String content = "/governance-email-templates/" + template + ".body.tmpl"; //$NON-NLS-1$ //$NON-NLS-2$
//                URL contentUrl = Governance.class.getClassLoader().getResource(content);
//                if (contentUrl!=null) content=IOUtils.toString(contentUrl);
//                content = content.replaceAll("\\$\\{uuid}", uuid); //$NON-NLS-1$
//                content = content.replaceAll("\\$\\{name}", artifactSummary.getName()); //$NON-NLS-1$
//                content = content.replaceAll("\\$\\{target}", target); //$NON-NLS-1$
//                content = content.replaceAll("\\$\\{dtgovurl}", governance.getDTGovUiUrl()); //$NON-NLS-1$
//                m.setContent(content,"text/plain"); //$NON-NLS-1$
//                Transport.send(m);
//            } catch (javax.mail.MessagingException e) {
//                logger.error(e.getMessage(),e);
//            }
//
//            // 4. build the response
//            results.put(GovernanceConstants.STATUS, new ValueEntity("success")); //$NON-NLS-1$
//            
            return results;
//        } catch (Exception e) {
//            logger.error(Messages.i18n.format("NotificationResource.EmailError", e.getMessage(), e)); //$NON-NLS-1$
//            throw new SrampAtomException(e);
//        }
    }

}
