package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.state.ApplicationView;
import com.snjdigitalsolutions.lablensfx.state.SelectedViewState;
import org.springframework.stereotype.Service;

@Service
public class ViewService {

    private final SelectedViewState selectedViewState;

    /**
     * Creates the view service backed by the given view-selection state.
     *
     * @param selectedViewState the state object that tracks the active view
     */
    public ViewService(SelectedViewState selectedViewState) {
        this.selectedViewState = selectedViewState;
    }

    /**
     * Returns whether the dashboard is the currently selected application view.
     *
     * @return {@code true} if the dashboard view is active
     */
    public boolean dashboardSelected() {
        return selectedViewState.getSelectedView() == ApplicationView.DASHBOARD;
    }

}
