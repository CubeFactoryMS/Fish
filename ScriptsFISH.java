package c.Fishing;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import c.Main;
import c.Messages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ScriptsFISH implements Listener {

	// Zone

	@EventHandler
	public void Jump(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if(p.getWorld() == Bukkit.getWorld("Fishing") && p.getLocation().getBlockY() < 10.0 && p.getGameMode() != GameMode.CREATIVE) {
			tp(e.getPlayer());
		}
		else if(p.getWorld() == Bukkit.getWorld("Fishing") && p.getLocation().getBlockZ() > 95.0 && p.getGameMode() != GameMode.CREATIVE) {
			tp(e.getPlayer());
		}
	}
	
	private void tp(Player p) {
		p.teleport(new Location(Bukkit.getWorld("Fishing"), 939.5, 11.01, 58.5, -90, 0));
	}
	
	// Fishing

	@EventHandler
	public void Fishing(PlayerFishEvent e){
		if(e.getPlayer().getExp() > 0) {
			e.setCancelled(true);
			try {e.getPlayer().setExp(e.getPlayer().getExp()+((float) 0.1));} catch(Exception e2) {}
		}
		else if((e.getCaught() instanceof Item)){
			if(e.getPlayer().getExp() == 0) {e.getPlayer().setExp((float) 0.5);}
			e.setCancelled(true);
			e.getCaught().remove();
			e.setExpToDrop(0);
			

			BukkitRunnable run = new BukkitRunnable() {
				int t;
				@Override
				public void run() {
					try {
						t++;
						if(t == 50) {fish(e.getPlayer());}
						else if(t > 50) {return;}
						if(e.getPlayer().getExp() > 0 && e.getPlayer().getExp() < ((float) 0.4)) {e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(" §a§lТяни! ").create());}
						else if(e.getPlayer().getExp() <= ((float) 0.6)) {e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(" §2§lМолодчина! ").create());}
						else {e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(" §a§lОтпусти! ").create());}
						try {e.getPlayer().setExp(e.getPlayer().getExp()-((float) 0.015));} catch(Exception e2) {}

					} catch(Exception e){
						cancel();
					}
				}
			};
			run.runTaskTimer(c.Main.getInstance(), 2L, 2L);
		}
	}
			
	public void fish(Player p) {
		String uuid = p.getUniqueId().toString();
		if(p.getExp() < ((float) 0.4) || p.getExp() > ((float) 0.6)) {
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(" §eРыба сорвалась! ").create());
			p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
			p.setExp(0);
			return;
		}
		p.setExp(0);
		String cr = Messages.crystal;

		int r = (int) (1+Math.random()*69+Main.getInstance().getConfig().getInt("Fishing." + uuid + ".lootChance"));
		
		//Fish
		
		if(r >= 30) {
			int r2 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".string"));
			if(r2 == 1) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (28*r3+Math.random()*150);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.RAW_FISH, 1,(short)0);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Окунь §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr + "§8" + cr+cr+cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-1-1");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
			if(r2 == 2) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (20*r3+Math.random()*25);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.RAW_FISH, 1,(short)1);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Ерш §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr + "§8" + cr+cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-1-2");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
			if(r2 >= 3) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (52*r3+Math.random()*125);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.COOKED_FISH, 1,(short)0);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Судак §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr+cr + "§8" + cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-1-3");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
		}
		
		//Treasure
		
		else if(r < 40) {
			int r2 = (int) (Math.random()*(1+Main.getInstance().getConfig().getInt("Fishing." + uuid + ".string")));
			if(r2 == 0) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (4*r3+Math.random());
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.DOUBLE_PLANT, r3,(short)0);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Монетка §7(1ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr+cr+cr+cr);
					il.add("§7 Масса §8§l" + Messages.close1 + " §a4г ");
					il.add("§8 1-0-0");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					r2 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".string"));
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					r2 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".string"));
				}
			}
			if(r2 == 1) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (15*r3+Math.random()*100);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.INK_SACK, 1,(short)2);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Водоросль §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr + "§8" + cr+cr+cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-2-1");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
			else if(r2 == 2) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (80*r3+Math.random()*200);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.DEAD_BUSH, 1,(short)0);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Палка §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr + "§8" + cr+cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-2-2");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
			else if(r2 == 3) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (125*r3+Math.random()*200);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.VINE, 1,(short)0);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Тина §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr+cr + "§8" + cr+cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-2-3");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
			else if(r2 == 4) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (40*r3+Math.random()*30);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.INK_SACK, 1,(short)8);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Беззубка §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr+cr+cr + "§8" + cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-2-4");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
			else if(r2 == 5) {
				int r3 = (int) (1+Math.random()*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".fishrod"));
				int r4 = (int) (8*r3+Math.random()*20);
				if(r4 <= 150+30*Main.getInstance().getConfig().getInt("Fishing." + uuid + ".rod")) {
					ItemStack i = new ItemStack(Material.FLOWER_POT_ITEM, 1,(short)0);
					ItemMeta  im =  i.getItemMeta();
					ArrayList<String> il = new ArrayList<>();
					im.setDisplayName("§f Стаканчик §7(" + r3 + "ур) ");
					il.add("§8");
					il.add("§7 Редкость §8§l" + Messages.close1 + " §e" + cr+cr+cr+cr+cr + " ");
					if(r4 > 1000) {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + (int)((double) r4/1000) + "кг ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + (int)((double) r4/1000) + "кг");}
					else {il.add("§7 Масса §8§l" + Messages.close1 + " §a" + r4 + "г ");
					p.sendMessage(" §aВы поймали"+ im.getDisplayName() + "§aмассой §f" + r4 + "г");}
					il.add("§8 1-2-5");
					im.setLore(il);
					i.setItemMeta(im);
					p.getInventory().addItem(i);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".kg", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".kg")+r4);
					Main.getInstance().getConfig().set("Fishing." + uuid + ".count", Main.getInstance().getConfig().getDouble("Fishing." + uuid + ".count")+1);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SWIM, 1, 1);
					return;
				}
				else {
					p.sendMessage(" §eУлов оказался слишком тяжелым!");
					p.playSound(p.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), false);
					return;
				}
			}
		}
	
	}
	
// Summons
			
	public static void NPCs() {
		for (Entity e : Bukkit.getWorld("Fishing").getEntities()) {
			if (!e.getType().equals(EntityType.PLAYER)) {
				e.remove();
			}
		}
		BukkitRunnable run = new BukkitRunnable() {
			@Override
			public void run() {
		
				// NPCs
		
				Witch npc1 = (Witch) Bukkit.getWorld("Fishing").spawnEntity(new Location(Bukkit.getWorld("Fishing"), 954.5, 11, 59.5, 90, 0), EntityType.WITCH);
				npc1.setCustomName(" §aПродать улов! ");
				npc1.setCustomNameVisible(true);
				npc1.setInvulnerable(true);
				npc1.setSilent(true);
				npc1.setAI(false);
				npc1.setCollidable(false);
			}
		};
		run.runTaskLater(c.Main.getInstance(), 20L);
	}

// NPC interact
		
	@EventHandler
	public void NPCint(PlayerInteractAtEntityEvent e) {
		e.setCancelled(true);
		if(e.getPlayer().getWorld() == Bukkit.getWorld("Fishing") && e.getRightClicked().getCustomName().equals(" §aПродать улов! ")) {
			Inventory menu = Bukkit.createInventory(null, 9, "§4Продажа");
			ItemStack i = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta m = i.getItemMeta();
			ArrayList<String> l = new ArrayList<>();
			m.setDisplayName("§a§l Продать!");
			m.setLore(l);
			i.setItemMeta(m);
			menu.setItem(8, i);
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
			e.getPlayer().openInventory(menu);
			e.setCancelled(true);
		}
	}
}
