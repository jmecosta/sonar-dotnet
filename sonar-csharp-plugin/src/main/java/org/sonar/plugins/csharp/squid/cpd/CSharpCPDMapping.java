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
package org.sonar.plugins.csharp.squid.cpd;

import net.sourceforge.pmd.cpd.Tokenizer;
import org.sonar.api.batch.AbstractCpdMapping;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.plugins.csharp.api.CSharp;

import java.nio.charset.Charset;

public class CSharpCPDMapping extends AbstractCpdMapping {

  private final CSharp csharp;
  private final Charset charset;

  public CSharpCPDMapping(CSharp csharp, Project project, Settings settings) {
    super();
    this.csharp = csharp;
    // FIXME: deprecated
    this.charset = project.getFileSystem().getSourceCharset();
  }

  @Override
  public Language getLanguage() {
    return csharp;
  }

  @Override
  public Tokenizer getTokenizer() {
    return new CSharpCPDTokenizer(charset);
  }

}
