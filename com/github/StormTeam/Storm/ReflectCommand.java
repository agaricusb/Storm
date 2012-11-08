package com.github.StormTeam.Storm;

import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectCommand {

    private HashMap<String, Set<Method>> everyoneCommands = new HashMap(), playerCommands = new HashMap(), consoleCommands = new HashMap();
    private CommandExecutor executor;
    private Plugin plugin;

    public ReflectCommand(Plugin plug) {
        plugin = plug;
        executor = new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {
                Object retval = false;
                String commandName = command.getName();
                Object[] varargs = prepend(args, commandSender);
                try {
                    if (everyoneCommands.containsKey(commandName)) {
                        for (Method m : everyoneCommands.get(command.getName()))
                            if (varargs.length >= m.getParameterTypes().length)
                                retval = m.invoke(null, trim(varargs, m.getParameterTypes().length));
                    } else if (commandSender instanceof Player) {
                        if (playerCommands.containsKey(commandName))
                            for (Method m : playerCommands.get(commandName))
                                if (varargs.length >= m.getParameterTypes().length)
                                    retval = m.invoke(null, trim(varargs, m.getParameterTypes().length));
                    } else if (commandSender instanceof ConsoleCommandSender) {
                        if (consoleCommands.containsKey(commandName))
                            for (Method m : consoleCommands.get(commandName))
                                if (varargs.length >= m.getParameterTypes().length)
                                    retval = m.invoke(null, trim(varargs, m.getParameterTypes().length));
                    }
                    return (Boolean) retval;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }

    public void register(Class clazz) {
        Registrator register = new Registrator(plugin, executor);
        for (Method m : clazz.getDeclaredMethods()) {
            Command com;
            if ((com = m.getAnnotation(Command.class)) != null) {
                String name = com.name();
                List<String> alias = new ArrayList<String>();
                if (!ArrayUtils.isEmpty(com.alias()))
                    alias = Arrays.asList(com.alias());
                alias.add(com.name());
                register.register(com.name(), alias.toArray(new String[alias.size()]), com.usage(), com.permission(), com.permissionMessage());
                HashMap<String, Set<Method>> hm;
                try {
                    Field map = this.getClass().getDeclaredField(com.sender().name().toLowerCase() + "Commands");
                    hm = (HashMap) map.get(this);
                    if (hm.containsKey(name))
                        hm.get(m.getName()).add(m);
                    else
                        hm.put(name, Sets.newHashSet(m));
                    map.set(this, hm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Object[] prepend(Object[] arr, Object firstElement) {
        List<Object> pre = new ArrayList<Object>();
        pre.add(firstElement);
        for (Object ob : arr)
            pre.add(ob);
        return pre.toArray(new Object[0]);
    }


    Object[] trim(Object[] input, int newsize) {
        Object result[] = new Object[newsize];
        for (int i = 0; i < newsize; i++) result[i] = input[i];
        return result;
    }

    public class Registrator {

        protected final Plugin plugin;
        protected final CommandExecutor executor;

        public Registrator(Plugin plugin, CommandExecutor executor) {
            this.plugin = plugin;
            this.executor = executor;
        }

        public void register(String name, String[] aliases, String usage, String[] perms, String permMessage) {
            getCommandMap().register(plugin.getDescription().getName(), new DynamicCommand(aliases, name, "Error! Correct usage is: /" + aliases[0] + " " + usage.replace("<command>", name), perms, permMessage, executor, plugin, plugin));
        }

        public CommandMap getCommandMap() {
            Field map;
            try {
                map = SimplePluginManager.class.getDeclaredField("commandMap");
                map.setAccessible(true);
                return (CommandMap) map.get(plugin.getServer().getPluginManager());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DynamicCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {

        public CommandExecutor owner;
        public Object registeredWith;
        public Plugin owningPlugin;
        public String[] permissions = new String[0];

        public DynamicCommand(String[] aliases, String name, String usage, String[] perms, String permMessage, CommandExecutor owner, Object registeredWith, Plugin plugin) {
            super(name, name, usage, Arrays.asList(aliases));
            this.owner = owner;
            this.owningPlugin = plugin;
            this.registeredWith = registeredWith;
            if (perms.length > 0)
                setPermissions(perms);
            if (!StringUtils.isEmpty(permMessage))
                setPermissionMessage(ChatColor.RED + permMessage);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            return owner.onCommand(sender, this, label, args);
        }

        public void setPermissions(String[] permissions) {
            this.permissions = permissions;
            super.setPermission(StringUtils.join(permissions, ";"));
        }

        @Override
        public Plugin getPlugin() {
            return owningPlugin;
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {

        public String name();

        public String usage() default "/<command>";

        public String[] alias() default {};

        public Sender sender() default Sender.PLAYER;

        public String[] permission() default "";

        public String permissionMessage() default "You do not have the permission to execute this command!";
    }

    public enum Sender {
        CONSOLE(new Class[]{ConsoleCommandSender.class}),
        PLAYER(new Class[]{Player.class}),
        EVERYONE(new Class[]{ConsoleCommandSender.class, Player.class});

        Sender(Class[] who) {
        }
    }
}