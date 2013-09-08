/*
 * Sonar .NET Plugin :: Gallio
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.csharp.gallio.results.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.advanceCursor;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.descendantElements;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findElementName;
import static org.sonar.plugins.csharp.gallio.results.execution.UnitTestResultParsingStrategy.LOG;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestCaseDetail;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestDescription;
import org.sonar.plugins.csharp.gallio.results.execution.model.UnitTestReport;


public class Nunit2ParsingStrategy extends UnitTestResultParsingStrategy {

    
    
    public Set<UnitTestReport> parse(File report) throws XMLStreamException {
      Map<String, TestCaseDetail> testCaseDetailsByTestIds = new HashMap<String, TestCaseDetail>();
      Map<String, TestDescription> testsDetails = new HashMap<String, TestDescription>();

      SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
      SMHierarchicCursor rootCursor = inf.rootElementCursor(report);
      advanceCursor(rootCursor);
      LOG.debug("rootCursor is at : {}", findElementName(rootCursor));
      
      //QName testModelTag = new QName(GALLIO_URI, "testModel");
      //SMInputCursor testModelCursor = descendantElements(rootCursor);
      
      // Finally, we fill the reports
      final Set<UnitTestReport> reports = createUnitTestsReport(testsDetails, testCaseDetailsByTestIds);
      rootCursor.getStreamReader().closeCompletely();
      LOG.debug("Parsing ended");
      
      return reports;
    }
    
}
