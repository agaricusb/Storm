package com.github.StormTeam.Storm.Weather;

import com.github.StormTeam.Storm.ErrorLogger;
import com.github.StormTeam.Storm.Pair;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherAlreadyRegisteredException;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotAllowedException;
import com.github.StormTeam.Storm.Weather.Exceptions.WeatherNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Weather Manager for Storm. All members thread-safe unless documented.
 *
 * @author Icyene
 * @author xiaomao
 */
public class WeatherManager implements Listener {

    private final Map<String, Pair<Class<? extends StormWeather>, Map<String, StormWeather>>> registeredWeathers = new HashMap<String, Pair<Class<? extends StormWeather>, Map<String, StormWeather>>>();
    private final Map<String, Set<String>> activeWeather = new HashMap<String, Set<String>>();
    private final Map<String, Map<String, Pair<Integer, WeatherTrigger>>> weatherTriggers = new HashMap<String, Map<String, Pair<Integer, WeatherTrigger>>>();
    private final Storm storm;
    private boolean currentRain, currentThunder;
    private final Map<String, String> worldTextures = new HashMap<String, String>();
    private final Set<Method> onLoadMethods = new HashSet<Method>();

    public WeatherManager(Storm storm) {
        this.storm = storm;
    }

    /**
     * Registers a weather. Only registers the weather for the worlds specified
     * in worlds.
     *
     * @param weather Weather class
     * @param name    Weather name
     * @throws WeatherAlreadyRegisteredException
     *
     */
    public void registerWeather(Class<? extends StormWeather> weather, String name) throws WeatherAlreadyRegisteredException {
        synchronized (this) {
            if (registeredWeathers.containsKey(name)) {
                throw new WeatherAlreadyRegisteredException(String.format("Weather %s is already registered", name));
            }
            try {
                Map<String, StormWeather> instances = new HashMap<String, StormWeather>();
                Map<String, Pair<Integer, WeatherTrigger>> triggers = new HashMap<String, Pair<Integer, WeatherTrigger>>();
                weatherTriggers.put(name, triggers);
                registeredWeathers.put(name, new Pair<Class<? extends StormWeather>, Map<String, StormWeather>>(weather, instances));
            } catch (Exception e) {
                ErrorLogger.generateErrorLog(e);
            }
        }
    }

    /**
     * Initialize a weather for a world.
     *
     * @param name          Weather name.
     * @param world         World name.
     * @param chance        Chance of occurring, in percent, i.e. probability* 100.
     * @param recalculation Ticks before trying to start with chance.
     * @param args          Arguments to weather constructor.
     */
    public void enableWeatherForWorld(String name, String world, int chance, int recalculation, Object... args) throws WeatherNotFoundException {
        synchronized (this) {
            if (!registeredWeathers.containsKey(name)) {
                throw new WeatherNotFoundException(String.format("Weather %s not found", name));
            }
            Map<String, StormWeather> instances = registeredWeathers.get(name).RIGHT;
            Map<String, Pair<Integer, WeatherTrigger>> triggers = weatherTriggers.get(name);
            Class<? extends StormWeather> weather = registeredWeathers.get(name).LEFT;

            Class[] classes = new Class[args.length + 2];
            classes[0] = Storm.class;
            classes[1] = String.class;
            for (int i = 2; i < classes.length; ++i)
                classes[i] = args[i].getClass();

            Object[] arguments = new Object[args.length + 2];
            arguments[0] = storm;
            arguments[1] = world;
            System.arraycopy(args, 2, arguments, 2, arguments.length - 2);
            try {
                instances.put(world, weather.getConstructor(classes).newInstance(arguments));
                WeatherTrigger trigger = new WeatherTrigger(this, name, world, chance);
                int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(storm, trigger, recalculation, recalculation);
                triggers.put(world, new Pair<Integer, WeatherTrigger>(id, trigger));
            } catch (Exception e) {
                ErrorLogger.generateErrorLog(e);
            }
        }
    }

    /**
     * Uninitialize a weather on a world.
     *
     * @param name  weather name
     * @param world world name
     * @throws WeatherNotFoundException
     */
    public void disableWeatherForWorld(String name, String world) throws WeatherNotFoundException {
        synchronized (this) {
            stopWeatherReal(name, Arrays.asList(world));
            Map<String, StormWeather> instances = registeredWeathers.get(name).RIGHT;
            Map<String, Pair<Integer, WeatherTrigger>> triggers = weatherTriggers.get(name);
            instances.remove(world);
            Bukkit.getScheduler().cancelTask(triggers.get(world).LEFT);
            triggers.remove(world);
        }
    }

    /**
     * Gets all active weathers on world.
     *
     * @param world world name as String
     * @return A newly constructed Set<String> containing the active weathers
     */
    public Set<String> getActiveWeathers(String world) {
        synchronized (this) {
            return Collections.unmodifiableSet(getActiveWeathersReal(world));
        }
    }

    /**
     * A getter function for data member activeWeather, with on demand
     * construction.
     *
     * @param world World name
     * @return A Set<String> containing the active weathers.
     */
    protected Set<String> getActiveWeathersReal(String world) {
        if (!activeWeather.containsKey(world)) {
            activeWeather.put(world, new HashSet<String>());
        }
        return activeWeather.get(world);
    }

    /**
     * Gets all active weathers on world.
     *
     * @param world world object
     * @return A newly constructed Set<String> containing the active weathers
     */
    public Set<String> getActiveWeathers(World world) {
        return getActiveWeathers(world.getName());
    }

    /**
     * Determines if a weather is registered.
     *
     * @param weather weather name
     * @return whether the weather is registered
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isWeatherRegistered(String weather) {
        synchronized (this) {
            return !registeredWeathers.containsKey(weather);
        }
    }

    public StormWeather getSampleInstance(String weather) {
        return registeredWeathers.get(weather).RIGHT.entrySet().iterator().next().getValue();
    }

    public void registerWorldLoadHandler(Method method) {
        if (!method.isAccessible())
            method.setAccessible(true);
        onLoadMethods.add(method);
    }

    private boolean isConflictingWeatherOneWay(String w1, String w2) {
        StormWeather sampleInstance = getSampleInstance(w1);
        Method getConflicts;
        Set<String> conflicts;
        try {
            getConflicts = sampleInstance.getClass().getDeclaredMethod("getConflicts");
            //noinspection unchecked
            conflicts = (Set<String>) getConflicts.invoke(sampleInstance);
        } catch (IllegalAccessException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
        return conflicts.contains(w2);
    }

    protected void controlMinecraftFlags(String world) {
        try {
            Field needRain = StormWeather.class.getDeclaredField("needRainFlag");
            Field needThunder = StormWeather.class.getDeclaredField("needThunderFlag");
            boolean rain = false, thunder = false;
            for (String weather : getActiveWeathersReal(world)) {
                // System.out.print
                StormWeather sample = registeredWeathers.get(weather).RIGHT.get(world);
                if (needRain.getBoolean(sample)) {
                    rain = true;
                }
                if (needThunder.getBoolean(sample)) {
                    thunder = true;
                }
            }
            if (currentRain != rain) {
                StormUtil.setRainNoEvent(Bukkit.getWorld(world), rain);
                currentRain = rain;
            }
            if (currentThunder != thunder) {
                StormUtil.setThunderNoEvent(Bukkit.getWorld(world), thunder);
                currentThunder = thunder;
            }
        } catch (Exception e) {
            ErrorLogger.generateErrorLog(e);
        }
    }

    /**
     * Determines if a weather conflicts with another. If one weather reports
     * itself to be conflicting with another, they two are considered
     * conflicting.
     *
     * @param w1 weather #1
     * @param w2 weather #2
     * @return whether the two are conflicting
     */
    public boolean isConflictingWeather(String w1, String w2) {
        synchronized (this) {
            try {
                return !(isWeatherRegistered(w1) || isWeatherRegistered(w2)) && (isConflictingWeatherOneWay(w1, w2) || isConflictingWeatherOneWay(w2, w1));
            } catch (Exception e) {
                ErrorLogger.generateErrorLog(e);
                return false;
            }
        }
    }

    /**
     * Starts a weather on a single world with conflict checking.
     *
     * @param name  weather name
     * @param world world name
     * @return whether the weather is actually started
     * @throws WeatherNotFoundException
     * @throws WeatherNotAllowedException
     */
    public boolean startWeather(String name, String world) throws WeatherNotFoundException, WeatherNotAllowedException {
        Set<String> started = startWeather(name, Arrays.asList(world));
        return started.isEmpty();
    }

    /**
     * Starts a world on a collection of worlds with conflict checking.
     *
     * @param name    Weather name
     * @param worlds_ Collection of worlds
     * @return A set of world the weather is actually started on
     * @throws WeatherNotFoundException
     * @throws WeatherNotAllowedException
     */
    public Set<String> startWeather(String name, Collection<String> worlds_) throws WeatherNotFoundException, WeatherNotAllowedException {
        synchronized (this) {
            Set<String> worlds = new HashSet<String>(worlds_);
            for (String world : worlds) {
                for (String weather : getActiveWeathersReal(world)) {
                    if (isConflictingWeather(name, weather)) {
                        worlds.remove(world);
                        break;
                    }
                }
            }
            startWeatherReal(name, worlds);
            return worlds;
        }
    }

    /**
     * Starts a weather on a single world *without* conflict checking.
     *
     * @param name  weather name
     * @param world world name
     * @throws WeatherNotFoundException
     * @throws WeatherNotAllowedException
     */
    public void startWeatherForce(String name, String world) throws WeatherNotFoundException, WeatherNotAllowedException {
        synchronized (this) {
            startWeatherReal(name, Arrays.asList(world));
        }
    }

    /**
     * Starts a world on a collection of worlds *without* conflict checking.
     *
     * @param name Weather name
     * @throws WeatherNotFoundException
     * @throws WeatherNotAllowedException
     */
    public void startWeatherForce(String name, Collection<String> worlds) throws WeatherNotFoundException, WeatherNotAllowedException {
        synchronized (this) {
            startWeatherReal(name, worlds);
        }
    }

    /**
     * Starts a weather without locking or conflict checking.
     *
     * @param name   Weather name
     * @param worlds Collection of worlds
     * @throws WeatherNotFoundException
     * @throws WeatherNotAllowedException
     */
    protected void startWeatherReal(String name, Collection<String> worlds) throws WeatherNotFoundException, WeatherNotAllowedException {
        Pair<Class<? extends StormWeather>, Map<String, StormWeather>> weatherData = registeredWeathers.get(name);
        if (weatherData == null) {
            throw new WeatherNotFoundException(String.format("Weather %s not found", name));
        }
        for (String world : worlds) {
            StormWeather weather = weatherData.RIGHT.get(world);
            if (weather == null) {
                throw new WeatherNotAllowedException(String.format("Weather %s not allowed in %s", name, world));
            }
            if (!getActiveWeathersReal(world).contains(name) && weather.canStart()) {
                WeatherEvent startEvent = new WeatherEvent(Bukkit.getWorld(world), true, name);
                Storm.pm.callEvent(startEvent);

                if (startEvent.isCancelled()) {
                    return;
                }

                weather.start();
                String texture = weather.getTexture();
                if (texture != null) {
                    for (Player player : Bukkit.getWorld(world).getPlayers()) {
                        StormUtil.setTexture(player, texture);
                    }
                    worldTextures.put(world, texture);
                }
                getActiveWeathersReal(world).add(name);
                controlMinecraftFlags(world);

                if (weather.autoKillTicks >= 0)
                    createAutoKillWeatherTask(name, world, weather.autoKillTicks);
            }
        }
    }

    /**
     * Stops a weather on a single world.
     *
     * @param name  weather name
     * @param world world name
     * @throws WeatherNotFoundException
     */
    public void stopWeather(String name, String world) throws WeatherNotFoundException {
        synchronized (this) {
            stopWeatherReal(name, Arrays.asList(world));
        }
    }

    /**
     * Stops a world on a collection of worlds.
     *
     * @param name Weather name
     * @throws WeatherNotFoundException
     */
    void stopWeather(String name, Collection<String> worlds) throws WeatherNotFoundException {
        synchronized (this) {
            stopWeatherReal(name, worlds);
        }
    }

    protected void stopWeatherReal(String name, Collection<String> worlds) throws WeatherNotFoundException {
        Pair<Class<? extends StormWeather>, Map<String, StormWeather>> weatherData = registeredWeathers.get(name);
        if (weatherData == null) {
            throw new WeatherNotFoundException(String.format("Weather %s not found", name));
        }
        for (String world : worlds) {
            StormWeather weather = weatherData.RIGHT.get(world);
            if (weather == null) {
                continue;
            }
            if (getActiveWeathersReal(world).contains(name)) {

                WeatherEvent endEvent = new WeatherEvent(Bukkit.getWorld(world), false, name);
                Storm.pm.callEvent(endEvent);

                if (endEvent.isCancelled()) {
                    return;
                }

                weather.end();
                String texture = weather.getTexture();
                if (texture != null) {
                    for (Player player : Bukkit.getWorld(world).getPlayers()) {
                        StormUtil.clearTexture(player);
                    }
                    worldTextures.put(world, null);
                }
                getActiveWeathersReal(world).remove(name);
                controlMinecraftFlags(world);
            }
        }
    }

    public int createAutoKillWeatherTask(final String name, final String world, int time) {
        return Bukkit.getScheduler().scheduleAsyncDelayedTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            stopWeather(name, world);
                        } catch (Exception ex) {
                            ErrorLogger.generateErrorLog(ex);
                        }
                    }
                }, time);
    }

    @EventHandler
    public void worldLoad(WorldLoadEvent e) {
        for (Method method : onLoadMethods) {
            try {
                method.invoke(null, e.getWorld());
            } catch (Exception ex) {
                ErrorLogger.generateErrorLog(ex);
            }
        }
    }

    /**
     * Event Handler to set appropriate texture for world when player switched
     * worlds.
     *
     * @param e Event object
     */
    @EventHandler
    public void worldEvent(PlayerChangedWorldEvent e) {
        Player hopper = e.getPlayer();
        World target = hopper.getWorld(), source = e.getFrom();

        if (target.equals(source)) {
            return;
        }

        final String texture;
        synchronized (this) {
            texture = worldTextures.get(target.getName());
        }
        if (texture == null) {
            StormUtil.clearTexture(hopper);
        } else {
            StormUtil.setTexture(hopper, texture);
        }
    }

    /**
     * Event Handler to set appropriate texture for the weather in the world the
     * player just logged on to.
     *
     * @param e Event object
     */
    @EventHandler
    public void loginEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        World world = player.getWorld();
        String texture;

        synchronized (this) {
            texture = worldTextures.get(world.getName());
        }
        if (texture != null) {
            StormUtil.setTexture(player, texture);
        }
    }

    @EventHandler
    public void worldUnloadEvent(WorldUnloadEvent event) {
        String world = event.getWorld().getName();
        List<String> worlds = Arrays.asList(world);
        try {
            synchronized (this) {
                for (String weather : getActiveWeathersReal(world)) {
                    stopWeatherReal(weather, worlds);
                }
            }
        } catch (WeatherNotFoundException ignored) {
        }
    }

    @EventHandler
    public void weatherChangeEvent(WeatherChangeEvent event) {
        if (!event.toWeatherState()) {
            String world = event.getWorld().getName();
            List<String> worlds = Arrays.asList(world);
            synchronized (this) {
                for (String weather : new HashSet<String>(getActiveWeathersReal(world))) {
                    try {
                        stopWeatherReal(weather, worlds);
                    } catch (WeatherNotFoundException ex) {
                        ErrorLogger.generateErrorLog(ex);
                    }
                }
            }
        }
    }
}
