package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;

import frc.robot.subsystems.Feeder.Feeder;
import frc.robot.subsystems.Feeder.FeederConstants;
import frc.robot.subsystems.Hood.Hood;
import frc.robot.subsystems.Hood.HoodConstants;
import frc.robot.subsystems.Shooter.Shooter;
import frc.robot.subsystems.Shooter.ShooterConstants;

public class RobotStateCommand extends Command{
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

        switch (state) {

            case IDLE:
                runIdle();
                break;

            case HOLD:
                runHold();
                break;

            case SHOOTING:
                runShooting();
                break;

            case EJECT:
                runEject();
                break;

            case OPEN_WALLS:
                runIdle();
                break;
            
            case INTAKE:
                runIdle();
                break;

            }
            
            }
            
            
    public void setState(State newState){
     state = newState; }

   private void runIdle() {
        feeder.setState(Feeder.State.IDLE);
        hood.setState(Hood.State.IDLE);
        shooter.setState(Shooter.State.IDLE);

        if (isInAllianceArea()) {
        shooter.setState(Shooter.State.WARM_UP);
        }  
        else {
        shooter.setState(Shooter.State.IDLE);
        }
    }

    private void runHold() {
        if(!hasBalls()){
         setState(State.IDLE);
        }
        feeder.setState(Feeder.State.HOLD);
        hood.setState(Hood.State.IDLE);
        shooter.setState(Shooter.State.IDLE);
        
           
        
    }

    private void runShooting() {
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
    }

    private void runEject() {
        if(hasBalls()){
         setState(State.IDLE);
        }
        feeder.setState(Feeder.State.FORWARD);
        hood.setState(Hood.State.EJECT);
        shooter.setState(Shooter.State.EJECT);
        

        
    }

    
   

    private boolean hasBalls() {
        return !feeder.isEmpty();
    }

    

    private boolean isInAllianceArea() {
        return true;
        //קפלן אמרת רק שיהיה את זה ושזה לא צריך לעבוד בינתיים כי לא לימדת
    }
     private boolean isActivePeriod() {
        return true;
        //כנל על זה
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

        
    

     


