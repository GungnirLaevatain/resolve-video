package ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import controller.VideoProviderController;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * The class Setting.
 *
 * @author jiangla
 * @version 2018 -10-12 10:37:50
 * @since JDK 1.8
 */
public class Setting implements Configurable {

    public static final String FOLDER = "ResolveVideoFile";
    public static final String CACHE_FOLDER = "ResolveVideoCacheFile";


    private TextFieldWithBrowseButton imageFolder;
    private TextFieldWithBrowseButton cacheFolder;

    private JPanel rootPanel;


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Resolve Video";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();

        imageFolder.addBrowseFolderListener(new TextBrowseFolderListener(descriptor) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                String current = imageFolder.getText();

                if (!current.isEmpty()) {
                    fc.setCurrentDirectory(new File(current));
                }

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                fc.showOpenDialog(rootPanel);

                File file = fc.getSelectedFile();

                imageFolder.setText(file == null ? "" : file.getAbsolutePath());
            }

        });
        cacheFolder.addBrowseFolderListener(new TextBrowseFolderListener(descriptor) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                String current = cacheFolder.getText();

                if (!current.isEmpty()) {
                    fc.setCurrentDirectory(new File(current));
                }

                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showOpenDialog(rootPanel);

                File file = fc.getSelectedFile();
                cacheFolder.setText(file == null ? "" : file.getAbsolutePath());
            }
        });
        return rootPanel;
    }

    @Override
    public boolean isModified() {

        PropertiesComponent prop = PropertiesComponent.getInstance();

        String file = prop.getValue(FOLDER, "");
        String cache = prop.getValue(CACHE_FOLDER, "");

        String nowFile = imageFolder.getText();
        String nowCacheFolder = cacheFolder.getText();

        return !file.equals(nowFile) || !cache.equals(nowCacheFolder);
    }

    @Override
    public void apply() throws ConfigurationException {

        PropertiesComponent prop = PropertiesComponent.getInstance();
        boolean modified = isModified();

        prop.setValue(FOLDER, imageFolder.getText());
        prop.setValue(CACHE_FOLDER, cacheFolder.getText());

        if (modified) {
            VideoProviderController.stop();
            VideoProviderController.clearCache();
        }
    }

    @Override
    public void reset() {

        PropertiesComponent prop = PropertiesComponent.getInstance();

        imageFolder.setText(prop.getValue(FOLDER));
        cacheFolder.setText(prop.getValue(CACHE_FOLDER));
    }

    @Override
    public void disposeUIResources() {

    }
}
