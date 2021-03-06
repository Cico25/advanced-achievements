package com.hm.achievement.module;

import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import org.bukkit.configuration.file.YamlConfiguration;

import com.hm.achievement.AdvancedAchievements;
import com.hm.achievement.config.AchievementMap;
import com.hm.achievement.db.AbstractDatabaseManager;
import com.hm.achievement.db.DatabaseUpdater;
import com.hm.achievement.db.H2DatabaseManager;
import com.hm.achievement.db.MySQLDatabaseManager;
import com.hm.achievement.db.PostgreSQLDatabaseManager;
import com.hm.achievement.db.SQLiteDatabaseManager;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

	@Provides
	@Singleton
	AbstractDatabaseManager provideSQLDatabaseManager(@Named("main") YamlConfiguration mainConfig, Logger logger,
			AchievementMap achievementMap, DatabaseUpdater databaseUpdater, AdvancedAchievements advancedAchievements) {
		String databaseType = advancedAchievements.getConfig().getString("DatabaseType", "sqlite");
		if ("mysql".equalsIgnoreCase(databaseType)) {
			return new MySQLDatabaseManager(mainConfig, logger, achievementMap, databaseUpdater);
		} else if ("postgresql".equalsIgnoreCase(databaseType)) {
			return new PostgreSQLDatabaseManager(mainConfig, logger, achievementMap, databaseUpdater);
		} else if ("h2".equalsIgnoreCase(databaseType)) {
			return new H2DatabaseManager(mainConfig, logger, achievementMap, databaseUpdater, advancedAchievements);
		} else {
			// User has specified "sqlite" or an invalid type.
			return new SQLiteDatabaseManager(mainConfig, logger, achievementMap, databaseUpdater, advancedAchievements);
		}
	}

}
