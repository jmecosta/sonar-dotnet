﻿namespace NSonarQubeAnalyzer.Diagnostics
{
    using Microsoft.CodeAnalysis;
    using Microsoft.CodeAnalysis.CSharp;
    using Microsoft.CodeAnalysis.CSharp.Syntax;
    using Microsoft.CodeAnalysis.Diagnostics;
    using System.Collections.Immutable;
    using System.Linq;
    using System.Text.RegularExpressions;
    using System.Xml.Linq;

    [DiagnosticAnalyzer(LanguageNames.CSharp)]
    public class MethodName : DiagnosticsRule
    {
        internal const string DiagnosticId = "MethodName";
        internal const string Description = "Method name should comply with a naming convention";
        internal const string MessageFormat = "Rename this method to match the regular expression: {0}";
        internal const string Category = "SonarQube";
        internal const DiagnosticSeverity Severity = DiagnosticSeverity.Warning;

        internal static DiagnosticDescriptor Rule = new DiagnosticDescriptor(DiagnosticId, Description, MessageFormat, Category, Severity, true);

        /// <summary>
        /// Rule ID
        /// </summary>
        public override string RuleId
        {
            get
            {
                return "MethodName";
            }
        }

        public override ImmutableArray<DiagnosticDescriptor> SupportedDiagnostics { get { return ImmutableArray.Create(Rule); } }

        public string Convention { get; set; }

        /// <summary>
        /// Configure the rule from the supplied settings
        /// </summary>
        /// <param name="settings">XML settings</param>
        public override void Configure(XDocument settings)
        {
            var parameters = from e in settings.Descendants("Rule")
                             where "MethodName".Equals(e.Elements("Key").Single().Value)
                             select e.Descendants("Parameter");
            var convention =
                (from e in parameters
                 where "format".Equals(e.Elements("Key").Single().Value)
                 select e.Elements("Value").Single().Value).Single();

            this.Convention = convention;
        }

        public override void Initialize(AnalysisContext context)
        {
            context.RegisterSyntaxNodeAction(
                c =>
                {
                    var identifierNode = ((MethodDeclarationSyntax)c.Node).Identifier;

                    if (!Regex.IsMatch(identifierNode.Text, this.Convention))
                    {
                        c.ReportDiagnostic(Diagnostic.Create(Rule, identifierNode.GetLocation(), this.Convention));
                    }
                },
                SyntaxKind.MethodDeclaration);
        }
    }
}