# ImageManager & Cleanup

`ImageManager` loads textures at runtime from files or URLs and handles caching. Weave automatically clears all managed textures when the client shuts down; you can also clear caches manually if needed.

---

## What this page covers

- Loading images from `File` or `URL`
- Using a placeholder while content loads
- Caching, re-use, and forced refresh
- Automatic and manual cleanup

---

## Loading from Files

```java
import java.io.File;
import net.minecraft.util.Identifier;
import tytoo.weave.utils.ImageManager;

File file = new File("/path/to/picture.png");
java.util.Optional<Identifier> id = ImageManager.getIdentifierForFile(file);
id.ifPresent(img -> /* use in an Image component */ {});
```

Subsequent calls for the same `File` reuse the cached texture.

## Loading from URLs

```java
import java.net.URL;
import tytoo.weave.utils.ImageManager;

URL url = new URL("https://example.com/pic.jpg");
ImageManager.getIdentifierForUrl(url).thenAccept(id -> {
    // Use the identifier on the client thread
});
```

Use `forceFetchIdentifierForUrl(URL)` to bypass the cache and re-download.

## Using the Placeholder

While a URL is loading, show a checker texture and replace it when ready:

```java
import tytoo.weave.component.components.display.Image;

Image avatar = Image.from(ImageManager.getPlaceholder());
ImageManager.getIdentifierForUrl(url).thenAccept(id -> avatar.setImage(id));
```

`Image.from(URL)` is a convenience that does this for you and logs failures.

## Cleanup

Weave clears all managed textures automatically on client shutdown:

```java
// Called from WeaveCore on CLIENT_STOPPING
ImageManager.clearCaches();
```

You can call `clearCaches()` yourself to release textures and cancel in-flight URL downloads.

---

**Next Step → [Components & Layout](components.md)**

