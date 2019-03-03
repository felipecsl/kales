* Bump the version in `gradle.properties` file
* Run `./gradlew publish`
* Run `./gradlew kales-cli:shadowJar`
* Run `cp kales-cli/build/libs/kales-cli-<VERSION>-all.jar scripts/kales-cli.jar`
* Zip both `scripts/kales-cli.jar` and `scripts/kales` into `kales-<VERSION>.zip`
* Upload release to GitHub, make sure the version tag matches exactly the vesion in `gradle.properties`