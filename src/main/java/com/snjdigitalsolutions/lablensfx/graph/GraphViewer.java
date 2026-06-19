package com.snjdigitalsolutions.lablensfx.graph;

import com.snjdigitalsolutions.lablensfx.orm.Relational;

public interface GraphViewer<V extends Relational> {

    void showGraph(String labelText);

}
