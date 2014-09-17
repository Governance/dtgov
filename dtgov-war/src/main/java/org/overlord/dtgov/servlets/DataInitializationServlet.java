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
package org.overlord.dtgov.servlets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.overlord.dtgov.server.i18n.Messages;
import org.overlord.dtgov.services.InitService;
import org.overlord.sramp.governance.Governance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet loaded on startup that creates a thread that is executed after the
 * server is started and all the app deployed. It's function is to initialize
 * the dtgov data.
 *
 * @author David Virgil Naranjo
 */
@ApplicationScoped
public class DataInitializationServlet extends HttpServlet{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    private InitService service;

    private final Governance governance = new Governance();

    private static Logger logger = LoggerFactory.getLogger(DataInitializationServlet.class);

    public final static int MILLISECONDS_UNTIL_SERVICE_STARTED = 30000;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        try {
            if (governance.isDataInicializationEnabled()) {
                Thread t = new InitializationThread();
                t.start();
            } else {
                logger.info(Messages.i18n.format("data.initialization.servlet.not.enabled"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public class InitializationThread extends Thread {

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            try {
                logger.info(Messages.i18n.format("data.initialization.servlet.sleeping", MILLISECONDS_UNTIL_SERVICE_STARTED));
                sleep(MILLISECONDS_UNTIL_SERVICE_STARTED);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            service.init();
        }
    }
}
