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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.staxmate.in.SMInputCursor;

import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestCaseDetail;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestDescription;
import org.sonar.plugins.csharp.gallio.results.execution.model.UnitTestReport;

public abstract class UnitTestResultParsingStrategy {

  protected static final Logger LOG = LoggerFactory.getLogger(UnitTestResultParsingStrategy.class);    
    
  abstract Set<UnitTestReport> parse(File report) throws XMLStreamException;
  
  protected Set<UnitTestReport> createUnitTestsReport(Map<String, TestDescription> testsDescriptionByTestIds, Map<String, TestCaseDetail> testCaseDetailsByTestIds) {

    Set<UnitTestReport> result = new HashSet<UnitTestReport>();
    Set<String> testIds = testCaseDetailsByTestIds.keySet();
    // We associate the descriptions with the test details
    List<String> testsToRemove = new ArrayList<String>();
    for (String testId : testIds) {
      TestDescription description = testsDescriptionByTestIds.get(testId);
      TestCaseDetail testCaseDetail = testCaseDetailsByTestIds.get(testId);
      if (description == null) {
        LOG.debug(
            "Test {} is not considered as a testCase in your xml, there should not be any testStep associated, please check your test report. Skipping result",
            testId);
        testsToRemove.add(testId);
      } else {
        testCaseDetail.merge(description);
        testCaseDetailsByTestIds.put(testId, testCaseDetail);
      }
    }

    LOG.debug("Tests to be removed {}", testsToRemove.size());
    for (String testToRemove : testsToRemove) {
      testCaseDetailsByTestIds.remove(testToRemove);
    }

    Collection<TestCaseDetail> testCases = testCaseDetailsByTestIds.values();
    Multimap<String, TestCaseDetail> testCaseDetailsBySrcKey = ArrayListMultimap.create();
    for (TestCaseDetail testCaseDetail : testCases) {
      String sourceKey = testCaseDetail.createSourceKey();
      testCaseDetailsBySrcKey.put(sourceKey, testCaseDetail);
    }

    Map<String, UnitTestReport> unitTestsReports = new HashMap<String, UnitTestReport>();
    LOG.debug("testCaseDetails size : {}", String.valueOf(testCaseDetailsByTestIds.size()));

    Set<String> pathKeys = testCaseDetailsBySrcKey.keySet();
    LOG.debug("There are {} different pathKeys", String.valueOf(pathKeys.size()));
    for (String pathKey : pathKeys) {
      // If the Key already exists in the map, we add the details
      if (unitTestsReports.containsKey(pathKey)) {
        UnitTestReport unitTest = unitTestsReports.get(pathKey);
        for (TestCaseDetail testDetail : testCaseDetailsBySrcKey.get(pathKey)) {
          LOG.debug("Adding testDetail {} to the unitTestReport", testDetail.getName());
          unitTest.addDetail(testDetail);
        }
        unitTestsReports.put(pathKey, unitTest);
      } else {
        // Else we create a new report
        UnitTestReport unitTest = new UnitTestReport();
        unitTest.setAssemblyName(testCaseDetailsBySrcKey.get(pathKey).iterator().next().getAssemblyName());
        unitTest.setSourceFile(testCaseDetailsBySrcKey.get(pathKey).iterator().next().getSourceFile());
        LOG.debug("Create new unitTest for path : {}", unitTest.getSourceFile().getPath());
        for (TestCaseDetail testDetail : testCaseDetailsBySrcKey.get(pathKey)) {
          LOG.debug("+ and add details : {}", testDetail.getName());
          unitTest.addDetail(testDetail);
        }
        unitTestsReports.put(pathKey, unitTest);
      }
    }
    result.addAll(unitTestsReports.values());

    LOG.debug("The result Set contains " + result.size() + " report(s)");

    return result;
  }
  
}
