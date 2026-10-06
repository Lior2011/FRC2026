package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import frc.robot.MALog;
import frc.robot.subsystems.Feeder.Feeder;
import frc.robot.subsystems.Feeder.FeederConstants;
import frc.robot.subsystems.Hood.Hood;
import frc.robot.subsystems.Hood.HoodConstants;
import frc.robot.subsystems.Shooter.Shooter;
import frc.robot.subsystems.Shooter.ShooterConstants;

public class RobotStateCommand extends Command {
    public enum State {
        IDLE,
        INTAKE,
        HOLD,
        SHOOTING,
        EJECT,
        OPEN_WALLS
    }

    private State state = State.IDLE;
    private final Feeder feeder;
    private final Hood hood;
    private final Shooter shooter;

    public RobotStateCommand() {

        feeder = Feeder.getInstance();
        hood = Hood.getInstance();
        shooter = Shooter.getInstance();

        addRequirements(feeder);
        addRequirements(hood);
        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        state = State.IDLE;
    }

    @Override
    public void execute() {
        MALog.log("/commands/RobotStateCommand/State", getState().name());
        //לא הייתי בטוחה איפה לשים את זה בקומנדים אז שמתי פה כי זה הכי הגיוני
    

        switch (state) {

            case IDLE:
                feeder.setState(Feeder.State.IDLE);
                hood.setState(Hood.State.IDLE);
                if (isInAllianceArea()) {
                    shooter.setState(Shooter.State.WARM_UP);
                } else {
                    shooter.setState(Shooter.State.IDLE);
                }
                break;

            case HOLD:
                if (!hasBalls()) {
                    setState(State.IDLE);
                }
                feeder.setState(Feeder.State.HOLD);
                hood.setState(Hood.State.IDLE);
                shooter.setState(Shooter.State.IDLE);
                break;

            case SHOOTING:
                if (!hasBalls() || !isInAllianceArea() || !isActivePeriod()) {
                    setState(State.IDLE);
                    return;
                }

                hood.setState(Hood.State.SHOOT);

                if (!hood.atShootPosition()) {
                    shooter.setState(Shooter.State.IDLE);
                    feeder.setState(Feeder.State.IDLE);
                    return;
                }

                shooter.setState(Shooter.State.SHOOT);

                if (!shooter.atShootVelocity()) {
                    feeder.setState(Feeder.State.IDLE);
                    return;
                }

                feeder.setState(Feeder.State.FORWARD);
                break;

            case EJECT:
                if (!hasBalls()) {
                    setState(State.IDLE);
                } else {
                    feeder.setState(Feeder.State.FORWARD);
                    hood.setState(Hood.State.EJECT);
                    shooter.setState(Shooter.State.EJECT);
                }
                break;

            case OPEN_WALLS:
                setState(State.IDLE);
                //עלמה אמרה שכי אין את המערכות בשביל זה לשים את המצב באידל
                break;

            case INTAKE:
                setState(State.IDLE);
                 //כנל על זה
                break;

        }

    }

    public void setState(State newState) {
        state = newState;
    }

    public State getState() {
        return state;
    }

    private boolean hasBalls() {
        return !feeder.isEmpty();
    }

    private boolean isInAllianceArea() {
        return true;
        // קפלן אמרת רק שיהיה את זה ושזה לא צריך לעבוד בינתיים כי לא לימדת
    }

    private boolean isActivePeriod() {
        return true;
        // כנל על זה
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        setState(State.IDLE);
    }
}
