package com.snjdigitalsolutions.lablensfx.service;

import com.snjdigitalsolutions.lablensfx.state.ApplicationView;
import com.snjdigitalsolutions.lablensfx.state.SelectedViewState;
import org.springframework.stereotype.Service;

@Service
public class ViewService {

    private final SelectedViewState selectedViewState;

    public ViewService(SelectedViewState selectedViewState) {
        this.selectedViewState = selectedViewState;
    }

    public boolean dashboardSelected() {
        return selectedViewState.getSelectedView() == ApplicationView.DASHBOARD;
    }

}
