/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.svatos.nb.osgi.runner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.netbeans.api.io.OutputWriter;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author svatos.jirka@gmail.com
 */
@ServiceProvider(service = OSGiController.class)
public class OSGiController {

    private File lastBat;
    private File lastPlugins;

    

    public void runAgain() {
        try {
            InputOutput io = prepareIO();
            runOSGi(io,lastPlugins,lastBat);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void debugOsgi(List<ModuleConfiguration> moduleConfigurations, File equinoxPlatform, String vmArgumentsString) throws IOException {
//        DebuggerInfo di = DebuggerInfo.create(
//                "OSGi Attaching First Debugger Info",
//                new Object[]{
//                    AttachingDICookie.create(
//                            "localhost",
//                            1234
//                    )
//                }
//        );
//        DebuggerEngine[] startDebugging = DebuggerManager.getDebuggerManager().startDebugging(di);
    }

    public void runOsgi(List<ModuleConfiguration> moduleConfigurations, File equinoxPlatform, File bat,String vmArgumentsString) throws IOException {
        this.lastBat = bat;
        this.lastPlugins = equinoxPlatform;
        String bundleParamString = "";
        bundleParamString = moduleConfigurations.stream().
                filter((bundle) -> bundle.isEnable()).
                map((bundle) -> bundle.generateRunParam() + ",").
                reduce(bundleParamString, String::concat);
        //
        InputOutput io = prepareIO();
        //generate BAT
        generateBatFile(vmArgumentsString, bundleParamString, equinoxPlatform,bat);

        runOSGi(io,equinoxPlatform,bat);

    }

    private InputOutput prepareIO() {
        InputOutput io = IOProvider.getDefault().getIO("OSGi console", false);
        io.getOut().write("Configuration to start "+lastBat+"  \n\n");
        io.getOut().flush();
        return io;
    }

    private void runOSGi(InputOutput io,File equinox,File bat) throws IOException {
        Process call = new ProcessBuilder("cmd /c \"" + bat.getAbsolutePath() + "\"")
                .directory(equinox)
                .start();
        //
        new Thread(() -> {
            try {
                InputStream pOut = call.getErrorStream();
                byte[] buffer = new byte[4096];
                int n;
                while ((n = pOut.read(buffer)) > 0) {
                    io.getErr().write(new String(buffer, 0, n));
                    io.getErr().flush();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }).start();
        new Thread(() -> {
            try {
                InputStream pOut = call.getInputStream();
                byte[] buffer = new byte[4096];
                int n;
                while ((n = pOut.read(buffer)) > 0) {
                    io.getOut().write(new String(buffer, 0, n));
                    io.getOut().flush();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }).start();
        //
        new Thread(() -> {
            try {
                OutputStream pIn = call.getOutputStream();
                char[] buffer = new char[1];
                int n;
                //
                char[] exit = new char[4];
                //
                while ((n = io.getIn().read(buffer)) > 0) {
                    pIn.write(buffer[0]);
                    //
                    for (int i = 0; i < 3; i++) {
                        exit[i] = exit[i + 1];
                    }
                    exit[3] = buffer[0];
                    final OutputWriter out = io.getOut();
                    if (new String(exit).equals("exit")) {
                        call.destroyForcibly();
                        out.write("OSGi stopped!\n");
                        out.flush();
                    }
                    //
                    pIn.flush();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }).start();
    }

    private void generateBatFile(String vmArgumentsString, String bundleParamString, File equinoxPlatform,File bat) throws IOException {
        try (FileWriter fw = new FileWriter(bat)) {
            fw.write("cd "+equinoxPlatform+" \n");
            fw.write("java ");
            fw.write(vmArgumentsString);
            fw.write(" -Declipse.ignoreApp=true ");
            fw.write(" -Declipse.application.launchDefault=false ");
            fw.write(" -Dosgi.bundles=\"" + bundleParamString);
            fw.write(OsgiRunPanel.findBundles(equinoxPlatform, "org.eclipse.equinox.ds").iterator().next().getFileName() + "@1:start\"");
            fw.write(" -jar " + OsgiRunPanel.findBundles(equinoxPlatform, "org.eclipse.osgi").iterator().next().getFileName());
            fw.write(" -console -clean ");
            fw.flush();
        }

    }
}
