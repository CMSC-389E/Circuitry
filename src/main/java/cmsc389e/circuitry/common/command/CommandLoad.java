package cmsc389e.circuitry.common.command;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Triple;

import cmsc389e.circuitry.ConfigCircuitry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public final class CommandLoad extends CommandCircuitryBase {
    public CommandLoad() {
	super("load", Triple.of("project number", true, int.class));
    }

    @Override
    public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
	int projectNumber = getOrDefault("project number", null);

	String serverURL = "https://cs.umd.edu/~abrassel/";
	String submitURL = serverURL + "submit.jar";
	String testsURL = serverURL + "proj" + projectNumber + "tests.txt";

	// Check if submit.jar exists and download a new one if it doesn't
	if (Files.notExists(Paths.get(SUBMIT)))
	    try {
		FileUtils.copyURLToFile(new URL(submitURL), new File(SUBMIT));
	    } catch (IOException e) {
		throw new CommandException("Could not download " + SUBMIT + " from " + serverURL + '.');
	    }

	// Attempt to download tests.txt
	sendMessage(sender, "Attempting to read project file " + projectNumber + " from " + serverURL + '.');
	try {
	    FileUtils.copyURLToFile(new URL(testsURL), new File(TESTS));
	} catch (MalformedURLException e) {
	    throw new CommandException("Project " + projectNumber + " is not valid.");
	} catch (IOException e) {
	    throw new CommandException(
		    SUBMIT + " is missing or " + TESTS + " is being used by another process.  Try running again.");
	}
	sendMessage(sender, "Successfully read project file " + projectNumber + " from " + serverURL + '.');

	tryReadTestsFile();

	// Save tags to Lists first since we don't know how many of each there are
	List<String> inputs = new ArrayList<>();
	List<String> outputs = new ArrayList<>();

	// Read in the header row and sort each tag into the correct List
	for (String name : TEST_LINES.get(1))
	    switch (name.charAt(0)) {
	    case 'i':
		inputs.add(name);
		break;
	    case 'o':
		outputs.add(name);
		break;
	    default:
		throw new CommandException("The following tag must start with i or o: " + name);
	    }

	// Save tags to the config file to store for future game sessions
	ConfigCircuitry.inputs = inputs.toArray(new String[0]);
	ConfigCircuitry.outputs = outputs.toArray(new String[0]);
	ConfigCircuitry.sync();
	sendMessage(sender, "Loaded passed file in correctly.");
    }
}