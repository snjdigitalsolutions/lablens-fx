package com.snjdigitalsolutions.lablensfx.nodes;

public enum SummaryPanelType {

    NUM_HOSTS("Total Hosts", "registered", ""),
    NUM_ONLINE("Hosts Online", "reachable via ssh", "summary-panel-count-green"),
    NUM_LOG_ERROR("Configuration Changes", "all hosts", "summary-panel-count-orange"),
    NUM_CONFIG_CHANGES("Log Errors", "all hosts", "summary-panel-count-red");

    private final String header;
    private final String moreInfo;
    private final String cssClass;

    SummaryPanelType(String header, String moreInfo, String cssClass) {
        this.header = header;
        this.moreInfo = moreInfo;
        this.cssClass = cssClass;
    }

    public String getHeader() {
        return header;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public String getCssClass() {
        return cssClass;
    }
}
