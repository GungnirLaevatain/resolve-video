package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import controller.VideoProviderController;
import org.jetbrains.annotations.NotNull;

/**
 * The class Main action.
 *
 * @author jiangla
 * @version 2018 -10-12 16:23:51
 * @since JDK 1.8
 */
public class StopAction extends AnAction {
    public StopAction() {
        super("Resolve Video");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VideoProviderController.stop();
    }
}
