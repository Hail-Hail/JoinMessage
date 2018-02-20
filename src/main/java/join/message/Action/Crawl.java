package join.message.Action;

import join.message.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Crawl implements Listener {

    Message Plugin =Message.getPlugin(Message.class);

    public Crawl() {
        Plugin.getServer().getPluginManager().registerEvents(this,Plugin);
    }

    public List<Player> crawl = new ArrayList<>();

    List<Player> slide = new ArrayList<>();

    List<Player> dash = new ArrayList<>();

    @EventHandler
    public void onCrawl(PlayerToggleSneakEvent e) {

        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (crawl.contains(player)) {
            if (!player.isSneaking()) {
                setCooltime(player);
                return;
            }
        }

        if (!player.isSneaking()) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        if (Plugin.action.contains(player)) {
            return;
        }

        if (dash.contains(player)) {
            return;
        }

        if (isCooldown(player)) {
            player.sendMessage("§c§lクールタイム中です");
            return;
        }

        player.setGliding(true);
        crawl.add(player);
        Plugin.action.add(player);
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED,2,1);
        player.sendMessage("§a§l匍匐");
    }

    @EventHandler
    public void onDash(PlayerToggleSprintEvent e) {

        Player player = e.getPlayer();

        if (!player.isSprinting()) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        dash.add(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                dash.remove(player);
                this.cancel();
            }
            //delay
        }.runTaskTimer(Plugin, 10, 0);
    }

    @EventHandler
    public void onSlide(PlayerToggleSneakEvent e) {

        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (Plugin.action.contains(player)) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        if (!dash.contains(player)) {
            return;
        }

        if (isCooldown(player)) {
            player.sendMessage("§c§lクールタイム中です");
            return;
        }

        player.setGliding(true);
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5, 1);
        player.sendMessage("§a§lスライディング");

        Plugin.action.add(player);
        slide.add(player);
        Location l = player.getLocation();

        new BukkitRunnable() {
            public void run() {
                loop++;
                if (loop == 13) {
                    setCooltime(player);
                    this.cancel();
                    return;
                }

                if (player.isSneaking()) {
                    setCooltime(player);
                    this.cancel();
                    return;
                }

                if (!player.isOnGround()) {
                    setCooltime(player);
                    this.cancel();
                    return;
                }

                player.setVelocity(l.getDirection().multiply(0.8).setY(0));
                player.playSound(player.getLocation(), Sound.BLOCK_GRASS_STEP, 6, 1);
            }private int loop = 0;
            //4tick間隔で処理を行う
        }.runTaskTimer(Plugin, 1L, 2L);
    }



    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) e.getEntity();

        if (crawl.contains(player)) {
            e.setCancelled(true);
            player.setVelocity(new Vector(0,0,0));
        }

        if (slide.contains(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
        public void onActionMove(PlayerMoveEvent e) {

        if (crawl.contains(e.getPlayer())) {
            Player player = e.getPlayer();

            if (!player.isOnGround()) {
                //匍匐クールタイムに突っ込む
                setCooltime(player);

                player.setGliding(false);
            }
        }
    }

    private void setCooltime(Player player) {
        //クールタイムリストに追加
        Plugin.cooltime.add(player);

        if (crawl.contains(player)) {

            crawl.remove(player);
            Plugin.action.remove(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Plugin.cooltime.remove(player);
                    this.cancel();
                }
                //1.5秒後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 10, 0L);
        }

        if (slide.contains(player)) {

            slide.remove(player);
            Plugin.action.remove(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Plugin.cooltime.remove(player);
                    this.cancel();
                }
                //2.5秒後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 20L, 0L);
        }
    }

    private boolean isCooldown(Player player) {
        if (Plugin.cooltime.contains(player)) {
            return true;
        }

        return false;
    }
}
