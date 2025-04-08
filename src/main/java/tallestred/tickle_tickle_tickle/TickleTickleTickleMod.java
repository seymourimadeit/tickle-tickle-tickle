package tallestred.tickle_tickle_tickle;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import tallestred.tickle_tickle_tickle.common.data_attachments.TTTDataAttachments;
import tallestred.tickle_tickle_tickle.config.Config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TickleTickleTickleMod.MODID)
public class TickleTickleTickleMod {
    public static final String MODID = "tickle_tickle_tickle";

    Method dropLoot = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "dropFromLootTable", DamageSource.class, boolean.class);

    public TickleTickleTickleMod(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        TTTDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteract event) throws InvocationTargetException, IllegalAccessException {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        LivingEntity hostile = (LivingEntity) event.getTarget();
        if (stack.canPerformAction(net.neoforged.neoforge.common.ItemAbilities.BRUSH_BRUSH) && hostile instanceof Enemy) {
            player.swing(event.getHand(), true);
            if (!player.level().isClientSide) {
                dropLoot.invoke(hostile, hostile.damageSources().generic(), false);
            }
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                ItemStack itemstack = hostile.getItemBySlot(equipmentslot);
                hostile.spawnAtLocation(itemstack);
                hostile.setItemSlot(equipmentslot, ItemStack.EMPTY);
            }
            ((Mob)hostile).setTarget(null);
            hostile.setData(TTTDataAttachments.TICKLED.get(), true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public void changeTarget(LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getData(TTTDataAttachments.TICKLED.get())) {
            if (event.getOriginalAboutToBeSetTarget() != null && event.getOriginalAboutToBeSetTarget() instanceof ServerPlayer)
                event.setCanceled(true);
        }
    }
}
