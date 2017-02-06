/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.svatos.nb.osgi.runner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OSGi",
        id = "org.svatos.mavenproject2.OSGiRunAction"
)
@ActionRegistration(
        iconBase = "org/svatos/nb/osgi/runner/runx.png",
        displayName = "#CTL_OSGiRunAction"
)
@ActionReference(path = "Toolbars/OSGi", position = 300)
@Messages("CTL_OSGiRunAction=Run OSGi container")
public final class OSGiRunAction implements ActionListener {

    private final List<Project> context;

    public OSGiRunAction(List<Project> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        OsgiRunPanel panel = new OsgiRunPanel( context);
        NotifyDescriptor notifyDescriptor = new NotifyDescriptor(panel,
                "Run Project in OSGi container",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                new String[]{"Run", "Cancel"},
                null);
        String notify = (String) DialogDisplayer.getDefault().notify(notifyDescriptor);
        switch (notify) {
            case "Cancel":
                return;
            case "Run": {
                try {
                    //save configuration
                    panel.save();
                    // 
                    Lookup.getDefault().lookup(OSGiController.class).runOsgi(
                            panel.getModuleConfigurations(),
                            panel.getPlatform(),
                            panel.getBat(),
                            panel.getVmArgumentsString());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                break;
            }
            default:
                throw new IllegalStateException("INVALID " + notify);
        }
    }

}
