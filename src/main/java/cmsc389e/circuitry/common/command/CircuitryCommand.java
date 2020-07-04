package cmsc389e.circuitry.common.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Config.Key;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class CircuitryCommand {
	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal(Circuitry.MODID);
		for (Key key : Key.values()) {
			Object value = Config.get(key);
			ArgumentType<?> type = value instanceof Integer ? IntegerArgumentType.integer()
					: StringArgumentType.greedyString();

			builder.then(Commands.literal(key.toString().replace(" ", ""))
					.then(Commands.argument("value", type).executes(context -> {
						Config.set(key, context.getArgument("value", value.getClass()));
						context.getSource().sendFeedback(
								new StringTextComponent("Set the value of " + key + " to " + Config.get(key) + '.'),
								true);
						return 0;
					})));
		}
		return builder;
	}
}