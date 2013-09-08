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
/*
 * Created on Jun 18, 2009
 *
 */
package org.sonar.plugins.csharp.gallio.results.execution;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestCaseDetail;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestStatus;
import org.sonar.plugins.csharp.gallio.results.execution.model.UnitTestReport;
import org.sonar.test.TestUtils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Nunit2ResultParserTest {

  private File sourcefile;
  private Nunit2ParsingStrategy parser;

  @Before
  public void setUp() {
    sourcefile = new File("Example\\Example.Core.Tests\\TestMoney.cs");

    parser = new Nunit2ParsingStrategy();
  }

  private Collection<UnitTestReport> parse(String fileName) {
      try {
          return parser.parse(TestUtils.getResource("/Results/execution/" + fileName));
      } catch (XMLStreamException ex) {
          Assert.fail("Parser Exception Not Expected: " + ex.getStackTrace());
          return null;
      }
  }

  //@Test (expected=XMLStreamException.class) 
  public void testShouldThrowExceptionWhenInvalidFormatReport() throws XMLStreamException {
    Collection<UnitTestReport> reports = parser.parse(TestUtils.getResource("/Results/execution/gallio-report.xml"));
  }
  
  //@Test
  public void testReportParsing() {
    Collection<UnitTestReport> reports = parse("nunit-report.xml");

    assertFalse("Could not parse a Nunit report", reports.isEmpty());

    Iterator<UnitTestReport> report = reports.iterator();

    assertTrue(report.hasNext());

    UnitTestReport firstReport = report.next();

    assertEquals("cs-failures", firstReport.getAssemblyName());
    assertEquals(6, firstReport.getAsserts());
    assertEquals(3, firstReport.getFailures());
    assertEquals(1, firstReport.getSkipped());
    assertTrue(StringUtils.contains(firstReport.getSourceFile().getName(), "CSharpTest.cs"));
    assertEquals(6, firstReport.getTests());
    assertEquals(420, firstReport.getTimeMS());

  }
}
