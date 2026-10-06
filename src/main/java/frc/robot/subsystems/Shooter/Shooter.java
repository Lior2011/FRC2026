package frc.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VelocityVoltage;
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

public class Shooter extends SubsystemBase {
    public enum State {
        IDLE,
        WARM_UP,
        SHOOT,
        EJECT
    }

    private final VelocityVoltage velocityChange = new VelocityVoltage(0);
    public static Shooter shooter;
    private TalonFX shooterMotor;
    private TalonFX shooterSlave;
    TalonFXConfiguration shooterConfig;

    private StrictFollower follower;
    private TalonFXConfiguration followerConfig;
    private StatusSignal<AngularVelocity> velocitySignal;

    private State state = State.IDLE;

    public Shooter() {
        shooterSlave = new TalonFX(PortMap.shooter.SLAVE_MOTOR);
        shooterMotor = new TalonFX(PortMap.shooter.SHOOTER_MOTOR);
        follower = new StrictFollower(PortMap.shooter.SHOOTER_MOTOR);

        shooterConfig = new TalonFXConfiguration();
        followerConfig = new TalonFXConfiguration();

        velocitySignal = shooterMotor.getVelocity();

        config();
    }

    private void config() {
        shooterConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        followerConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        followerConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        shooterConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

        shooterConfig.Feedback.SensorToMechanismRatio = ShooterConstants.GEAR_RATIO;
        followerConfig.Feedback.SensorToMechanismRatio = ShooterConstants.GEAR_RATIO;

        shooterConfig.CurrentLimits.SupplyCurrentLimit = ShooterConstants.PICK_CURRENT_LIMIT;
        followerConfig.CurrentLimits.SupplyCurrentLimit = ShooterConstants.PICK_CURRENT_LIMIT;

        followerConfig.CurrentLimits.StatorCurrentLimit = ShooterConstants.STATOR_CURRENT_LIMIT;
        shooterConfig.CurrentLimits.StatorCurrentLimit = ShooterConstants.STATOR_CURRENT_LIMIT;

        shooterMotor.getConfigurator().apply(shooterConfig);
        shooterSlave.getConfigurator().apply(followerConfig);
    }

    public void setState(State newState) {
        state = newState;
    }

    public State getState() {
        return state;
    }

    public void setVelocity(double velocity) {

        shooterMotor.setControl(
                velocityChange.withVelocity(velocity));

        shooterSlave.setControl(follower);
    }

    private void runState() {

        switch (state) {

            case IDLE:
                shooterMotor.stopMotor();
                shooterSlave.stopMotor();
                break;

            case WARM_UP:
                shooterMotor.setControl(velocityChange.withVelocity(ShooterConstants.WARM_UP_VELOCITY)

                );

                shooterSlave.setControl(follower);
                break;

            case SHOOT:
                shooterMotor.setControl(velocityChange.withVelocity(ShooterConstants.SHOOT_VELOCITY)

                );
                shooterSlave.setControl(follower);
                break;

            case EJECT:
                shooterMotor.setControl(velocityChange.withVelocity(ShooterConstants.EJECT_VELOCITY)

                );

                shooterSlave.setControl(follower);
                break;
        }
    }

    public void stop() {
        setState(State.IDLE);
        shooterSlave.setControl(follower);

    }

    public double getVelocity() {
        return velocitySignal.getValueAsDouble();
    }

    public boolean atShootVelocity() {
        return Math.abs(getVelocity() - ShooterConstants.SHOOT_VELOCITY) == 0;
    }

    public static Shooter getInstance() {
        if (shooter == null) {
            shooter = new Shooter();
        }
        return shooter;

    }

    @Override
    public void periodic() {
        MALog.log("/subsystems/Shooter/Velocity", getVelocity());
        MALog.log("/subsystems/Shooter/State", getState().name());
        runState();
        BaseStatusSignal.refreshAll(velocitySignal);
    }

}
