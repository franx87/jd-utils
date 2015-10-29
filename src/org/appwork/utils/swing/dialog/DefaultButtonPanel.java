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
package org.appwork.utils.swing.dialog;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.appwork.swing.MigPanel;

/**
 * @author Thomas
 * 
 */
public class DefaultButtonPanel extends MigPanel {

    /**
     * @param string
     * @param string2
     * @param string3
     */
    public DefaultButtonPanel(final String ins, final String columns, final String rows) {
        super(ins, columns, rows);
    }

    /**
     * @param okButton
     */
    public void addOKButton(final JButton okButton) {
        super.add(okButton, "alignx right,tag ok,sizegroup confirms");
    }

    /**
     * @param cancelButton
     */
    public void addCancelButton(final JButton cancelButton) {
        add(cancelButton, "alignx right,tag cancel,sizegroup confirms");

    }

    /**
     * @param a
     * @return
     */
    public JButton addAction(final AbstractAction a) {

        String tag = (String) a.getValue("tag");
        if (tag == null) {
            tag = "help";
        }
        JButton bt;
        add(bt = new JButton(a), "tag " + tag + ",sizegroup confirms");

        return bt;
    }

}
