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

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.test.APITestUtil;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class SimpleDatabaseFiller6_0_2 extends DatabaseFiller6_0_2 {
    
    public static final String USER_TASK_NAME = "step1";

    public static final String PROCESS_VERSION = "1.0";

    public static final String PROCESS_NAME = "SimpleProcessToBeMigrated";

    @Override
    public Map<String, String> fillDatabase(int nbProcessesDefinitions, int nbProcessInstances, int nbWaitingEvents, int nbDocuments) throws BonitaException,
            Exception {
        logger.info("Starting to fill the database");
        APISession session = APITestUtil.loginDefaultTenant();
        Map<String, String> stats = new HashMap<String, String>();
        stats.putAll(fillOrganization(session));
        stats.putAll(fillSimpleProcess(session, nbProcessInstances));
        APITestUtil.logoutTenant(session);
        logger.info("Finished to fill the database");
        return stats;
    }
    
    private Map<String, String> fillSimpleProcess(APISession session, int nbProcessInstances) throws Exception {
        ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
        IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(session);
        ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance(PROCESS_NAME, PROCESS_VERSION);
        builder.addActor("delivery");
        builder.addStartEvent("start");
        builder.addUserTask(USER_TASK_NAME, "delivery");
        builder.addEndEvent("end");
        builder.addTransition("start", USER_TASK_NAME);
        builder.addTransition(USER_TASK_NAME, "end");

        ProcessDefinition processDefinition = processAPI.deploy(builder.done());
        processAPI.addUserToActor("delivery", processDefinition, identityAPI.getUserByUserName("william.jobs").getId());
        processAPI.enableProcess(processDefinition.getId());
        for (int j = 0; j < nbProcessInstances; j++) {
            processAPI.startProcess(processDefinition.getId());
        }
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("Process definitions", String.valueOf(1));
        map.put("Process instances", String.valueOf(nbProcessInstances));
        return map;
    }

}
