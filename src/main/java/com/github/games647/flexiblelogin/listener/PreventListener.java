package com.github.games647.flexiblelogin.listener;

import com.flowpowered.math.vector.Vector3d;
import com.github.games647.flexiblelogin.FlexibleLogin;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.CauseTracked;
import org.spongepowered.api.event.command.MessageSinkEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public class PreventListener {

    private final FlexibleLogin plugin;

    public PreventListener(FlexibleLogin plugin) {
        this.plugin = plugin;
    }

    @Listener(ignoreCancelled = true)
    public void onPlayerMove(DisplaceEntityEvent.TargetPlayer playerMoveEvent) {
        Vector3d oldLocation = playerMoveEvent.getFromTransform().getPosition();
        Vector3d newLocation = playerMoveEvent.getToTransform().getPosition();
        if ((oldLocation.getFloorX()!= newLocation.getFloorX()
                || oldLocation.getFloorZ()!= newLocation.getFloorZ())) {
            checkLoginStatus(playerMoveEvent, playerMoveEvent.getTargetEntity());
        }
    }

    @Listener(ignoreCancelled = true)
    public void onChat(MessageSinkEvent.Chat chatEvent) {
        checkLoginStatus(chatEvent, chatEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onCommand(SendCommandEvent commandEvent) {
        Optional<Player> playerOptional = commandEvent.getCause().first(Player.class);
        if (playerOptional.isPresent()) {
            String command = commandEvent.getCommand();
            //do not blacklist our own commands
            if ("register".equals(command) || "login".equals(command)
                    || "forgotpassword".equals(command)) {
                return;
            }

            checkLoginStatus(commandEvent, playerOptional.get());
        }
    }

    @Listener(ignoreCancelled = true)
    public void onPlayerItemDrop(DropItemEvent dropItemEvent) {
        checkLoginStatus(dropItemEvent, dropItemEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onPlayerItemPickup(CollideEntityEvent collideEntityEvent) {
        if (collideEntityEvent.getCause().first(ItemStack.class).isPresent()) {
            checkLoginStatus(collideEntityEvent, collideEntityEvent);
        }
    }

    @Listener(ignoreCancelled = true)
    public void onItemConsume(UseItemStackEvent itemConsumeEvent) {
        checkLoginStatus(itemConsumeEvent, itemConsumeEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onBlockBreak(ChangeBlockEvent.Break breakBlockEvent) {
        checkLoginStatus(breakBlockEvent, breakBlockEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onBlockBreak(ChangeBlockEvent.Place blockPlaceEvent) {
        checkLoginStatus(blockPlaceEvent, blockPlaceEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onBlockChange(ChangeBlockEvent changeBlockEvent) {
        checkLoginStatus(changeBlockEvent, changeBlockEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onBlockInteract(InteractBlockEvent interactBlockEvent) {
        checkLoginStatus(interactBlockEvent, interactBlockEvent);
    }

    @Listener(ignoreCancelled = true)
    public void onEntityInteract(InteractEntityEvent interactEntityEvent) {
        checkLoginStatus(interactEntityEvent, interactEntityEvent);
    }

    private void checkLoginStatus(Cancellable event, CauseTracked causeEvent) {
        Optional<Player> playerOptional = causeEvent.getCause().first(Player.class);
        if (playerOptional.isPresent()) {
            checkLoginStatus(event, playerOptional.get());
        }
    }

    private void checkLoginStatus(Cancellable event, Player player) {
        if (!plugin.getDatabase().isLoggedin(player)) {
            event.setCancelled(true);
        }
    }
}
