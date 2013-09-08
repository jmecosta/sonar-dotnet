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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findAttributeValue;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findAttributeIntValue;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findElementName;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestCaseDetail;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestStatus;
import org.sonar.plugins.csharp.gallio.results.execution.model.UnitTestReport;


public class Nunit2ParsingStrategy implements UnitTestResultParsingStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GallioResultParser.class);    
        
    public Set<UnitTestReport> parse(File report) {
      try {
        Map<String, UnitTestReport> unitTestsReports = new HashMap<String, UnitTestReport>();


        SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
        SMHierarchicCursor rootCursor = inf.rootElementCursor(report);
        rootCursor.advance();
        LOG.debug("rootCursor is at : {}", findElementName(rootCursor));
        
        SMInputCursor rootChildCursor = rootCursor.childElementCursor();
        while (rootChildCursor.getNext() != null) {
          handleRootChildElement(rootChildCursor, unitTestsReports, "");
        }        
        
        Set<UnitTestReport> reports = new HashSet<UnitTestReport>();
        reports.addAll(unitTestsReports.values());
        
        return reports;
      } catch (XMLStreamException e) {
        throw new SonarException("nunit report parsing error", e);        
      }
    }
    
    private void handleRootChildElement(SMInputCursor rootChildCursor, Map<String, UnitTestReport> reports, String assemblyName) throws XMLStreamException {
      do{
       String name = rootChildCursor.getLocalName();
       if(name.equals("test-suite") || name.equals("results")) {
         String nameAttr = findAttributeValue(rootChildCursor, "name");

         if (nameAttr != null && (nameAttr.endsWith(".dll") || nameAttr.endsWith(".DLL"))) {
           assemblyName = nameAttr;           
         }
         handleRootChildElement(rootChildCursor.childElementCursor().advance(), reports, assemblyName);
       }  
       
       if(name.equals("test-case")) {
         handleTestCases(rootChildCursor, reports, assemblyName);
       }          
      } while(rootChildCursor.getNext() != null);
    }

    private void handleTestCases(SMInputCursor childElementCursor, Map<String, UnitTestReport> reports, String assemblyName) throws XMLStreamException {
        do {
          
          String name = findAttributeValue(childElementCursor, "name");
          
          if(name != null && !"".equals(name)){
            String[] elems = name.split("\\.");
            String textFixtureName = "";
            for(Integer i = 0; i < elems.length -1; i++) {
              textFixtureName += elems[i] + ".";              
            }
            
            textFixtureName = textFixtureName.substring(0, textFixtureName.length() - 1);
            
            String testExecuted = findAttributeValue(childElementCursor, "executed");
            String testCaseResult = findAttributeValue(childElementCursor, "result");
            String testSuccess = findAttributeValue(childElementCursor, "success");
            String testCaseTime = findAttributeValue(childElementCursor, "time");
            Integer testCaseAsserts = findAttributeIntValue(childElementCursor, "asserts");   
            
            TestCaseDetail testCase = new TestCaseDetail();
            testCase.setName(name);
            testCase.setCountAsserts(testCaseAsserts);
            testCase.setTimeMillis((int) Math.round(Double.parseDouble(testCaseTime) * 1000.));
            if(testExecuted.equals("False"))
            {
              testCase.setStatus(TestStatus.SKIPPED);
            } else {
              if(testSuccess.equals("True"))
              {
                testCase.setStatus(TestStatus.SUCCESS);
              } else {
                testCase.setStatus(TestStatus.FAILED);                
              }            
            }
                                              
            if(reports.containsKey(textFixtureName)) {
              UnitTestReport existentReport = reports.get(textFixtureName);
              existentReport.addDetail(testCase);
            } else {
              UnitTestReport existentReport = new UnitTestReport();
              existentReport.setAssemblyName(assemblyName);
              existentReport.addDetail(testCase);
              reports.put(textFixtureName, existentReport);
            }                        
          }
        } while(childElementCursor.getNext() != null);
    }

  public boolean isCompatible(File report) {
      boolean isCompatible = false;    
      
      try {                
        SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
        SMHierarchicCursor rootCursor = inf.rootElementCursor(report);
        rootCursor.advance();
        SMInputCursor rootChildCursor = rootCursor.childElementCursor();
        while (rootChildCursor.getNext() != null) {
          String name = rootChildCursor.getLocalName();
          if (name != null && name.equals("environment")) {
            String version = findAttributeValue(rootChildCursor, "nunit-version");
            if(version != null) {
              isCompatible = true;
            }
          }
        }       
      } catch (XMLStreamException ex) {
        LOG.debug("Report Not Compatible : {}", Nunit2ParsingStrategy.class);
      }
      
      return isCompatible;      
  }
}
