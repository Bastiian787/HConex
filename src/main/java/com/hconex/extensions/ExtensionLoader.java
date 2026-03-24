package com.hconex.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Loads and manages {@link Extension} instances at runtime.
 * <p>
 * Extensions are discovered via the {@link ServiceLoader} mechanism from
 * JAR files placed in the configured extensions directory.  Each JAR must
 * contain a {@code META-INF/services/com.hconex.extensions.Extension} file
 * listing the implementing class names.
 * </p>
 */
public final class ExtensionLoader {

    private static final Logger logger = LogManager.getLogger(ExtensionLoader.class);

    private final ExtensionAPI api;
    private final CopyOnWriteArrayList<Extension> loadedExtensions = new CopyOnWriteArrayList<>();

    /**
     * Creates a new loader that will pass the given API to each extension.
     *
     * @param api the {@link ExtensionAPI} to provide to loaded extensions
     */
    public ExtensionLoader(ExtensionAPI api) {
        this.api = api;
    }

    /**
     * Scans {@code directory} for JAR files and loads any {@link Extension}
     * implementations found within them.
     *
     * @param directory the directory to scan
     */
    public void loadFromDirectory(File directory) {
        if (!directory.isDirectory()) {
            logger.warn("Extensions directory does not exist or is not a directory: {}",
                    directory.getAbsolutePath());
            return;
        }

        File[] jars = directory.listFiles(f -> f.getName().endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            logger.info("No extension JARs found in {}", directory.getAbsolutePath());
            return;
        }

        for (File jar : jars) {
            loadFromJar(jar);
        }
    }

    /**
     * Loads {@link Extension} implementations from a single JAR file.
     *
     * @param jar the JAR file to load extensions from
     */
    public void loadFromJar(File jar) {
        try {
            URL jarUrl = jar.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jarUrl}, getClass().getClassLoader());

            ServiceLoader<Extension> serviceLoader =
                    ServiceLoader.load(Extension.class, classLoader);

            for (Extension ext : serviceLoader) {
                register(ext);
            }
        } catch (Exception e) {
            logger.error("Failed to load extensions from {}", jar.getName(), e);
        }
    }

    /**
     * Registers an extension programmatically and calls its
     * {@link Extension#onStart(ExtensionAPI)} method.
     *
     * @param extension the extension to register
     */
    public void register(Extension extension) {
        loadedExtensions.add(extension);
        try {
            extension.onStart(api);
            logger.info("Extension loaded: {} v{}", extension.getName(), extension.getVersion());
        } catch (Exception e) {
            logger.error("Error starting extension {}", extension.getName(), e);
        }
    }

    /**
     * Unloads an extension and calls its {@link Extension#onStop()} method.
     *
     * @param extension the extension to unload
     */
    public void unload(Extension extension) {
        if (loadedExtensions.remove(extension)) {
            try {
                extension.onStop();
                logger.info("Extension unloaded: {}", extension.getName());
            } catch (Exception e) {
                logger.error("Error stopping extension {}", extension.getName(), e);
            }
        }
    }

    /**
     * Unloads all registered extensions.
     */
    public void unloadAll() {
        new ArrayList<>(loadedExtensions).forEach(this::unload);
    }

    /**
     * Returns an unmodifiable view of all currently loaded extensions.
     *
     * @return immutable list of loaded extensions
     */
    public List<Extension> getLoadedExtensions() {
        return Collections.unmodifiableList(new ArrayList<>(loadedExtensions));
    }

    /**
     * Returns the number of currently loaded extensions.
     *
     * @return extension count
     */
    public int getCount() {
        return loadedExtensions.size();
    }
}
