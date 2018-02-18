package join.message.Action;

import join.message.Message;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Glide implements Listener {

    Message Plugin =Message.getPlugin(Message.class);

    public Glide() {
        Plugin.getServer().getPluginManager().registerEvents(this,Plugin);
    }

    List<Player> dive = new ArrayList<>();

    @EventHandler
    public void onDive(PlayerToggleSneakEvent e) {

        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        if (dive.contains(player)) {
            setCooltime(player);
            dive.remove(player);
            Plugin.action.remove(player);
            return;
        }

        if (player.isOnGround()) {
            return;
        }

        if (Plugin.action.contains(player)) {
            return;
        }

        if (isCooldown(player)) {
            player.sendMessage("§c§lクールタイム中です");
            return;
        }

        player.setGliding(true);
        Plugin.action.add(player);
        dive.add(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 3, 2);
        player.sendMessage("§a§l滑空");
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) e.getEntity();

        if (dive.contains(player)) {
            if (!player.isOnGround()) {
                e.setCancelled(true);
                return;
            }
            if (player.isGliding()) {
                setCooltime(player);
                dive.remove(player);
                Plugin.action.remove(player);
            }
        }
    }

        private void setCooltime (Player player) {
            //クールタイムリストに追加
            Plugin.cooltime.add(player);

            if (dive.contains(player)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Plugin.cooltime.remove(player);
                        this.cancel();
                    }
                    //1.5秒後にクールタイムリストから外す
                }.runTaskTimer(Plugin, 30, 0);
                return;
            }
        }

    private boolean isCooldown(Player player) {
        if (Plugin.cooltime.contains(player)) {
            return true;
        }

        return false;
    }
}
