/******************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package prizm.env;

import prizm.util.Logger;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class DesktopMode implements RuntimeMode {

    private DesktopSystemTray desktopSystemTray;
    private Class desktopApplication;

    @Override
    public void init() {
        LookAndFeel.init();
        desktopSystemTray = new DesktopSystemTray();
        SwingUtilities.invokeLater(desktopSystemTray::createAndShowGUI);
    }

    @Override
    public void setServerStatus(ServerStatus status, URI wallet, File logFileDir) {
        desktopSystemTray.setToolTip(new SystemTrayDataProvider(status.getMessage(), wallet, logFileDir));
    }

    @Override
    public void launchDesktopApplication() {
        Logger.logInfoMessage("Launching desktop wallet");
        try {
            desktopApplication = Class.forName("prizmdesktop.DesktopApplication");
            desktopApplication.getMethod("launch").invoke(null);
        } catch (ReflectiveOperationException e) {
            Logger.logInfoMessage("prizmdesktop.DesktopApplication failed to launch", e);
        }
    }

    @Override
    public void shutdown() {
        desktopSystemTray.shutdown();
        if (desktopApplication == null) {
            return;
        }
        try {
            desktopApplication.getMethod("shutdown").invoke(null);
        } catch (ReflectiveOperationException e) {
            Logger.logInfoMessage("prizmdesktop.DesktopApplication failed to shutdown", e);
        }
    }

    @Override
    public void alert(String message) {
        desktopSystemTray.alert(message);
    }
}
