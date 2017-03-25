package uk.co.loonyrules.isles2017;

import org.bukkit.entity.Arrow;

public class HomingArrow extends HomingProjectile
{

    public HomingArrow(Arrow arrow)
    {
        super(arrow, 20 * 3);
    }

}