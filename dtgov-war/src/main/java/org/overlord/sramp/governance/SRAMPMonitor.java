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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.commons.codec.binary.Base64;
import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.sramp.client.SrampClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
@Singleton
public class SRAMPMonitor {

    private Logger log = LoggerFactory.getLogger(this.getClass());
	Governance governance = new Governance();

	private long interval = governance.getQueryInterval();

	@Resource
	private TimerService timerService;
	private Timer timer;

	public SRAMPMonitor() {
	}

	public void init () {
		TimerConfig timerConfig = new TimerConfig(null, false);
		this.timer = timerService.createIntervalTimer(interval, interval, timerConfig);
	}

	public boolean cancel() {
		this.timer.cancel();
		return true;
	}

	@Timeout
    public synchronized void executeMonitoring(Timer timer)
	{
	    try {
    		if (isAppserverReady()) {
    			
    			long startTime = System.currentTimeMillis();
    			QueryExecutor.execute();
                long endTime   = System.currentTimeMillis();

                if ((endTime-startTime) > interval) {
                    log.debug(Messages.i18n.format("SRAMPMonitor.IntervalExceeded", //$NON-NLS-1$
                                    interval, (endTime - startTime)));
                } else {
                	log.debug(Messages.i18n.format("SRAMPMonitor.TaskTiming", (endTime - startTime))); //$NON-NLS-1$
                }
    		} else {
    			log.debug(Messages.i18n.format("SRAMPMonitor.NotReady")); //$NON-NLS-1$
    		}
	    } catch (ConfigException confEx) {
	        log.error(confEx.getMessage());
	    } catch (SrampClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 	}

	/**
	 * Checks if we can ready the S-RAMP repository as well as the BPM API.
	 *
	 * @return
	 * @throws MalformedURLException
	 */
	private boolean isAppserverReady() throws MalformedURLException {
	    boolean isReady = true;
	    String serviceDocumentUrl = governance.getSrampUrl().toExternalForm() + "/s-ramp/servicedocument"; //$NON-NLS-1$
	    isReady =  urlExists(serviceDocumentUrl);
	    if (!isReady) log.debug(Messages.i18n.format("SRAMPMonitor.CannotConnect", governance.getSrampUrl().toExternalForm())); //$NON-NLS-1$
	    return isReady;
	}

    /**
     * Returns true if the given URL can be accessed.
     * @param checkUrl
     */
    public boolean urlExists(String checkUrl) {
    	HttpURLConnection checkConnection = null;
        try {
            URL checkURL = new URL(checkUrl);
            checkConnection = (HttpURLConnection) checkURL.openConnection();
            checkConnection.setRequestMethod("HEAD"); //$NON-NLS-1$
            checkConnection.setConnectTimeout(10000);
            checkConnection.setReadTimeout(10000);
            addAuthorization(checkConnection);
            checkConnection.connect();
            return (checkConnection.getResponseCode() == 200);
        } catch (Exception e) {
            return false;
        } finally {
        	if (checkConnection!=null) checkConnection.disconnect();
        }
    }

    /**
     * Adds Authorization config to the connection prior to the request
     * being sent to the server.
     * @param connection
     */
    private void addAuthorization(HttpURLConnection connection) {
    	Governance governance = new Governance();
    	String username = governance.getSrampUser();
    	String password = governance.getSrampPassword();

        if (username != null && password != null) {
            String b64Auth = Base64.encodeBase64String((username + ":" + password).getBytes()).trim(); //$NON-NLS-1$
            connection.setRequestProperty("Authorization", "Basic " + b64Auth); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            log.warn(Messages.i18n.format("SRAMPMonitor.MissingCreds")); //$NON-NLS-1$
        }
    }


}
