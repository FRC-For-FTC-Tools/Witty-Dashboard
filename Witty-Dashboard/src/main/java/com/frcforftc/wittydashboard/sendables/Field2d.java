package com.frcforftc.wittydashboard.sendables;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.geometry.Translation2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The Field2d class represents a 2D field with a robot position.
 * It implements the Sendable interface to allow for data sending.
 */
public class Field2d implements Sendable {
    List<FieldObject2d> m_objects = new ArrayList<>();

    public Field2d() {
        FieldObject2d obj = new FieldObject2d("Robot");
        obj.setPose(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        m_objects.add(obj);
    }

    public synchronized Pose2d getRobotPose() {
        return m_objects.get(0).getPose();
    }

    public synchronized void setRobotPose(Pose2d pose) {
        m_objects.get(0).setPose(pose);
    }

    public synchronized Optional<FieldObject2d> getObject(String name) {
        for (FieldObject2d obj : m_objects) {
            if (obj.m_name.equals(name)) {
                return Optional.of(obj);
            }
        }
        return Optional.empty();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Field2d");

        for (FieldObject2d obj : m_objects) {
            builder.addDoubleArrayProperty(obj.m_name, obj::getAsDoubleArray, null);
        }
    }

    public static class FieldObject2d {
        final String m_name;
        private final List<Pose2d> m_poses = new ArrayList<>();

        public FieldObject2d(String name) {
            m_name = name;
        }

        public Pose2d getPose() {
            if (m_poses.isEmpty()) {
                return new Pose2d(0, 0, Rotation2d.fromDegrees(0));
            }
            return m_poses.get(0);
        }

        public synchronized void setPose(Pose2d pose) {
            setPoses(pose);
        }

        public List<Pose2d> getPoses() {
            return new ArrayList<>(m_poses);
        }

        public synchronized void setPoses(List<Pose2d> poses) {
            m_poses.clear();
            m_poses.addAll(poses);
        }

        public synchronized void setPoses(Pose2d... poses) {
            m_poses.clear();
            Collections.addAll(m_poses, poses);
        }

        public double[] getAsDoubleArray() {
            double[] arr = new double[m_poses.size() * 3];
            int ndx = 0;
            for (Pose2d pose : m_poses) {
                Translation2d translation = pose.getTranslation();
                arr[ndx] = translation.getX();
                arr[ndx + 1] = translation.getX();
                arr[ndx + 2] = translation.getX();
                ndx += 3;
            }

            return arr;
        }
    }
}
