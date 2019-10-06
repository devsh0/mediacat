package org.mediacat.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class Quality {
    private final String name;
    private final Pattern pattern;

    private Quality(String name, String regex) {
        this.name = name;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean equals(Object q) {
        if (q instanceof Quality)
            return name.equals(((Quality) q).name);

        return false;
    }

    public String toString() {
        return this.name;
    }

    private static final String THEATRE_REGEX = "((hd|new)[ -]?)cam|hd[ -]?ts|telesync|p(re)?dvd([- ]?rip)?|cam[- ]rip";
    public static final Quality THEATRE = new Quality("THEATRE", THEATRE_REGEX);

    private static final String HD_REGEX =
            "((web-?(dl)?|h?d|ppv|dv[db]|h?d?tv|vod|full|iso|d(s|th|vb)|sat)[ -]?rip)|" +
                    "((dvd|bd)?[ -]?scr)|" +
                    "(web[ -]?(dl|hd|cap)?|720p[- .]?web)|" +
                    "(hd[- ]?tc|wp|workprint|telecine|ppv|(dvd|vod)r|dvd[- ]?(full|mux)|(hd|pd)[-]?tv)|" +
                    "blu[-]?ray|b((d|r)[-]?)?rip|bd(mv|r)|(1080|2160)p";

    public static final Quality HD = new Quality("HD", HD_REGEX);

    public static final Quality UNKNOWN = new Quality("UNKNOWN", "");

    public static Quality fromName(String name) {
        Matcher theatreMatcher = THEATRE.pattern.matcher(name);
        Matcher hdMatcher = HD.pattern.matcher(name);

        // find the longest match
        int theatreLen = findLongestMatchLength(theatreMatcher);
        int hdLen = findLongestMatchLength(hdMatcher);

        if (theatreLen > hdLen)
            return THEATRE;

        return hdLen > 0 ? HD : UNKNOWN;
    }

    private static int findLongestMatchLength(Matcher matcher) {
        int greatestLen = 0;
        while (matcher.find()) {
            String match = matcher.group();
            if (match != null) {
                greatestLen = Math.max(match.length(), greatestLen);
            }
        }
        return greatestLen;
    }
}
