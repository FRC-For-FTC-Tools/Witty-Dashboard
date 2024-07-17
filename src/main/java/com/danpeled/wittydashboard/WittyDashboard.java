package com.danpeled.wittydashboard;

import androidx.annotation.NonNull;

import com.danpeled.wittydashboard.sendables.RobotSendable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.*;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;

/**
 * The WittyDashboard class manages the integration with NetworkTables * and handles sending data from the robot to the dashboard.
 */
public class WittyDashboard {
    private static NetworkTableInstance m_ntInstance;
    private static NetworkTable m_ntTable;
    private static final ConcurrentMap<String, SendableBuilderImpl> m_networkTables = new ConcurrentHashMap<>();
    private static final Set<String> addedValues = ConcurrentHashMap.newKeySet();
    private static RobotSendable m_robotSendable;
    private static Thread runThread;
    private static boolean isRunning = false;


    static {
        System.loadLibrary("ntcorejni");
        System.loadLibrary("ntcore");
    }

    /**
     * Starts the WittyDashboard with the given OpMode. * * @param opMode the OpMode to associate with the dashboard * @see OpMode
     */
    public static synchronized void start(OpMode opMode) {


        m_ntInstance = NetworkTableInstance.getDefault();
        m_ntInstance.startServer("localhost");
        m_ntInstance.startClient4("WittyDashboard");
        m_ntTable = m_ntInstance.getTable("Witty Datatable");
        if (opMode != null) m_robotSendable = new RobotSendable(opMode);
        isRunning = true;
        sendRobotData();
        runThread = new Thread(() -> {
            while (isRunning) {
                update();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        runThread.start();
    }

    /**
     * Adds a value to the NetworkTable and sets up a listener for remote changes. * * @param key    the key for the value * @param value  the initial value * @param setter the consumer to handle remote updates
     */
    public static <T> void put(@NonNull String key, T value, Consumer<T> setter) {
        put(key, value);
        if (!addedValues.add(key)) return;
        m_ntTable.addListener(key, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), (NetworkTable table, String key_, NetworkTableEvent event) -> {
            NetworkTableEntry entry = table.getEntry(key);
            if (value instanceof Integer) {
                ((Consumer<Integer>) setter).accept((int) entry.getInteger(0));
            } else if (value instanceof Integer[]) {
                ((Consumer<Integer[]>) setter).accept(Arrays.stream(entry.getIntegerArray(new long[0])).mapToInt((long s) -> ((Long) s).intValue()).boxed().toArray(Integer[]::new));
            } else if (value instanceof Double) {
                ((Consumer<Double>) setter).accept(entry.getDouble(0.0));
            } else if (value instanceof Double[]) {
                ((Consumer<Double[]>) setter).accept(Arrays.stream(entry.getDoubleArray(new double[0])).boxed().toArray(Double[]::new));
            } else if (value instanceof Boolean) {
                ((Consumer<Boolean>) setter).accept(entry.getBoolean(false));
            } else if (value instanceof Boolean[]) {
                boolean[] boolArray = entry.getBooleanArray(new boolean[0]);
                Boolean[] boxedBoolArray = new Boolean[boolArray.length];
                for (int i = 0; i < boolArray.length; i++) {
                    boxedBoolArray[i] = boolArray[i];
                }
                ((Consumer<Boolean[]>) setter).accept(boxedBoolArray);
            } else if (value instanceof String) {
                ((Consumer<String>) setter).accept(entry.getString(""));
            } else if (value instanceof String[]) {
                ((Consumer<String[]>) setter).accept(entry.getStringArray(new String[0]));
            } else {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass().getSimpleName());
            }
        });
    }

    /**
     * Updates the dashboard with the current state of the robot.
     */
    private static void update() {
        sendRobotData();
    }

    /**
     * Sends the robot data sendable to the network tables * * @see RobotSendable * @see NetworkTable * @see #addSendable(String, Sendable)
     */
    private static void sendRobotData() {
        if (m_robotSendable != null) WittyDashboard.addSendable("OpMode", m_robotSendable);
    }

    /**
     * Gets the NetworkTable instance. * * @return the NetworkTable instance * @see NetworkTable
     */
    @NonNull
    public static synchronized NetworkTable getNtTableInstance() {
        return m_ntTable;
    }

    /**
     * Stops the WittyDashboard.
     */
    public static synchronized void stop() {
        isRunning = false;
        runThread.interrupt();
        m_ntInstance.stopServer();
        m_ntInstance.stopClient();
    }

    /**
     * Adds a value to the NetworkTable. * If provided with a sendable will automatically call the addSendable method instead * * @param key   the key for the value * @param value the value to add * @see #addSendable(String, Sendable) * @see NetworkTable
     */
    public static <T> void put(@NonNull String key, T value) {
        synchronized (m_ntTable) {
            if (value instanceof Sendable) {
                addSendable(key, (Sendable) value);
                return;
            }
            m_ntTable.putValue(key, makeValue(value));
        }
    }

    /**
     * Retrieves a value from the NetworkTable. * * @param key the key for the value * @return the value associated with the key * @see NetworkTableType
     */
    public static synchronized Object get(@NonNull String key) {
        NetworkTableValue value = m_ntTable.getValue(key);
        if (value == null) {
            return null;
        }
        switch (value.getType()) {
            case kInteger:
                return value.getInteger();
            case kIntegerArray:
                return value.getIntegerArray();
            case kBoolean:
                return value.getBoolean();
            case kDouble:
                return value.getDouble();
            case kString:
                return value.getString();
            case kRaw:
                return value.getRaw();
            case kBooleanArray:
                return value.getBooleanArray();
            case kDoubleArray:
                return value.getDoubleArray();
            case kStringArray:
                return value.getStringArray();
            default:
                throw new IllegalArgumentException("Unsupported NetworkTableValue type: " + value.getType());
        }
    }

    /**
     * Converts a value to a NetworkTableValue. * * @param value the value to convert * @return the NetworkTableValue representing the value * @see NetworkTableValue
     */
    public static <T> NetworkTableValue makeValue(T value) {
        if (value instanceof Integer) {
            return NetworkTableValue.makeInteger((Integer) value);
        } else if (value instanceof Integer[]) {
            List<Long> list = new ArrayList<>();
            for (Integer i : (Integer[]) value) {
                list.add(i.longValue());
            }
            return NetworkTableValue.makeIntegerArray(list.stream().mapToLong(Long::longValue).toArray());
        } else if (value instanceof Long) {
            return NetworkTableValue.makeInteger((Long) value);
        } else if (value instanceof Long[]) {
            return NetworkTableValue.makeIntegerArray(Arrays.stream((Long[]) value).mapToLong(Long::longValue).toArray());
        } else if (value instanceof Double) {
            return NetworkTableValue.makeDouble((Double) value);
        } else if (value instanceof Double[]) {
            return NetworkTableValue.makeDoubleArray((Double[]) value);
        } else if (value instanceof Boolean) {
            return NetworkTableValue.makeBoolean((Boolean) value);
        } else if (value instanceof Boolean[]) {
            return NetworkTableValue.makeBooleanArray((Boolean[]) value);
        } else if (value instanceof String) {
            return NetworkTableValue.makeString((String) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getSimpleName());
        }
    }

    /**
     * Adds a Sendable to the NetworkTable. * * @param key      the key for the Sendable * @param sendable the Sendable to add * @see Sendable
     */
    public static void addSendable(@NonNull String key, Sendable sendable) {
        NetworkTable table;
        synchronized (m_ntTable) {
            table = m_ntTable.getSubTable(key);
            if (SendableRegistry.contains(sendable)) return;
            else m_networkTables.put(table.getPath(), new SendableBuilderImpl(table));
            SendableRegistry.add(sendable, key);
        }
        sendable.initSendable(m_networkTables.get(table.getPath()));
    }

    /**
     * Gets a subtable from the NetworkTable. * * @param key the key for the subtable * @return the subtable associated with the key * @see NetworkTable
     */
    public static synchronized NetworkTable getSubTable(@NonNull String key) {
        return m_ntTable.getSubTable(key);
    }
}