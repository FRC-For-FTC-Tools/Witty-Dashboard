package com.danpeled.wittydashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.util.function.FloatConsumer;
import edu.wpi.first.util.function.FloatSupplier;
import edu.wpi.first.util.sendable.SendableBuilder;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;

class SendableBuilderImpl implements SendableBuilder {
    private final NetworkTable table;
    private final Set<String> keysWithListeners = Collections.synchronizedSet(new HashSet<>());

    public SendableBuilderImpl(NetworkTable table) {
        this.table = table;
    }

    @Override
    public void close() {
    }

    @Override
    public void setSmartDashboardType(String type) {
        table.getEntry(".type").setString(type);
    }

    @Override
    public void setActuator(boolean b) {
        table.getEntry(".actuator").setBoolean(b);
    }

    @Override
    public void setSafeState(Runnable func) {
        table.getEntry(".safeState").setBoolean(true);
        func.run();
    }

    @Override
    public void addBooleanProperty(String key, BooleanSupplier getter, BooleanConsumer setter) {
        table.getEntry(key).setBoolean(getter.getAsBoolean());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getBoolean(false)));
    }


    @Override
    public void addIntegerProperty(String key, LongSupplier getter, LongConsumer setter) {
        table.getEntry(key).setDouble(getter.getAsLong());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getInteger(0)));
    }


    @Override
    public void addFloatProperty(String key, FloatSupplier getter, FloatConsumer setter) {
        table.getEntry(key).setDouble(getter.getAsFloat());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept((float) table.getEntry(key_).getDouble(0)));
    }


    @Override
    public void addDoubleProperty(String key, DoubleSupplier getter, DoubleConsumer setter) {
        table.getEntry(key).setDouble(getter.getAsDouble());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getDouble(0)));
    }


    @Override
    public void addStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {
        table.getEntry(key).setString(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getString("")));
    }


    @Override
    public void addBooleanArrayProperty(String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
        table.getEntry(key).setBooleanArray(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getBooleanArray(new boolean[0])));
    }

    @Override
    public void addIntegerArrayProperty(String key, Supplier<long[]> getter, Consumer<long[]> setter) {
        table.getEntry(key).setIntegerArray(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getIntegerArray(new long[]{})));
    }


    @Override
    public void addFloatArrayProperty(String key, Supplier<float[]> getter, Consumer<float[]> setter) {
        table.getEntry(key).setFloatArray(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getFloatArray(new float[]{})));
    }


    @Override
    public void addDoubleArrayProperty(String key, Supplier<double[]> getter, Consumer<double[]> setter) {
        table.getEntry(key).setDoubleArray(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getDoubleArray(new double[0])));
    }


    @Override
    public void addStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter) {
        table.getEntry(key).setStringArray(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getStringArray(new String[0])));
    }


    @Override
    public void addRawProperty(String key, String keyType, Supplier<byte[]> getter, Consumer<byte[]> setter) {
        table.getEntry(key).setRaw(getter.get());

        if (keysWithListeners.contains(key)) return;
        else keysWithListeners.add(key);

        table.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> setter.accept(table.getEntry(key_).getRaw(new byte[0])));
    }


    @Override
    public BackendKind getBackendKind() {
        return BackendKind.kNetworkTables;
    }

    @Override
    public boolean isPublished() {
        return table.getEntry(".type").exists();
    }

    @Override
    public void update() {
    }

    @Override
    public void clearProperties() {
        //TODO?
    }

    @Override
    public void addCloseable(AutoCloseable autoCloseable) {
        try {
            autoCloseable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
