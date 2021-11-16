# Release (CoreMedia-only)

--------------------------------------------------------------------------------

\[[Up](README.md)\] \[[Top](#top)\]

--------------------------------------------------------------------------------

## Table of Content

1. [Release Steps](#release-steps)
1. [Post Process](#post-process)

## Release Steps

* **Make sure that all dependencies to CoreMedia core artifacts only use `<scope>provided</scope>` or `<scope>runtime</scope>` so that these non-public artifacts are not accidentally included in the resulting ZIP file.**

* Ensure you have built the CMCC version (snapshot versions) which this
  workspace dedicates to. Otherwise, the third-party versions won't
  match the declared CMCC version (most third-party dependencies)
  are managed in Blueprint and CMCC Core.

* Update [THIRD-PARTY.txt](../THIRD-PARTY.txt) and license downloads either manually or by running if you are using Maven and Java:

    ```bash
    $ mvn -Pdocs-third-party generate-resources
    ```

* Update badges at main workspace [`README.md`](https://github.com/CoreMedia/content-hub-adapter-cloudinary/blob/main/README.md).

* Update the [`CHANGELOG.md`](https://github.com/CoreMedia/content-hub-adapter-cloudinary/blob/main/CHANGELOG.md).

* Use [Create new release](https://github.com/CoreMedia/content-hub-adapter-cloudinary/releases/new) with a proper name for the tag, a release title that matches the other releases, and a description that matches the changelog. The GitHub action will then attach the Plugin artifacts to the release.

## Post Process

* Review GitHub issues and possibly adjust state.
* Close and possibly rename the milestone.
