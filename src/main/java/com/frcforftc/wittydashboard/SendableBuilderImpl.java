package com.frcforftc.wittydashboard;

import com.frcforftc.wittydashboard.sendables.Sendable;
import com.frcforftc.wittydashboard.sendables.SendableBuilder;

import org.frcforftc.networktables.NetworkTablesEvent;
import org.frcforftc.networktables.NetworkTablesEventListener;
import org.frcforftc.networktables.NetworkTablesInstance;
import org.frcforftc.networktables.NetworkTablesValue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

class SendableBuilderImpl extends SendableBuilder {
    private final Set<String> keysWithListeners = Collections.synchronizedSet(new HashSet<>());

    public SendableBuilderImpl(Sendable sendable) {
        super(sendable);
    }

    private <T> void publishProperty(String key, Supplier<T> getter, Consumer<T> setter, Class<T> type) {
        if (keysWithListeners.contains(key)) return;

        NetworkTablesInstance.getDefaultInstance().get(key)
                .addListener(new NetworkTablesEventListener(EnumSet.of(NetworkTablesEvent.kTopicUpdated), (NetworkTablesEvent e) -> {
                    NetworkTablesValue value = NetworkTablesInstance.getDefaultInstance().get(key).getValue();
                    if (type.isInstance(value.get())) {
                        setter.accept(type.cast(value.get()));
                    }
                }));

        keysWithListeners.add(key);
    }

    @Override
    public void publishDoubleProperty(String key, Supplier<Double> getter, Consumer<Double> setter) {
        publishProperty(key, getter, setter, Double.class);
    }

    @Override
    public void publishDoubleArrayProperty(String key, Supplier<double[]> getter, Consumer<double[]> setter) {
        publishProperty(key, getter, setter, double[].class);
    }

    @Override
    public void publishBooleanProperty(String key, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        publishProperty(key, getter, setter, Boolean.class);
    }

    @Override
    public void publishBooleanArrayProperty(String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
        publishProperty(key, getter, setter, boolean[].class);
    }

    @Override
    public void publishStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {
        publishProperty(key, getter, setter, String.class);
    }

    @Override
    public void publishStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter) {
        publishProperty(key, getter, setter, String[].class);
    }

    @Override
    public void publishRawProperty(String key, Supplier<byte[]> getter, Consumer<byte[]> setter) {
        publishProperty(key, getter, setter, byte[].class);
    }

    @Override
    public void publishIntProperty(String key, Supplier<Integer> getter, Consumer<Integer> setter) {
        publishProperty(key, getter, setter, Integer.class);
    }

    @Override
    public void publishFloatProperty(String key, Supplier<Float> getter, Consumer<Float> setter) {
        publishProperty(key, getter, setter, Float.class);
    }

    @Override
    public void publishIntArrayProperty(String key, Supplier<int[]> getter, Consumer<int[]> setter) {
        publishProperty(key, getter, setter, int[].class);
    }

    @Override
    public void publishFloatArrayProperty(String key, Supplier<float[]> getter, Consumer<float[]> setter) {
        publishProperty(key, getter, setter, float[].class);
    }
}
