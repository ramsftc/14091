// TeamCode/src/main/java/org/firstinspires/ftc/teamcode/FieldCentricTeleOp.java

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Field Centric Drive", group = "Competition")
public class FieldCentric extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // --- HARDWARE MAPPING ---
        // Make sure the names in quotes match your robot's configuration file.
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRight");
        DcMotor LeftSpinner = hardwareMap.dcMotor.get("LeftSpinner");
        DcMotor RightSpinner = hardwareMap.dcMotor.get("RightSpinner");
        
        Servo BallDrop = hardwareMap.get(Servo.class, "BallDrop");

        // --- MOTOR DIRECTION ---
        // Reverse the right side motors so "forward" is the same direction for all.
        // This might be different for your robot; test and adjust.
        // For goBILDA strafer chassis, you typically reverse the left side.
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // --- IMU INITIALIZATION ---
        // The IMU is the sensor that tells us the robot's heading (rotation).
        IMU imu = hardwareMap.get(IMU.class, "imu");

        // This is the orientation of the REV Hub on your robot.
        // If the USB ports on the Hub face the LEFT side of the robot, this is correct.
        // If they face UP, use UP. If they face FORWARD, use FORWARD.
        // This is CRITICAL for the IMU to report angles correctly.
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));
        imu.initialize(parameters);
        
         BallDrop.setPosition(0.2);


        // Wait for the driver to press START on the Driver Station.
        waitForStart();

        // The opmode is running!
        while (opModeIsActive()) {

            // --- GAMEPAD INPUTS ---
            // The Y-axis is inverted on the gamepad, so we multiply by -1.
            double y = -gamepad1.left_stick_y; // "Forward" and "Backward"
            double x = gamepad1.left_stick_x;  // "Strafe" left and right
            double rx = gamepad1.right_stick_x; // "Turn" left and right

            // --- HEADING RESET ---
            // This allows the driver to reset the "forward" direction at any time.
            // This is useful if the robot gets bumped or the IMU drifts.
            if (gamepad1.a) {
                imu.resetYaw(); // The "Options" button on a PS4 controller
            }
            
            if (gamepad2.right_bumper) {
                LeftSpinner.setPower(-0.99);
                RightSpinner.setPower(0.99);
            } else {
                LeftSpinner.setPower(0);
                RightSpinner.setPower(0);
            }
            
            
            
            if (gamepad2.y) {
                BallDrop.setPosition(0.5);
                sleep(225);
                BallDrop.setPosition(0.2);
            }

            // --- FIELD-CENTRIC MATH ---
            // This is the core of field-centric drive.
            // We get the robot's current heading from the IMU.
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the joystick inputs by the negative of the robot's heading.
            // This transforms the vectors from robot-centric to field-centric.
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            // The joystick inputs can be a bit imprecise, this helps clean them up.
            rotX = rotX * 1.1;

            // --- MECANUM DRIVE CALCULATION ---
            
            // Calculate the power for each wheel based on the field-centric inputs.
            // The denominator is the largest possible power value, used for normalization.
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            // --- SET MOTOR POWER ---
            // Send the calculated power to the motors.
            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            // --- TELEMETRY ---
            // Optional: Display data on the Driver Station for debugging.
            telemetry.addData("Robot Heading (Degrees)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.addData("X Input", x);
            telemetry.addData("Y Input", y);
            telemetry.addData("Rotated X", rotX);
            telemetry.addData("Rotated Y", rotY);
            telemetry.update();
        }
    }
}