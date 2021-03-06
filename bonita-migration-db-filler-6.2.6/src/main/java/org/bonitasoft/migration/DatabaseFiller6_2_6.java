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
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.actor.ActorCriterion;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.TimerType;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.test.APITestUtil;
import org.bonitasoft.engine.test.ClientEventUtil;
import org.bonitasoft.engine.test.wait.WaitForPendingTasks;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DatabaseFiller6_2_6 extends SimpleDatabaseFiller6_0_2 {

    public static void main(final String[] args) throws Exception {
        final DatabaseFiller6_2_6 databaseFiller = new DatabaseFiller6_2_6();
        databaseFiller.execute(1, 1, 1, 1);
    }

    @Override
    public void setup() throws BonitaException, IOException, Exception {
        logger.info("Using bonita.home: " + System.getProperty(BONITA_HOME));
        // Force these system properties
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.bonitasoft.engine.local.SimpleMemoryContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.bonitasoft.engine.local");
        springContext = new ClassPathXmlApplicationContext("datasource.xml", "jndi-setup.xml");
        APITestUtil.createInitializeAndStartPlatformWithDefaultTenant(true);
    }

    @Override
    public Map<String, String> fillDatabase(final int nbProcessesDefinitions, final int nbProcessInstances, final int nbWaitingEvents, final int nbDocuments)
            throws Exception {
        logger.info("Starting to fill the database");
        final APISession session = APITestUtil.loginDefaultTenant();
        final Map<String, String> stats = new HashMap<String, String>();
        stats.putAll(fillOrganization(session));
        stats.putAll(fillProcesses(session, nbProcessesDefinitions, nbProcessInstances));
        stats.putAll(fillProcessesWithEvents(session, nbWaitingEvents));
        stats.putAll(fillCompletedProcess(session));

        stats.putAll(fillProsessesWithMessageAndTimer(session));
        stats.putAll(fillOthers(session));
        stats.putAll(fillExtensions(session));
        APITestUtil.logoutTenant(session);

        // fillProcessStartedFor has it owns login-logout
        stats.putAll(fillProcessStartedFor());
        logger.info("Finished to fill the database");
        return stats;
    }

    /**
     * @param session
     * @return
     * @throws Exception
     */
    protected Map<? extends String, ? extends String> fillOthers(final APISession session) throws Exception {
        return Collections.emptyMap();
    }

    /**
     * 
     * p1 start with timer and send message so p2 start
     * p2 have a user task for john
     * 
     * @param session
     * @return
     * @throws BonitaHomeNotSetException
     */
    @Override
    protected Map<? extends String, ? extends String> fillProsessesWithMessageAndTimer(final APISession session) throws Exception {
        final IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);
        final User john = identityAPI.createUser("john", "bpm");
        final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
        final DesignProcessDefinition processWithTimer = new ProcessDefinitionBuilder().createNewInstance("ProcessWithStartTimer", "1.0")
                .addStartEvent("start")
                .addTimerEventTriggerDefinition(TimerType.CYCLE, new ExpressionBuilder().createConstantStringExpression("0/15 * * * * ?"))
                .addEndEvent("end").addMessageEventTrigger("message1", new ExpressionBuilder().createConstantStringExpression("ProcessWithStartMessage"))
                .addTransition("start", "end").getProcess();

        final DesignProcessDefinition processWithMessage = new ProcessDefinitionBuilder()
                .createNewInstance("ProcessWithStartMessage", "1.0")
                .addActor("johnActor")
                .addStartEvent("start")
                .addMessageEventTrigger("message1")
                .addUserTask("johnTask", "johnActor")
                .addDisplayName(
                        new ExpressionBuilder().createGroovyScriptExpression("displayName", "println('Executed the user task');return 'user task';",
                                String.class.getName()))
                .addTransition("start", "johnTask").getProcess();

        final ProcessDefinition processDefinition = processAPI.deploy(processWithMessage);
        processAPI.addUserToActor(processAPI.getActors(processDefinition.getId(), 0, 10, ActorCriterion.NAME_ASC).get(0).getId(), john.getId());
        processAPI.enableProcess(processDefinition.getId());

        processAPI.deployAndEnableProcess(processWithTimer);

        if (!new WaitForPendingTasks(100, 40000, 1, john.getId(), processAPI).waitUntil()) {
            throw new IllegalStateException("process do not work");
        }

        return Collections.emptyMap();
    }

    /**
     * @param session
     * @return
     * @throws Exception
     */
    protected Map<? extends String, ? extends String> fillExtensions(final APISession session) throws Exception {
        return Collections.emptyMap();
    }

    @Override
    protected InputStream getProfilesXMLStream() {
        return getClass().getResourceAsStream("profiles.xml");
    }

    protected Map<? extends String, ? extends String> fillProcessStartedFor() throws Exception {
        final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
        final APISession session = loginAPI.login("walter.bates", "bpm");
        final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
        final IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);

        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance("ProcessStartedFor", "1.0");
        final String actorName = "actorName";
        builder.addActor(actorName).addUserTask("step1", actorName).addUserTask("step2", actorName).addTransition("step1", "step2");

        final BusinessArchiveBuilder archiveBuilder = new BusinessArchiveBuilder().createNewBusinessArchive();
        archiveBuilder.setProcessDefinition(builder.done());
        final ProcessDefinition processDefinition = processAPI.deploy(archiveBuilder.done());
        final User william = identityAPI.getUserByUserName("william.jobs");
        processAPI.addUserToActor(actorName, processDefinition, william.getId());
        processAPI.enableProcess(processDefinition.getId());
        final ProcessInstance processInstance = processAPI.startProcess(william.getId(), processDefinition.getId());
        final HumanTaskInstance activityInstance = waitForUserTask(session, processInstance, "step1");
        processAPI.assignUserTask(activityInstance.getId(), william.getId());
        processAPI.executeFlowNode(william.getId(), activityInstance.getId());
        waitForUserTask(session, processInstance, "step2");
        loginAPI.logout(session);
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
