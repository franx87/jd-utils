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
package org.appwork.storage.config.handler;

import java.lang.annotation.Annotation;

import org.appwork.storage.config.ValidationException;
import org.appwork.storage.config.annotations.DefaultByteValue;
import org.appwork.storage.config.annotations.LookUpKeys;
import org.appwork.storage.config.annotations.SpinnerValidator;

/**
 * @author Thomas
 * 
 */
public class ByteKeyHandler extends KeyHandler<Byte> {

    private SpinnerValidator validator;
    private byte             min;
    private byte             max;

    /**
     * @param storageHandler
     * @param key
     */
    public ByteKeyHandler(final StorageHandler<?> storageHandler, final String key) {
        super(storageHandler, key);
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends Annotation>[] getAllowedAnnotations() {
        // final java.util.List<Class<? extends Annotation>> list = new
        // ArrayList<Class<? extends Annotation>>();
        //
        // list.add(SpinnerValidator.class);

        // return (Class<? extends Annotation>[]) list.toArray(new Class<?>[]
        // {});
        //

        return (Class<? extends Annotation>[]) new Class<?>[] { LookUpKeys.class, SpinnerValidator.class };
    }

    @Override
    protected Class<? extends Annotation> getDefaultAnnotation() {
        return DefaultByteValue.class;
    }

    @Override
    protected void initDefaults() throws Throwable {
        this.setDefaultValue((byte) 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.storage.config.KeyHandler#initHandler()
     */
    @Override
    protected void initHandler() {
        this.validator = this.getAnnotation(SpinnerValidator.class);
        if (this.validator != null) {
            this.min = (byte) this.validator.min();
            this.max = (byte) this.validator.max();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.storage.config.KeyHandler#putValue(java.lang.Object)
     */
    @Override
    protected void putValue(final Byte object) {
        this.storageHandler.getPrimitiveStorage().put(this.getKey(), object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.appwork.storage.config.KeyHandler#validateValue(java.lang.Object)
     */
    @Override
    protected void validateValue(final Byte object) throws Throwable {
        if (this.validator != null) {
            final byte v = object.byteValue();
            if (v < this.min || v > this.max) { throw new ValidationException(); }
        }
    }

}
