package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "EsiAutonomousblue (Blocks to Java)")
public class EsiAutonomousblue extends LinearOpMode {

  private DcMotor frontLeft;
  private DcMotor backLeft;
  private DcMotor frontRight;
  private DcMotor backRight;
  private Servo BallDrop;
  private DcMotor LeftSpinner;
  private DcMotor RightSpinner;

  /**
   * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
   * Comment Blocks show where to place Initialization code (runs once, after touching the
   * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
   * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
   * Stopped).
   */
  @Override
  public void runOpMode() {
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    BallDrop = hardwareMap.get(Servo.class, "BallDrop");
    LeftSpinner = hardwareMap.get(DcMotor.class, "LeftSpinner");
    RightSpinner = hardwareMap.get(DcMotor.class, "RightSpinner");

    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        frontLeft.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(-0.5);
        frontRight.setPower(-0.5);
        sleep(675);
        frontLeft.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        frontRight.setPower(0);
        LeftSpinner.setPower(-0.94);
        RightSpinner.setPower(0.94);
        sleep(200);
        BallDrop.setPosition(0.2);
        sleep(1750);
        BallDrop.setPosition(0);
        sleep(150);
        BallDrop.setPosition(0.2);
        sleep(1750);
        BallDrop.setPosition(0);
        sleep(150);
        BallDrop.setPosition(0.2);
        sleep(1750);
        BallDrop.setPosition(0);
        sleep(150);
        BallDrop.setPosition(0.2);
        sleep(1750);
        BallDrop.setPosition(0);
        sleep(2000);
        RightSpinner.setPower(0);
        LeftSpinner.setPower(0);
        BallDrop.setPosition(0.2);
        sleep(1000);
        frontLeft.setPower(-0.4);
        frontRight.setPower(-0.4);
        backLeft.setPower(-0.4);
        backRight.setPower(-0.4);
        sleep(250);
        backLeft.setPower(0);
        backRight.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        sleep(750);
        backLeft.setPower(-0.4);
        frontLeft.setPower(-0.4);
        frontRight.setPower(0.4);
        backRight.setPower(0.4);
        sleep(750);
        backLeft.setPower(0);
        backRight.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        telemetry.update();
        break;
      }
    }
  }
}
