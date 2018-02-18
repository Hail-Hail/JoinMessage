package join.message.Action;

import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import join.message.Message;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Grapple implements Listener {

    Message Plugin = Message.getPlugin(Message.class);

    List<Player> jump = new ArrayList<>();

    public Grapple() {
        Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
    }

    @EventHandler
    public void DoubleJumpCooldown(PlayerMoveEvent e) {

        Player player = e.getPlayer();

        if (!jump.contains(player)) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        jump.remove(player);
        Plugin.action.remove(player);

    }

    @EventHandler
    public void setFlyOnJump(PlayerToggleFlightEvent e) {

        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.isOnGround()) {
            e.setCancelled(true);
            return;
        }

        if (Plugin.action.contains(player)) {
            e.setCancelled(true);
            return;
        }

        if (isCooldown(player)) {
            player.sendMessage("§c§lクールタイム中です");
            e.setCancelled(true);
            return;
        }

        if (!player.isFlying()) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.setVelocity(player.getLocation().getDirection().multiply(0.7).setY(1.1));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 3, 1);
                e.setCancelled(true);
                jump.add(player);
                Plugin.action.add(player);
                setCooltime(player);
                player.sendMessage("§a§lダブルジャンプ");
            }
        }
    }

    private void setCooltime(Player player) {
        //クールタイムリストに追加
        Plugin.cooltime.add(player);

        if (jump.contains(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Plugin.cooltime.remove(player);
                    this.cancel();
                }
                //2秒後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 45, 0);
        }
    }

    private boolean isCooldown(Player player) {
        if (Plugin.cooltime.contains(player)) {
            return true;
        }

        return false;
    }

}
