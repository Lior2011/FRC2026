
package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.commands.RobotStateCommand;
import frc.robot.commands.RobotStateCommand.State;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Feeder.Feeder;
import frc.robot.subsystems.Hood.Hood;
import frc.robot.subsystems.Shooter.Shooter;;

public class RobotContainer {

  private final Feeder feeder = Feeder.getInstance();
  private final Shooter shooter = Shooter.getInstance();
  private final Hood hood = Hood.getInstance();
  private final CommandPS5Controller driverController = new CommandPS5Controller(PortMap.CONTROLLER);
  private final RobotStateCommand robotStateCommand = new RobotStateCommand();

  public RobotContainer() {

    configureBindings();
    robotStateCommand.schedule();

  }

  private void configureBindings() {

    new Trigger(() -> driverController.circle().getAsBoolean() && !feeder.isEmpty())
        .onTrue(new InstantCommand(() -> robotStateCommand.setState(RobotStateCommand.State.SHOOTING)));
    new Trigger(() -> driverController.touchpad().getAsBoolean())
        .onTrue(new InstantCommand(() -> robotStateCommand.setState(RobotStateCommand.State.IDLE)));
    new Trigger(() -> driverController.cross().getAsBoolean())
        .onTrue(new InstantCommand(() -> robotStateCommand.setState(RobotStateCommand.State.EJECT)));
    new Trigger(() -> driverController.L1().getAsBoolean() && !driverController.circle().getAsBoolean() )
        .onTrue(new InstantCommand(() -> hood.setState(Hood.State.HOMING)));
    new Trigger(() -> driverController.triangle().getAsBoolean())
        .onTrue(new InstantCommand(() -> robotStateCommand.setState(RobotStateCommand.State.INTAKE)));
    new Trigger(() -> driverController.L2().getAsBoolean())
        .onTrue(new InstantCommand(() -> robotStateCommand.setState(RobotStateCommand.State.OPEN_WALLS)));
    new Trigger(() -> !driverController.triangle().getAsBoolean() && !feeder.isEmpty() && robotStateCommand.getState() != State.SHOOTING && robotStateCommand.getState() != State.EJECT)
        .onTrue(new InstantCommand(() -> robotStateCommand.setState(RobotStateCommand.State.HOLD)));
   
    //השוטר מתכונן לירי אוטומטית כשרובוט באידל ובצד שלו של הברית
    

  }

}
