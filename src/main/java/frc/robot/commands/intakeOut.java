
package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake.Intake;


public class intakeOut extends Command {
   private final Intake intake;
  public Timer timer;
 
  public intakeOut(Intake intake) {
    this.intake = intake;
    addRequirements(intake);
    timer = new Timer();
  }

  
  @Override
  public void initialize() {
  timer.start();
  }

  
  @Override
  public void execute() {
    if (timer.hasElapsed(2)){
      System.out.println("Start");
      intake.setVoltage(8);
    }
    else{
      intake.setVoltage(5);
    }

    if (intake.getVelocity() == 20){
      intake.setVoltage(0);
    }
  }

  
  @Override
  public void end(boolean interrupted) {}

  
  @Override
  public boolean isFinished() {
    return false;
  }
}
