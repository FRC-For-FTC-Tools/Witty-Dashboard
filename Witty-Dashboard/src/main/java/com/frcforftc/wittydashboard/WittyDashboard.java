package com.frcforftc.wittydashboard;

import androidx.annotation.NonNull;

import com.arcrobotics.ftclib.command.Command;
import com.frcforftc.wittydashboard.sendables.Sendable;
import com.frcforftc.wittydashboard.sendables.ftclib.CommandSendable;
import com.frcforftc.wittydashboard.sendables.opModeControl.OpModeSendable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.frcforftc.networktables.NetworkTablesEntry;
import org.frcforftc.networktables.NetworkTablesEvent;
import org.frcforftc.networktables.NetworkTablesEventListener;
import org.frcforftc.networktables.NetworkTablesInstance;
import org.frcforftc.networktables.NetworkTablesValue;
import org.frcforftc.networktables.NetworkTablesValueType;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * The WittyDashboard class manages the integration with NetworkTables
 * and handles sending data from the robot to the dashboard.
 */
public class WittyDashboard {
    //    private static NetworkTable m_ntTable;
    private static final ConcurrentMap<String, SendableBuilderImpl> m_sendableBuilders = new ConcurrentHashMap<>();
    private static final Set<String> m_addedValues = ConcurrentHashMap.newKeySet();
    private static NetworkTablesInstance m_ntInstance;
    private static OpModeSendable m_opModeSendable;
    private static Thread m_runThread;
    private static boolean m_isRunning = false;

    /**
     * Starts the WittyDashboard with the given OpMode.
     *
     * @param opMode the OpMode to associate with the dashboard
     * @see OpMode
     */
    public static synchronized void start(OpMode opMode) {
        m_ntInstance = NetworkTablesInstance.getDefaultInstance();
        setOpMode(opMode);
        m_isRunning = true;
        m_ntInstance.startNT4Server("192.168.49.1", 5810);

        m_runThread = new Thread(() -> {
            while (m_isRunning) {
                update();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        m_runThread.start();
        if (m_ntInstance.getServer() == null) {
            throw new RuntimeException("AHHHHHHH");
        } else {
            RobotLog.vv("NetworkTables", "Server started...");
        }
    }

    public static synchronized void setOpMode(OpMode opMode) {
        if (opMode != null) m_opModeSendable = new OpModeSendable(opMode);
    }

    public static boolean isRunning() {
        return m_isRunning;
    }

    /**
     * Updates the dashboard with the current state of the robot.
     */
    private static void update() {
        sendRobotData();
    }

    /**
     * Sends the robot data sendable to the network tables
     *
     * @see NetworkTablesInstance
     * @see #putSendable(String, Sendable)
     */
    private static void sendRobotData() {
        if (m_opModeSendable != null) WittyDashboard.putSendable("OpMode", m_opModeSendable);
    }

    /**
     * Stops the WittyDashboard.
     */
    public static synchronized void stop() {
        m_isRunning = false;
        m_runThread.interrupt();
        m_ntInstance.closeServer();
    }

    /**
     * Adds a value to the NetworkTable.
     * If provided with a sendable will automatically call the addSendable method instead
     *
     * @param key   the key for the value
     * @param value the value to add * @see #addSendable(String, Sendable)
     * @see NetworkTablesEntry
     */
    public synchronized static <T> void put(@NonNull String key, T value) {
        if (value instanceof Sendable) {
            putSendable(key, (Sendable) value);
            return;
        }

        switch (NetworkTablesValueType.determineType(value)) {
            case Boolean -> m_ntInstance.putBoolean(key, (boolean) value);
            case Double -> m_ntInstance.putNumber(key, (double) value);
            case Float -> m_ntInstance.putNumber(key, (float) value);
            case Int -> m_ntInstance.putNumber(key, (int) value);
            case String -> m_ntInstance.putString(key, (String) value);
            case BooleanArray -> m_ntInstance.putBooleanArray(key, (boolean[]) value);
            case DoubleArray -> m_ntInstance.putNumberArray(key, (double[]) value);
            case FloatArray -> m_ntInstance.putNumberArray(key, (float[]) value);
            case IntArray -> m_ntInstance.putNumberArray(key, (int[]) value);
            case StringArray -> m_ntInstance.putStringArray(key, (String[]) value);
        }
    }

    public synchronized static <T> void put(@NonNull String key, T value, Consumer<T> setter, Class<T> type) {
        put(key, value);

        if (!m_sendableBuilders.containsKey(key)) {
            m_sendableBuilders.put(key, null);

            NetworkTablesInstance.getDefaultInstance().get(key)
                    .addListener(new NetworkTablesEventListener(EnumSet.of(NetworkTablesEvent.kTopicUpdated), (NetworkTablesEvent e) -> {
                        NetworkTablesValue networkTablesValue = NetworkTablesInstance.getDefaultInstance().get(key).getValue();
                        if (type.isInstance(networkTablesValue.get())) {
                            setter.accept(type.cast(networkTablesValue.get()));
                        }
                    }));
        }
    }

    /**
     * Retrieves a value from the NetworkTable.
     *
     * @param key the key for the value
     * @return the value associated with the key * @see NetworkTableType
     */
    public static synchronized Object get(@NonNull String key) {
        NetworkTablesEntry value = m_ntInstance.get(key);
        if (value == null) {
            return null;
        }

        return value.getValue().get();
    }

    /**
     * Adds a Sendable to the NetworkTable.
     *
     * @param key      the key for the Sendable
     * @param sendable the Sendable to add * @see Sendable
     */
    public static void putSendable(@NonNull String key, Sendable sendable) {
        SendableBuilderImpl impl;
        if (m_sendableBuilders.containsKey(key)) {
            impl = m_sendableBuilders.get(key);
        } else {
            impl = new SendableBuilderImpl(sendable);
            m_sendableBuilders.put(key, impl);
        }

        if (impl != null)
            impl.post(key, WittyDashboard::put);
    }

    public void putCommand(String key, Command command) {
        putSendable(key, new CommandSendable(command));
    }
}
