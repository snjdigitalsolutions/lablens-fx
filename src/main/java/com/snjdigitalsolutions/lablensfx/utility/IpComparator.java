package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.nodes.IpSortable;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class IpComparator implements Comparator<IpSortable> {

    /**
     * Compares two {@link IpSortable} objects lexicographically by their IP address strings.
     *
     * @param o1 the first object
     * @param o2 the second object
     * @return a negative integer, zero, or a positive integer as {@code o1}'s IP is less than,
     *         equal to, or greater than {@code o2}'s
     */
    @Override
    public int compare(IpSortable o1, IpSortable o2) {
        return o1.getIpAddress().compareTo(o2.getIpAddress());
    }
}
