package cmsc389e.circuitry.common.command.test;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.common.Tester;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class StopCommand {
	@SuppressWarnings("resource")
	public static int execute(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();
		World world = source.getWorld();
		if (Tester.INSTANCES.remove(world) == null)
			throw new CommandException(new StringTextComponent("No Tester is currently running!"));
		source.sendFeedback(new StringTextComponent("Tester stopped successfully."), true);
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("stop").executes(StopCommand::execute);
	}
}