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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.naming.Context;

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.PlatformAPI;
import org.bonitasoft.engine.api.PlatformAPIAccessor;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstance;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstancesSearchDescriptor;
import org.bonitasoft.engine.bpm.process.ProcessInstanceState;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.PlatformSession;
import org.bonitasoft.engine.test.APITestUtil;
import org.bonitasoft.engine.test.PlatformTestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Elias Ricken de Medeiros
 */
public class DatabaseCheckerInitiliazer6_3_1 {

    protected static ClassPathXmlApplicationContext springContext;

    protected ProcessAPI processAPI;

    private ProfileAPI profileAPI;

    private IdentityAPI identityApi;

    private CommandAPI commandApi;

    private APISession session;

    private static PlatformTestUtil platformTestUtil = new PlatformTestUtil();

    public static APITestUtil apiTestUtil = new APITestUtil();

    public ProcessAPI getProcessAPI() {
        return processAPI;
    }

    public ProfileAPI getProfileAPI() {
        return profileAPI;
    }

    public IdentityAPI getIdentityApi() {
        return identityApi;
    }

    public CommandAPI getCommandApi() {
        return commandApi;
    }

    public APISession getSession() {
        return session;
    }

    public static PlatformTestUtil getPlatformTestUtil() {
        return platformTestUtil;
    }

    public static APITestUtil getApiTestUtil() {
        return apiTestUtil;
    }

    @BeforeClass
    public static void setup() throws BonitaException {
        setupSpringContext();
        final PlatformSession platformSession = platformTestUtil.loginOnPlatform();
        final PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        platformAPI.startNode();
        platformTestUtil.logoutOnPlatform(platformSession);
    }

    @AfterClass
    public static void teardown() throws BonitaException {
        apiTestUtil.logoutOnTenant();
        final PlatformSession pSession = apiTestUtil.loginOnPlatform();
        final PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(pSession);
        apiTestUtil.stopPlatformAndTenant(platformAPI, false);
        apiTestUtil.logoutOnPlatform(pSession);
        closeSpringContext();
    }

    @Before
    public void before() throws BonitaException {
        apiTestUtil.loginOnDefaultTenantWith(APITestUtil.DEFAULT_TECHNICAL_LOGGER_USERNAME, APITestUtil.DEFAULT_TECHNICAL_LOGGER_PASSWORD);
        session = apiTestUtil.getSession();
        processAPI = TenantAPIAccessor.getProcessAPI(session);
        identityApi = TenantAPIAccessor.getIdentityAPI(session);
        profileAPI = TenantAPIAccessor.getProfileAPI(session);
        commandApi = TenantAPIAccessor.getCommandAPI(session);
    }

    protected static void setupSpringContext() {
        setSystemPropertyIfNotSet("sysprop.bonita.db.vendor", "h2");

        // Force these system properties
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.bonitasoft.engine.local.SimpleMemoryContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.bonitasoft.engine.local");

        springContext = new ClassPathXmlApplicationContext("datasource.xml", "jndi-setup.xml");
    }

    protected static void closeSpringContext() {
        springContext.close();
    }

    protected ClassPathXmlApplicationContext getSpringContext() {
        return springContext;
    }

    protected static void setSystemPropertyIfNotSet(final String property, final String value) {
        System.setProperty(property, System.getProperty(property, value));
    }

    protected HumanTaskInstance waitForUserTask(final String taskName, final long processInstanceId, final int timeout) throws Exception {
        final long now = System.currentTimeMillis();
        SearchResult<ActivityInstance> searchResult;
        do {
            final SearchOptionsBuilder builder = new SearchOptionsBuilder(0, 1);
            builder.filter(ActivityInstanceSearchDescriptor.PROCESS_INSTANCE_ID, processInstanceId);
            builder.filter(ActivityInstanceSearchDescriptor.NAME, taskName);
            builder.filter(ActivityInstanceSearchDescriptor.STATE_NAME, "ready");
            searchResult = getProcessAPI().searchActivities(builder.done());
        } while (searchResult.getCount() == 0 && now + timeout > System.currentTimeMillis());
        assertEquals(1, searchResult.getCount());
        final HumanTaskInstance getHumanTaskInstance = getProcessAPI().getHumanTaskInstance(searchResult.getResult().get(0).getId());
        assertNotNull(getHumanTaskInstance);
        return getHumanTaskInstance;
    }

    protected void waitForProcessToFinish(final long processInstanceId, final int timeout) throws Exception {
        final long now = System.currentTimeMillis();
        SearchResult<ArchivedProcessInstance> searchResult;
        do {
            final SearchOptionsBuilder builder = new SearchOptionsBuilder(0, 1);
            builder.filter(ArchivedProcessInstancesSearchDescriptor.SOURCE_OBJECT_ID, processInstanceId);
            builder.filter(ArchivedProcessInstancesSearchDescriptor.STATE_ID, ProcessInstanceState.COMPLETED.getId());
            searchResult = getProcessAPI().searchArchivedProcessInstances(builder.done());
        } while (searchResult.getCount() == 0 && now + timeout > System.currentTimeMillis());
        assertEquals(1, searchResult.getCount());
    }

}
