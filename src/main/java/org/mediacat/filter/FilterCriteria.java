package org.mediacat.filter;

public class FilterCriteria {
    public final boolean includeUntrusted;
    public final Quality[] allowedQualities;
    public final int fetchCount;

    public FilterCriteria(boolean includeUntrusted, Quality[] allowedQualities, int fetchCount) {
        this.includeUntrusted = includeUntrusted;
        this.allowedQualities = allowedQualities;
        this.fetchCount = fetchCount;
    }
}
