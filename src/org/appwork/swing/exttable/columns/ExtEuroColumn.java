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
package org.appwork.swing.exttable.columns;

import java.text.DecimalFormat;

import javax.swing.JComponent;

import org.appwork.swing.exttable.ExtColumn;
import org.appwork.swing.exttable.ExtDefaultRowSorter;
import org.appwork.swing.exttable.ExtTableModel;
import org.appwork.utils.swing.renderer.RenderLabel;

public abstract class ExtEuroColumn<E> extends ExtColumn<E> {

    private static final long   serialVersionUID = 3468695684952592989L;
    private RenderLabel         renderer;
    final private DecimalFormat format           = new DecimalFormat("0.00");

    public ExtEuroColumn(final String name, final ExtTableModel<E> table) {
        super(name, table);
        this.renderer = new RenderLabel();

        this.setRowSorter(new ExtDefaultRowSorter<E>() {
            /**
             * sorts the icon by hashcode
             */
            @Override
            public int compare(final Object o1, final Object o2) {
                if (ExtEuroColumn.this.getCent(o1) == ExtEuroColumn.this.getCent(o2)) { return 0; }
                if (this.getSortOrderIdentifier() == ExtColumn.SORT_ASC) {
                    return ExtEuroColumn.this.getCent(o1) > ExtEuroColumn.this.getCent(o2) ? -1 : 1;
                } else {
                    return ExtEuroColumn.this.getCent(o1) < ExtEuroColumn.this.getCent(o2) ? -1 : 1;
                }
            }

        });
    }

    @Override
    public void configureRendererComponent(final E value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {

        try {
            this.renderer.setText(this.format.format(this.getCent(value) / 100.0f) + " €");
        } catch (final Exception e) {
            this.renderer.setText(this.format.format("0.0f") + " €");
        }

    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    abstract protected long getCent(Object o2);

    /**
     * @return
     */
    @Override
    public JComponent getEditorComponent(final E value, final boolean isSelected, final int row, final int column) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public JComponent getRendererComponent(final E value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        return this.renderer;
    }

    @Override
    public boolean isEditable(final Object obj) {
        return false;
    }

    @Override
    public boolean isEnabled(final Object obj) {
        return true;
    }

    @Override
    public boolean isSortable(final Object obj) {
        return true;
    }

    @Override
    public void resetEditor() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetRenderer() {

        this.renderer.setOpaque(false);
        this.renderer.setBorder(ExtColumn.DEFAULT_BORDER);

    }

    @Override
    public void setValue(final Object value, final Object object) {
    }
}
