package org.usfirst.frc.team3647.robot;

import edu.wpi.first.wpilibj.Timer;
import team3647ConstantsAndFunctions.Constants;
import team3647ConstantsAndFunctions.Functions;
import team3647elevator.Elevator;
import team3647elevator.ElevatorLevel;
import team3647elevator.IntakeWheels;
import team3647pistons.Forks;
import team3647pistons.Shifter;
import team3647pistons.Intake;
import team3647subsystems.Drivetrain;
import team3647subsystems.Encoders;

public class Autonomous 
{
	//Timer-Stuff
	public static Timer stopWatch = new Timer();
	static double time;
	
	//Other variables for auto
	static double prevLeftEncoder, prevRightEncoder;
	static int currentState;
	static double lSSpeed, rSSpeed, speed;
	
	public static void initialize()
	{
		stopWatch.reset();
		Drivetrain.stop();
		Forks.lockTheForks();
		Shifter.lowGear();
		Intake.closeIntake();
		Encoders.resetEncoders();
		IntakeWheels.runIntake(0, 0, true, 0, 0);
		Elevator.stopEleVader();
		Elevator.elevatorState = 0;
		Drivetrain.setToBrake();
		prevLeftEncoder = 0;
		prevRightEncoder = 0;
		currentState = 0;
		time = 0;
	}
	
	public static void testTurnLeft(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 3:
				if(!Drivetrain.reachedDistance(lValue, rValue, 2000))
				{
					Drivetrain.driveForw(lValue, rValue, .5);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 6000))
				{
					Drivetrain.driveForw(lValue, rValue, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 7500))
				{
					Drivetrain.driveForw(lValue, rValue, .5);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					Encoders.testEncoders();
					currentState = 4;
				}
				break;
			case 4:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				double dist = 5300;
				double ratio = 2.3;
				if(Functions.testCurve(rValue, dist) != 0)
				{
					rSSpeed = Functions.testCurve(rValue, dist);
					lSSpeed = rSSpeed/ratio;
					Drivetrain.goStraightLeft(lValue, rValue, ratio, lSSpeed, rSSpeed, .06);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 6;
				}
				break;
			case 5:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 6000))
				{
					Drivetrain.driveForw(lValue, rValue, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 7500))
				{
					Drivetrain.driveForw(lValue, rValue, .3);
				}
				else
				{
					currentState = 6;
				}
				break;
			case 6:
				Drivetrain.stop();
				break;
		}
	}
	
	public static void testScale(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 3:
				if(!Drivetrain.reachedDistance(lValue, rValue, 2000))
				{
					Drivetrain.driveForw(lValue, rValue, .5);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 6000))
				{
					Drivetrain.driveForw(lValue, rValue, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 7500))
				{
					Drivetrain.driveForw(lValue, rValue, .5);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				stopWatch.reset();
				rValue -= prevRightEncoder;
				lSSpeed = Drivetrain.keepMotorInPlace(prevLeftEncoder, lValue);
				rSSpeed = .6;
				double dist = 2000;
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				Drivetrain.stop();
				stopWatch.start();
				currentState = 6;
				break;
			case 6:
				if(stopWatch.get() > .6)
				{
					stopWatch.stop();
					currentState = 7;
					prevRightEncoder = rValue;
				}
				break;
			case 7:
				stopWatch.reset();
				rValue -= prevRightEncoder;
				lSSpeed = Drivetrain.keepMotorInPlace(prevLeftEncoder, lValue);
				rSSpeed = -.6;
				dist = 2000;
				rValue = Math.abs(rValue);
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 8;
				}
				break;
			case 8:
				Drivetrain.stop();
				break;
		}
	}
	
	public static void testBackRight(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 3:
				if(!Drivetrain.reachedDistance(lValue, rValue, 2000))
				{
					Drivetrain.driveBack(lValue, rValue, -.5);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 3000))
				{
					Drivetrain.driveBack(lValue, rValue, -.4);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				double dist = 5200;
				double ratio = 2.3;
				if(Functions.testCurve(lValue, dist) != 0)
				{
					lSSpeed = Functions.testCurve(lValue, dist);
					rSSpeed = lSSpeed/ratio;
					Drivetrain.goBackRight(lValue, rValue, ratio, -lSSpeed, -rSSpeed, .06);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 6;
				}
				break;
			case 5:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 2200))
				{
					Drivetrain.driveBack(lValue, rValue, -.6);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 3000))
				{
					Drivetrain.driveBack(lValue, rValue, -.2);
				}
				else
				{
					currentState = 6;
				}
				break;
			case 6:
				Drivetrain.stop();
				break;
		}
	}
	
	public static void rightSwitch(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.stop();
				stopWatch.reset();
				stopWatch.start();
				IntakeWheels.pickUp(.2);
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				stopWatch.reset();
				if(ElevatorLevel.reachedSwitch())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToSwitch(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainSwitchPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 2000))
				{
					Drivetrain.driveForw(lValue, rValue, .5);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 7000 ))
				{
					Drivetrain.driveForw(lValue, rValue, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 8500))
				{
					Drivetrain.driveForw(lValue, rValue, .5);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainSwitchPosition();
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				double dist = 5300;
				double ratio = 2.3;
				if(Functions.testCurve(rValue, dist) != 0)
				{
					rSSpeed = Functions.testCurve(rValue, dist);
					lSSpeed = rSSpeed/ratio;
					Drivetrain.goStraightLeft(lValue, rValue, ratio, lSSpeed, rSSpeed, .06);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 5;
				}
				break;
			case 5:
				Drivetrain.stop();
				ElevatorLevel.maintainSwitchPosition();
				IntakeWheels.shoot(1);
				break;
		}
	}
	
	public static void testTurnRight(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 3:
				if(!Drivetrain.reachedDistance(lValue, rValue, 2200))
				{
					Drivetrain.driveForw(lValue, rValue, .6);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 3000))
				{
					Drivetrain.driveForw(lValue, rValue, .2);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				double dist = 5300;
				double ratio = 2.3;
				if(Functions.testCurve(rValue, dist) != 0)
				{
					lSSpeed = Functions.testCurve(rValue, dist);
					rSSpeed = lSSpeed/ratio;
					Drivetrain.goStraightRight(lValue, rValue, ratio, lSSpeed, rSSpeed, .06);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 6;
				}
				break;
			case 5:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 2200))
				{
					Drivetrain.driveForw(lValue, rValue, .6);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 3000))
				{
					Drivetrain.driveForw(lValue, rValue, .2);
				}
				else
				{
					currentState = 6;
				}
				break;
			case 6:
				Drivetrain.stop();
				break;
		}
	}
	
	public static void testS(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				stopWatch.reset();
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 6500))
				{
					Drivetrain.driveForw(lValue, rValue, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 8000))
				{
					Drivetrain.driveForw(lValue, rValue, .3);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				rValue -= prevRightEncoder;
				lSSpeed = Drivetrain.keepMotorInPlace(prevLeftEncoder, lValue);
				rSSpeed = .7;
				// 5000 and 2.3
				double dist = 4000;
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				Encoders.resetEncoders();
				stopWatch.start();
				currentState = 6;
				break;
			case 6:
				ElevatorLevel.maintainPickUpPosition();
				if(lValue == 0 && rValue == 0 && stopWatch.get() > 1)
				{
					stopWatch.stop();
					currentState = 7;
				}
				else
				{
					Encoders.resetEncoders();
				}
				break;
			case 7:
				ElevatorLevel.maintainPickUpPosition();
				lSSpeed = Drivetrain.keepMotorInPlace(0, lValue);
				rSSpeed = -.7;
				// 5000 and 2.3
				rValue = Math.abs(rValue);
				dist = 4000;
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					currentState = 8;
				}
				break;
			case 8:
				ElevatorLevel.maintainPickUpPosition();
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 4000))
				{
					Drivetrain.driveBack(lValue, rValue, -.7);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 5500))
				{
					Drivetrain.driveBack(lValue, rValue, -.3);
				}
				else
				{
					currentState = 9;
				}
				break;
			case 10:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	public static void test1(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(.4);
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 9500))
				{
					Drivetrain.tankDrive(.8, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 11000))
				{
					Drivetrain.tankDrive(.5, .5);
				}
				else
				{
					Encoders.testEncoders();
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	public static void test2(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 5000))
				{
					Drivetrain.driveBack(lValue, rValue, -.74);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 7000))
				{
					Drivetrain.driveBack(lValue, rValue, -.3);
				}
				else
				{
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	
	
	public static void test3(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 3;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 8000))
				{
					Drivetrain.driveForw(lValue, rValue, .8);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 9500))
				{
					Drivetrain.driveForw(lValue, rValue, .3);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				rValue -= prevRightEncoder;
				lSSpeed = Drivetrain.keepMotorInPlace(prevLeftEncoder, lValue);
				rSSpeed = .7;
				// 5000 and 2.3
				double dist = 4000;
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	public static void test4(double lValue, double rValue)//no need
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				lSSpeed = Drivetrain.keepMotorInPlace(0, lValue);
				rSSpeed = -.7;
				// 5000 and 2.3
				rValue = Math.abs(rValue);
				double dist = 4000;
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 4000))
				{
					Drivetrain.driveBack(lValue, rValue, -.7);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	public static void test5(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 3000))
				{
					Drivetrain.driveBack(lValue, rValue, -.6);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				double dist = 5400;
				double ratio = 3.26;
				if(Math.abs(lValue) < dist)
				{
					lSSpeed = Functions.test5Turn(lValue, dist);
					rSSpeed = lSSpeed/ratio;
					Drivetrain.goBackRight(lValue, rValue, ratio, lSSpeed, rSSpeed, .6);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
			case 6:
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 3000))
				{
					Drivetrain.driveBack(lValue, rValue, -.6);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 4000))
				{
					Drivetrain.driveBack(lValue, rValue, -.3);
				}
				else
				{
					currentState = 5;
				}
				break;
		}
	}
	
	public static void test6(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				double dist = 5400;
				double ratio = 3.26;
				if(Math.abs(rValue) < dist)
				{
					rSSpeed = Functions.test4Turn(rValue, dist);
					lSSpeed = rSSpeed/ratio;
					Drivetrain.goBackLeft(lValue, rValue, ratio, lSSpeed, rSSpeed, .6);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 4000))
				{
					Drivetrain.driveBack(lValue, rValue, -.6);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 5;
				}
				break;
			case 5:
				ElevatorLevel.maintainPickUpPosition();
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				double dist1 = 5400;
				double ratio1 = 3.26;
				if(Math.abs(lValue) < dist1)
				{
					lSSpeed = Functions.test5Turn(lValue, dist1);
					rSSpeed = lSSpeed/ratio1;
					Drivetrain.goBackRight(lValue, rValue, ratio1, lSSpeed, rSSpeed, .6);
				}
				else
				{
					currentState = 6;
				}
				break;
			case 6:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	public static void test7(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.start();
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToPickUp(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 3:
				ElevatorLevel.maintainPickUpPosition();
				if(!Drivetrain.reachedDistance(lValue, rValue, 2000))
				{
					Drivetrain.driveForw(lValue, rValue, .74);
				}
				else if(!Drivetrain.reachedDistance(lValue, rValue, 2800))
				{
					Drivetrain.driveForw(lValue, rValue, .34);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 4;
				}
				break;
			case 4:
				ElevatorLevel.maintainPickUpPosition();
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				double dist = 5400;
				double ratio = 3.26;
				if(rValue < dist)
				{
					rSSpeed = Functions.test3Turn(rValue, dist);
					lSSpeed = rSSpeed/ratio;
					Drivetrain.goStraightLeft(lValue, rValue, ratio, lSSpeed, rSSpeed, .06);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				ElevatorLevel.maintainPickUpPosition();
				Drivetrain.stop();
				break;
		}
	}
	
	public static void rrScaleFirstSwitchSecond(double lValue, double rValue)
	{
		switch(currentState)
		{
			case 0:
				stopWatch.stop();
				stopWatch.reset();
				stopWatch.start();
				IntakeWheels.pickUp(.2);
				currentState = 1;
				break;
			case 1:
				time = stopWatch.get();
				if(lValue == 0 && rValue == 0 && time >= 2)
				{
					
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else if(lValue == 0 && rValue == 0 && ElevatorLevel.reachedStop())
				{
					Elevator.stopEleVader();
					ElevatorLevel.resetElevatorEncoders();
					currentState = 2;
					stopWatch.stop();
				}
				else
				{
					Elevator.moveEleVader(-.23);
					Encoders.resetEncoders();
				}
				break;
			case 2:
				stopWatch.reset();
				if(ElevatorLevel.reachedPickUp())
				{
					currentState = 3;
				}
				else
				{
					Elevator.moveEleVader(.4);
				}
				break;
			case 3:
				Functions.lrandrrElevatorForFirstScale(lValue, rValue, ElevatorLevel.elevatorEncoderValue, 1);
				if(Functions.lrandrrSpeedForFirstScale(lValue, rValue, 23500) != 0)
				{
					Drivetrain.driveForw(lValue, rValue, Functions.lrandrrSpeedForFirstScale(lValue, rValue, 23500));
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					Encoders.testEncoders();
					currentState = 4;
				}
				break;
			case 4:
				Functions.lrandrrElevatorForFirstScale(lValue, rValue, ElevatorLevel.elevatorEncoderValue, 1);
				rValue -= prevRightEncoder;
				lSSpeed = Drivetrain.keepMotorInPlace(prevLeftEncoder, lValue);
				rSSpeed = .5;
				double dist = 1700;
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					currentState = 5;
				}
				break;
			case 5:
				Drivetrain.stop();
				if(ElevatorLevel.reachedScale())
				{
					ElevatorLevel.maintainScalePosition();
					stopWatch.start();
					currentState = 6;
				}
				else
				{
					Elevator.moveEleVader(Functions.stopToScale(ElevatorLevel.elevatorEncoderValue));
				}
				break;
			case 6:
				ElevatorLevel.maintainScalePosition();
				if(stopWatch.get() < .6)
				{
					IntakeWheels.shoot(.4);
				}
				else
				{
					stopWatch.stop();
					stopWatch.reset();
					prevRightEncoder = rValue;
					currentState = 7;
				}
				break;
			case 7:
				ElevatorLevel.maintainScalePosition();
				rValue -= prevRightEncoder;
				lSSpeed = Drivetrain.keepMotorInPlace(prevLeftEncoder, lValue);
				rSSpeed = -.4;
				dist = 2400;
				rValue = Math.abs(rValue);
				if(rValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 99;
				}
				break;
			case 99:
				Functions.moveElevatorToStop(ElevatorLevel.elevatorEncoderValue);
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				System.out.println(lValue + " " + rValue);
				if(!Drivetrain.reachedDistance(lValue, rValue, 2000))
				{
					Drivetrain.driveBack(lValue, rValue, -.4);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 8;
				}
				break;
			case 8:
				Functions.moveElevatorToStop(ElevatorLevel.elevatorEncoderValue);
				System.out.println(lValue + " " + rValue);
				lValue -= prevLeftEncoder;
				rSSpeed = Drivetrain.keepMotorInPlace(prevRightEncoder, rValue);
				lSSpeed = -.4;
				dist = 3200;
				lValue = Math.abs(lValue);
				if(lValue < dist)
				{
					Drivetrain.tankDrive(lSSpeed, rSSpeed);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 15;
				}
				break;
			case 9:
				Functions.moveElevatorToStop(ElevatorLevel.elevatorEncoderValue);
				lValue -= prevLeftEncoder;
				rValue -= prevRightEncoder;
				stopWatch.stop();
				stopWatch.reset();
				if(Functions.lrandrrBackUpToWallTurn(lValue, 6500) != 0)
				{
					lSSpeed = Functions.lrandrrBackUpToWallTurn(lValue, 6500);
					rSSpeed = lSSpeed/2.3;
					Drivetrain.goBackRight(lValue, rValue, 2.3, lSSpeed, rSSpeed, .06);
				}
				else
				{
					currentState = 15;
				}
				break;
			case 10:
				Functions.moveElevatorToStop(ElevatorLevel.elevatorEncoderValue);
				Drivetrain.stop();
				stopWatch.start();
				Encoders.resetEncoders();
				currentState = 11;
				break;
			case 11:
				Functions.moveElevatorToStop(ElevatorLevel.elevatorEncoderValue);
				if(stopWatch.get() > .5)
				{
					currentState = 12;
				}
				else
				{
					Encoders.resetEncoders();
				}
				break;
			case 12:
				Functions.moveElevatorToStop(ElevatorLevel.elevatorEncoderValue);
				if(!Drivetrain.reachedDistance(lValue, rValue, 2800))
				{
					Drivetrain.driveForw(lValue, rValue, .6);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 13;
				}
				break;
			case 13:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				dist = 5300;
				double ratio = 2.3;
				if(Functions.testCurve(rValue, dist) != 0)
				{
					rSSpeed = Functions.testCurve(rValue, dist);
					lSSpeed = rSSpeed/ratio;
					Drivetrain.goStraightLeft(lValue, rValue, ratio, lSSpeed, rSSpeed, .06);
				}
				else
				{
					prevLeftEncoder = lValue;
					prevRightEncoder = rValue;
					currentState = 14;
				}
				break;
			case 14:
				rValue -= prevRightEncoder;
				lValue -= prevLeftEncoder;
				if(!Drivetrain.reachedDistance(lValue, rValue, 2800))
				{
					Intake.openIntake();
					IntakeWheels.pickUp(.8);
					Drivetrain.driveForw(lValue, rValue, .6);
				}
				else
				{
					Intake.closeIntake();
					IntakeWheels.runIntake(0, 0, true, 0, 0);
					Drivetrain.stop();
				}
				break;
			case 15:
				Drivetrain.stop();
				Elevator.stopEleVader();
				break;
		}
	}
	
}
