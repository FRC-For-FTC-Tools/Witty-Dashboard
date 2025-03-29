package org.firstinspires.ftc.teamcode;

import com.frcforftc.wittydashboard.WittyDashboard;
import com.frcforftc.wittydashboard.sendables.hardware.ServoSendable;
import com.frcforftc.wittydashboard.util.WittyOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class ExampleOpMode extends WittyOpMode {
    private Servo m_servo1;
    private ServoSendable m_servoSendable;

    @Override
    public void initRobot() {
        m_servo1 = hardwareMap.get(Servo.class, "servo1");
        m_servoSendable = new ServoSendable(m_servo1);
    }

    @Override
    public void startRobot() {
        WittyDashboard.putSendable("servo1", m_servoSendable);
        WittyDashboard.putNumber("oh no", 1);
    }
}
