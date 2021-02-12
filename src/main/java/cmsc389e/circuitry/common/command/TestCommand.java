package cmsc389e.circuitry.common.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
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
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.Tester;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public final class TestCommand {
	private static HttpEntity buildEntity(final byte[] b, final Object... bodies) {
		final MultipartEntityBuilder builder = MultipartEntityBuilder.create().addBinaryBody("submittedFiles", b,
				ContentType.DEFAULT_BINARY, "submit.zip");
		for (int i = 0; i < bodies.length; i += 2)
			builder.addTextBody(bodies[i].toString(), bodies[i + 1].toString());
		return builder.build();
	}

	private static String execute(final CommandSource source, final CloseableHttpClient client, final String uri,
			final HttpEntity entity, final Object... parameters) throws IOException {
		final RequestBuilder builder = RequestBuilder.post().setUri(uri).setEntity(entity);
		for (int i = 0; i < parameters.length; i += 2)
			builder.addParameter(parameters[i].toString(), parameters[i + 1].toString());

		try (CloseableHttpResponse response = client.execute(builder.build())) {
			final String contents = EntityUtils.toString(response.getEntity());
			if (response.getStatusLine().getStatusCode() != 200)
				throw new CommandException(new StringTextComponent(contents));
			source.sendFeedback(new StringTextComponent(contents), true);
			return contents;
		}
	}

	@SuppressWarnings("resource")
	private static int load(final CommandContext<CommandSource> context, final Integer projectNumber) {
		if (Tester.tester.running)
			throw new CommandException(new StringTextComponent("Cannot load a new project while a test is running!"));

		final CommandSource source = context.getSource();
		try {
			Config.projectNumber.set(projectNumber);
			Config.load();
			source.sendFeedback(new StringTextComponent("Project " + projectNumber + " loaded successfully!"), true);
		} catch (final UnknownHostException e) {
			e.printStackTrace();
			throw new CommandException(new StringTextComponent("Unable to connect to " + e.getLocalizedMessage()));
		} catch (final IOException e) {
			e.printStackTrace();
			throw new CommandException(
					new StringTextComponent(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
		} finally {
			NodeTileEntity.notifyBlockUpdates(source.getWorld());
		}
		return 0;
	}

	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		final String projectNumber = "Project Number";
		final String delay = "Delay";
		final String loginName = "Login Name";
		final String password = "Password";

		final ArgumentBuilder<CommandSource, ?> load = Commands.literal("load")
				.then(Commands.argument(projectNumber, IntegerArgumentType.integer(0))
						.executes(context -> load(context, context.getArgument(projectNumber, Integer.class))));
		final ArgumentBuilder<CommandSource, ?> start = Commands.literal("start").executes(context -> start(context, 0))
				.then(Commands.argument(delay, IntegerArgumentType.integer(0))
						.executes(context -> start(context, IntegerArgumentType.getInteger(context, delay))));
		final ArgumentBuilder<CommandSource, ?> stop = Commands.literal("stop").executes(TestCommand::stop);
		final ArgumentBuilder<CommandSource, ?> submit = Commands.literal("submit")
				.executes(context -> submit(context, "", ""))
				.then(Commands.argument(loginName, StringArgumentType.string())
						.then(Commands.argument(password, StringArgumentType.greedyString())
								.executes(context -> submit(context, StringArgumentType.getString(context, loginName),
										StringArgumentType.getString(context, password)))));

		dispatcher.register(Commands.literal("test").requires(context -> context.hasPermissionLevel(4)).then(load)
				.then(start).then(stop).then(submit));
	}

	private static int start(final CommandContext<CommandSource> context, final int delay) {
		if (Tester.tester.running)
			throw new CommandException(new StringTextComponent("A test is already running!"));

		try {
			Tester.tester.start(context.getSource(), delay);
		} catch (final IllegalStateException e) {
			throw new CommandException(new StringTextComponent(e.getLocalizedMessage()));
		}
		return 0;
	}

	private static int stop(final CommandContext<CommandSource> context) {
		if (!Tester.tester.running)
			throw new CommandException(new StringTextComponent("No test is currently running!"));

		Tester.tester.running = false;
		context.getSource().sendFeedback(new StringTextComponent("Test stopped successfully!"), true);
		return 0;
	}

	private static int submit(final CommandContext<CommandSource> context, final String loginName,
			final String password) {
		if (Tester.tester.running)
			throw new CommandException(new StringTextComponent("Cannot submit test results while a test is running!"));
		if (Tester.tester.results.length() == 0)
			throw new CommandException(new StringTextComponent("Cannot find any test results!"));

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			final Integer projectNumber = Config.projectNumber.get();
			final String base = "https://submit.cs.umd.edu/spring2021/eclipse/";
			String cvsAccount = Config.cvsAccount.get();
			String oneTimePassword = Config.oneTimePassword.get();
			final CommandSource source = context.getSource();
			if (!loginName.isEmpty()) {
				final Properties properties = new Properties();
				properties.load(new StringReader(execute(source, client, base + "NegotiateOneTimePassword", null,
						"courseKey", "3b66b95c85961489", "loginName", loginName, "password", password, "projectNumber",
						projectNumber)));
				Config.cvsAccount.set(cvsAccount = properties.getProperty("cvsAccount"));
				Config.oneTimePassword.set(oneTimePassword = properties.getProperty("oneTimePassword"));
			}
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (ZipOutputStream zip = new ZipOutputStream(out)) {
				zip.putNextEntry(new ZipEntry("Dummy.java"));
				zip.write("public class Dummy{public static void main(String[]args){}}".getBytes());
				zip.putNextEntry(new ZipEntry("Results.txt"));
				zip.write(Tester.tester.results.toString().getBytes());
			}
			execute(source, client, base + "SubmitProjectViaEclipse",
					buildEntity(out.toByteArray(), "courseName", "CMSC389E", "cvsAccount", cvsAccount,
							"oneTimePassword", oneTimePassword, "projectNumber", projectNumber, "semester",
							Integer.valueOf(202008), "submitClientTool", "CommandLineTool", "submitClientVersion",
							Integer.valueOf(Integer.MAX_VALUE)));
		} catch (final IOException e) {
			e.printStackTrace();
			throw new CommandException(new StringTextComponent(e.getLocalizedMessage()));
		}
		return 0;
	}
}
