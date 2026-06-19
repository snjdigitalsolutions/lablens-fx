package com.snjdigitalsolutions.lablensfx.nodes;

public enum SummaryPanelType {

    NUM_HOSTS("Total Hosts", "registered", ""),
    NUM_ONLINE("Hosts Online", "reachable via ssh", "summary-panel-count-green"),
    NUM_CONFIG_CHANGE("Configuration Changes", "all hosts", "summary-panel-count-orange"),
    NUM_LOG_ERRORS("Log Errors", "all hosts", "summary-panel-count-red");

    private final String header;
    private final String moreInfo;
    private final String cssClass;

    /**
     * Creates a summary panel type descriptor.
     *
     * @param header   the label shown at the top of the panel
     * @param moreInfo the secondary detail text
     * @param cssClass the CSS class applied to the count label
     */
    SummaryPanelType(String header, String moreInfo, String cssClass) {
        this.header = header;
        this.moreInfo = moreInfo;
        this.cssClass = cssClass;
    }

    /**
     * Returns the panel header label text.
     *
     * @return the header string
     */
    public String getHeader() {
        return header;
    }

    /**
     * Returns the secondary detail text.
     *
     * @return the more-info string
     */
    public String getMoreInfo() {
        return moreInfo;
    }

    /**
     * Returns the CSS class name for the count label.
     *
     * @return the CSS class string
     */
    public String getCssClass() {
        return cssClass;
    }
}
