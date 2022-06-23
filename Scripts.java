package c.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import c.Main;
import c.Messages;

public class Scripts implements Listener {
	static File file = new File(Main.getInstance().getDataFolder() + File.separator + "test.yml");
	static FileConfiguration data = YamlConfiguration.loadConfiguration(file);
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void blockBreake(BlockBreakEvent e){
		if(e.getPlayer().getWorld() == Bukkit.getWorld("Test") && e.getBlock().getType() == Material.DIRT && e.getPlayer().isSneaking()) {
			String rs = RandomStringUtils.randomAlphabetic(10);
			Zombie entity = (Zombie) Bukkit.getWorld("Test").spawnEntity(e.getBlock().getLocation(), EntityType.ZOMBIE);
			entity.setCustomName("§6§l " + rs);
			entity.setRemoveWhenFarAway(true);
			entity.setCustomNameVisible(true);
			entity.setBaby(false);
			entity.setSilent(true);
			entity.setMaxHealth(40);
			entity.setHealth(40);
			AreaEffectCloud nick = (AreaEffectCloud) Bukkit.getWorld("Test").spawnEntity(entity.getLocation(), EntityType.AREA_EFFECT_CLOUD);
			nick.setRadius(0);
			nick.setDuration(214748365);
			nick.setCustomNameVisible(true);
			nick.setCustomName("§6§l " + entity.getCustomName() + " §f" + (int) entity.getHealth() + "§c§l" + Messages.heart + " ");
			entity.setPassenger(nick);
			//((CraftMonster) entity).getHandle().goalSelector(0, new PathfinderGoalAvoidTarget<EntityPlayer>((EntityCreature) entity, EntityPlayer.class, 0, 0, 0));
			entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999999, 1), false);
			e.setCancelled(true);
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void death(EntityDeathEvent e){
		Player dick = null;
		if(e.getEntity().getKiller() != null) {dick = e.getEntity().getKiller();}
		if(dick.getWorld() == Bukkit.getWorld("Test")) {
			
			ItemStack drop = new ItemStack(Material.ROTTEN_FLESH);
			ItemMeta meta = drop.getItemMeta();
			meta.setDisplayName("§7 Плоть жертвы насилия игроком " + dick.getName() + " ");
			drop.setItemMeta(meta);

			e.getDrops().clear();
			e.getDrops().add(drop);
			e.setDroppedExp(0);
			e.getEntity().getPassenger().remove();

			try{
				List<String> list = data.getStringList(dick.getName() + ".killed");
				list.add(e.getEntity().getCustomName());
				data.set(dick.getName() + ".killed", list);
			}catch(Exception e2) {}
			try {data.save(file);} catch (IOException e1) {}
			
			ArmorStand nick = (ArmorStand) Bukkit.getWorld("Test").spawnEntity(e.getEntity().getLocation(), EntityType.ARMOR_STAND);
			nick.setCustomNameVisible(true);
			nick.setVisible(false);
			nick.setInvulnerable(true);
			nick.setSmall(true);
			nick.setGravity(false);
			nick.setCustomName("§d" + e.getEntity().getCustomName() + "§f выебан!~ ");
			BukkitRunnable run = new BukkitRunnable() {
				@Override
				public void run() {
					try {if(nick != null) {nick.remove();}
					} catch(Exception e){cancel();}
				}
			};
			run.runTaskLater(c.Main.getInstance(), 200L);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void fuck(EntityDamageByEntityEvent e) {
		Entity dick = e.getDamager();
		Entity hole = e.getEntity();
		if(dick instanceof Player && hole instanceof Zombie) {
			if(data.getInt(dick.getName() + ".sound") == 0) {((Player) dick).playSound(hole.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);}
			else if(data.getInt(dick.getName() + ".sound") == 1) {((Player) dick).playSound(hole.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);}
			if(hole.getPassenger() != null) {hole.getPassenger().setCustomName("§6§l " + hole.getCustomName() + " §f" + (int) (((Damageable) hole).getHealth()-e.getDamage()) + "§c§l" + Messages.heart + " ");}
			if(((Damageable) hole).getHealth() < 30){((LivingEntity) hole).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 2), false);}
			if(((Damageable) hole).getHealth() < 20){((LivingEntity) hole).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999999, 1), false);}
			if(((Damageable) hole).getHealth() < 10){((LivingEntity) dick).setHealth(((LivingEntity) dick).getHealth()-1);}
		}
	}
	
	@EventHandler
	public void NPCint(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked().getType() == EntityType.ARMOR_STAND && e.getPlayer().getWorld() == Bukkit.getWorld("Test")) {
			Inventory inv = Bukkit.createInventory(null, 27, "§8Вы убили зомбу(");
			
			ItemStack empty = new ItemStack(Material.WEB, 1, (short)0);
			ItemMeta emptymeta = empty.getItemMeta();
			emptymeta.setDisplayName("§8 Паутинка ");
			empty.setItemMeta(emptymeta);
			
			ItemStack i = new ItemStack(Material.ROTTEN_FLESH, 1, (short)0);
			ItemMeta m = i.getItemMeta();
			m.setDisplayName("§a " + p.getName() + " ");
			i.setItemMeta(m);

			for(int с = 0; с < 27; с++) {
				int ri = (int) (Math.random()*40);
				if(ri < 10) {inv.setItem(с, empty);}
			}
			inv.setItem((int) (Math.random()*27), i);
			
			p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 2);
			p.openInventory(inv);
			e.getRightClicked().remove();
			return;
		}
	}
	
	public static void sound(Player p) {
		Inventory menu = Bukkit.createInventory(null, 27, "§1Выбор звука пиздюлей зомбу");
		ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1,(short)15);
		ItemStack empty1 = new ItemStack(Material.STAINED_GLASS_PANE, 1,(short)4);
		ItemStack i1 = new ItemStack(Material.ARMOR_STAND, 1,(short)0);
		ItemStack i2 = new ItemStack(Material.EXP_BOTTLE, 1,(short)0);

		ItemMeta emptymeta = empty.getItemMeta();
		emptymeta.setDisplayName("§8");
		empty.setItemMeta(emptymeta);
		ItemMeta empty1meta = empty1.getItemMeta();
		empty1meta.setDisplayName("§8");
		empty1.setItemMeta(empty1meta);

		ItemMeta m1 = i1.getItemMeta();
		m1.setDisplayName("§e Удар по игроку ");
		if(data.getInt(p.getName() + ".sound") == 0) {m1.setDisplayName("§e §nУдар по игроку§7 (Выбран) ");}
		i1.setItemMeta(m1);

		ItemMeta m2 = i2.getItemMeta();
		m2.setDisplayName("§a Шипы ");
		if(data.getInt(p.getName() + ".sound") == 1) {m2.setDisplayName("§a §nШипы§7 (Выбран) ");}
		i2.setItemMeta(m2);

		menu.setItem(0, empty);
		menu.setItem(1, empty);
		menu.setItem(2, empty);
		menu.setItem(3, empty);
		menu.setItem(4, empty);
		menu.setItem(5, empty);
		menu.setItem(6, empty);
		menu.setItem(7, empty);
		menu.setItem(8, empty);
		menu.setItem(9, empty);
		menu.setItem(10, empty1);
		menu.setItem(11, empty1);
		menu.setItem(13, empty1);
		menu.setItem(15, empty1);
		menu.setItem(16, empty1);
		menu.setItem(17, empty);
		menu.setItem(18, empty);
		menu.setItem(19, empty);
		menu.setItem(20, empty);
		menu.setItem(21, empty);
		menu.setItem(22, empty);
		menu.setItem(23, empty);
		menu.setItem(24, empty);
		menu.setItem(25, empty);
		menu.setItem(26, empty);

		menu.setItem(12, i1);
		menu.setItem(14, i2);
		
		p.openInventory(menu);	
	}
	
	@EventHandler
	public void InvInt(InventoryClickEvent e) {
		try {
			Player p = (Player) e.getWhoClicked();
			if(e.getClickedInventory().getName().equals("§8Вы убили зомбу(")) {
				if(e.getCurrentItem().getType() == Material.WEB) {
					e.setCancelled(true);
					e.getCurrentItem().setAmount(0);
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 3);
				}
				if(e.getCurrentItem().getType() == Material.ROTTEN_FLESH) {
					e.setCancelled(true);
					p.getInventory().addItem(e.getCurrentItem());
					e.getCurrentItem().setAmount(0);
					p.playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 3);
					p.closeInventory();
				}
			}
			else if(e.getClickedInventory().getName().equals("§1Выбор звука пиздюлей зомбу")) {
				if(e.getSlot() == 12) {
					data.set(p.getName() + ".sound", 0);
					p.playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 3);
					e.setCancelled(true);
					sound(p);
				}
				else if(e.getSlot() == 14) {
					data.set(p.getName() + ".sound", 1);
					p.playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 3);
					e.setCancelled(true);
					sound(p);
				}
				else {
					e.setCancelled(true);
				}
			}
		} catch(Exception e2) {}
	}
}
