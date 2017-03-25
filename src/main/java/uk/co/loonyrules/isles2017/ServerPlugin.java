package uk.co.loonyrules.isles2017;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ServerPlugin extends JavaPlugin implements Listener
{

    /**
     * Where we store the {@link org.bukkit.entity.EntityType}'s that are associated with being a Homing
     * projectile.
     */
    private final Map<EntityType, Class<? extends HomingProjectile>> homingTypes = ImmutableMap.of(
            EntityType.ARROW, HomingArrow.class,
            EntityType.EGG, HomingEgg.class,
            EntityType.ENDER_PEARL, HomingEnderPearl.class
    );

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {

    }

    /**
     * Retrieve the class that inherits {@link uk.co.loonyrules.isles2017.HomingProjectile} for a specific
     * {@link org.bukkit.entity.EntityType}
     * @param entityType The {@link org.bukkit.entity.EntityType} to get the homing projectile class for
     * @return The class type
     */
    private Class<? extends HomingProjectile> getHomingClass(EntityType entityType)
    {
        return homingTypes.get(entityType);
    }

    @EventHandler
    public void onPlayerInteractEvent(ProjectileLaunchEvent event)
    {
        Projectile projectile = event.getEntity();

        // If it's not a homing type, lets do nothing
        Class<? extends HomingProjectile> extended = getHomingClass(projectile.getType());
        if(extended == null)
            return;

        // Create a new instance and then run the task timer.
        try {
            HomingProjectile homingProjectile1 = (HomingProjectile) extended.getConstructors()[0].newInstance(projectile);
            homingProjectile1.runTaskTimer(this, 1L, 1L);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}