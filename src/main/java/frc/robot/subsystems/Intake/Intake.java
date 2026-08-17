// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Intake;


import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import frc.robot.commands.intakeIn;
import frc.robot.commands.intakeOut;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.MALog;
import frc.robot.PortMap;


public class Intake extends SubsystemBase {
  
 
  private static Intake intake;
  private TalonFX feederMotor;
  private TalonFX slave;
  private TalonFXConfiguration feederConfig;
  private StrictFollower follower;
  private TalonFXConfiguration followerConfig;
  private StatusSignal<Current> currentSignal;
  private StatusSignal<AngularVelocity> velocitySignal;
  private StatusSignal<Voltage> voltageSignal;
  



  public Intake(){

    slave = new TalonFX(PortMap.feeder.SLAVE_MOTOR);
    feederMotor = new TalonFX(PortMap.feeder.FEEDER_MOTOR);
    follower = new StrictFollower(PortMap.feeder.FEEDER_MOTOR);
    currentSignal = feederMotor.getStatorCurrent();
    velocitySignal = feederMotor.getVelocity();
    voltageSignal = feederMotor.getMotorVoltage();

    feederConfig = new TalonFXConfiguration();
    

    config();
  }

  private void config(){
  feederConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
  followerConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

  followerConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
  feederConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

  feederConfig.Feedback.SensorToMechanismRatio = IntakeConstants.GEAR;
  followerConfig.Feedback.SensorToMechanismRatio = IntakeConstants.GEAR;


  feederMotor.getConfigurator().apply(feederConfig);
  slave.getConfigurator().apply(followerConfig);
  }

  

  public void setVoltage(double voltage){
   feederMotor.setVoltage(voltage);
    slave.setControl(follower);
  }

  public double getCurrent() {
    return currentSignal.getValueAsDouble();
  }

  public double getVelocity(){
    return velocitySignal.getValueAsDouble();
  }

  public double getAppliedVols(){
    return voltageSignal.getValueAsDouble();
  }


  public static Intake getInstance(){
    if (intake == null){
      intake = new Intake();
    }
    return intake;
  }


  

  @Override
  public void periodic() {
    BaseStatusSignal.refreshAll(velocitySignal, voltageSignal, currentSignal);

    MALog.log("/subsystems/Feeder/Velocity", getVelocity());
    MALog.log("/subsystems/Feeder/Current", getCurrent());
    MALog.log("/subsystems/Feeder/Voltage", getAppliedVols());
  }
}


