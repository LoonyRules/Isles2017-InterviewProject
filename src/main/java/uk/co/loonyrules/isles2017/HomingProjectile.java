package uk.co.loonyrules.isles2017;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Optional;

abstract class HomingProjectile extends BukkitRunnable
{

    private final Projectile projectile;
    private final LivingEntity source;
    private final long allowedTargetTicks;

    public double range = 5.0D;
    private double cutOff = 45.0;
    private LivingEntity lastTarget;
    private int targettedTicks = 0;

    HomingProjectile(Projectile projectile, long allowedTargetTicks)
    {
        this.projectile = projectile;
        this.source = (LivingEntity) this.projectile.getShooter();
        this.allowedTargetTicks = allowedTargetTicks;
    }

    public Projectile getProjectile()
    {
        return projectile;
    }

    private boolean isValid()
    {
        boolean isValid = (projectile.isValid() && !projectile.isOnGround()) && (source != null && !source.isDead());

        if(!isValid)
            cancel();

        return isValid;
    }

    boolean isValidTarget(LivingEntity livingEntity)
    {
        return !livingEntity.getUniqueId().toString().equals(this.source.getUniqueId().toString());
    }

    @Override
    public void run()
    {
        System.out.println(getClass().getSimpleName());

        // If the projectile (or the shooter) is no longer valid, lets cancel
        if(!isValid())
        {
            System.out.println(" cancelled homing " + projectile.getType());

            projectile.remove();
            cancel();
            return;
        }

        System.out.println(" other");

        if(lastTarget == null)
        {
            System.out.println(" finding new target");
            // Getting closest allowed entity
            Optional<LivingEntity> entityOptional = projectile.getNearbyEntities(range, range, range).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .map(entity -> (LivingEntity) entity)
                    .filter(this::isValidTarget)
                    .findFirst();

            // if not present, do nothing this tick
            if(!entityOptional.isPresent())
            {
                System.out.println("  no target :(");
                return;
            }

            // Get the target and assign lastTarget data
            LivingEntity target = entityOptional.get();
            if(lastTarget != target)
            {
                System.out.println("  REASSIGNING");
                lastTarget = target;
                targettedTicks = 0;
            }
        }

        // Increase the number of ticks we've tracked for
        targettedTicks++;

        /*
         * This block comment marks the starting point of crediting where credit is due.
         * The following algorithm was taken from SethBling's plugin "BlingHomingArrows".
         *
         * It has been modified a little to further improve accuracy.
         *
         * No copyright infringement, or plagiarism was intended.
         */
        Vector directionVelocity = projectile.getVelocity().clone().normalize(),
                directionToTarget = lastTarget.getLocation().clone().subtract(projectile.getLocation()).toVector().clone().normalize();

        double angle = directionVelocity.angle(directionToTarget),
                speed = this.speed(projectile.getVelocity().length()),
                newSpeed = this.newSpeed(speed);

        if(angle >= cutOff)
        {
            source.sendMessage(ChatColor.RED + "HOMING " + projectile.getType().toString().toUpperCase() + " WAS ABORTED!!!");
            cancel();
            return;
        }

        Vector newVelocity = angle < 0.12
                ? directionVelocity.clone().multiply(newSpeed)
                : directionVelocity.clone().multiply((angle - 0.12) / angle).add(directionToTarget.clone().multiply(0.12 / angle)).normalize().clone().multiply(newSpeed);

        projectile.setVelocity(newVelocity);
        // SethBling's credit ends here

        System.out.println(" tracked " + lastTarget.getName() + " for " + targettedTicks + "/" + allowedTargetTicks);

        // Resetting target because it was longer than X ticks ago
        if(allowedTargetTicks > 0 && targettedTicks >= allowedTargetTicks)
        {
            System.out.println(" allowing for a new target");
            lastTarget = null;
        }
    }

    public double speed(double speed)
    {
        return speed;
    }

    /**
     * The method called to assign the new speed to the projectile in question. Although it's not currently overriden
     * in any of the current homing projectile classes, doesn't mean it wouldn't be useful one day.
     *
     * @param currentSpeed The current speed of the projectile
     * @return The new speed of the projectile
     */
    public double newSpeed(double currentSpeed)
    {
        return 0.8D * currentSpeed + 0.13999999999999999D;
    }

}