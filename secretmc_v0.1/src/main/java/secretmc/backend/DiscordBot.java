package secretmc.backend;

import secretmc.backend.utils.Util;

import java.util.*;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;

import discord4j.rest.util.Color;
import discord4j.rest.entity.RestChannel;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DiscordBot implements Listener {

	private static ArrayList<String> facts;
	public static ArrayList<RestChannel> chatChannels = new ArrayList<>();

	private static final Random r = new Random();

	public DiscordBot() {
		if (!Config.getValue("discord.bot").equals("true"))
			return;

		facts = loadFacts();
		String botID = Config.getValue("analytics.bot.id");
		if (botID.equals("")) return;

		// Login to the bot account
		DiscordClientBuilder.create(botID).build().login().subscribe((client) -> {

			//called when the bot is ready
			client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
				final User self = event.getSelf();
				System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
			});

			//called on all "Guild" (discord servers) when they are "created" (on startup, or when the bot is added)
			client.getEventDispatcher().on(GuildCreateEvent.class).subscribe(event -> {
				//get all channels with name "server-info"
				event.getGuild().getChannels().filter(e -> e.getName().equals("server-chat"))
						.subscribe((c) -> chatChannels.add(c.getRestChannel()));
			});

			//!tps command
			client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					//bots can't do commands
					.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
					//if it's the right command
					.filter(message -> message.getContent().equalsIgnoreCase("!tps")).flatMap(Message::getChannel)
					.flatMap(channel -> channel
							.createMessage(
									messageSpec -> messageSpec.setEmbed(embedSpec ->
											embedSpec.setTitle("RVAS Server | Server TPS")
													.setAuthor("HappyDroid Bot", "https://avas.cc/", "https://avas.cc/favicon.png")
									 .setColor(Color.RUBY)
													.addField("Server Info",
															"TPS is currently " + new DecimalFormat("0.00").format(LagProcessor.getTPS()),
															false)
													.addField("\u200B", "\u200B", false)
													.addField("Current Player Count",
															new DecimalFormat("##").format(Bukkit.getOnlinePlayers().size()) + "/13",
															true)
													.addField("Server Up Time", Util.timeToString(ServerMeta.getUptime()), true)
									))).subscribe();

			//!facts commands
			client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					//cannot be run by bots
					.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
					//is the right command
					.filter(message -> message.getContent().equalsIgnoreCase("!facts"))
					//only on friday
					.filter(message -> Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
					//on the channels
					.flatMap(Message::getChannel)
					.flatMap(channel -> channel
					.createMessage(//facts[r.nextInt(facts.length)])
						messageSpec -> messageSpec.setEmbed(embedSpec -> {
							int fact_number = r.nextInt(facts.size());
									embedSpec.setTitle("RVAS Server | Fun Fact Friday")
											.setAuthor("HappyDroid Bot", "https://avas.cc/", "https://avas.cc/favicon.png")
											.setColor(Color.RUBY)
											.addField("Fun Fact " + fact_number + "/" + (facts.size() - 1), facts.get(fact_number), false)
											.addField("\u200B", "\u200B", false)
											.addField("Current Player Count",
													new DecimalFormat("##").format(Bukkit.getOnlinePlayers().size()) + "/20",
													true)
											.addField("Server Up Time", Util.timeToString(ServerMeta.getUptime()), true);
								}
						))).subscribe();
		});
	}

	/*
		USAGE EXAMPLE :
		addCommand(client,"!hello",(event)->{
				event.getMessage().getChannel().createMessage("Hi there !").subscribe();
			});

			<user> : !hello
			<bot> : Hi there!
	 */
	private static void addCommand(GatewayDiscordClient c, String name, Consumer<MessageCreateEvent> cons) {
		c.getEventDispatcher().on(MessageCreateEvent.class).filter((e) -> e.getMessage()
				.getContent().startsWith(name)).subscribe(cons);
	}

	private ArrayList<String> loadFacts() {
		ArrayList<String> fact = new ArrayList<>();

		Scanner scanner = new Scanner(
				Objects.requireNonNull(DiscordBot.class.getClassLoader().getResourceAsStream("facts.txt")));

		scanner.forEachRemaining(fact_line -> {
			if (!fact_line.trim().isEmpty())
				fact.add(fact_line);
		});

		return fact;
	}

	//handle chat messages to send them to discord
	//priority is "monitor" : should be ran LAST, so that muted messages won't get to discord
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		for (RestChannel c : chatChannels) {
			//TODO maybe some formatting ?
			c.createMessage("<" + e.getPlayer().getName() + "> : " + e.getMessage()).subscribe();
		}
	}
}