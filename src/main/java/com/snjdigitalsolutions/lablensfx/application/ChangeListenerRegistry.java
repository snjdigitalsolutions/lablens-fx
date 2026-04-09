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

    public <T> void add(Object registrant, ObservableValue<T> prop, ChangeListener<T> listener) {
        prop.addListener(listener);
        if (!listenerRemovers.containsKey(registrant)){
            listenerRemovers.put(registrant, new ArrayList<>());
        }
        listenerRemovers.get(registrant).add(() -> prop.removeListener(listener));
    }

    public void disposeAll(){
        listenerRemovers.keySet().forEach(key -> {
            listenerRemovers.get(key).forEach(Runnable::run);
        });
        listenerRemovers.clear();
    }

    public void disposeForRegistrant(Object registrant){
        listenerRemovers.get(registrant).forEach(Runnable::run);
        listenerRemovers.remove(registrant);
    }

}
