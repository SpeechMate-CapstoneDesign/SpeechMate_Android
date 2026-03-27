fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android test

```sh
[bundle exec] fastlane android test
```

Build Debug APK and deploy to Firebase App Distribution

### android firebase_debug

```sh
[bundle exec] fastlane android firebase_debug
```



### android firebase_release

```sh
[bundle exec] fastlane android firebase_release
```

Build Release APK and deploy to Firebase App Distribution

### android firebase_production

```sh
[bundle exec] fastlane android firebase_production
```

Build Production Release APK and deploy to Firebase

### android playstore

```sh
[bundle exec] fastlane android playstore
```

Build AAB and deploy to Play Store Internal Testing

### android deploy_production

```sh
[bundle exec] fastlane android deploy_production
```

Deploy to both Firebase and Play Store

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
