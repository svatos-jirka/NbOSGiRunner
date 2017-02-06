
package org.svatos.nb.osgi.runner;

import java.io.File;

/**
 *
 * @author svatos.jirka@gmail.com
 */
public class ExternalModuleConfiguration extends ModuleConfiguration {

    public static ExternalModuleConfiguration create(String path) {
         final File file = new File(path);
        final String fileName = file.getName();
        final String moduleName = fileName.substring(0, fileName.length() - 4);
        return new ExternalModuleConfiguration(path, fileName, moduleName, file, true);
    }

    public ExternalModuleConfiguration(String path, String fileName, String moduleName, File file, boolean enable) {
        super(path, fileName, moduleName, file, enable);
    }


    @Override
    public String generateRunParam() {
        return getFile().getAbsolutePath()+ "@start";
    }

}
