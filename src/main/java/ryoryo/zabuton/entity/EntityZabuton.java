package ryoryo.zabuton.entity;

import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ryoryo.polishedlib.util.Utils;
import ryoryo.zabuton.item.ModItems;

public class EntityZabuton extends Entity implements IProjectile, IEntityAdditionalSpawnData {
	protected double zabutonX;
	protected double zabutonY;
	protected double zabutonZ;
	protected double zabutonYaw;
	protected double zabutonPitch;
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	protected int health;
	public boolean isDispensed;
	public byte color;
	protected int boatPosRotationIncrements;
	private static final DataParameter<Byte> DISPENSED = EntityDataManager.<Byte> createKey(EntityZabuton.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> RIDING_ENTITY_ID = EntityDataManager.<Integer> createKey(EntityZabuton.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> COLOR = EntityDataManager.<Byte> createKey(EntityZabuton.class, DataSerializers.BYTE);

	public EntityZabuton(World world) {
		super(world);
		this.preventEntitySpawning = true;
		this.setSize(0.81F, 0.2F);
		this.health = 20;
		this.isDispensed = false;
		this.color = (byte) 0xFF;
	}

	public EntityZabuton(World world, byte color) {
		this(world);
		this.color = color;
	}

	public EntityZabuton(World world, ItemStack stack) {
		this(world, (byte) (stack.getItemDamage() & 0x0f));
	}

	public EntityZabuton(World world, double x, double y, double z, byte color) {
		this(world, color);
		this.setPositionAndRotation(x, y + (double) this.getYOffset(), z, 0F, 0F);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
	}

	@Override
	public double getYOffset() {
		return 0.0D;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		// ディスペンサー用
		float f2 = MathHelper.sqrt(x * x + y * y + z * z);
		x /= f2;
		y /= f2;
		z /= f2;
		x += rand.nextGaussian() * 0.0074999998323619366D * (double) inaccuracy;
		y += rand.nextGaussian() * 0.0074999998323619366D * (double) inaccuracy;
		z += rand.nextGaussian() * 0.0074999998323619366D * (double) inaccuracy;
		x *= velocity;
		y *= velocity;
		z *= velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f3 = MathHelper.sqrt(x * x + z * z);
		prevRotationYaw = rotationYaw = (float) ((Math.atan2(x, z) * 180D) / 3.1415927410125732D);
		prevRotationPitch = rotationPitch = (float) ((Math.atan2(y, f3) * 180D) / 3.1415927410125732D);
		this.setDispensed(true);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(DISPENSED, new Byte((byte) (isDispensed ? 0x01 : 0x00)));
		this.dataManager.register(RIDING_ENTITY_ID, Integer.valueOf(0));
		this.dataManager.register(COLOR, new Byte((byte) 0xFF));
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		color = compound.getByte("Color");
		health = compound.getShort("Health");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setByte("Color", (byte) (color & 0x0f));
		compound.setShort("Health", (byte) health);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeByte(color);
		buffer.writeFloat(rotationYaw);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		color = additionalData.readByte();
		setRotation(additionalData.readFloat(), 0.0F);
	}

	@Override
	public double getMountedYOffset() {
		if (this.getRidingEntity() instanceof EntitySpider)
			return (double) height * 0.0D - 0.1D;

		if (this.getRidingEntity() instanceof EntityZombie || this.getRidingEntity() instanceof EntityEnderman)
			return (double) height * 0.0D - 0.4D;

		return (double) height * 0.0D + 0.1D;
	}

	@Override
	public boolean handleWaterMovement() {
		// 独自の水没判定
		int minX = MathHelper.floor(this.getEntityBoundingBox().minX);
		int maxX = MathHelper.floor(this.getEntityBoundingBox().maxX + 1.0D);
		int minY = MathHelper.floor(this.getEntityBoundingBox().minY);
		int maxY = MathHelper.floor(this.getEntityBoundingBox().maxY + 1.0D);
		int minZ = MathHelper.floor(this.getEntityBoundingBox().minZ);
		int maxZ = MathHelper.floor(this.getEntityBoundingBox().maxZ + 1.0D);

		boolean flag = false;

		for (int x = minX; x < maxX; ++ x) {
			for (int y = minY; y < maxY; ++ y) {
				for (int z = minZ; z < maxZ; ++ z) {
					IBlockState state = world.getBlockState(new BlockPos(x, y, z));

					if (state != null && state.getMaterial() == Material.WATER) {
						inWater = true;
						double level = (double) ((float) (y + 1) - BlockLiquid.getLiquidHeightPercent(state.getBlock().getMetaFromState(state)));

						if ((double) maxY >= level) {
							flag = true;
						}
					} else {
						this.inWater = false;
					}
				}
			}
		}
		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		Entity entity = source.getTrueSource();
		if (this.world.isRemote || this.isDead) {
			return true;
		}
		this.markVelocityChanged();
		if (entity instanceof EntityPlayer) {
			if (this.color >= 0 && this.color < 16 && !Utils.isCreative((EntityPlayer) entity)) {
				this.entityDropItem(new ItemStack(ModItems.ITEM_ZABUTON, 1, this.color), 0.0F);
			}
			this.setDead();
		} else {
			this.health -= amount;
			if (this.health <= 0) {
				this.setDead();
			}
		}
		if (this.isDead && this.getRidingEntity() != null) {
			this.getRidingEntity().dismountRidingEntity();
			this.setRiddenByEntityID(this.getRidingEntity());
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z) {
		this.velocityX = this.motionX = x;
		this.velocityY = this.motionY = y;
		this.velocityZ = this.motionZ = z;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		// クライアントへはパケットで送ってたと思われる。dataWatcherに切り替え。
		if (!this.world.isRemote) {
			this.dataManager.set(COLOR, this.color);
		} else {
			this.color = this.dataManager.get(COLOR);
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		// ボートの判定のコピー
		// ボートは直接サーバーと位置情報を同期させているわけではなく、予測位置計算系に値を渡している。
		// 因みにボートの座標同期間隔は結構長めなので動きが変。
		double var6;
		double var8;
		double var12;
		double var26;
		double var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

		if (this.world.isRemote) {
			// Client
			if (this.boatPosRotationIncrements > 0) {
				var6 = this.posX + (this.zabutonX - this.posX) / (double) this.boatPosRotationIncrements;
				var8 = this.posY + (this.zabutonY - this.posY) / (double) this.boatPosRotationIncrements;
				var26 = this.posZ + (this.zabutonZ - this.posZ) / (double) this.boatPosRotationIncrements;
				var12 = MathHelper.wrapDegrees(this.zabutonYaw - (double) this.rotationYaw);
				this.rotationYaw = (float) ((double) this.rotationYaw + var12 / (double) this.boatPosRotationIncrements);
				this.rotationPitch = (float) ((double) this.rotationPitch + (this.zabutonPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
				-- this.boatPosRotationIncrements;
				this.setPosition(var6, var8, var26);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				motionY -= 0.08D;
				if (this.onGround) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
					this.setDispensed(false);
				}
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

				this.motionX *= 0.9900000095367432D;
				this.motionY *= 0.949999988079071D;
				this.motionZ *= 0.9900000095367432D;
			}
		} else {
			// Server
			// 落下
			motionY -= 0.08D;

			// 搭乗者によるベクトル操作
			if (this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer) {
				this.motionX += this.getRidingEntity().motionX * 0.2D;
				this.motionZ += this.getRidingEntity().motionZ * 0.2D;
			}

			// 最高速度判定
			Double lmaxspeed = isDispensed() ? 10.0D : 0.35D;
			var6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (var6 > lmaxspeed) {
				var8 = lmaxspeed / var6;
				this.motionX *= var8;
				this.motionZ *= var8;
				var6 = lmaxspeed;
			}
			if (this.onGround) {
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
				setDispensed(false);
				// setVelocityの呼ばれる回数が少なくて変な動きをするので対策
				// this.velocityChanged = true;
			}
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

			this.motionX *= 0.9900000095367432D;
			this.motionY *= 0.949999988079071D;
			this.motionZ *= 0.9900000095367432D;

			// ヘッディング
			this.rotationPitch = 0.0F;
			var8 = (double) this.rotationYaw;
			var26 = this.prevPosX - this.posX;
			var12 = this.prevPosZ - this.posZ;

			if (var26 * var26 + var12 * var12 > 0.001D) {
				var8 = (double) ((float) (Math.atan2(var12, var26) * 180.0D / Math.PI));
			}

			double var14 = MathHelper.wrapDegrees(var8 - (double) this.rotationYaw);
			if (var14 > 20.0D) {
				var14 = 20.0D;
			}
			if (var14 < -20.0D) {
				var14 = -20.0D;
			}

			this.rotationYaw = (float) ((double) this.rotationYaw + var14);
			this.setRotation(this.rotationYaw, this.rotationPitch);

			// 当たり判定
			List<Entity> var16 = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.17D, 0.0D, 0.17D));
			if (var16 != null && !var16.isEmpty()) {
				Iterator<Entity> var28 = var16.iterator();

				while (var28.hasNext()) {
					Entity var18 = (Entity) var28.next();

					if (var18 != this.getRidingEntity() && var18.canBePushed() && var18 instanceof EntityZabuton) {
						var18.applyEntityCollision(this);
					}
				}
			}
		}
		if (this.getRidingEntity() != null) {
			if (this.getRidingEntity() instanceof EntityMob) {
				// 座ってる間は消滅させない
				this.setEntityLivingAge((EntityLivingBase) getRidingEntity(), 0);
			}
			if (this.getRidingEntity().isDead) {
				// 着座対象が死んだら無人化
				this.dismountRidingEntity();
				this.setRiddenByEntityID(this.getRidingEntity());
			} else if (this.inWater) {
				// ぬれた座布団はひゃぁってなる
				this.dismountRidingEntity();
				this.setRiddenByEntityID(this.getRidingEntity());
			}
		}
	}

	public void setEntityLivingAge(EntityLivingBase entity, int a) {
		ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, a, "field_70708_bq", "entityAge");
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		// 吸着判定
		if (!this.world.isRemote) {
			if (entity == this.getRidingEntity()) {
				super.applyEntityCollision(entity);
				return;
			}
			if (entity instanceof EntityLiving && !(entity instanceof EntityPlayer) && !this.isBeingRidden() && !entity.isRiding()) {
				entity.startRiding(this);
				this.setRiddenByEntityID(this.getRidingEntity());
			}
			super.applyEntityCollision(entity);
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		// ラーイド・オン！
		if (this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer && this.getRidingEntity() != player) {
			return true;
		}
		if (!world.isRemote && !player.isSneaking()) {
			player.startRiding(this);
		}
		return true;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		if (this instanceof EntityZabuton) {
			return new ItemStack(ModItems.ITEM_ZABUTON, 1, this.color);
		}

		return null;
	}

	// 射出判定
	public boolean isDispensed() {
		return this.dataManager.get(DISPENSED) > 0x00;
	}

	public void setDispensed(boolean isDispensed) {
		this.dataManager.set(DISPENSED, (byte) (isDispensed ? 0x01 : 0x00));
	}

	// クライアント側補正用
	public int getRiddenByEntityID() {
		return this.dataManager.get(RIDING_ENTITY_ID);
	}

	public Entity getRiddenByEntity() {
		return ((WorldClient) this.world).getEntityByID(this.getRiddenByEntityID());
	}

	public void setRiddenByEntityID(Entity pentity) {
		this.dataManager.set(RIDING_ENTITY_ID, Integer.valueOf(pentity == null ? 0 : pentity.getEntityId()));
	}
}