package frc.robot.subsystems.Feeder;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
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
import frc.robot.subsystems.Shooter.ShooterConstants;
import edu.wpi.first.wpilibj.DigitalInput;

public class Feeder extends SubsystemBase{
  public enum State {
        IDLE,
        FORWARD,
        BACKWARD,
        HOLD
       
    }

  
  private static Feeder feeder;
  private TalonFX feederMotor;
  private TalonFXConfiguration feederConfig;
  private DigitalInput feederSensor;
  private  StatusSignal<Current> currentSignal;
  private  StatusSignal<AngularVelocity> velocitySignal;
  private  StatusSignal<Voltage> voltageSignal;
  private State state = State.IDLE;

  public Feeder(){
    feederMotor = new TalonFX(PortMap.feeder.FEEDER_MOTOR);
    currentSignal = feederMotor.getStatorCurrent();
    velocitySignal = feederMotor.getVelocity();
    voltageSignal = feederMotor.getMotorVoltage();
    feederSensor = new DigitalInput(PortMap.feeder.FEEDER_SENSOR);
    feederConfig = new TalonFXConfiguration();
    config();

  }
  private void config(){
  feederConfig.CurrentLimits.SupplyCurrentLimit = FeederConstants.PICK_CURRENT_LIMIT;
  feederConfig.CurrentLimits.StatorCurrentLimit = FeederConstants.STATOR_CURRENT_LIMIT;
  feederConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
  feederConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
  feederConfig.Feedback.SensorToMechanismRatio = FeederConstants.GEAR_RATIO;
  feederMotor.getConfigurator().apply(feederConfig);
  
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
                feederMotor.stopMotor();
                break;

            case FORWARD:
                feederMotor.set(FeederConstants.FORWARD_SPEED);
                
                break;

            case BACKWARD:
                feederMotor.set(FeederConstants.BACKWARD_SPEED);
                
                break;

            case HOLD:
                feederMotor.set(FeederConstants.HOLD_SPEED);

              
                break;
        }
    }
  public void set(double speed) {
        feederMotor.set(speed);
    }

    public void stop() {
        setState(State.IDLE);
    }

    public boolean isEmpty() {
        return feederSensor.get();
    }

    public double getCurrent() {
        return currentSignal.getValueAsDouble();
    }

    public double getVelocity() {
        return velocitySignal.getValueAsDouble();
    }
    public double getVoltage() {
        return voltageSignal.getValueAsDouble();
    }
  
  public static Feeder getInstance(){
    if (feeder == null){
      feeder = new Feeder();
    }
    return feeder;
  }
  @Override
  public void periodic() {
    runState();
    BaseStatusSignal.refreshAll(velocitySignal, voltageSignal, currentSignal);}
 
}
