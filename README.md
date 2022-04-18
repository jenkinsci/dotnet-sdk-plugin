# dotnet-sdk-plugin

[![Jenkins Plugin][Badge:Version]](https://plugins.jenkins.io/dotnet-sdk)
[![GitHub Release][Badge:ReleaseNotes]](https://github.com/jenkinsci/dotnet-sdk-plugin/releases/latest)
[![Jenkins Plugin Installs][Badge:Installs]](https://plugins.jenkins.io/dotnet-sdk)
[![Jenkins CI Status][Badge:Build]](https://ci.jenkins.io/job/Plugins/job/dotnet-sdk-plugin/job/main/)

This is a plugin for [Jenkins][Jenkins], providing convenient use of
[.NET SDKs][MS:Download], specifically the SDKs for .NET Core and .NET
5.0.

This includes:

- configuration of named SDKs as global tools, with automatic
  installation via download
  - `dotnetsdk` in the `tools` section of a declarative pipeline
- a build wrapper, to set up the environment for a particular .NET SDK
  - a "With .NET" section in freestyle jobs
  - `withDotNet` in pipelines
- several builders, for [common `dotnet` commands][MS:Docs]:
  - `dotnetBuild` - runs "`dotnet build`"
  - `dotnetClean` - runs "`dotnet clean`"
  - `dotnetListPackage` - runs "`dotnet list package`"
  - `dotnetNuGetDelete` - runs "`dotnet nuget delete`"
  - `dotnetNuGetPush` - runs "`dotnet nuget push`"
  - `dotnetPack` - runs "`dotnet pack`"
  - `dotnetPublish` - runs "`dotnet publish`"
  - `dotnetRestore` - runs "`dotnet restore`"
  - `dotnetTest` - runs "`dotnet test`"
  - `dotnetToolRestore` - runs "`dotnet tool restore`"

The builders are just for convenience; when using the build wrapper,
any dotnet command line can be executed using a shell step (`bat`,
`sh`, `powershell`, `pwsh`, ...), as applicable.

## Reporting Problems

Please report issues in the [Jenkins Issue Tracker][Issues].
File them under the `JENKINS` project, making sure to add the
`dotnet-sdk-plugin` component.

## Release Notes

Available as part of the [GitHub Releases][Releases].

[Badge:Version]: https://img.shields.io/jenkins/plugin/v/dotnet-sdk.svg?label=latest%20version&color=indigo
[Badge:ReleaseNotes]: https://img.shields.io/github/release/jenkinsci/dotnet-sdk-plugin.svg?label=Release%20Notes&color=indigo
[Badge:Installs]: https://img.shields.io/jenkins/plugin/i/dotnet-sdk.svg?color=indigo
[Badge:Build]: https://ci.jenkins.io/job/Plugins/job/dotnet-sdk-plugin/job/main/badge/icon?subject=Jenkins%20CI
[Issues]: https://issues.jenkins-ci.org/
[Jenkins]: https://www.jenkins.io/
[MS:Docs]: https://docs.microsoft.com/en-us/dotnet/core/tools/
[MS:Download]: https://dotnet.microsoft.com/download/dotnet-core
[Releases]: https://github.com/jenkinsci/dotnet-sdk-plugin/releases
