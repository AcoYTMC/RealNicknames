package com.nitron.nickname.mixin;

import com.mojang.authlib.GameProfile;
import com.nitron.nickname.cca.PlayerNickComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void spawn(CallbackInfo ci) {
        PlayerNickComponent.updateTabList((ServerPlayerEntity)(Object)this);
    }

    @Inject(method = "getPlayerListName", at = @At("TAIL"), cancellable = true)
    private void replaceNameOnTabList(CallbackInfoReturnable<Text> cir) {
        PlayerNickComponent component = PlayerNickComponent.KEY.get(this);
        component.nick.ifPresent(cir::setReturnValue);
    }
}