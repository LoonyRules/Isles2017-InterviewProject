package uk.co.loonyrules.isles2017;

import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

public class HomingEgg extends HomingProjectile
{

    public HomingEgg(Egg egg)
    {
        super(egg, 20 * 5);
        this.range = 20;
    }

    /**
     * Overriding the isValidTarget method to check if the target is an instance of a {@link org.bukkit.entity.Monster}
     * @param livingEntity The {@link org.bukkit.entity.LivingEntity} to check check for
     * @return True if the target is valid
     */
    @Override
    public boolean isValidTarget(LivingEntity livingEntity)
    {
        return livingEntity instanceof Monster && super.isValidTarget(livingEntity);
    }

}