package task;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * The class Video provider.
 *
 * @author jiangla
 * @version 2018 -10-12 13:51:50
 * @since JDK 1.8
 */
public class VideoProvider implements Runnable {

    private String videoPath;
    private String cachePath;
    private volatile boolean stop;
    private FFmpegFrameGrabber videoCapture;

    public VideoProvider(String videoPath, String cachePath) throws IOException {
        this.videoPath = videoPath;
        this.cachePath = cachePath;
        File cacheDirectory = new File(cachePath);
        if (!cacheDirectory.exists()) {
            Files.createDirectory(cacheDirectory.toPath());
        }
        videoCapture = new FFmpegFrameGrabber(videoPath);
        videoCapture.start();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            checkCache();
            if (stop && videoCapture != null) {
                videoCapture.close();
            }
        } catch (IOException | InterruptedException e) {
            Notifications.Bus.notify(new Notification("extras", "Notice",
                    "file not found", NotificationType.INFORMATION));
        }
    }

    private void checkCache() throws IOException, InterruptedException {
        System.out.println("start queque   " + System.currentTimeMillis());
        int maxVideoFrames = videoCapture.getLengthInVideoFrames();
        for (int i = 0; i < maxVideoFrames && !stop; ) {
            Path tempPath = Paths.get(cachePath, i + ".jpg");
            if (!tempPath.toFile().exists()) {
                videoCapture.setVideoFrameNumber(i);
                Frame frame = videoCapture.grabImage();
                if (frame != null) {
                    Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                    BufferedImage bufferedImage = java2DFrameConverter.getBufferedImage(frame);
                    ImageIO.write(bufferedImage, "jpg", tempPath.toFile());
                }
            }
            if (!CacheQueue.blockingQueue.offer(tempPath.toString(), 100L, TimeUnit.MILLISECONDS)) {
                continue;
            }
            i++;
        }
    }


    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
