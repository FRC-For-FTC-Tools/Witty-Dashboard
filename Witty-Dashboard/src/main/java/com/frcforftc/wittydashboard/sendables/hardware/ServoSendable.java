package com.frcforftc.wittydashboard.sendables.hardware;

import androidx.annotation.NonNull;

import com.frcforftc.wittydashboard.sendables.Sendable;
import com.frcforftc.wittydashboard.sendables.SendableBuilder;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoSendable implements Sendable {
    private final Servo m_servo;

    public ServoSendable(@NonNull Servo servo) {
        this.m_servo = servo;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Servo");
        builder.addDoubleProperty("Value", this.m_servo::getPosition, this.m_servo::setPosition);
        builder.addBooleanProperty("Direction", this::getDirection, this::setDirection);
        builder.addStringProperty("Device name", this.m_servo::getDeviceName, null);
    }

    public boolean getDirection() {
        return this.m_servo.getDirection() == Servo.Direction.FORWARD;
    }

    public void setDirection(boolean direction) {
        this.m_servo.setDirection(direction ? Servo.Direction.FORWARD : Servo.Direction.REVERSE);
    }
}
