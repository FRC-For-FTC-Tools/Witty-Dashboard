package com.frcforftc.wittydashboard.util;

import com.frcforftc.wittydashboard.WittyDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The abstract base class for creating robot programs.
 * Extend this class to create a custom robot program.
 *
 * @see LinearOpMode
 */
public abstract class WittyOpMode extends LinearOpMode {
    /**
     * Thread-safe map for storing components and their threads
     **/
    private final Map<Component, Thread> m_components = new ConcurrentHashMap<>();

    /**
     * The alliance of the robot (e.g., BLUE or RED).
     * This field indicates the alliance color of the robot.
     */
    public Alliance alliance = Alliance.BLUE;

    /**
     * The type of the robot program (e.g., Autonomous or TeleOp).
     * This field denotes the type of the robot program being executed.
     */
    public TYPE type;

    /**
     * The elapsed time since the initialization of the robot.
     */
    private ElapsedTime m_elapsedTime;

    /**
     * The telemetry object used for communication with the driver station.
     */
    private Telemetry m_robotTelemetry;

    /**
     * Runs the main loop of the robot program.
     * Override this method to define the robot's behavior.
     *
     * @throws InterruptedException If the program is interrupted
     */
    @Override
    public final void runOpMode() throws InterruptedException {
        initialize();

        waitForStart();

        startComponents();
        startRobot();
        startComponentThreads();

        while (opModeIsActive() && !isStopRequested()) {
            updateLoop();
            updateComponents();
            sleep(1);
        }

        WittyDashboard.stop();
        stopComponents();
    }

    /**
     * Initializes the robot and sets up essential components.
     */
    private void initialize() {
        WittyDashboard.start(this);

        m_elapsedTime = new ElapsedTime();
        m_elapsedTime.reset();

        retrieveTelemetry();
        initRobot();
    }

    /**
     * Initializes the robot.
     * Override this method to perform any necessary initialization steps for the robot.
     */
    public abstract void initRobot();

    /**
     * Starts the robot.
     * Override this method to define actions that should be taken when the robot starts running.
     */
    public abstract void startRobot();

    /**
     * The main update loop of the robot.
     * Override this method to define the behavior that should be executed repeatedly during the program.
     */
    public void updateLoop() {
    }

    /**
     * Stops the robot.
     * Override this method to define actions that should be taken when the robot is stopped.
     */
    public void onStop() {
    }

    /**
     * Adds a component to the robot.
     *
     * @param <T>               The type of the component
     * @param componentClass    The class of the component
     * @param componentInstance The instance of the component to add
     * @return The added component or null if an error occurred
     */
    public <T extends Component> T addComponent(Class<T> componentClass, T componentInstance) {
        if (componentInstance == null) {
            throw new IllegalArgumentException("Component instance cannot be null");
        }

        try {
            initializeComponent(componentInstance);
            return getComponent(componentClass);
        } catch (Exception e) {
            throw new RuntimeException("Error adding component: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a component to the robot.
     *
     * @param <T>            The type of the component
     * @param componentClass The class of the component
     * @return The added component or null if an error occurred
     */
    public <T extends Component> T addComponent(Class<T> componentClass) {
        T componentInstance = createComponentInstance(componentClass);
        return addComponent(componentClass, componentInstance);
    }

    /**
     * Gets a component of the specified type.
     *
     * @param <T>            The type of the component
     * @param componentClass The class of the component
     * @return The component of the specified type or null if not found
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        synchronized (m_components) {
            for (Component component : m_components.keySet()) {
                if (component != null && componentClass.isInstance(component)) {
                    return componentClass.cast(component);
                }
            }
        }
        return null;
    }

    /**
     * Starts all the attached components.
     */
    private void startComponents() {
        synchronized (m_components) {
            m_components.keySet().forEach(c -> {
                c.componentState = Component.ComponentState.START;
                c.start();
                c.componentState = Component.ComponentState.IDLE;
            });
        }
    }

    /**
     * Starts threads for components that require them.
     */
    private void startComponentThreads() {
        synchronized (m_components) {
            m_components.values().forEach(t -> {
                if (t != null) t.start();
            });
        }
    }

    /**
     * Stops all the attached components and interrupts their threads.
     */
    private void stopComponents() {
        synchronized (m_components) {
            m_components.forEach((c, t) -> {
                c.stop();
                if (t != null) t.interrupt();
            });
        }
    }

    /**
     * Initializes a component and attaches it to the robot.
     */
    private void initializeComponent(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }

        component.componentState = Component.ComponentState.INIT;
        component.attach(this, m_robotTelemetry);
        Thread thread = createComponentThread(component);
        synchronized (m_components) {
            m_components.put(component, thread);
            component.init();
            component.componentState = Component.ComponentState.IDLE;

            if (opModeIsActive() || isStarted()) {
                component.start();
            }
        }
    }

    /**
     * Creates a thread for the given component if it requires threading.
     */
    private Thread createComponentThread(Component component) {
        return component.getClass().isAnnotationPresent(ThreadedComponent.class) ? new Thread(component) : null;
    }

    /**
     * Creates an instance of the given component class.
     */
    private <T extends Component> T createComponentInstance(Class<T> componentClass) {
        try {
            return componentClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error creating component instance", e);
        }
    }

    /**
     * Retrieves telemetry based on the type of the robot program.
     */
    void retrieveTelemetry() {
        if (this.getClass().isAnnotationPresent(Autonomous.class)) {
            this.type = TYPE.Auto;
        } else {
            this.type = TYPE.TeleOp;
        }
        m_robotTelemetry = telemetry;
    }

    /**
     * Updates all the attached components.
     */
    private void updateComponents() {
        m_components.forEach((c, t) -> {
            if (t == null && c.enabled) {
                c.update();
                c.componentState = Component.ComponentState.LOOPING;
            }
        });
    }

    /**
     * Represents the type of the robot program.
     * It can be either Autonomous or TeleOp.
     */
    public enum TYPE {
        /**
         * Represents an Autonomous robot program.
         */
        Auto,
        /**
         * Represents a TeleOp robot program.
         */
        TeleOp
    }
}
