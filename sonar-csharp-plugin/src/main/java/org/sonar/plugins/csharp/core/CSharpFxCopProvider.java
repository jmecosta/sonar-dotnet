/*
 * Sonar C# Plugin :: Core
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
package org.sonar.plugins.csharp.core;

import com.google.common.collect.ImmutableList;
import org.sonar.api.PropertyType;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.rules.XMLRuleParser;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.fxcop.FxCopConfiguration;
import org.sonar.plugins.fxcop.FxCopRuleRepository;
import org.sonar.plugins.fxcop.FxCopSensor;

import java.util.List;

public class CSharpFxCopProvider {

  private static final String CATEGORY = "C#";
  private static final String SUBCATEGORY = "Code Analysis / FxCop";

  private static final String FXCOP_ASSEMBLIES_PROPERTY_KEY = "sonar.cs.fxcop.assembly";
  private static final String FXCOP_DIRECTORY_PROPERTY_KEY = "sonar.cs.fxcop.directory";
  private static final String FXCOP_FXCOPCMD_PATH_PROPERTY_KEY = "sonar.cs.fxcop.fxCopCmdPath";
  private static final String FXCOP_TIMEOUT_PROPERTY_KEY = "sonar.cs.fxcop.timeoutMinutes";
  private static final String FXCOP_ASPNET_PROPERTY_KEY = "sonar.cs.fxcop.aspnet";

  private static final FxCopConfiguration FXCOP_CONF = new FxCopConfiguration(
    CSharpConstants.LANGUAGE_KEY,
    "fxcop",
    FXCOP_ASSEMBLIES_PROPERTY_KEY,    
    FXCOP_FXCOPCMD_PATH_PROPERTY_KEY,
    FXCOP_TIMEOUT_PROPERTY_KEY,
    FXCOP_ASPNET_PROPERTY_KEY,
    FXCOP_DIRECTORY_PROPERTY_KEY);

  public static List extensions() {
    return ImmutableList.of(
      CSharpFxCopRuleRepository.class,
      CSharpFxCopSensor.class,
      PropertyDefinition.builder(FXCOP_TIMEOUT_PROPERTY_KEY)
        .name("FxCop execution timeout")
        .description("Time in minutes after which FxCop's execution should be interrupted if not finished")
        .defaultValue("10")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.INTEGER)
        .build(),
      PropertyDefinition.builder(FXCOP_ASSEMBLIES_PROPERTY_KEY)
        .name("Assembly to analyze")
        .description("Example: bin/Debug/MyProject.dll")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(FXCOP_DIRECTORY_PROPERTY_KEY)
        .name("List of directories (seperated by ',') to scan for referenced assemblies")
        .description("Example: s:/Binaries,d:/dev/builddrop")
        .defaultValue(null)
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(FXCOP_FXCOPCMD_PATH_PROPERTY_KEY)
        .name("Path to FxCopCmd.exe")
        .description("Example: C:/Program Files (x86)/Microsoft Visual Studio 12.0/Team Tools/Static Analysis Tools/FxCop/FxCopCmd.exe")
        .defaultValue("C:/Program Files (x86)/Microsoft Visual Studio 12.0/Team Tools/Static Analysis Tools/FxCop/FxCopCmd.exe")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(FXCOP_ASPNET_PROPERTY_KEY)
        .name("ASP.NET")
        .description("Whether or not to set the /aspnet flag when launching FxCopCmd.exe")
        .defaultValue("false")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build()
      );
  }

  public static class CSharpFxCopRuleRepository extends FxCopRuleRepository {

    public CSharpFxCopRuleRepository(XMLRuleParser xmlRuleParser) {
      super(FXCOP_CONF, xmlRuleParser);
    }

  }

  public static class CSharpFxCopSensor extends FxCopSensor {

    public CSharpFxCopSensor(Settings settings, RulesProfile profile, ModuleFileSystem fileSystem, ResourcePerspectives perspectives) {
      super(FXCOP_CONF, settings, profile, fileSystem, perspectives);
    }

  }

}

