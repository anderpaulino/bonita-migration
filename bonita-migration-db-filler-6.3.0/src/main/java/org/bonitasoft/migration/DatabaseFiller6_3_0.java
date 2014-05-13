/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.migration;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.test.APITestUtil;
import org.bonitasoft.engine.test.ClientEventUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DatabaseFiller6_3_0 extends SimpleDatabaseFiller6_0_2 {

    public static void main(final String[] args) throws Exception {
        final DatabaseFiller6_3_0 databaseFiller = new DatabaseFiller6_3_0();
        databaseFiller.execute(1, 1, 1, 1);
    }

    @Override
    public void setup() throws BonitaException, IOException, Exception {
        logger.info("Using bonita.home: " + System.getProperty(BONITA_HOME));
        // Force these system properties
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.bonitasoft.engine.local.SimpleMemoryContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.bonitasoft.engine.local");
        springContext = new ClassPathXmlApplicationContext("datasource.xml", "jndi-setup.xml");
        new APITestUtil().createInitializeAndStartPlatformWithDefaultTenant(true);
    }

    @Override
    public Map<String, String> fillDatabase(final int nbProcessesDefinitions, final int nbProcessInstances, final int nbWaitingEvents, final int nbDocuments)
            throws Exception {
        logger.info("Starting to fill the database");
        final APITestUtil apiTestUtil = new APITestUtil();
        final APISession session = apiTestUtil.loginDefaultTenant();
        final Map<String, String> stats = new HashMap<String, String>();
        stats.putAll(fillOrganization(session));
        stats.putAll(fillGroupWithEmptyParentPath(session));
        stats.putAll(fillProcesses(session, nbProcessesDefinitions, nbProcessInstances));
        stats.putAll(fillProcessesWithEvents(session, nbWaitingEvents));
        stats.putAll(fillCompletedProcess(session));

        stats.putAll(fillProsessesWithMessageAndTimer(session));
        apiTestUtil.logoutTenant(session);

        logger.info("Finished to fill the database");
        return stats;
    }

    private Map<? extends String, ? extends String> fillGroupWithEmptyParentPath(final APISession session) throws Exception {
        final IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);
        identityAPI.createGroup("groupWithEmptyParentPath", "");
        return new HashMap<String, String>(1);
    }

    protected HumanTaskInstance waitForUserTask(final APISession session, final ProcessInstance processInstance, final String taskName) throws Exception {
        final CommandAPI commandAPI = TenantAPIAccessor.getCommandAPI(session);
        final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);

        final Map<String, Serializable> readyTaskEvent = ClientEventUtil.getReadyTaskEvent(processInstance.getId(), taskName);
        final Long activityInstanceId = ClientEventUtil.executeWaitServerCommand(commandAPI, readyTaskEvent, APITestUtil.DEFAULT_TIMEOUT);
        final HumanTaskInstance activityInstance = processAPI.getHumanTaskInstance(activityInstanceId);
        assertNotNull(activityInstance);
        return activityInstance;
    }
}
