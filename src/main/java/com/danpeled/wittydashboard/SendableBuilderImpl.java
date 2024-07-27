package com.danpeled.wittydashboard;


import org.frcforftc.networktables.sendable.Sendable;
import org.frcforftc.networktables.sendable.SendableBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;

class SendableBuilderImpl extends SendableBuilder {
    private final Set<String> keysWithListeners = Collections.synchronizedSet(new HashSet<>());

    public SendableBuilderImpl(Sendable sendable) {
        super(sendable);
    }

    @Override
    public void publishDoubleProperty(String key, Supplier<Double> getter, Consumer<Double> setter) {

    }

    @Override
    public void publishDoubleArrayProperty(String key, Supplier<Double[]> getter, Consumer<Double[]> setter) {

    }

    @Override
    public void publishBooleanProperty(String key, Supplier<Boolean> getter, Consumer<Boolean> setter) {

    }

    @Override
    public void publishBooleanArrayProperty(String key, Supplier<Boolean[]> getter, Consumer<Boolean[]> setter) {

    }

    @Override
    public void publishStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {

    }

    @Override
    public void publishStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter) {

    }

    @Override
    public void publishRawProperty(String key, Supplier<Byte[]> getter, Consumer<Byte[]> setter) {

    }

    @Override
    public void publishIntProperty(String key, Supplier<Integer> getter, Consumer<Integer> setter) {

    }

    @Override
    public void publishFloatProperty(String key, Supplier<Float> getter, Consumer<Float> setter) {

    }

    @Override
    public void publishIntArrayProperty(String key, Supplier<Integer[]> getter, Consumer<Integer[]> setter) {

    }

    @Override
    public void publishFloatArrayProperty(String key, Supplier<Float[]> getter, Consumer<Float[]> setter) {

    }
}
