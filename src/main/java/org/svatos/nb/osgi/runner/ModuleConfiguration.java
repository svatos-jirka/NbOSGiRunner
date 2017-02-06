/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.svatos.nb.osgi.runner;

import java.io.File;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author jiri.svatos
 */
public class ModuleConfiguration {

    public static ModuleConfiguration createFromJar(String path) {
        return createFromJar(path, true);
    }
    public static ModuleConfiguration createFromJar(String path,boolean select) {
        final File file = new File(path);
        final String fileName = file.getName();
        final String moduleName = fileName.substring(0, fileName.length() - 4);
        return new ModuleConfiguration(path, fileName, moduleName, file, select);
    }

    public String generateRunParam() {
        return file.getAbsolutePath()+"@start";
    }

    public static ModuleConfiguration createFromProject(Project project) {
        if (project.getProjectDirectory().getFileObject("build") != null) {
            FileObject[] children = project.getProjectDirectory().getFileObject("build").getFileObject("libs").getChildren();
            if (children.length == 1) {
                final FileObject jar = children[0];
                return new ModuleConfiguration(jar.getPath(), jar.getNameExt(), jar.getName(), FileUtil.toFile(jar), true);
            } else {
                throw new IllegalStateException("Project " + project.toString() + " is not built");
            }
        }
        NotifyDescriptor.Message message = new NotifyDescriptor.Message("Project "+project.getProjectDirectory().getName()+" is not built!", NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(message);
        throw new IllegalStateException();
    }
    private final String path, fileName, moduleName;
    private final File file;
    private boolean enable;

    public ModuleConfiguration(String path, String fileName, String moduleName, File file, boolean enable) {
        this.path = path;
        this.fileName = fileName;
        this.moduleName = moduleName;
        this.file = file;
        this.enable = enable;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBundleName() {
        return fileName.substring(0, fileName.length() - 4);
    }

    public File getFile() {
        return file;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPath() {
        return path;
    }

}
