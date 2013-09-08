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

import org.sonar.plugins.csharp.gallio.results.coverage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.plugins.csharp.gallio.results.execution.model.UnitTestReport;

/**
 * Parses a coverage report using Stax
 * 
 * @author Jorge Costa
 */
public class UnitTestResultParser implements BatchExtension {

  /**
   * Generates the logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(CoverageResultParser.class);

  private final List<UnitTestResultParsingStrategy> parsingStrategies;
  private UnitTestResultParsingStrategy currentStrategy;

  public UnitTestResultParser() {
    parsingStrategies = new ArrayList<UnitTestResultParsingStrategy>();
    parsingStrategies.add(new GallioResultParser());    
    parsingStrategies.add(new Nunit2ParsingStrategy());
  }

  public Set<UnitTestReport> parse(File report) {
    for (UnitTestResultParsingStrategy parser : parsingStrategies) {
      if (parser.isCompatible(report)) {
        return parser.parse(report);        
      }
    }
    return new HashSet<UnitTestReport>();
  }
}
