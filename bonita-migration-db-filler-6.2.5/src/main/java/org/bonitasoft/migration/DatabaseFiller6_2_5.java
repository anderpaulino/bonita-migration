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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.actor.ActorCriterion;
import org.bonitasoft.engine.bpm.flownode.TimerType;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.test.APITestUtil;
import org.bonitasoft.engine.test.wait.WaitForPendingTasks;

/**
 * @author Elias Ricken de Medeiros
 * 
 */
public class DatabaseFiller6_2_5 extends SimpleDatabaseFiller6_0_2 {

    public static void main(final String[] args) throws Exception {
        DatabaseFiller6_2_5 databaseFiller = new DatabaseFiller6_2_5();
        databaseFiller.execute(1, 1, 1, 1);
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.migration.DatabaseFiller6_0_2#fillDatabase(int, int, int, int)
     */
    @Override
    public Map<String, String> fillDatabase(final int nbProcessesDefinitions, final int nbProcessInstances, final int nbWaitingEvents, final int nbDocuments)
            throws BonitaException,
            Exception {
        logger.info("Starting to fill the database");
        APISession session = APITestUtil.loginDefaultTenant();
        Map<String, String> stats = new HashMap<String, String>();
        stats.putAll(fillOrganization(session));
        stats.putAll(fillProcesses(session, nbProcessesDefinitions, nbProcessInstances));
        stats.putAll(fillProcessesWithEvents(session, nbWaitingEvents));
        stats.putAll(fillCompletedProcess(session));

        // 6.2.3 specific
        stats.putAll(fillProsessesWithMessageAndTimer(session));

        APITestUtil.logoutTenant(session);
        logger.info("Finished to fill the database");
        return stats;
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
    private Map<? extends String, ? extends String> fillProsessesWithMessageAndTimer(final APISession session) throws Exception {
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);
        User john = identityAPI.createUser("john", "bpm");
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
        DesignProcessDefinition processWithTimer = new ProcessDefinitionBuilder().createNewInstance("ProcessWithStartTimer", "1.0")
                .addStartEvent("start")
                .addTimerEventTriggerDefinition(TimerType.CYCLE, new ExpressionBuilder().createConstantStringExpression("0/15 * * * * ?"))
                .addEndEvent("end").addMessageEventTrigger("message1", new ExpressionBuilder().createConstantStringExpression("ProcessWithStartMessage"))
                .addTransition("start", "end").getProcess();

        DesignProcessDefinition processWithMessage = new ProcessDefinitionBuilder()
                .createNewInstance("ProcessWithStartMessage", "1.0")
                .addActor("johnActor")
                .addStartEvent("start")
                .addMessageEventTrigger("message1")
                .addUserTask("johnTask", "johnActor")
                .addDisplayName(
                        new ExpressionBuilder().createGroovyScriptExpression("displayName", "println('Executed the user task');return 'user task';",
                                String.class.getName()))
                .addTransition("start", "johnTask").getProcess();

        ProcessDefinition processDefinition = processAPI.deploy(processWithMessage);
        processAPI.addUserToActor(processAPI.getActors(processDefinition.getId(), 0, 10, ActorCriterion.NAME_ASC).get(0).getId(), john.getId());
        processAPI.enableProcess(processDefinition.getId());

        processAPI.deployAndEnableProcess(processWithTimer);

        if (!new WaitForPendingTasks(100, 40000, 1, john.getId(), processAPI).waitUntil()) {
            throw new IllegalStateException("process do not work");
        }

        return Collections.emptyMap();
    }
}