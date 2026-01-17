package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range; // Import Range for safety
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.hardware.dfrobot.HuskyLens;

@TeleOp(name = "Auto Aim Red", group = "Competition")
public class AutoAimRed extends LinearOpMode {

    private HuskyLens huskyLens;

    @Override
    public void runOpMode() throws InterruptedException {
        // --- HARDWARE MAPPING ---
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRight");
        DcMotor LeftSpinner = hardwareMap.dcMotor.get("LeftSpinner");
        DcMotor RightSpinner = hardwareMap.dcMotor.get("RightSpinner");
        Servo BallDrop = hardwareMap.get(Servo.class, "BallDrop");

        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");

        // --- HUSKYLENS SETUP ---
        // Vital: This only sets the algorithm in code. 
        // You MUST ensure the HuskyLens physical menu is also set correctly!
        if (!huskyLens.knock()) {
            telemetry.addData(">>", "Problem communicating with HuskyLens!");
            telemetry.addData(">>", "Check your connections (I2C vs UART)");
        } else {
            telemetry.addData(">>", "HuskyLens Connection Confirmed");
            huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
        }
        
        // --- MOTOR DIRECTION ---
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // --- IMU INITIALIZATION ---
        IMU imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));
        imu.initialize(parameters);
        
        BallDrop.setPosition(.2);
        ElapsedTime ballDropTimer = new ElapsedTime();
        boolean isDropping = false;

        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // --- GAMEPAD INPUTS ---
            double y = -gamepad1.left_stick_y; 
            double x = gamepad1.left_stick_x;  
            double rx = gamepad1.right_stick_x; 

            // --- AUTO AIM LOGIC ---
            if (gamepad1.left_bumper) {
                
                HuskyLens.Block[] blocks = huskyLens.blocks();
                
                // DEBUG: Tell us exactly what the camera sees
                telemetry.addData("Block Count", blocks.length);

                HuskyLens.Block targetTag = null;
                for (HuskyLens.Block block : blocks) {
                    // Look for ID 2 (Red Alliance Center usually)
                    if (block.id == 1) {
                        targetTag = block;
                        break;
                    }
                }

                if (targetTag != null) {
                    // Calculate error (Center is 160)
                    double error = targetTag.x - 200;
                    
                    // Increased P-Gain slightly. 
                    // If robot oscillates (shakes), lower this back to 0.005
                    double kP = 0.005; 
                    
                    // Calculate turn power
                    double turnPower = error * kP;
                    
                    // SAFETY: Clip power so it doesn't spin insanely fast
                    // Also useful if kP is high
                    rx = Range.clip(turnPower, -0.2, 0.2);

                    telemetry.addData("Target", "FOUND Tag:ID1");
                    telemetry.addData("X Position", targetTag.x);
                    telemetry.addData("Calculated rx", rx);
                } else {
                    telemetry.addData("Target", "Searching... (No tag:ID 1 seen)");
                    // OPTIONAL: Make rx = 0 here if you want to DISABLE manual turning 
                    // while searching. Currently, it allows the driver to turn manually 
                    // if the tag isn't seen.
                }
            }

            // --- HEADING RESET ---
            if (gamepad1.a) {
                imu.resetYaw();
            }

            // --- SPINNERS ---
            if (gamepad2.right_bumper) {
                LeftSpinner.setPower(-1);
                RightSpinner.setPower(1);
            } else {
                LeftSpinner.setPower(0);
                RightSpinner.setPower(0);
            }
            
            // --- BALL DROP ---
            if (gamepad2.y && !isDropping) {
                isDropping = true;
                ballDropTimer.reset();
                BallDrop.setPosition(0);
                
            }
            
            if (ballDropTimer.seconds() > 0.2) {
                BallDrop.setPosition(0.2);
            }

            // Phase 2: After 2.2 seconds (0.2 + 2.0), finish the drop
            if (ballDropTimer.seconds() > 2.0) {
                isDropping = false;
            }

            // --- FIELD CENTRIC DRIVE ---
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1; 

            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            telemetry.update();
        }
    }
}
