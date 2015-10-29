/**
 * 
 * ====================================================================================================================================================
 * "AppWork Utilities" License
 * ====================================================================================================================================================
 * Copyright (c) 2009-2015, AppWork GmbH <e-mail@appwork.org>
 * Schwabacher Straße 117
 * 90763 Fürth
 * Germany
 * 
 * === Preamble ===
 * This license establishes the terms under which the AppWork Utilities Source Code & Binary files may be used, copied, modified, distributed, and/or redistributed.
 * The intent is that the AppWork GmbH is able to provide  their utilities library for free to non-commercial projects whereas commercial usage is only permitted after obtaining a commercial license.
 * These terms apply to all files that have the "AppWork Utilities" License header (IN the file), a <filename>.license or <filename>.info (like mylib.jar.info) file that contains a reference to this license.
 * 
 * === 3rd Party Licences ===
 * Some parts of the AppWork Utilities use or reference 3rd party libraries and classes. These parts may have different licensing conditions. Please check the *.license and *.info files of included libraries
 * to ensure that they are compatible to your use-case. Further more, some *.java have their own license. In this case, they have their license terms in the java file header.
 * 
 * === Definition: Commercial Usage ===
 * If anybody or any organization is generating income (directly or indirectly) by using "AppWork Utilities" or if there's as much as a
 * sniff of commercial interest or aspect in what you are doing, we consider this as a commercial usage. If you are unsure whether your use-case is commercial or not, consider it as commercial.
 * === Dual Licensing ===
 * === Commercial Usage ===
 * If you want to use AppWork Utilities in a commercial way (see definition above), you have to obtain a paid license from AppWork GmbH.
 * Contact AppWork for further details: e-mail@appwork.org
 * === Non-Commercial Usage ===
 * If there is no commercial usage (see definition above), you may use AppWork Utilities under the terms of the
 * "GNU Affero General Public License" (http://www.gnu.org/licenses/agpl-3.0.en.html).
 * 
 * If the AGPL does not fit your needs, please contact us. We'll find a solution.
 * ====================================================================================================================================================
 * ==================================================================================================================================================== */
package org.appwork.utils.os;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author daniel
 *
 */
public class DesktopSupportJavaDesktop implements DesktopSupport {

    private Boolean openFileSupported  = null;
    private Boolean browseURLSupported = null;

    @Override
    public void browseURL(final URL url) throws IOException, URISyntaxException {
        if (this.isBrowseURLSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            desktop.browse(url.toURI());
        } else {
            throw new IOException("browseURL not supported");
        }
    }

    @Override
    public boolean isBrowseURLSupported() {
        if (this.browseURLSupported != null) {
            return this.browseURLSupported;
        }
        if (!Desktop.isDesktopSupported()) {
            this.browseURLSupported = false;
            return false;
        }
        final Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            this.browseURLSupported = false;
            return false;
        }
        this.browseURLSupported = true;
        return true;
    }

    @Override
    public boolean isOpenFileSupported() {
        if (this.openFileSupported != null) {
            return this.openFileSupported;
        }
        if (!Desktop.isDesktopSupported()) {
            this.openFileSupported = false;
            return false;
        }
        final Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            this.openFileSupported = false;
            return false;
        }
        this.openFileSupported = true;
        return true;
    }

    @Override
    public void openFile(final File file) throws IOException {
        if (this.isOpenFileSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            final URI uri = file.getCanonicalFile().toURI();
            desktop.open(new File(uri));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.utils.os.DesktopSupport#shutdown()
     */
    @Override
    public boolean shutdown(boolean force) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.utils.os.DesktopSupport#standby()
     */
    @Override
    public boolean standby() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.utils.os.DesktopSupport#hibernate()
     */
    @Override
    public boolean hibernate() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.utils.os.DesktopSupport#getDefaultDownloadDirectory()
     */
    @Override
    public String getDefaultDownloadDirectory() {
        return null;
    }

}
