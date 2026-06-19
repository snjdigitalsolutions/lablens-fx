package com.snjdigitalsolutions.lablensfx.state;

import com.snjdigitalsolutions.lablensfx.setting.Interval;
import javafx.beans.property.*;
import org.springframework.stereotype.Component;

@Component
public class SettingState {

    private final BooleanProperty settingsLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty showIPs = new SimpleBooleanProperty(true);
    private final BooleanProperty promptWhenConfigSelectionChanges = new SimpleBooleanProperty(true);
    private final LongProperty snapshotIntervalInSeconds = new SimpleLongProperty(0L);
    private final IntegerProperty graphLevelSpacing = new SimpleIntegerProperty(0);

    /**
     * Returns whether application settings have been loaded from the database.
     *
     * @return {@code true} if settings are loaded
     */
    public boolean isSettingsLoaded() {
        return settingsLoaded.get();
    }

    /**
     * Returns the observable property indicating whether settings have been loaded.
     *
     * @return the settings-loaded property
     */
    public BooleanProperty settingsLoadedProperty() {
        return settingsLoaded;
    }

    /**
     * Returns whether IP addresses should be displayed in the host list.
     *
     * @return {@code true} if IP addresses are visible
     */
    public boolean isShowIPs() {
        return showIPs.get();
    }

    /**
     * Returns the observable property for the show-IP-addresses setting.
     *
     * @return the show-IPs property
     */
    public BooleanProperty showIPsProperty() {
        return showIPs;
    }

    /**
     * Returns whether the user should be prompted when changing configuration selection.
     *
     * @return {@code true} if the prompt is enabled
     */
    public boolean isPromptWhenConfigSelectionChanges() {
        return promptWhenConfigSelectionChanges.get();
    }

    /**
     * Returns the observable property for the prompt-on-config-change setting.
     *
     * @return the prompt-when-config-selection-changes property
     */
    public BooleanProperty promptWhenConfigSelectionChangesProperty() {
        return promptWhenConfigSelectionChanges;
    }

    /**
     * Returns the current snapshot interval expressed in seconds.
     *
     * @return the snapshot interval in seconds
     */
    public long getSnapshotIntervalInSeconds() {
        return snapshotIntervalInSeconds.get();
    }

    /**
     * Returns the observable {@link LongProperty} for the snapshot interval in seconds,
     * allowing UI components to bind directly to interval changes.
     *
     * @return the snapshot-interval-in-seconds property
     */
    public LongProperty snapshotIntervalInSecondsProperty() {
        return snapshotIntervalInSeconds;
    }

    /**
     * Sets the snapshot interval by multiplying the given {@link Interval} unit's seconds
     * value by the specified quantity and storing the result.
     *
     * @param snapshotInterval the time unit to use (e.g. {@link com.snjdigitalsolutions.lablensfx.setting.Interval#MINUTES})
     * @param quantity         the number of units
     */
    public void setSnapshotInterval(Interval snapshotInterval, Integer quantity) {
        this.snapshotIntervalInSeconds.set(snapshotInterval.multiplier() * quantity);
    }

    public int getGraphLevelSpacing() {
        return graphLevelSpacing.get();
    }

    public IntegerProperty graphLevelSpacingProperty() {
        return graphLevelSpacing;
    }

    public void setGraphLevelSpacing(Integer graphLevelSpacing) {
        this.graphLevelSpacing.set(graphLevelSpacing);
    }
}
