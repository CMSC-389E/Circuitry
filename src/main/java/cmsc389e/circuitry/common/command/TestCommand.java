package cmsc389e.circuitry.common.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Tester;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class TestCommand {
    private static String execute(CommandSource source, CloseableHttpClient client, String uri, HttpEntity entity,
	    Object... parameters) throws IOException {
	RequestBuilder builder = RequestBuilder.post().setUri(uri).setEntity(entity);
	for (int i = 0; i < parameters.length; i += 2)
	    builder.addParameter(parameters[i].toString(), parameters[i + 1].toString());

	try (CloseableHttpResponse response = client.execute(builder.build())) {
	    String contents = EntityUtils.toString(response.getEntity());
	    if (response.getStatusLine().getStatusCode() != 200)
		throw new CommandException(new StringTextComponent(contents));
	    source.sendFeedback(new StringTextComponent(contents), true);
	    return contents;
	}
    }

    private static int load(CommandContext<CommandSource> context, int projectNumber) {
	if (Tester.INSTANCE.running)
	    throw new CommandException(new StringTextComponent("Cannot load a new project while a test is running!"));

	Config.projectNumber.set(projectNumber);
	try {
	    Config.load();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CommandException(new StringTextComponent(e.getLocalizedMessage()));
	}
	context.getSource().sendFeedback(new StringTextComponent("Project " + projectNumber + " loaded successfully!"),
		true);
	return 0;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
	String projectNumber = "Project Number";
	String delay = "Delay";
	String campusUID = "Campus UID";
	String uidPassword = "UID Password";

	ArgumentBuilder<CommandSource, ?> load = Commands.literal("load")
		.then(Commands.argument(projectNumber, IntegerArgumentType.integer(0, 9))
			.executes(context -> load(context, IntegerArgumentType.getInteger(context, projectNumber))));
	ArgumentBuilder<CommandSource, ?> start = Commands.literal("start").executes(context -> start(context, 0))
		.then(Commands.argument(delay, IntegerArgumentType.integer(0))
			.executes(context -> start(context, IntegerArgumentType.getInteger(context, delay))));
	ArgumentBuilder<CommandSource, ?> stop = Commands.literal("stop").executes(TestCommand::stop);
	ArgumentBuilder<CommandSource, ?> submit = Commands.literal("submit")
		.executes(context -> submit(context, "", ""))
		.then(Commands.argument(campusUID, StringArgumentType.string())
			.then(Commands.argument(uidPassword, StringArgumentType.string())
				.executes(context -> submit(context, StringArgumentType.getString(context, campusUID),
					StringArgumentType.getString(context, uidPassword)))));

	dispatcher.register(Commands.literal("test").then(load).then(start).then(stop).then(submit));
    }

    private static int start(CommandContext<CommandSource> context, int delay) {
	if (Tester.INSTANCE.running)
	    throw new CommandException(new StringTextComponent("A test is already running!"));
	Tester.INSTANCE.start(context.getSource(), delay);
	return 0;
    }

    private static int stop(CommandContext<CommandSource> context) {
	if (!Tester.INSTANCE.running)
	    throw new CommandException(new StringTextComponent("No test is currently running!"));
	context.getSource().sendFeedback(new StringTextComponent("Test stopped successfully!"), true);
	return 0;
    }

    @SuppressWarnings("resource")
    private static int submit(CommandContext<CommandSource> context, String campusUID, String uidPassword) {
	if (Tester.INSTANCE.running)
	    throw new CommandException(new StringTextComponent("Cannot submit test results while a test is running!"));
	if (Tester.RESULTS.length() == 0)
	    throw new CommandException(new StringTextComponent("Cannot find any test results!"));

	try (CloseableHttpClient client = HttpClients.createMinimal()) {
	    CommandSource source = context.getSource();
	    String base = "https://submit.cs.umd.edu/spring2020/eclipse/";
	    if (!campusUID.isEmpty()) {
		String content = execute(source, client, base + "NegotiateOneTimePassword", null, "campusUID",
			campusUID, "courseName", "CMSC389E", "projectNumber", Config.projectNumber.get(), "uidPassword",
			uidPassword);
		Properties properties = new Properties();
		properties.load(new StringReader(content));
		Config.cvsAccount.set(properties.getProperty("cvsAccount"));
		Config.oneTimePassword.set(properties.getProperty("oneTimePassword"));
	    }

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ZipOutputStream zip = new ZipOutputStream(out);
	    zip.putNextEntry(new ZipEntry("Dummy.java"));
	    zip.write("public class Dummy{public static void main(String[]args){}}".getBytes());
	    zip.putNextEntry(new ZipEntry("Results.txt"));
	    zip.write(Tester.RESULTS.toString().getBytes());
	    zip.close();

	    execute(source, client, base + "SubmitProjectViaEclipse",
		    MultipartEntityBuilder.create()
			    .addBinaryBody("submittedFiles", out.toByteArray(), ContentType.DEFAULT_BINARY,
				    "submit.zip")
			    .build(),
		    "courseName", "CMSC389E", "cvsAccount", Config.cvsAccount.get(), "oneTimePassword",
		    Config.oneTimePassword.get());
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CommandException(new StringTextComponent(e.getLocalizedMessage()));
	}
	return 0;
    }
}
