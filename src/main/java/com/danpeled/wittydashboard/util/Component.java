package com.frcforftc.wittydashboard.util;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * An abstract class representing a component of the robot.
 * Components are the building blocks of the robot control system.
 * Extend this class to create custom robot components.
 *
 * @see Runnable
 */
public abstract class Component implements Runnable {
    /**
     * The robot to which this component is attached.
     */
    public WittyOpMode opMode;

    /**
     * The hardware map providing access to robot hardware.
     */
    public HardwareMap hardwareMap;

    /**
     * The telemetry object for logging information.
     */
    public Telemetry telemetry;

    /**
     * Flag indicating whether the component is enabled.
     */
    public boolean enabled = true;
    public ComponentState componentState = ComponentState.IDLE;

    /**
     * Initializes the component.
     * Override this method to perform any necessary initialization steps.
     */
    public abstract void init();

    /**
     * Starts the component.
     * Override this method to define actions that should be taken when the component starts running.
     */
    public void start() {
    }

    /**
     * The main loop of the component.
     * Override this method to define the behavior that should be executed repeatedly.
     */
    public void update() {
    }

    /**
     * Stops the component.
     * Override this method to define actions that should be taken when the component is stopped.
     */
    public void stop() {
    }

    /**
     * The main run method that executes the component's loop in a separate thread.
     * This method continuously calls the loop() method while the op mode is active.
     */
    public final void run() {
        while (opMode.opModeIsActive() && !opMode.isStopRequested() && !opMode.opModeInInit()) {
            if (this.enabled) {
                update();
            }
        }
    }

    /**
     * Attaches the component to the robot.
     * This method sets the robot, hardware map, and telemetry objects for the component.
     *
     * @param opMode    The robot to which the component is attached.
     * @param telemetry The telemetry object for logging information.
     */
    public void attach(WittyOpMode opMode, Telemetry telemetry) {
        this.opMode = opMode;
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = telemetry;
    }

    public enum ComponentState {
        IDLE, INIT, START, LOOPING, STOPPED
    }
}