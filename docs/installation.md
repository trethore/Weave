# Installation & Setup

Weave targets Fabric for Minecraft 1.21.4 with Yarn 1.21.4+build.8. Use Fabric Loom and add Weave from GitHub Packages (Maven).

What you’ll do here
- Point Gradle at the Weave package registry on GitHub Packages, add the dependency, and call `WeaveCore.init()` from your client initializer. If you’ve never used GitHub Packages for Maven, note that it requires a Personal Access Token with the `read:packages` scope and credentials configured for Gradle.

1) Add the GitHub Packages repository

Add to your `repositories` (either the root `build.gradle` of your mod or via `settings.gradle` if you centralize repos):

```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/trethore/Weave")
        credentials {
            username = findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Recommended credential placement in `~/.gradle/gradle.properties`:

```
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_PERSONAL_ACCESS_TOKEN
```

Your token must have the `read:packages` scope.

2) Add the dependency

In your mod’s Gradle `dependencies` (Fabric loader and Fabric API as usual):

```
dependencies {
    modImplementation "net.fabricmc:fabric-loader:${loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${fabric_version}"

    // Weave UI
    modImplementation "tytoo.weave:weave-ui:1.0.0+1.21.4"
}
```

Replace the version with the desired release tag.

3) Initialize Weave in your client initializer

Release artifacts do not include an entrypoint. Call `WeaveCore.init()` from your `ClientModInitializer`:

```
import net.fabricmc.api.ClientModInitializer;
import tytoo.weave.WeaveCore;

public final class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WeaveCore.init();
    }
}
```

4) Run in dev

```
./gradlew runClient
```

In-game dev commands
- `/weave testgui` → open demo screen
- `/weave reloadtheme` → reload the default theme

Note
- First runs download Minecraft, mappings, and dependencies — allow extra time.
- Ensure Java 21.

Troubleshooting
- 401 Unauthorized from the Maven repo usually means your token is missing the `read:packages` scope or Gradle is not picking up `gpr.user`/`gpr.key`. Verify with `gradle.properties` in your user home.
- If IDE sync fails after adding Weave, try `./gradlew --refresh-dependencies` and reimport the Gradle project.

Next Step: [Creating a WeaveScreen](https://github.com/trethore/Weave/blob/main/docs/weave-screen.md)
