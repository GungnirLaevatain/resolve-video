package controller;

import com.intellij.ide.util.PropertiesComponent;
import org.apache.commons.io.FileUtils;
import task.VideoProvider;
import ui.Setting;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The class Video provider.
 *
 * @author jiangla
 * @version 2018 -10-12 13:39:54
 * @since JDK 1.8
 */
public class VideoProviderController {
    private static ExecutorService service;
    private static VideoProvider videoProvider;

    public static void start() {

        PropertiesComponent prop = PropertiesComponent.getInstance();

        String filePath = prop.getValue(Setting.FOLDER, "");
        String cachePath = prop.getValue(Setting.CACHE_FOLDER, "");

        if (filePath.length() <= 0 || cachePath.length() <= 0) {
            return;
        }
        if (service != null) {
            stop();
        }
        service = Executors.newSingleThreadExecutor();
        videoProvider = new VideoProvider(filePath, cachePath);
        service.execute(videoProvider);

    }

    public static void stop() {

        if (videoProvider != null) {
            videoProvider.setStop(true);
        }
        if (service != null && !service.isTerminated()) {
            service.shutdownNow();
        }
        service = null;
    }

    public static void restart() {
        stop();
        start();
    }

    public static void clearCache() {
        PropertiesComponent prop = PropertiesComponent.getInstance();
        String cachePath = prop.getValue(Setting.CACHE_FOLDER, "");
        if (cachePath.length() > 0) {
            File file = new File(cachePath);
            if (file.exists()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException ignored) {
                }
            }
        }
    }

}
