package com.cardshifter.server.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.zomis.cardshifter.ecs.usage.PhrancisGame;
import net.zomis.cardshifter.ecs.usage.PhrancisGameNewAttackSystem;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cardshifter.ai.AIs;
import com.cardshifter.ai.ScoringAI;
import com.cardshifter.api.CardshifterConstants;
import com.cardshifter.api.both.ChatMessage;
import com.cardshifter.api.incoming.LoginMessage;
import com.cardshifter.api.incoming.StartGameRequest;
import com.cardshifter.modapi.ai.CardshifterAI;
import com.cardshifter.server.commands.AICommand;
import com.cardshifter.server.commands.AICommand.AICommandParameters;
import com.cardshifter.server.commands.CommandContext;
import com.cardshifter.server.commands.EntityCommand;
import com.cardshifter.server.commands.EntityCommand.EntityInspectParameters;
import com.cardshifter.server.commands.HelpCommand;
import com.cardshifter.server.commands.HelpCommand.HelpParameters;
import com.cardshifter.server.main.FakeAIClientTCG;
import com.cardshifter.server.utils.export.DataExporter;

/**
 *Starts the Server object, sets up the AIs and GameFactories, and controls other functions of Server
 * 
 * @author Simon Forsberg
 */
public class MainServer {
	
	private static final Logger logger = LogManager.getLogger(MainServer.class);
	
	/**
	 * Server handles incoming messages and passes them to appropriate methods
	 */
	private final Server server = new Server();
	private final Map<String, CardshifterAI> ais = new LinkedHashMap<>();

	private Thread consoleThread;
	
	/**
	 * Adds connections, AIs, and GameFactories to the Server, and starts the ServerConsole.
	 * CommandHandler is a reference to the CommandHandler in Server
	 * 
	 * @return The configured Server object
	 */
	public Server start() {
		ais.put("Loser", new ScoringAI(AIs.loser()));
		ais.put("Idiot", new ScoringAI(AIs.idiot()));
		ais.put("Medium", new ScoringAI(AIs.medium()));
		ais.put("Fighter", new ScoringAI(AIs.fighter()));
		
		try {
			logger.info("Starting Server...");
			
			server.addConnections(new ServerSock(server, 4242));
			server.addConnections(new ServerWeb(server, 4243));
			
			logger.info("Starting Console...");
			CommandHandler commandHandler = server.getCommandHandler();
			initializeCommands(commandHandler);
			ServerConsole console = new ServerConsole(server, commandHandler);
			consoleThread = new Thread(console, "Console-Thread");
			consoleThread.start();
			
			ais.entrySet().forEach(entry -> {
				ClientIO tcgAI = new FakeAIClientTCG(server, entry.getValue());
				server.newClient(tcgAI);
				server.getIncomingHandler().perform(new LoginMessage("AI " + entry.getKey()), tcgAI);
			});
			
			server.addGameFactory(CardshifterConstants.VANILLA, (serv, id) -> new TCGGame(serv, id, new PhrancisGame()));
			server.addGameFactory("New Attack Style", (serv, id) -> new TCGGame(serv, id, new PhrancisGameNewAttackSystem()));
//			server.addGameFactory("SIMPLE", (serv, id) -> new TCGGame(serv, id, new SimpleGame()));
			
			logger.info("Started");
		}
		catch (Exception e) {
			logger.error("Initializing Error", e);
		}
		return server;
	}
	
	/**
	 * Adds specific commands to the CommandHandler such as "exit" and "chat" and attaches them to various methods
	 * 
	 * @param commandHandler The command handler that commands will be added to
	 */
	private void initializeCommands(CommandHandler commandHandler) {
		commandHandler.addHandler("exit", () -> new Object(), this::shutdown);
		commandHandler.addHandler("help", () -> new HelpParameters(), new HelpCommand(commandHandler));
		commandHandler.addHandler("export", this::export);
		commandHandler.addHandler("users", this::users);
		commandHandler.addHandler("play", this::play);
		commandHandler.addHandler("say", this::say);
		commandHandler.addHandler("chat", this::chatInfo);
		commandHandler.addHandler("games", this::showGames);
		commandHandler.addHandler("invites", this::showInvites);
		commandHandler.addHandler("ai", () -> new AICommandParameters(), new AICommand());
		commandHandler.addHandler("ent", () -> new EntityInspectParameters(), new EntityCommand());
		commandHandler.addHandler("threads", cmd -> showAllStackTraces(server, System.out::println));
	}
	
	/**
	 * Prints out the game invites of the Server
	 * 
	 * @param command The command object
	 */
	private void showInvites(Command command) {
		CommandContext context = new CommandContext(server, command, command.getSender());
		for (Entry<Integer, GameInvite> ee : server.getInvites().all().entrySet()) {
			context.sendChatResponse(ee.getKey() + " = " + ee.getValue());
		}
	}
	
	/**
	 * Prints out the current games of the Server
	 * 
	 * @param command The command object
	 */
	private void showGames(Command command) {
		CommandContext context = new CommandContext(server, command, command.getSender());
		for (Entry<Integer, ServerGame> ee : server.getGames().entrySet()) {
			context.sendChatResponse(ee.getKey() + " = " + ee.getValue());
		}
	}
	
	/**
	 * Sends a chat message to the master chat of the Server
	 * 
	 * @param command The command object
	 */
	private void say(Command command) {
		ChatArea chat = server.getMainChat();
		chat.broadcast(new ChatMessage(chat.getId(), "Server", command.getFullCommand(1)));
	}
	
	/**
	 * Either prints out all of the chats, or the users currently in the master chat
	 * 
	 * @param command The command object
	 */
	private void chatInfo(Command command) {
		int chatId = command.getParameterInt(1);
		CommandContext context = new CommandContext(server, command, command.getSender());
		if (chatId == 0) {
			context.sendChatResponse(server.getChats().keySet().toString());
		}
		else {
			ChatArea chat = server.getChats().get(chatId);
			context.sendChatResponse(chat.getUsers().toString());
		}
	}
	
	/**
	 * Stops the Server and consoleThread, and shows all stack traces for Server
	 * 
	 * @param command A command object with the Server and ClientIO also (unused)
	 * @param parameters Unused parameter
	 */
	private void shutdown(CommandContext command, Object parameters) {
		server.stop();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.out.println("Interrupted when shutting down");
		}
		
		showAllStackTraces(server, System.out::println);
		consoleThread.interrupt();
	}
	
	/**
	 * Print out the clients
	 * 
	 * @param command The command object
	 */
	private void users(Command command) {
		server.getClients().values().forEach(cl -> System.out.println(cl));
	}
	
	/**
	 * Get the client that sent the command, perform a StartGameRequest. 
	 * Right now this only sends CardshifterConstants.VANILLA
	 * 
	 * @param command The command object
	 */
	private void play(Command command) {
		int userId = command.getParameterInt(1);
		ClientIO client = server.getClients().get(userId);
		server.getIncomingHandler().perform(new StartGameRequest(-1, CardshifterConstants.VANILLA), client);
	}
	
	/**
	 * Send createGame to the Server for CardshifterConstants.VANILLA. 
	 * Exports the Server along with the full command
	 * 
	 * @param command The command object
	 */
	private void export(Command command) {
		server.createGame(CardshifterConstants.VANILLA);
		DataExporter exporter = new DataExporter();
		exporter.export(server, command.getAllParameters());
	}
	
	/**
	 * The Consumer accepts all stack traces for all threads
	 * 
	 * @param server Unused parameter
	 * @param output Receives all stack trace strings
	 */
	private void showAllStackTraces(Server server, Consumer<String> output) {
		output.accept("All stack traces:");
		Map<Thread, StackTraceElement[]> allTraces = Thread.getAllStackTraces();
		for (Thread thread : allTraces.keySet()) {
			output.accept(thread.getName());
			this.stackTrace(thread, output);
		}
	}
	
	/**
	 * Convert the stack traces to strings and send them to the output
	 * 
	 * @param thread Thread to get a stack trace for
	 * @param output Where the stack traces are sent
	 */
	private void stackTrace(Thread thread, Consumer<String> output) {
		StackTraceElement[] stackTrace = thread.getStackTrace();
		output.accept("Stack trace for thread " + thread.getId() + ": " + thread.getName());
		for (StackTraceElement trace : stackTrace) {
			output.accept(trace.toString());
		}
		output.accept("");
	}
	
}
