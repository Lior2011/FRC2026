

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake.Intake;


public class intakeIn extends Command {
  private final Intake intake;
  public Timer timer;
  
  public intakeIn(Intake intake) {
    this.intake = intake;
    addRequirements(intake);
    timer = new Timer();
    
    
  }

  
  @Override
  public void initialize() {
    timer.start();
  }
  //trying to see if github works

  
  @Override
  public void execute() {
    if(timer.hasElapsed(5)){
      intake.setVoltage(12);
    }
    else{
      intake.setVoltage(6);
    }
  }

  
  @Override
  public void end(boolean interrupted) {
    intake.setVoltage(0);
    timer.restart();
  }

  
  @Override
  public boolean isFinished() {
    return false;
  }
}
