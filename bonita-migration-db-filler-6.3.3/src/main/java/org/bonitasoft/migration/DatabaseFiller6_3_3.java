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

import org.bonitasoft.engine.bpm.bar.BarResource;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.document.DocumentValue;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.operation.OperationBuilder;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.test.APITestUtil;

public class DatabaseFiller6_3_3 extends DatabaseFiller6_3_2 {

    private final APITestUtil apiTestUtil = new APITestUtil();

    public static void main(final String[] args) throws Exception {
        DatabaseFiller6_3_3 databaseFiller = new DatabaseFiller6_3_3();
        databaseFiller.execute(1, 1, 1, 1);
    }

    @Override
    protected void initializePlatform() throws BonitaException {
        apiTestUtil.createInitializeAndStartPlatformWithDefaultTenant(true);
    }



    @Override
    public Map<String, String> fillDatabase(final int nbProcessesDefinitions, final int nbProcessInstances, final int nbWaitingEvents, final int nbDocuments)
            throws Exception {
        logger.info("Starting to fill the database");
        apiTestUtil.loginOnDefaultTenantWithDefaultTechnicalLogger();
        final APISession session = apiTestUtil.getSession();
        final Map<String, String> stats = new HashMap<String, String>();
        stats.putAll(fillOrganization(session));
        stats.putAll(fillSimpleProcess(session, nbProcessInstances));
        stats.putAll(fillProfiles(session));
        stats.putAll(fillDocuments());
        apiTestUtil.loginOnDefaultTenantWithDefaultTechnicalLogger();
        logger.info("Finished to fill the database");
        return stats;
    }

    private Map<? extends String, ? extends String> fillDocuments() throws Exception {
        User user = apiTestUtil.createUser("userForDocuments", "bpm");

        ProcessDefinitionBuilder processWithDocuments = new ProcessDefinitionBuilder().createNewInstance("ProcessWithDocuments", "1.0");
        processWithDocuments.addStartEvent("start");
        processWithDocuments.addUserTask("step1", "actor")
                .addOperation(new OperationBuilder()
                        .createSetDocument("doc1", new ExpressionBuilder()
                                .createGroovyScriptExpression("update doc1",
                                        "return new org.bonitasoft.engine.bpm.document.DocumentValue(\"newContent1\".getBytes(),\"plain/text\",\"file2.txt\")",
                                        DocumentValue.class.getName())))
                .addOperation(new OperationBuilder()
                        .createSetDocument("doc2", new ExpressionBuilder()
                                .createGroovyScriptExpression("create doc2",
                                        "return new org.bonitasoft.engine.bpm.document.DocumentValue(\"newContent2\".getBytes(),\"plain/text\",\"newFile.txt\")",
                                        DocumentValue.class.getName())));
        processWithDocuments.addUserTask("step2", "actor")
                .addTransition("start", "step1").addTransition("step1", "step2");
        processWithDocuments.addDocumentDefinition("doc1").addContentFileName("file.txt").addFile("file.txt").addMimeType("plain/text").addDescription("It is a text file");
        processWithDocuments.addActor("actor");
        BusinessArchiveBuilder businessArchiveBuilder = new BusinessArchiveBuilder().createNewBusinessArchive();
        businessArchiveBuilder.addDocumentResource(new BarResource("file.txt", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.".getBytes()));
        businessArchiveBuilder.setProcessDefinition(processWithDocuments.done());

        ProcessDefinition processDefinition = apiTestUtil.deployAndEnableProcessWithActor(businessArchiveBuilder.done(), "actor", user);

        //finished instance
        ProcessInstance inst1 = apiTestUtil.getProcessAPI().startProcess(processDefinition.getId());
        HumanTaskInstance step1 = apiTestUtil.waitForUserTask("step1",inst1);
        apiTestUtil.assignAndExecuteStep(step1,user.getId());
        HumanTaskInstance step2 = apiTestUtil.waitForUserTask("step2",inst1);
        apiTestUtil.assignAndExecuteStep(step2,user.getId());
        apiTestUtil.waitForProcessToFinish(inst1);

        //instance with step1 having operations executed
        ProcessInstance inst2 = apiTestUtil.getProcessAPI().startProcess(processDefinition.getId());
        step1 = apiTestUtil.waitForUserTask("step1",inst2);
        apiTestUtil.assignAndExecuteStep(step1,user.getId());
        apiTestUtil.waitForUserTask("step2",inst2);

        //instance with document attached using api
        ProcessInstance inst3 = apiTestUtil.getProcessAPI().startProcess(processDefinition.getId());
        apiTestUtil.waitForUserTask("step1",inst3);
        apiTestUtil.getProcessAPI().attachDocument(inst3.getId(),"documentAttachedUsingAPI","doc.txt","plain/text","The content of the file attached using the api".getBytes());
        apiTestUtil.getProcessAPI().attachDocument(inst3.getId(),"urlDocumentAttachedUsingAPI","doc.txt","plain/text","http://MyWebSite.com/file.txt");
        apiTestUtil.getProcessAPI().attachNewDocumentVersion(inst3.getId(),"documentAttachedUsingAPI","doc2.txt","plain/text","The content of the file attached using the api2".getBytes());
        apiTestUtil.getProcessAPI().attachNewDocumentVersion(inst3.getId(),"urlDocumentAttachedUsingAPI","doc2.txt","plain/text","http://MyWebSite.com/file2.txt");


        // just started instance
        ProcessInstance inst4 = apiTestUtil.getProcessAPI().startProcess(processDefinition.getId());
        apiTestUtil.waitForUserTask("step1",inst4);


        return Collections.emptyMap();
    }


}
