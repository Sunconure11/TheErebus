package erebus.entity;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erebus.Erebus;
import erebus.network.PacketPipeline;
import erebus.network.client.PacketParticle;
import erebus.network.client.PacketParticle.ParticleType;

public class EntitySporeBall extends EntityThrowable {

	public EntitySporeBall(World world) {
		super(world);
		setSize(0.7F, 0.7F);
	}

	public EntitySporeBall(World world, EntityLiving entity) {
		super(world, entity);
	}

	public EntitySporeBall(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntitySporeBall(World world, EntityPlayer player) {
		super(world, player);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ridingEntity == null)
			if (worldObj.isRemote)
				trailParticles(worldObj, posX, posY + 0.35D, posZ, rand);
		
		if (ridingEntity != null) {
			yOffset= -1F;
			if (worldObj.isRemote)
				confusionParticles(worldObj, posX, posY, posZ, rand);
			
			if (ticksExisted > 140)
				setDead();
		}		
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) {

		if (mop.entityHit != null)
			if (mop.entityHit instanceof EntityPlayer) {

				if (!worldObj.isRemote) {
					if(mop.entityHit.riddenByEntity == null) {
						mountEntity(mop.entityHit);
						ticksExisted=0;
					}
				}
				if (worldObj.isRemote)
					PacketPipeline.sendToAllAround(mop.entityHit, 64D, new PacketParticle(this, ParticleType.BEETLE_LARVA_SQUISH));
			}
			else
				setDead();
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean attackEntityFrom(DamageSource source, int amount) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void trailParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			Erebus.proxy.spawnCustomParticle("cloud", worldObj, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void confusionParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 2; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextInt(2) * 2 - 1;
			int motionZ = rand.nextInt(2) * 2 - 1;
			velY = (rand.nextFloat() - 0.5D) * 0.125D;
			velZ = rand.nextFloat() * 0.5F * motionZ;
			velX = rand.nextFloat() * 0.5F * motionX;
			Erebus.proxy.spawnCustomParticle("cloud", worldObj, x, y, z, velX, velY, velZ);
		}
	}
}