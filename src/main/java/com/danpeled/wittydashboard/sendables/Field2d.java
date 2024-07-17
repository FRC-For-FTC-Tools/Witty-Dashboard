package com.danpeled.wittydashboard.sendables;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

/**
 * The Field2d class represents a 2D field with a robot position.
 * It implements the Sendable interface to allow for data sending.
 */
public class Field2d implements Sendable {
    private Pose2d m_robotPosition = new Pose2d(0, 0, Rotation2d.fromDegrees(0));

    /**
     * Initializes the SendableBuilder for the SmartDashboard.
     *
     * @param builder the SendableBuilder to initialize
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Field2d");
        builder.addDoubleArrayProperty("Robot", this::getRobotPoseAsArray, this::setRobotPose);
    }

    /**
     * Gets the robot's pose as an array.
     *
     * @return a double array representing the robot's pose (x, y, rotation in degrees)
     */
    public double[] getRobotPoseAsArray() {
        return new double[]{m_robotPosition.getX(), m_robotPosition.getY(), m_robotPosition.getRotation().getDegrees()};
    }

    /**
     * Gets the robot's pose.
     *
     * @return the current pose of the robot
     */
    public Pose2d getRobotPose() {
        return m_robotPosition;
    }

    /**
     * Sets the robot's pose using a double array.
     *
     * @param newPoseArray a double array with a length of 3 (x, y, rotation in degrees)
     * @throws RuntimeException if the length of the pose array is not 3
     */
    public void setRobotPose(double[] newPoseArray) {
        if (newPoseArray.length == 3) {
            m_robotPosition = new Pose2d(newPoseArray[0], newPoseArray[1], Rotation2d.fromDegrees(newPoseArray[2]));
        } else {
            throw new RuntimeException(
                    String.format(
                            "Expected pose double array with the length of 3 (x, y, rotation) but provided with length of %d",
                            newPoseArray.length));
        }
    }

    /**
     * Sets the robot's pose using a Pose2d object.
     *
     * @param newPose the new pose of the robot
     */
    public void setRobotPose(Pose2d newPose) {
        this.m_robotPosition = newPose;
    }
}
