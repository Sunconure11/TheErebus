package erebus.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import erebus.ModItems;
import erebus.entity.ai.EntityAITarantulaMinibossAttack;
import erebus.item.Materials;

public class EntityTarantulaMiniboss extends EntityMob implements IBossDisplayData
{
	public int skin = rand.nextInt(99);

	public EntityTarantulaMiniboss(World world)
	{
		super(world);
		setSize(4.0F, 1.2F);
		experienceValue = 100;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAITarantulaMinibossAttack(this, EntityPlayer.class, 0.3D, false));
		tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataWatcher.addObject(16, new Byte((byte) 0));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
	}
	
	@Override
	public boolean isAIEnabled()
	{
		return true;
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public int getTotalArmorValue()
	{
		return 8;
	}

	@Override
	public boolean isOnLadder()
	{
		return isBesideClimbableBlock();
	}

	@Override
	public void setInWeb()
	{
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potionEffect)
	{
		return potionEffect.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(potionEffect);
	}

	public boolean isBesideClimbableBlock()
	{
		return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setBesideClimbableBlock(boolean besideBlock)
	{
		byte b0 = dataWatcher.getWatchableObjectByte(16);

		if (besideBlock)
		{
			b0 = (byte) (b0 | 1);
		} else
		{
			b0 &= -2;
		}

		dataWatcher.updateObject(16, Byte.valueOf(b0));
	}

	@Override
	protected String getLivingSound()
	{
		return "mob.spider.say";
	}

	@Override
	protected String getHurtSound()
	{
		return "mob.spider.say";
	}

	@Override
	protected String getDeathSound()
	{
		return "mob.spider.death";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block)
	{
		playSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (!worldObj.isRemote)
		{
			setBesideClimbableBlock(isCollidedHorizontally);
		}
		
		EnumDifficulty difficulty = worldObj.difficultySetting;
		if (difficulty == EnumDifficulty.PEACEFUL)
		{
			worldObj.difficultySetting = EnumDifficulty.EASY;
		}
		worldObj.difficultySetting = difficulty;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (super.attackEntityAsMob(entity))
		{

			if (entity instanceof EntityLiving)
			{
				byte duration = 0;

				if (worldObj.difficultySetting.ordinal() > EnumDifficulty.EASY.ordinal() && rand.nextInt(19) == 0)
				{
					if (worldObj.difficultySetting == EnumDifficulty.NORMAL)
					{
						duration = 5;
					} else if (worldObj.difficultySetting == EnumDifficulty.HARD)
					{
						duration = 10;
					}
				}

				if (duration > 0)
				{
					((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.poison.id, duration * 20, 0));
				}
			}
			return true;
		} else
		{
			return false;
		}
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting)
	{
		int chanceFiftyFifty = rand.nextInt(1) + 1;
		int chance20x60x20 = rand.nextInt(4);
		int legDrop = 0;
		switch (chance20x60x20)
		{
			case 0:
				legDrop = 1;
				break;
			case 1:
			case 2:
			case 3:
				legDrop = 2;
				break;
			case 4:
				legDrop = 3;
				break;
		}
		if (isBurning())
		{
			entityDropItem(new ItemStack(ModItems.food, legDrop + looting, 5), 0.0F);
		} else
		{
			entityDropItem(new ItemStack(ModItems.food, legDrop + looting, 4), 0.0F);
		}
		dropItem(Items.spider_eye, chanceFiftyFifty + looting);
		entityDropItem(Materials.createStack(Materials.DATA.poisonGland, 1 + rand.nextInt(2)), 0.0F);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData)
	{
		Object entityLivingData1 = super.onSpawnWithEgg(entityLivingData);

		if (worldObj.rand.nextInt(100) == 0)
		{
			EntityMoneySpider entityspidermoney = new EntityMoneySpider(worldObj);
			entityspidermoney.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
			entityspidermoney.onSpawnWithEgg((IEntityLivingData) null);
			worldObj.spawnEntityInWorld(entityspidermoney);
			entityspidermoney.mountEntity(this);
		}
		if (entityLivingData1 == null)
		{
			entityLivingData1 = new EntitySpider.GroupData();
			if (worldObj.difficultySetting == EnumDifficulty.HARD && worldObj.rand.nextFloat() < 0.1F * worldObj.func_147462_b(posX, posY, posZ))
			{
				((EntitySpider.GroupData) entityLivingData1).func_111104_a(worldObj.rand);
			}

			if (entityLivingData1 instanceof EntitySpider.GroupData)
			{
				int i = ((EntitySpider.GroupData) entityLivingData1).field_111105_a;
				if (i > 0 && Potion.potionTypes[i] != null)
				{
					addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
				}
			}
		}
		return (IEntityLivingData) entityLivingData1;
	}
}