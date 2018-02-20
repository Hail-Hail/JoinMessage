package join.message;

import join.message.Action.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class Message extends JavaPlugin implements Listener {

    Crawl crawl = null;

    Glide glide = null;

    DoubleJump doublejump = null;

    public List<Player> action = new ArrayList<>();

    public List<Player> cooltime = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        crawl = new Crawl();
        glide = new Glide();
        doublejump = new DoubleJump();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        player.setWalkSpeed(0.3f);
        player.setFlySpeed(0.1f);

        if (e.getPlayer().isOp()) {
            e.setJoinMessage("§4§lOperator §f§l" + e.getPlayer().getName() + "さんがサーバーに入りました");
            return;
        }
        e.setJoinMessage(e.getPlayer().getName() + "さんがサーバーに入りました！");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(e.getPlayer().getName() + "さんがサーバーから退出しました！");
    }
}
