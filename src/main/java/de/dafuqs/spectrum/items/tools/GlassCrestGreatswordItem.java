package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import de.dafuqs.spectrum.sound.GreatswordChargingSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassCrestGreatswordItem extends GreatswordItem implements SplitDamageItem {

	public final float magicDamageShare = 0.25F;
	public final int groundSlamChargeTicks;
	public final int baseGroundSlamStrength;

	public GlassCrestGreatswordItem(ToolMaterial material, int attackDamage, float attackSpeed, float extraReach, int groundSlamChargeTicks, int baseGroundSlamStrength, Settings settings) {
		super(material, attackDamage, attackSpeed, extraReach, settings);
		this.groundSlamChargeTicks = groundSlamChargeTicks;
		this.baseGroundSlamStrength = baseGroundSlamStrength;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("item.spectrum.glass_crest_ultra_greatsword.tooltip", (int) (this.magicDamageShare * 100)));
		tooltip.add(Text.translatable("item.spectrum.glass_crest_ultra_greatsword.tooltip2"));
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient) {
			startSoundInstance(user);
		}
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	public int getMaxUseTime(ItemStack stack) {
		return groundSlamChargeTicks;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.usageTick(world, user, stack, remainingUseTicks);
		if (world.isClient && this.baseGroundSlamStrength > 0) {
			Random random = world.random;
			for (int i = 0; i < (groundSlamChargeTicks - remainingUseTicks) / 8; i++) {
				world.addParticle(ParticleTypes.INSTANT_EFFECT,
						user.getParticleX(1.0), user.getY(), user.getParticleZ(1.0),
						random.nextDouble() * 5.0D - 2.5D, random.nextDouble() * 1.2D, random.nextDouble() * 5.0D - 2.5D);
			}
		}
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity user) {
		if (!world.isClient && this.baseGroundSlamStrength > 0) {
			int sweepingLevel = EnchantmentHelper.getLevel(Enchantments.SWEEPING, itemStack);
			performGroundSlam(world, user.getPos(), user, this.baseGroundSlamStrength + sweepingLevel);

			world.playSound(null, user.getBlockPos(), SpectrumSoundEvents.GROUND_SLAM, SoundCategory.PLAYERS, 1.0F, 1.0F);

			Vec3d particlePos = new Vec3d(user.getParticleX(1.0), user.getY(), user.getParticleZ(1.0));
			SpectrumS2CPacketSender.playParticleWithExactVelocity((ServerWorld) world, particlePos, ParticleTypes.EXPLOSION, 1, Vec3d.ZERO);
			SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity((ServerWorld) world, particlePos, ParticleTypes.CRIT, 16, Vec3d.ZERO, new Vec3d(7.5D, 0, 7.5D));

			if (user instanceof ServerPlayerEntity serverPlayer) {
				serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}

		return itemStack;
	}

	public void performGroundSlam(World world, Vec3d pos, LivingEntity attacker, float strength) {
		world.emitGameEvent(attacker, GameEvent.ENTITY_ROAR, new BlockPos(pos.x, pos.y, pos.z));

		double posX = pos.x;
		double posY = pos.y;
		double posZ = pos.z;

		double k = MathHelper.floor(posX - (double) strength - 1.0D);
		double l = MathHelper.floor(posX + (double) strength + 1.0D);
		int r = MathHelper.floor(posY - (double) strength - 1.0D);
		int s = MathHelper.floor(posY + (double) strength + 1.0D);
		int t = MathHelper.floor(posZ - (double) strength - 1.0D);
		int u = MathHelper.floor(posZ + (double) strength + 1.0D);
		List<Entity> list = world.getOtherEntities(attacker, new Box(k, r, t, l, s, u));
		Vec3d vec3d = new Vec3d(posX, posY, posZ);

		for (Entity entity : list) {
			if (!entity.isImmuneToExplosion()) {
				double w = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double) strength;
				if (w <= 1.0D) {
					double x = entity.getX() - posX;
					double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - posY;
					double z = entity.getZ() - posZ;
					double aa = Math.sqrt(x * x + y * y + z * z);
					if (aa != 0.0D) {
						x /= aa;
						y /= aa;
						z /= aa;
						double ab = Explosion.getExposure(vec3d, entity);
						double ac = (1.0D - w) * ab;

						float damage = (float) ((int) ((ac * ac + ac) / 2.0D * (double) strength + 1.0D));
						if (entity instanceof PlayerEntity player) {
							entity.damage(DamageSource.player(player), damage);
						} else {
							entity.damage(DamageSource.mob(attacker), damage);
						}

						double ad = ac;
						if (entity instanceof LivingEntity) {
							ad = ProtectionEnchantment.transformExplosionKnockback((LivingEntity) entity, ac);
						}

						entity.setVelocity(entity.getVelocity().add(x * ad, y * ad, z * ad));
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void startSoundInstance(PlayerEntity user) {
		MinecraftClient.getInstance().getSoundManager().play(new GreatswordChargingSoundInstance(user, this.groundSlamChargeTicks));
	}

	@Override
	public DamageComposition getDamageComposition(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {
		DamageComposition composition = new DamageComposition();
		composition.addPlayerOrEntity(attacker, damage * (1 - this.magicDamageShare));
		composition.add(DamageSource.magic(attacker, attacker), damage * this.magicDamageShare);
		return composition;
	}

}
