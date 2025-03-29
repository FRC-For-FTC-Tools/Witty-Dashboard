package com.frcforftc.wittydashboard.util;

import com.frcforftc.wittydashboard.WittyDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public abstract class WittyOpMode extends LinearOpMode {
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

        startRobot();

        while (opModeIsActive() && !isStopRequested()) {
            updateLoop();
        }

        WittyDashboard.stop();
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
