package com.tea.common.util;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * 
 */
public class HttpUtils {
    public static Hashtable<String, String[]> parseQueryString(String s) {

        String valArray[] = null;

        if (s == null) {
            throw new IllegalArgumentException();
        }

        Hashtable<String, String[]> ht = new Hashtable<String, String[]>();
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                // XXX
                // should give more detail about the illegal argument
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos+1, pair.length()), sb);
            if (ht.containsKey(key)) {
                String oldVals[] = ht.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++) {
                    valArray[i] = oldVals[i];
                }
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            ht.put(key, valArray);
        }

        return ht;
    }

    public static Hashtable<String, String[]> parsePostData(int len,
                                                            ServletInputStream in) {
        // XXX
        // should a length of 0 be an IllegalArgumentException

        if (len <=0) {
            // cheap hack to return an empty hash
            return new Hashtable<String, String[]>();
        }

        if (in == null) {
            throw new IllegalArgumentException();
        }

        //
        // Make sure we read the entire POSTed body.
        //
        byte[] postedBytes = new byte [len];
        try {
            int offset = 0;

            do {
                int inputLen = in.read (postedBytes, offset, len - offset);
                if (inputLen <= 0) {
                    throw new IllegalArgumentException ("Error in read");
                }
                offset += inputLen;
            } while ((len - offset) > 0);

        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // XXX we shouldn't assume that the only kind of POST body
        // is FORM data encoded using ASCII or ISO Latin/1 ... or
        // that the body should always be treated as FORM data.
        //

        try {
            String postedBody = new String(postedBytes, 0, len, "8859_1");
            return parseQueryString(postedBody);
        } catch (java.io.UnsupportedEncodingException e) {
            // XXX function should accept an encoding parameter & throw this
            // exception.  Otherwise throw something expected.
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    /*
     * Parse a name in the query string.
     */
    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i+1, i+3),
                                16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        // XXX
                        // need to be more specific about illegal arg
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest  = s.substring(i);
                        sb.append(rest);
                        if (rest.length()==2)
                            i++;
                    }

                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }
}
