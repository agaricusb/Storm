package com.github.StormTeam.Storm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class CommandRegistration {

    protected final Plugin plugin;
    protected final CommandExecutor executor;

    public CommandRegistration(Plugin plugin) {
        this(plugin, plugin);
    }

    public CommandRegistration(Plugin plugin, CommandExecutor executor) {
        this.plugin = plugin;
        this.executor = executor;
    }

    public boolean register(String name, String desc, String[] aliases, String usage, String[] perms, String permMessage) {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            return false;
        }
        DynamicCommand cmd = new DynamicCommand(aliases, desc, "/" + aliases[0] + " " + usage, executor, plugin, plugin);

        cmd.setPermissions(perms);
        cmd.setPermissionMessage(permMessage);
        commandMap.register(plugin.getDescription().getName(), cmd);

        return true;
    }

    public CommandMap getCommandMap() {
        Field map;
        try {
            map = plugin.getServer().getPluginManager().getClass().getField("commandMap");
            map.setAccessible(true);
            return (CommandMap) map.get(plugin.getServer().getPluginManager());
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
            return null;
        }
    }

    public boolean unregisterCommands() {
        CommandMap commandMap = getCommandMap();
        List<String> toRemove = new ArrayList<String>();
        Map<String, org.bukkit.command.Command> knownCommands = new HashMap<String, Command>();
        Set<String> aliases = new HashSet<String>();
        try {
            knownCommands = (Map<String, Command>) commandMap.getClass().getField("knownCommands").get(commandMap);
            aliases = (Set<String>) commandMap.getClass().getField("aliases").get(commandMap);
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
        if (knownCommands == null || aliases == null) {
            return false;
        }
        for (Iterator<org.bukkit.command.Command> i = knownCommands.values().iterator(); i.hasNext(); ) {
            org.bukkit.command.Command cmd = i.next();
            if (cmd instanceof DynamicCommand && ((DynamicCommand) cmd).getOwner().equals(executor)) {
                i.remove();
                for (String alias : cmd.getAliases()) {
                    org.bukkit.command.Command aliasCmd = knownCommands.get(alias);
                    if (cmd.equals(aliasCmd)) {
                        aliases.remove(alias);
                        toRemove.add(alias);
                    }
                }
            }
        }
        for (String string : toRemove) {
            knownCommands.remove(string);
        }
        return true;
    }
}