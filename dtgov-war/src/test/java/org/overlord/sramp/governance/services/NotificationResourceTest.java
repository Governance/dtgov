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
package org.overlord.sramp.governance.services;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;


/**
 * Tests the Notification API.
 *
 * @author kurt.stam@redhat.com
 */
public class NotificationResourceTest {
	
	private static SimpleSmtpServer mailServer;
	private static Integer smtpPort = 25;
	
	@BeforeClass
	public static void init() {
		smtpPort = 9700 + new Random().nextInt(99);
	}
	
    @Test
    public void testMail() {
        try {
        	mailServer = SimpleSmtpServer.start(smtpPort);
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.host", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("mail.smtp.port", String.valueOf(smtpPort)); //$NON-NLS-1$
            Session mailSession = Session.getDefaultInstance(properties);
            MimeMessage m = new MimeMessage(mailSession);
            Address from = new InternetAddress("me@gmail.com"); //$NON-NLS-1$
            Address[] to = new InternetAddress[1];
            to[0] = new InternetAddress("dev@mailinator.com"); //$NON-NLS-1$
            m.setFrom(from);
            m.setRecipients(Message.RecipientType.TO, to);
            m.setSubject("test"); //$NON-NLS-1$
            m.setContent("test","text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
            Transport.send(m);
            
            Assert.assertTrue(mailServer.getReceivedEmailSize() > 0);
            @SuppressWarnings("rawtypes")
			Iterator iter = mailServer.getReceivedEmail();
            while (iter.hasNext()) {
            	SmtpMessage email = (SmtpMessage) iter.next();
            	System.out.println(email.getBody());
            	Assert.assertEquals("test",email.getBody()); //$NON-NLS-1$
			}
            
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally {
        	mailServer.stop();
        }
        
    }
}
