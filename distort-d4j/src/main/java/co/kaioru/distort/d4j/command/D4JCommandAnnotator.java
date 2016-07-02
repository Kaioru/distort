package co.kaioru.distort.d4j.command;

import co.kaioru.retort.command.ICommand;
import co.kaioru.retort.util.annotation.AnnotatedCommand;
import co.kaioru.retort.util.annotation.CommandAnnotator;
import com.google.common.collect.Lists;
import sx.blah.discord.handle.obj.IMessage;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class D4JCommandAnnotator extends CommandAnnotator<D4JCommandAnnotator, D4JCommand> {

	@Override
	protected Optional<D4JCommand> generateCommand(Object object, Method method, List<ICommand> dependencies) {
		AnnotatedCommand annotation = method.getAnnotation(AnnotatedCommand.class);
		D4JAnnotatedCommand custom = method.getAnnotation(D4JAnnotatedCommand.class);

		List<String> aliases = Arrays.asList(annotation.aliases());
		List<ICommand> commands = Lists.newArrayList();

		Arrays.asList(annotation.commands())
				.forEach(s -> dependencies.stream()
						.filter(c -> c.getName().equals(s))
						.findFirst()
						.ifPresent(commands::add));

		return Optional.of(new D4JCommand() {

			@Override
			public String getName() {
				return annotation.name();
			}

			@Override
			public String getDesc() {
				return annotation.desc();
			}

			@Override
			public List<String> getAliases() {
				return aliases;
			}

			@Override
			public List<ICommand> getCommands() {
				return commands;
			}

			@Override
			public void execute(LinkedList<String> args, IMessage message) throws Exception {
				method.setAccessible(true);
				method.invoke(object, args, message);
			}

		});
	}

}
