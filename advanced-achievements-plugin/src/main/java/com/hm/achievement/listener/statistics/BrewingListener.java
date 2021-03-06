package com.hm.achievement.listener.statistics;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.hm.achievement.AdvancedAchievements;
import com.hm.achievement.category.NormalAchievements;
import com.hm.achievement.config.AchievementMap;
import com.hm.achievement.db.CacheManager;
import com.hm.achievement.utils.InventoryHelper;
import com.hm.achievement.utils.MaterialHelper;
import com.hm.achievement.utils.RewardParser;

/**
 * Listener class to deal with Brewing achievements.
 * 
 * @author Pyves
 *
 */
@Singleton
public class BrewingListener extends AbstractRateLimitedListener {

	private final MaterialHelper materialHelper;
	private final InventoryHelper inventoryHelper;

	@Inject
	public BrewingListener(@Named("main") YamlConfiguration mainConfig, int serverVersion, AchievementMap achievementMap,
			CacheManager cacheManager, RewardParser rewardParser, AdvancedAchievements advancedAchievements,
			@Named("lang") YamlConfiguration langConfig, Logger logger, MaterialHelper materialHelper,
			InventoryHelper inventoryHelper) {
		super(NormalAchievements.BREWING, mainConfig, serverVersion, achievementMap, cacheManager, rewardParser,
				advancedAchievements, langConfig, logger);
		this.materialHelper = materialHelper;
		this.inventoryHelper = inventoryHelper;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (event.getInventory().getType() != InventoryType.BREWING || event.getAction() == InventoryAction.NOTHING
				|| event.getClick() == ClickType.NUMBER_KEY && event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
				|| !isBrewablePotion(item)) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		int eventAmount = item.getAmount();
		if (event.isShiftClick()) {
			eventAmount = Math.min(eventAmount, inventoryHelper.getAvailableSpace(player, item));
			if (eventAmount == 0) {
				return;
			}
		}

		updateStatisticAndAwardAchievementsIfAvailable(player, eventAmount, event.getRawSlot());
	}

	/**
	 * Determine whether the event corresponds to a brewable potion, i.e. not water.
	 * 
	 * @param item
	 * @return true if for any brewable potion
	 */
	private boolean isBrewablePotion(ItemStack item) {
		return item != null &&
				(materialHelper.isAnyPotionButWater(item) || serverVersion >= 9 && item.getType() == Material.SPLASH_POTION);
	}
}
