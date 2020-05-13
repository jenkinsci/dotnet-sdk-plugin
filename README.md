# dotnet-sdk-plugin

This is a plugin for [Jenkins](https://www.jenkins.io), providing convenient use of .NET SDKs,
specifically the SDKs for .NET Core and .NET 5.0.

The first release will include:
- configuration of named SDKs as global tools, with automatic installation via download
  - this matches what is available for JDKs
- a build wrapper (`withDotNet`), to set up a particular .NET SDK for use in jobs or pipeline steps
- four builders:
  - `dotnetBuild`: runs "dotnet build"
  - `dotnetPack`: runs "dotnet pack"
  - `dotnetTest`: runs "dotnet test"
  - `dotnetVsTest`: runs "dotnet vstest"

The builders are just for convenience; when using the build wrapper, any dotnet command line can
be executed using a batch or shell step, as applicable.


## Release Notes

No releases yet.
