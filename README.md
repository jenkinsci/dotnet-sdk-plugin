# dotnet-sdk-plugin

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/dotnet-sdk.svg?label=latest%20version)](https://plugins.jenkins.io/dotnet-sdk)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/dotnet-sdk.svg?color=red)](https://plugins.jenkins.io/dotnet-sdk)
[![Jenkins](https://ci.jenkins.io/job/Plugins/job/dotnet-sdk-plugin/job/master/badge/icon?subject=Jenkins%20CI)](https://ci.jenkins.io/job/Plugins/job/dotnet-sdk-plugin/job/master/)

This is a plugin for [Jenkins](https://www.jenkins.io), providing convenient use of
[.NET SDKs](https://dotnet.microsoft.com/download/dotnet-core), specifically the SDKs for .NET Core and .NET 5.0.

This includes:

- configuration of named SDKs as global tools, with automatic installation via download
  - `dotnetsdk` in the `tools` section of a declarative pipeline
- a build wrapper, to set up the environment for a particular .NET SDK
  - a "With .NET" section in freestyle jobs
  - `withDotNet` in pipelines
- several builders, for [common `dotnet` commands](https://docs.microsoft.com/en-us/dotnet/core/tools/):
  - `dotnetBuild`: runs "`dotnet build`"
  - `dotnetClean`: runs "`dotnet clean`"
  - `dotnetListPackage`: runs "`dotnet list package`"
  - `dotnetNuGetDelete`: runs "`dotnet nuget delete`"
  - `dotnetNuGetPush`: runs "`dotnet nuget push`"
  - `dotnetPack`: runs "`dotnet pack`"
  - `dotnetPublish`: runs "`dotnet publish`"
  - `dotnetRestore`: runs "`dotnet restore`"
  - `dotnetTest`: runs "`dotnet test`"
  - `dotnetToolRestore`: runs "`dotnet tool restore`"

The builders are just for convenience; when using the build wrapper, any dotnet command line can
be executed using a batch or shell step, as applicable.

## Reporting Problems

Please report issues in the [Jenkins Issue Tracker](https://issues.jenkins-ci.org/).
File them under the `JENKINS` project, making sure to add the `dotnet-sdk-plugin` component.

## Release Notes

Available as part of the [GitHub Releases](https://github.com/jenkinsci/dotnet-sdk-plugin/releases).
