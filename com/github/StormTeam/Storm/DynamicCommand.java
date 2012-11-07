package com.github.StormTeam.Storm;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class DynamicCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {

    protected final CommandExecutor owner;
    protected final Object registeredWith;
    protected final Plugin owningPlugin;
    protected String[] permissions = new String[0];

    public DynamicCommand(String[] aliases, String desc, String usage, CommandExecutor owner, Object registeredWith, Plugin plugin) {
        super(aliases[0], desc, usage, Arrays.asList(aliases));
        this.owner = owner;
        this.owningPlugin = plugin;
        this.registeredWith = registeredWith;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return owner.onCommand(sender, this, label, args);
    }

    public Object getOwner() {
        return owner;
    }

    public Object getRegisteredWith() {
        return registeredWith;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
        if (permissions != null) {
            super.setPermission(joinString(permissions, ";", 0));
        }
    }

    private static String joinString(String[] str, String delimiter, int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex]);
        for (int i = initialIndex + 1; i < str.length; ++i) {
            buffer.append(delimiter).append(str[i]);
        }
        return buffer.toString();
    }

    public String[] getPermissions() {
        return permissions;
    }

    @Override
    public Plugin getPlugin() {
        return owningPlugin;
    }
}