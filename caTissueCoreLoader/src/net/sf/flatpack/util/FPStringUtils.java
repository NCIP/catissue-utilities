/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

package net.sf.flatpack.util;

/**
 * Thanks to the Apache Commons Contributors.
 * 
 * @author Jakarta Commons
 */
public final class FPStringUtils {
    private FPStringUtils() {
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * PZStringUtils.isBlank(null)      = true
     * PZStringUtils.isBlank("")        = true
     * PZStringUtils.isBlank(" ")       = true
     * PZStringUtils.isBlank("bob")     = false
     * PZStringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(final String str) {
        int strLen = str == null ? 0 : str.length();        
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * PZStringUtils.isNotBlank(null)      = false
     * PZStringUtils.isNotBlank("")        = false
     * PZStringUtils.isNotBlank(" ")       = false
     * PZStringUtils.isNotBlank("bob")     = true
     * PZStringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(final String str) {
        return !FPStringUtils.isBlank(str);
    }

}
