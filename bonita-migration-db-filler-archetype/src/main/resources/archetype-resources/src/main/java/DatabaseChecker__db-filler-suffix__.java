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
package ${package};

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.runner.JUnitCore;


public class DatabaseChecker${db-filler-suffix} extends SimpleDatabaseChecker6_5_0 {
    
    public static void main(final String[] args) throws Exception {
        JUnitCore.main(DatabaseChecker${db-filler-suffix}.class.getName());
    }
    
    @Override
    protected Document getProfilesXML(final SAXReader reader) throws Exception {
        return reader.read(DatabaseChecker${db-filler-suffix}.class.getResource("profiles.xml"));
    }


}
