1. Bump the version in `gradle.properties`, `scripts/install` and the project Readme
    1. TODO replace this with a `sed` oneliner
1. If it's a stable release, run `./gradlew assemble signArchives`
1. Run `./gradlew publish` to push the jars to [Maven Central](https://oss.sonatype.org/)
    1. If it's a stable release, you'll also need to go the dashboard and promote it 
1. Run `scripts/release`
1. Upload release to GitHub, make sure the version tag matches exactly the vesion in `gradle.properties`
