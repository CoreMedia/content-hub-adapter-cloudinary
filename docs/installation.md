# Installation

--------------------------------------------------------------------------------

\[[Up](README.md)\] \[[Top](#top)\]

--------------------------------------------------------------------------------

## Table of Content

1. [Introduction](#introduction)
1. [Release Download](#release-download)
2. [Git Submodule](#git-submodule)
3. [Activate the extension](#activate-the-extension)
4. [Intellij IDEA Hints](#intellij-idea-hints)

## Introduction

Depending on what you are setup and your plans, you can integrate this project in different ways.

* If you want to use the extension in your project, then it is recommended to fork the repository and integrate it as described in [Git Submodule](#git-submodule).
* If you do not want to use GitHub, proceed as described in [Release Download](#release-download).
* If you just want to contribute a new feature or a bugfix to the extension, you will need to work with the [Git Submodule](#git-submodule). As an external developer you will still need a fork of the repository to create a Pull Request. 

## Release Download

Go to [Release](https://github.com/CoreMedia/content-hub-adapter-cloudinary/releases) and download the version that matches your CMCC release version.

From the Blueprint workspace root folder, extract the ZIP file into `modules/extensions`.

Continue with [Activate the extension](#activate-the-extension).

## Git Submodule
The first step to enable the extension, please ensure that the following maven modules are present:
* <WORKSPACE_ROOT>/modules (root pom as parent, packaging pom)
* <WORKSPACE_ROOT/modules/extensions (modules as parent, packaging pom)

From the Blueprint workspace root folder, clone this repository or your fork as a submodule into the extension folder. Make sure to use the branch name that matches your workspace version. A fork is required if you plan to customize the extension.

```
$ cd modules/extensions
$ git submodule add https://github.com/CoreMedia/content-hub-adapter-cloudinary.git content-hub-adapter-cloudinary
$ git submodule init
$ git checkout -b content-hub-adapter-cloudinary
```

Continue with [Activate the extension](#activate-the-extension).

## Activate the Extension

In order to activate the extension, you need to configure the extension tool. The configuration for the tool can be found under `workspace-configuration/extensions`. Make sure that you use at least version 4.0.1 of the extension tool and that you have adjusted the `<groupId>` and `<version>` so that they match your Blueprint workspace.
After adapting the configuration run the extension tool in
`workspace-configuration/extensions` by executing:

```bash
$ mvn extensions:sync
$ mvn extensions:sync -Denable=content-hub-adapter-cloudinary
``` 

This will activate the extension. The extension tool will also set the relative path for the parents of the extension modules.

## Intellij IDEA Hints

For the IDEA import:
- Ignore folder ".remote-package"
- Disable "Settings > Compiler > Clear output directory on rebuild"
