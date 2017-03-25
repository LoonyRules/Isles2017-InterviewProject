package uk.co.loonyrules.isles2017;

import org.bukkit.entity.EnderPearl;

public class HomingEnderPearl extends HomingProjectile
{

    public HomingEnderPearl(EnderPearl enderPearl)
    {
        super(enderPearl, 0);
        this.range = 7.5;
    }

    /**
     * Overriding the speed of the {@link org.bukkit.entity.EnderPearl} so that it slows down when within a specific
     * range of the target.
     * @param speed The current speed of the EnderPearl
     * @return The new speed of the EnderPearl
     */
    @Override
    public double speed(double speed)
    {
        return speed * 0.5D;
    }

}
