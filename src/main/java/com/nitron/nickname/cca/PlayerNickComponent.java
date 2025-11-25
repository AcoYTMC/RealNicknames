package com.nitron.nickname.cca;

import com.mojang.datafixers.util.Pair;
import com.nitron.nickname.RealNickname;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.Optional;

@SuppressWarnings("ALL")
public class PlayerNickComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final ComponentKey<PlayerNickComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(RealNickname.MOD_ID, "nickname"), PlayerNickComponent.class);
    private final PlayerEntity player;
    public Optional<Text> nick = Optional.empty();

    public PlayerNickComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) RealNickname.LOGGER.info("Synced component");
        if (this.player instanceof ServerPlayerEntity serverPlayer) {
            updateTabList(serverPlayer);
        }
    }

    public void tick() {
        //
    }

    public void readFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (nbt.contains("nick")) {
            this.nick = Codecs.optional(TextCodecs.CODEC).decode(NbtOps.INSTANCE, nbt.get("nick")).mapOrElse(Pair::getFirst, error -> {
                RealNickname.LOGGER.error("Nickname error: {}", error);
                return Optional.empty();
            });
        }
    }

    public void writeToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (this.nick.isPresent() && this.nick.get() != null) {
            NbtElement element = Codecs.optional(TextCodecs.CODEC).encode(this.nick, NbtOps.INSTANCE, new NbtCompound()).result().orElse(null);
            if (element != null) {
                nbt.put("nick", element);
            }
        }
    }

    public static void updateTabList(ServerPlayerEntity player) {
        ServerPlayNetworkHandler handler = player.networkHandler;
        if (handler != null) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
                if (playerEntity != null) {
                    server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, playerEntity));
                }
            }
        }
    }
}
