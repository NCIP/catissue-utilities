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


import java.util.ArrayList;
import java.util.List;

/**
 *  Static utilities that are used to perform parsing in the
 *         DataSet class These can also be used for low level parsing, if not
 *         wishing to use the DataSet class.
 *
 * @author Paul Zepernick
 * @author Benoit Xhenseval
 * 
 * @author Ian Fore
 * Severely cutdown version of ParserUtils class from FlatPack
 * see flatpack.sourceforge.net for the full package
 */
public final class ParserUtils {
    private ParserUtils() {
    }

    /**
     * Returns an ArrayList of items in a delimited string. If there is no
     * qualifier around the text, the qualifier parameter can be left null, or
     * empty. There should not be any line breaks in the string. Each line of
     * the file should be passed in individually.
     * Elements which are not qualified will have leading and trailing white
     * space removed.  This includes unqualified elements, which may be
     * contained in an unqualified parse: "data",  data  ,"data"
     *
     * Special thanks to Benoit for contributing this much improved speedy parser :0)
     *
     * @author Benoit Xhenseval
     * @param line -
     *            String of data to be parsed
     * @param delimiter -
     *            Delimiter seperating each element
     * @param qualifier -
     *            qualifier which is surrounding the text
     * @param initialSize -
     *            intial capacity of the List size
     * @return List
     */
    public static List splitLine(final String line, final char delimiter, final char qualifier, final int initialSize) {
        final List list = new ArrayList(initialSize);

        if (delimiter == 0) {
            list.add(line);
            return list;
        } else if (line == null) {
            return list;
        }

        String trimmedLine;
        if (delimiter == '\t' || delimiter == ' ') {
            //skip the trim for these delimiters, doing the trim will mess up the parse
            //on empty records which contain just the delimiter
            trimmedLine = line;
        } else {
            trimmedLine = line.trim();
        }

        final int size = trimmedLine.length();

        if (size == 0) {
            list.add("");
            return list;
        }

        boolean insideQualifier = false;
        char previousChar = 0;
        int startBlock = 0;
        int endBlock = 0;
        boolean blockWasInQualifier = false;

        final String doubleQualifier = String.valueOf(qualifier) + String.valueOf(qualifier);
        for (int i = 0; i < size; i++) {

            final char currentChar = trimmedLine.charAt(i);
            if (currentChar != delimiter && currentChar != qualifier) {
                previousChar = currentChar;
                endBlock = i + 1;
                continue;
            }

            if (currentChar == delimiter) {
                // we've found the delimiter (eg ,)
                if (!insideQualifier) {
                    String trimmed = trimmedLine.substring(startBlock, endBlock > startBlock ? endBlock : startBlock + 1);
                    if (!blockWasInQualifier) {
                        trimmed = trimmed.trim();
                    } else {
                        //need to run the qualifier replace when it was in qualifier
                        trimmed = trimmed.replaceAll(doubleQualifier, String.valueOf(qualifier));
                    }

                    if (trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
                        list.add("");
                    } else {
                        list.add(trimmed);
                    }
                    blockWasInQualifier = false;
                    startBlock = i + 1;
                }
            } else if (currentChar == qualifier) {
                if (!insideQualifier && previousChar != qualifier) {
                    if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
                        insideQualifier = true;
                        startBlock = i + 1;
                    } else {
                        endBlock = i + 1;
                    }
                } else {
                    if (i + 1 < size && delimiter != ' ') {
                        //this is used to allow unescaped qualifiers to be contained within the element
                        //do not run this check is a space is being used as a delimiter
                        //we don't want to trim the delimiter off
                        //loop until we find a char that is not a space, or we reach the end of the line.
                        int start = i + 1;
                        char charToCheck = trimmedLine.charAt(start);
                        while (charToCheck == ' ') {
                            start++;
                            if (start == size) {
                                break;
                            }
                            charToCheck = trimmedLine.charAt(start);
                        }

                        if (charToCheck != delimiter) {
                            previousChar = currentChar;
                            endBlock = i + 1;
                            continue;
                        }

                    }
                    insideQualifier = false;
                    blockWasInQualifier = true;
                    endBlock = i;
                    // last column (e.g. finishes with ")
                    if (i == size - 1) {
                        String str = trimmedLine.substring(startBlock, size - 1);
                        str = str.replaceAll(doubleQualifier, String.valueOf(qualifier));
                        list.add(str);
                        startBlock = i + 1;
                    }
                }
            }
            previousChar = currentChar;
        }

        if (startBlock < size) {
            String str = trimmedLine.substring(startBlock, size);
            str = str.replaceAll(doubleQualifier, String.valueOf(qualifier));
            if (blockWasInQualifier) {
                if (str.charAt(str.length() - 1) == qualifier) {
                    list.add(str.substring(0, str.length() - 1));
                } else {
                    list.add(str);
                }
            } else {
                list.add(str.trim());
            }
        } else if (trimmedLine.charAt(size - 1) == delimiter) {
            list.add("");
        }

        return list;
    }

 }
