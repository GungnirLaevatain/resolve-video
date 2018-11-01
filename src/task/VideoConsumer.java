package task;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class VideoConsumer implements Runnable {
    private PropertiesComponent prop = PropertiesComponent.getInstance();

    @Override
    public void run() {
        try {
            String path = CacheQueue.blockingQueue.poll(40, TimeUnit.MILLISECONDS);
            if (StringUtils.isNoneEmpty(path)) {
                prop.setValue(IdeBackgroundUtil.FRAME_PROP, null);
                prop.setValue(IdeBackgroundUtil.EDITOR_PROP, path);
//                IdeBackgroundUtil.repaintAllWindows();
            }
//            System.out.println(System.currentTimeMillis());
        } catch (Exception ignored) {

        }
    }
}
