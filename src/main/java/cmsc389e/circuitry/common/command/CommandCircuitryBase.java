package cmsc389e.circuitry.common.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public abstract class CommandCircuitryBase extends CommandBase {
    protected static final String SUBMIT = "submit" + File.separatorChar + "submit.jar", TESTS = "tests.txt";
    @Nullable
    protected static final List<String[]> TEST_LINES = new ArrayList<>();

    protected static void sendMessage(ICommandSender sender, String message) {
	sendMessage(sender, message, new Style().getColor());
    }

    protected static void sendMessage(ICommandSender sender, String message, TextFormatting color) {
	sender.sendMessage(new TextComponentString(message).setStyle(new Style().setItalic(true).setColor(color)));
    }

    protected static void tryReadTestsFile() throws CommandException {
	// Load in valid tags and rows for the current test
	TEST_LINES.clear();
	try (BufferedReader in = Files.newBufferedReader(Paths.get(TESTS))) {
	    String line;
	    while ((line = in.readLine()) != null)
		TEST_LINES.add(line.split("\t"));
	} catch (IOException e) {
	    throw new CommandException("Unable to read " + TESTS + ". Try running /load again.");
	}
    }
}