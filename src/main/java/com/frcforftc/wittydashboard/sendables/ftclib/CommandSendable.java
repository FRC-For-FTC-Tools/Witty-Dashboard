package com.frcforftc.wittydashboard.sendables.ftclib;

import com.arcrobotics.ftclib.command.Command;
import com.frcforftc.wittydashboard.sendables.Sendable;
import com.frcforftc.wittydashboard.sendables.SendableBuilder;

public class CommandSendable implements Sendable {
    private final Command m_command;

    public CommandSendable(Command command) {
        this.m_command = command;
    }

    public String getCommandName() {
        return m_command.getName();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        if (m_command == null) {
//            throw new NullPointerException("Command of CommandSendable is null, cannot operate"); // Optional
            return;
        }

        builder.setSmartDashboardType("Command");
        builder.addStringProperty(".name", m_command::getName, null);
        builder.addBooleanProperty("running", m_command::isScheduled, (value) -> {
            if (value) {
                if (!m_command.isScheduled()) {
                    m_command.schedule();
                }
            } else {
                if (m_command.isScheduled()) {
                    m_command.cancel();
                }
            }
        });

//        builder.addBooleanProperty(".isParented", );
        builder.addStringProperty("interruptBehavior", () -> "kCancelSelf", null); // Always the value in allwpilib
        builder.addBooleanProperty("runsWhenDisabled", m_command::runsWhenDisabled, null);
    }
}
