# Installation & Setup

You can install Weave like any other Fabric mod library: add the GitHub Packages Maven repo, declare the dependency, and initialize Weave in your client initializer.

---

## What you’ll do

1. Configure Gradle to resolve Weave from GitHub Packages.
2. Add the dependency to your mod’s build.
3. Call `WeaveCore.init()` from your client initializer.
4. Run in dev and verify.

> Note  
> Using GitHub Packages requires a Personal Access Token (PAT) with the `read:packages` scope. Gradle must be configured with these credentials.

---

## 1) Add the GitHub Packages repository

In your `repositories` block (either in the root `build.gradle` of your mod or in `settings.gradle` if you centralize repos):

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/trethore/Weave")
        credentials {
            username = findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
````

Recommended credential placement in `~/.gradle/gradle.properties`:

```
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_PERSONAL_ACCESS_TOKEN
```

Your token must have the **read\:packages** scope.

---

## 2) Add the dependency

In your mod’s Gradle `dependencies` (alongside Fabric loader and Fabric API):

```gradle
dependencies {
    modImplementation "net.fabricmc:fabric-loader:${loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${fabric_version}"

    // Weave UI
    modImplementation "tytoo.weave:weave-ui:1.0.0+1.21.4"
}
```

Replace the version string with the release tag you want. \
See [GitHub Packages](https://github.com/trethore/Weave/packages/) for the latest versions.

---

## 3) Initialize Weave

Weave artifacts do not include an entrypoint.
You must call `WeaveCore.init()` from your `ClientModInitializer`:

```java
import net.fabricmc.api.ClientModInitializer;
import tytoo.weave.WeaveCore;

public final class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WeaveCore.init();
    }
}
```

---

## 4) Run in development

```bash
./gradlew runClient
```

In-game dev commands:

* `/weave demo` → open the demo screen.
* `/weave reloadtheme` → reload the default theme.

Note: debug-only commands (like `/weave testgui`) are available only when developing the Weave library with `./gradlew runDebugClient` and are not included in the published artifact.

---

## Troubleshooting

* **401 Unauthorized** when resolving the Maven repo:

    * Your token may be missing the `read:packages` scope.
    * Gradle may not be picking up `gpr.user` and `gpr.key`. Verify with `gradle.properties`.

* **IDE sync fails after adding Weave:**
  Run:

  ```bash
  ./gradlew --refresh-dependencies
  ```

  then reimport your Gradle project.

---

**Next Step → [Creating a WeaveScreen](weave-screen.md)**
