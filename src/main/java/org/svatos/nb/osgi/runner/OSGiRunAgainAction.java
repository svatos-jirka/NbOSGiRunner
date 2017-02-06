package org.svatos.nb.osgi.runner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "OSGi",
        id = "org.svatos.mavenproject2.OSGiRunAgainAction"
)
@ActionRegistration(
        iconBase = "org/svatos/nb/osgi/runner/againx.png",
        displayName = "#CTL_OSGiRunAgainAction"
)
@ActionReference(path = "Toolbars/OSGi", position = 500)
@Messages("CTL_OSGiRunAgainAction=Run OSGi container")
public final class OSGiRunAgainAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ev) {
        Lookup.getDefault().lookup(OSGiController.class).runAgain();
    }
}
