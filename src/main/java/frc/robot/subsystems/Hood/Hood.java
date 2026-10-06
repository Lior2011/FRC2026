package frc.robot.subsystems.Hood;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.MALog;
import frc.robot.PortMap;
import frc.robot.subsystems.Feeder.FeederConstants;
import frc.robot.subsystems.Shooter.Shooter;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import edu.wpi.first.units.measure.Angle;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;

public class Hood extends SubsystemBase {
  public enum State {
    IDLE,
    HOMING,
    SHOOT,
    EJECT
  }

  public static Hood hood;
  private TalonFX hoodMotor;
  private CANcoder hoodEncoder;
  private TalonFXConfiguration hoodConfig;
  private CANcoderConfiguration encoderConfig;
  private StatusSignal<Angle> positionSignal;
  private final PositionVoltage positionRequest = new PositionVoltage(0);
  private State state = State.IDLE;

  public Hood() {

    hoodMotor = new TalonFX(PortMap.hood.HOOD_MOTOR);
    hoodEncoder = new CANcoder(PortMap.hood.HOOD_ENCODER);
    positionSignal = hoodEncoder.getPosition();
    hoodConfig = new TalonFXConfiguration();
    encoderConfig = new CANcoderConfiguration();
    config();

  }

  private void config() {
    hoodConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    hoodConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
    hoodConfig.Feedback.SensorToMechanismRatio = HoodConstants.GEAR_RATIO;
    hoodConfig.Feedback.FeedbackRemoteSensorID = hoodEncoder.getDeviceID();
    hoodConfig.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder;
    hoodMotor.getConfigurator().apply(hoodConfig);
    hoodEncoder.getConfigurator().apply(encoderConfig);
    hoodConfig.CurrentLimits.SupplyCurrentLimit = HoodConstants.PICK_CURRENT_LIMIT;
    hoodConfig.CurrentLimits.StatorCurrentLimit = HoodConstants.STATOR_CURRENT_LIMIT;

  }

  public void setState(State newState) {
    state = newState;
  }

  public State getState() {
    return state;
  }

  private void runState() {

    switch (state) {

      case IDLE:
        hoodMotor.stopMotor();
        break;

      case HOMING:
        hoodMotor.setControl(positionRequest.withPosition(HoodConstants.HOMING_POSITION));

        break;

      case SHOOT:
        hoodMotor.setControl(positionRequest.withPosition(HoodConstants.SHOOT_POSITION));

        break;
      case EJECT:
        hoodMotor.setControl(positionRequest.withPosition(HoodConstants.EJECT_POSITION));

        break;
    }
  }

  public void setPosition(double position) {

    hoodMotor.setControl(
        positionRequest.withPosition(position));
  }

  public void stop() {
    setState(State.IDLE);
  }

  public void homing() {
    setState(State.HOMING);

  }

  public double getPosition() {
    return positionSignal.getValueAsDouble();
  }

  public boolean atShootPosition() {
    return Math.abs(getPosition() - HoodConstants.SHOOT_POSITION) == 0;
  }

  public static Hood getInstance() {
    if (hood == null) {
      hood = new Hood();
    }
    return hood;
  }

  @Override
  public void periodic() {
    runState();
    MALog.log("/subsystems/Hood/Position", getPosition());
    MALog.log("/subsystems/Hood/State", getState().name());
    BaseStatusSignal.refreshAll(positionSignal);
  }
}
