package join.message.Action;

import join.message.Message;
import org.bukkit.GameMode;
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
                crawl.remove(player);
                Plugin.action.remove(player);
                player.setGliding(false);
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

        if (slide.contains(player)) {
            if (!player.isSneaking()) {
                setCooltime(player);
                slide.remove(player);
                Plugin.action.remove(player);
                player.setGliding(false);
                return;
            }
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

        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5, 1);
        player.setGliding(true);
        slide.add(player);
        Plugin.action.add(player);
        player.sendMessage("§a§l匍匐前進");
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

                    crawl.remove(player);

                    Plugin.action.remove(player);

                    player.setGliding(false);
                }
                return;
            }

        if (slide.contains(e.getPlayer())) {
            Player player = e.getPlayer();

            if (!player.isOnGround()) {
                //スライディングクールタイムに突っ込む
                setCooltime(player);

                slide.remove(player);

                Plugin.action.remove(player);

                player.setGliding(false);
            }
        }
    }

    private void setCooltime(Player player) {
        //クールタイムリストに追加
        Plugin.cooltime.add(player);

        if (crawl.contains(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Plugin.cooltime.remove(player);
                    this.cancel();
                }
                //1.5秒後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 15, 0);
            return;
        }

        if (slide.contains(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Plugin.cooltime.remove(player);
                    this.cancel();
                }
                //2.5秒後にクールタイムリストから外す
            }.runTaskTimer(Plugin, 25, 0);
        }
    }

    private boolean isCooldown(Player player) {
        if (Plugin.cooltime.contains(player)) {
            return true;
        }

        return false;
    }
}
