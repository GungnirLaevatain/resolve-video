package controller;

import com.intellij.ide.util.PropertiesComponent;
import org.apache.commons.io.FileUtils;
import task.CacheQueue;
import task.VideoConsumer;
import task.VideoProvider;
import ui.Setting;

import java.io.File;
import java.io.IOException;
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
    private static ScheduledExecutorService service;
    private static ScheduledExecutorService consumerService;
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
        service = Executors.newSingleThreadScheduledExecutor();
        consumerService = Executors.newSingleThreadScheduledExecutor();
        try {
            videoProvider = new VideoProvider(filePath, cachePath);
            service.scheduleWithFixedDelay(videoProvider, 40L, 3000L, TimeUnit.MILLISECONDS);
        } catch (IOException ignored) {
        }
        VideoConsumer videoConsumer = new VideoConsumer();
        consumerService.scheduleWithFixedDelay(videoConsumer, 40L, 40L, TimeUnit.MILLISECONDS);

    }

    public static void stop() {
        stopProvider();
        stopConsuemr();
        CacheQueue.blockingQueue.clear();
    }

    private static void stopProvider() {

        if (videoProvider != null) {
            videoProvider.setStop(true);
        }
        if (service != null && !service.isTerminated()) {
            service.shutdownNow();
        }
        service = null;
    }

    private static void stopConsuemr() {
        if (consumerService != null && !consumerService.isTerminated()) {
            consumerService.shutdownNow();
        }
        consumerService = null;
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
