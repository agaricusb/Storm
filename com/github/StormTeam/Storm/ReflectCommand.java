package com.github.StormTeam.Storm;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public class ReflectCommand {

    public HashMap<String, Set<Method>> everyoneCommands = new HashMap();
    public HashMap<String, Set<Method>> consoleCommands = new HashMap();
    public HashMap<String, Set<Method>> playerCommands = new HashMap();
    public CommandExecutor executor;
    public Plugin plugin;

    public ReflectCommand(Plugin plug) {
        plugin = plug;
        executor = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

                if (commandSender instanceof Player) {
                    if (playerCommands.containsKey(command.getName())) {
                        try {
                            for (Method m : playerCommands.get(command.getName())) {
                                return (Boolean) m.invoke(null, commandSender, args);
                            }
                        } catch (Exception ignored) {
                        } //This exception is thrown if the command is "overloaded"
                    }
                }
                if (commandSender instanceof ConsoleCommandSender) {
                    if (consoleCommands.containsKey(command.getName())) {
                        try {
                            for (Method m : consoleCommands.get(command.getName())) {
                                return (Boolean) m.invoke(null, commandSender, args);
                            }
                        } catch (Exception ignored) {
                        } //This exception is thrown if the command is "overloaded"
                    }
                }

                if (everyoneCommands.containsKey(command.getName())) {
                    try {
                        for (Method m : everyoneCommands.get(command.getName())) {
                            return (Boolean) m.invoke(null, commandSender, args);
                        }
                    } catch (Exception ignored) {
                    } //This exception is thrown if the command is "overloaded"
                }

                return false;
            }
        };
    }

    public void register(Class clazz) {
        CommandRegistration register = new CommandRegistration(plugin, executor);
        for (Method m : clazz.getMethods()) {
            Command com = null;
            m.getAnnotation(Command.class);
            if (com != null) {
                //String name, String desc, String[] aliases, String usage, String[] perms
                register.register(com.name(), com.description(), com.alias(), com.usage(), com.permission(), com.permissionMessage());
            }
        }
    }

    //****Test Commands****
    @Command(name = "acidrain", sender = Command.Sender.EVERYONE, permission = "storm.acidrain.command")
    public boolean acidRain(ConsoleCommandSender sender, String world, int duration) {
        return false;
    }

    @Command(name = "acidrain", permission = "storm.acidrain.command")
    public boolean acidrain(Player sender, int duration) {
        return false;
    }


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {

        public String name();

        public String description() default "This command has no description. Sorry!";

        public String usage() default "/<command>";

        public String[] alias() default {};

        public Sender sender() default Sender.PLAYER;

        public String[] permission();

        public String help() default "Sowwy, but there is no help provided for this command!";

        public String permissionMessage() default ChatColor.RED + "You do not have the permission to execute this command!";

        public enum Sender {
            CONSOLE(new Class[]{ConsoleCommandSender.class}),
            PLAYER(new Class[]{Player.class}),
            EVERYONE(new Class[]{ConsoleCommandSender.class, Player.class});

            Sender(Class[] who) {
            }
        }
    }
}
