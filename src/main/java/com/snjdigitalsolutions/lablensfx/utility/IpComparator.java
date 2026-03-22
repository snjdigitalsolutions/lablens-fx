package com.snjdigitalsolutions.lablensfx.utility;

import com.snjdigitalsolutions.lablensfx.nodes.IpSortable;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class IpComparator implements Comparator<IpSortable> {

    @Override
    public int compare(IpSortable o1, IpSortable o2) {
        return o1.getIpAddress().compareTo(o2.getIpAddress());
    }
}
