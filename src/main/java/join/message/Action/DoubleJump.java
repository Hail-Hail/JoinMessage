package join.message.Action;

import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import join.message.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DoubleJump implements Listener {

    Message Plugin = Message.getPlugin(Message.class);

    List<Player> jump = new ArrayList<>();

    List<Player> wallrun = new ArrayList<>();

    public DoubleJump() {
        Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        player.setAllowFlight(true);
        jump.remove(player);
        wallrun.remove(player);
        Plugin.action.remove(player);
    }

    @EventHandler
    public void DoubleJumpCooldown(PlayerMoveEvent e) {

        Player player = e.getPlayer();

        player.setAllowFlight(true);

        if (!player.isOnGround()) {
            return;
        }

        if (!jump.contains(player)) {
            return;
        }

        jump.remove(player);

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

        if (player.isSneaking()) {
            e.setCancelled(true);
            return;
        }

        if (Plugin.action.contains(player)) {
            e.setCancelled(true);
            return;
        }

        if (jump.contains(player)) {
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
                player.setVelocity(player.getLocation().getDirection().multiply(0.6).setY(0.9));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 3, 1);
                e.setCancelled(true);
                jump.add(player);
                Plugin.action.add(player);
                setCooltime(player);
                player.sendMessage("§a§lダブルジャンプ");
            }
        }
    }


    @EventHandler
    public void setWallRun(PlayerToggleSneakEvent e) {

        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        if (player.isOnGround()) {
            return;
        }

        if (Plugin.action.contains(player)) {
            return;
        }

        if (isCooldown(player)) {
            return;
        }

        Location l = player.getLocation();
        Location lr = getRightSide(l, 1);
        Location ll = getLeftSide(l, 1);

        if (lr.getBlock().getType() == Material.AIR || lr.getBlock().getType() == Material.WATER || lr.getBlock().getType() == Material.LAVA && (ll.getBlock().getType() == Material.AIR || ll.getBlock().getType() == Material.WATER || ll.getBlock().getType() == Material.LAVA)) {
            if (ll.getBlock().getType() == Material.AIR || ll.getBlock().getType() == Material.WATER || ll.getBlock().getType() == Material.LAVA) {
                return;
            }
        }
        wallrun.add(player);
        Plugin.action.add(player);
        player.setVelocity(player.getLocation().getDirection().multiply(0.9).setY(0.1));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 3, 1);
        player.sendMessage("§a§l壁走り");
        if (wallrun.contains(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setVelocity(player.getLocation().getDirection().multiply(1.0).setY(0.1));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 2, 2);

                    Location l = player.getLocation();
                    Location lr = getRightSide(l, 1);
                    Location ll = getLeftSide(l, 1);
                    if (lr.getBlock().getType() == Material.AIR || lr.getBlock().getType() == Material.WATER || lr.getBlock().getType() == Material.LAVA && (ll.getBlock().getType() == Material.AIR || ll.getBlock().getType() == Material.WATER || ll.getBlock().getType() == Material.LAVA)) {
                        if (ll.getBlock().getType() == Material.AIR || ll.getBlock().getType() == Material.WATER || ll.getBlock().getType() == Material.LAVA) {
                            player.setVelocity(player.getLocation().getDirection().multiply(1.3).setY(0.9));
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 2, 2);
                            jump.remove(player);
                            this.cancel();
                            return;
                        }
                    }
                    if (!player.isSneaking()) {
                        player.setVelocity(player.getLocation().getDirection().multiply(1.3).setY(0.9));
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 2, 2);
                        jump.remove(player);
                        this.cancel();
                        return;
                    }
                    if (player.isOnGround()) {
                        this.cancel();
                        return;
                    }

                }
                //4tick間隔で処理を行う
            }.runTaskTimer(Plugin, 1L, 3L); {
                setCooltime(player);
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
                //5tick後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 5L, 0L); {
                Plugin.action.remove(player);
            }
            return;
        }

        if (wallrun.contains(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Plugin.cooltime.remove(player);
                    this.cancel();
                }
                //5tick後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 5L, 0L); {
                wallrun.remove(player);
                Plugin.action.remove(player);
            }
        }
    }

    private boolean isCooldown(Player player) {
        if (Plugin.cooltime.contains(player)) {
            return true;
        }
        return false;
    }

    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

}