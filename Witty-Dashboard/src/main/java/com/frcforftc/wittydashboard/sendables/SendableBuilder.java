package com.frcforftc.wittydashboard.sendables;

import org.frcforftc.networktables.AnnounceMethod;
import org.frcforftc.networktables.NetworkTablesValue;
import org.frcforftc.networktables.NetworkTablesValueType;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An abstract class that provides a mechanism for managing and publishing properties
 * to a network table.
 */
public abstract class SendableBuilder {
    private final HashMap<String, NetworkTablesValue> properties = new HashMap<>();
    private final Sendable sendable;
    private String m_type;


    public SendableBuilder(Sendable sendable) {
        this.sendable = sendable;
        sendable.initSendable(this);
    }


    /**
     * Sets the type of the SmartDashboard that this builder will be used for.
     *
     * @param type the type of the SmartDashboard
     */
    public void setSmartDashboardType(String type) {
        this.m_type = type;
    }

    /**
     * Adds a double property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addDoubleProperty(String key, Supplier<Double> getter, Consumer<Double> setter) {
        addProperty(key, NetworkTablesValueType.Double, getter, setter);
    }

    /**
     * Adds a double array property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addDoubleArrayProperty(String key, Supplier<double[]> getter, Consumer<double[]> setter) {
        addProperty(key, NetworkTablesValueType.DoubleArray, getter, setter);
    }

    /**
     * Adds a boolean property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addBooleanProperty(String key, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        addProperty(key, NetworkTablesValueType.Boolean, getter, setter);
    }

    /**
     * Adds a boolean array property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addBooleanArrayProperty(String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
        addProperty(key, NetworkTablesValueType.BooleanArray, getter, setter);
    }

    /**
     * Adds a string property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {
        addProperty(key, NetworkTablesValueType.String, getter, setter);
    }

    /**
     * Adds a string array property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter) {
        addProperty(key, NetworkTablesValueType.StringArray, getter, setter);
    }

    /**
     * Adds a raw property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addRawProperty(String key, Supplier<byte[]> getter, Consumer<byte[]> setter) {
        addProperty(key, NetworkTablesValueType.Raw, getter, setter);
    }

    /**
     * Adds an integer property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addIntProperty(String key, Supplier<Integer> getter, Consumer<Integer> setter) {
        addProperty(key, NetworkTablesValueType.Int, getter, setter);
    }

    /**
     * Adds a float property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addFloatProperty(String key, Supplier<Float> getter, Consumer<Float> setter) {
        addProperty(key, NetworkTablesValueType.Float, getter, setter);
    }

    /**
     * Adds an integer array property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addIntArrayProperty(String key, Supplier<int[]> getter, Consumer<int[]> setter) {
        addProperty(key, NetworkTablesValueType.IntArray, getter, setter);
    }

    /**
     * Adds a float array property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public void addFloatArrayProperty(String key, Supplier<float[]> getter, Consumer<float[]> setter) {
        addProperty(key, NetworkTablesValueType.FloatArray, getter, setter);
    }

    /**
     * Adds a property to be managed by this builder.
     *
     * @param key    the key for the property
     * @param type   the type of the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     * @param <T>    the type of the property value
     */
    private <T> void addProperty(String key, NetworkTablesValueType type, Supplier<T> getter, Consumer<T> setter) {
        NetworkTablesValue value = new NetworkTablesValue(getter, setter, type);

        NetworkTablesValue containedValue = properties.get(key);
        if (containedValue != null && !Objects.equals(containedValue.getType(), type.typeString)) {
            throw new RuntimeException(String.format("Non matching types for topic %s (%s and %s)", key, type.typeString, containedValue.getType()));
        } else {
            containedValue = value;
        }

        properties.put(key, containedValue);
    }

    /**
     * Publishes a double property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishDoubleProperty(String key, Supplier<Double> getter, Consumer<Double> setter);

    /**
     * Publishes a double array property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishDoubleArrayProperty(String key, Supplier<double[]> getter, Consumer<double[]> setter);

    /**
     * Publishes a boolean property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishBooleanProperty(String key, Supplier<Boolean> getter, Consumer<Boolean> setter);

    /**
     * Publishes a boolean array property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishBooleanArrayProperty(String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter);

    /**
     * Publishes a string property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishStringProperty(String key, Supplier<String> getter, Consumer<String> setter);

    /**
     * Publishes a string array property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter);

    /**
     * Publishes a raw property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishRawProperty(String key, Supplier<byte[]> getter, Consumer<byte[]> setter);

    /**
     * Publishes an integer property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishIntProperty(String key, Supplier<Integer> getter, Consumer<Integer> setter);

    /**
     * Publishes a float property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishFloatProperty(String key, Supplier<Float> getter, Consumer<Float> setter);

    /**
     * Publishes an integer array property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishIntArrayProperty(String key, Supplier<int[]> getter, Consumer<int[]> setter);

    /**
     * Publishes a float array property.
     *
     * @param key    the key for the property
     * @param getter a supplier that provides the current value of the property
     * @param setter a consumer that sets the value of the property
     */
    public abstract void publishFloatArrayProperty(String key, Supplier<float[]> getter, Consumer<float[]> setter);

    /**
     * Posts all properties to the network table using the specified announce method.
     *
     * @param key            the base key for the properties
     * @param announceMethod the method used to announce the properties
     */
    public void post(String key, AnnounceMethod announceMethod) {
        System.out.println(properties);
        for (String topic : properties.keySet()) {
            NetworkTablesValue value = properties.get(topic);

            NetworkTablesValueType type = NetworkTablesValueType.getFromString(value.getType());

            announceMethod.apply(key + "/" + topic, value.get());

            if (type != NetworkTablesValueType.Unknown) {
                switch (type) {
                    case Int ->
                            publishIntProperty(key, (Supplier<Integer>) value.getGetter(), (Consumer<Integer>) value.getSetter());
                    case Double ->
                            publishDoubleProperty(key, (Supplier<Double>) value.getGetter(), (Consumer<Double>) value.getSetter());
                    case String ->
                            publishStringProperty(key, (Supplier<String>) value.getGetter(), (Consumer<String>) value.getSetter());
                    case Float ->
                            publishFloatProperty(key, (Supplier<Float>) value.getGetter(), (Consumer<Float>) value.getSetter());
                    case Boolean ->
                            publishBooleanProperty(key, (Supplier<Boolean>) value.getGetter(), (Consumer<Boolean>) value.getSetter());
                    case Raw ->
                            publishRawProperty(key, (Supplier<byte[]>) value.getGetter(), (Consumer<byte[]>) value.getSetter());
                    case BooleanArray ->
                            publishBooleanArrayProperty(key, (Supplier<boolean[]>) value.getGetter(), (Consumer<boolean[]>) value.getSetter());
                    case DoubleArray ->
                            publishDoubleArrayProperty(key, (Supplier<double[]>) value.getGetter(), (Consumer<double[]>) value.getSetter());
                    case IntArray ->
                            publishIntArrayProperty(key, (Supplier<int[]>) value.getGetter(), (Consumer<int[]>) value.getSetter());
                    case FloatArray ->
                            publishFloatArrayProperty(key, (Supplier<float[]>) value.getGetter(), (Consumer<float[]>) value.getSetter());
                    case StringArray ->
                            publishStringArrayProperty(key, (Supplier<String[]>) value.getGetter(), (Consumer<String[]>) value.getSetter());
                    default -> throw new IllegalArgumentException("Unsupported type: " + type);
                }
            } else {

            }
        }

        announceMethod.apply(key + "/" + ".type", m_type);
    }
}