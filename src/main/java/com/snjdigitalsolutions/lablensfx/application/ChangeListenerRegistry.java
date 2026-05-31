package com.snjdigitalsolutions.lablensfx.application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChangeListenerRegistry {

    private final Map<Object, List<Runnable>> listenerRemovers = new HashMap<>();

    /**
     * Add a listener to a property and register it to the object containing it
     * such that all listeners can be removed.
     * @param registrant the object with the listener
     * @param property the property being observed
     * @param listener the listener
     * @param <T> Observable Type
     */
    public <T> void add(Object registrant, ObservableValue<T> property, ChangeListener<T> listener) {
        property.addListener(listener);
        if (!listenerRemovers.containsKey(registrant)){
            listenerRemovers.put(registrant, new ArrayList<>());
        }
        listenerRemovers.get(registrant).add(() -> property.removeListener(listener));
    }

    /**
     * Removes and disposes all registered change listeners, preventing memory leaks.
     */
    public void disposeAll(){
        listenerRemovers.keySet().forEach(key -> {
            listenerRemovers.get(key).forEach(Runnable::run);
        });
        listenerRemovers.clear();
    }

    /**
     * Removes and disposes all change listeners registered by the given owner.
     *
     * @param registrant the object whose listeners should be released
     */
    public void disposeForRegistrant(Object registrant){
        if (listenerRemovers.get(registrant) != null){
            listenerRemovers.get(registrant).forEach(Runnable::run);
            listenerRemovers.remove(registrant);
        }
    }

}
