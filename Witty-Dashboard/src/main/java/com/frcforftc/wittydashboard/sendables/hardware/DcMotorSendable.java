package com.frcforftc.wittydashboard.sendables.hardware;

import androidx.annotation.NonNull;

import com.frcforftc.wittydashboard.sendables.Sendable;
import com.frcforftc.wittydashboard.sendables.SendableBuilder;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class DcMotorSendable implements Sendable {
    private final DcMotorEx m_motor;

    public DcMotorSendable(@NonNull DcMotorEx motor) {
        this.m_motor = motor;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Motor Controller");
        builder.addDoubleProperty("Value", this.m_motor::getPower, this.m_motor::setPower);
        builder.addIntProperty("Current Position", this.m_motor::getCurrentPosition, null);
        builder.addIntProperty("Target Position", this.m_motor::getTargetPosition, this.m_motor::setTargetPosition);
        builder.addBooleanProperty("Direction", this::getDirection, this::setDirection); // Forward = true
        builder.addDoubleProperty("Current", () -> this.m_motor.getCurrent(CurrentUnit.AMPS), null);
        builder.addBooleanProperty("Is Over Current", this.m_motor::isOverCurrent, null);
        builder.addStringProperty("Device name", this.m_motor::getDeviceName, null);
        builder.addStringProperty("Zero Power Behaviour", () -> m_motor.getZeroPowerBehavior().name(), null);
    }

    public boolean getDirection() {
        return this.m_motor.getDirection() == DcMotorSimple.Direction.FORWARD;
    }

    public void setDirection(boolean direction) {
        this.m_motor.setDirection(direction ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE);
    }
}
