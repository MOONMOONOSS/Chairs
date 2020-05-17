package com.cnaude.chairs.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChairsConfig {
	protected final Chairs plugin;
	protected final ChairsConfig(Chairs plugin) {
		this.plugin = plugin;
	}

	protected static final String sitConfigSectionPath = "sit-config";
	protected static final String sitConfigDisabledWorldsPath = "disabled-worlds";
	protected static final String sitConfigDistancePath = "distance";
	protected static final String sitConfigRequireEmptyHandPath = "require-empty-hand";

	protected static final String sitConfigStairsSectionPath = "stairs";
	protected static final String sitConfigStairsEnabledPath = "enabled";
	protected static final String sitConfigStairsRotatePath = "rotate";
	protected static final String sitConfigStairsMaxWidthPath = "max-width";
	protected static final String sitConfigStairsSpecialEndPath = "special-end";
	protected static final String sitConfigStairsSpecialEndSignPath = "sign";
	protected static final String sitConfigStairsSpecialEndCornerStairsPath = "corner-stairs";

	protected static final String sitConfigAdditionalChairsPath = "additional-blocks";

	protected static final String sitEffectsSectionPath = "sit-effects";

	protected static final String sitEffectsHealingSectionPath = "healing";
	protected static final String sitEffectsHealingEnabledPath = "enabled";
	protected static final String sitEffectsHealingMaxPercentPath = "max-percent";
	protected static final String sitEffectsHealingIntervalPath = "interval";
	protected static final String sitEffectsHealingAmountPath = "amount";

	protected static final String sitEffectsItempickupPath = "itempickup";
	protected static final String sitEffectsItempickupEnabledPath = "enabled";

	protected static final String sitRestrictionsSectionPath = "sit-restrictions";
	protected static final String sitRestricitonsCommandsSectionPath = "commands";
	protected static final String sitRestrictionsCommandsBlockAllPath = "all";
	protected static final String sitRestrictionsCommandsBlockListPath = "list";

	protected static final String msgSectionPath = "messages";
	protected static final String msgEnabledPath = "enabled";
	protected static final String msgSitSectionPath = "sit";
	protected static final String msgSitEnterPath = "enter";
	protected static final String msgSitLeavePath = "leave";
	protected static final String msgSitEnabledPath = "enabled";
	protected static final String msgSitDisabledPath = "disabled";
	protected static final String msgSitCommandRestrictedPath = "commandrestricted";


	public final Set<String> sitDisabledWorlds = new final HashSet<>();
	public boolean sitRequireEmptyHand = false;
	public double sitMaxDistance = 2;

	public boolean stairsEnabled = true;
	public boolean stairsAutoRotate = true;
	public int stairsMaxWidth = 16;
	public boolean stairsSpecialEndEnabled = false;
	public boolean stairsSpecialEndSign = true;
	public boolean stairsSpecialEndCornerStairs = true;

	public final Map<Material, Double> additionalChairs = new final EnumMap<>(Material.class);

	public boolean effectsHealEnabled = false;
	public int effectsHealMaxHealth = 100;
	public int effectsHealInterval = 20;
	public int effectsHealHealthPerInterval = 1;
	public boolean effectsItemPickupEnabled = false;

	public boolean restrictionsDisableAllCommands = false;
	public final Set<String> restrictionsDisabledCommands = new final HashSet<>();

	public boolean msgEnabled = true;
	public final String msgSitEnter = "&7You are now sitting.";
	public final String msgSitLeave = "&7You are no longer sitting.";
	public final String msgSitDisabled = "&7You have disabled chairs for yourself!";
	public final String msgSitEnabled = "&7You have enabled chairs for yourself!";
	public final String msgSitCommandRestricted = "&7You can't issue this command while sitting";

	public void reloadConfig() {
		final File file = new final File(plugin.getDataFolder(), "config.yml");

		{
			final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			final ConfigurationSection sitConfigSection = config.getConfigurationSection(sitConfigSectionPath);
			if (sitConfigSection != null) {
				sitDisabledWorlds.clear();
				sitDisabledWorlds.addAll(sitConfigSection.getStringList(sitConfigDisabledWorldsPath));
				sitRequireEmptyHand = sitConfigSection.getBoolean(sitConfigRequireEmptyHandPath, sitRequireEmptyHand);

				ConfigurationSection sitConfigStairsSection = sitConfigSection.getConfigurationSection(sitConfigStairsSectionPath);
				if (sitConfigStairsSection != null) {
					stairsEnabled = sitConfigStairsSection.getBoolean(sitConfigStairsEnabledPath, stairsEnabled);
					stairsAutoRotate = sitConfigStairsSection.getBoolean(sitConfigStairsRotatePath, stairsAutoRotate);
					stairsMaxWidth = sitConfigStairsSection.getInt(sitConfigStairsMaxWidthPath, stairsMaxWidth);
					final ConfigurationSection sitConfigStairsSpecialEndSection = sitConfigStairsSection.getConfigurationSection(sitConfigStairsSpecialEndPath);
					if (sitConfigStairsSpecialEndSection != null) {
						stairsSpecialEndSign = sitConfigStairsSpecialEndSection.getBoolean(sitConfigStairsSpecialEndSignPath, stairsSpecialEndSign);
						stairsSpecialEndCornerStairs = sitConfigStairsSpecialEndSection.getBoolean(sitConfigStairsSpecialEndCornerStairsPath, stairsSpecialEndCornerStairs);
						stairsSpecialEndEnabled = stairsSpecialEndSign || stairsSpecialEndCornerStairs;
					}
				}

				final ConfigurationSection sitConfigAdditionalBlocksSection = sitConfigSection.getConfigurationSection(sitConfigAdditionalChairsPath);
				if (sitConfigAdditionalBlocksSection != null) {
					for (final String materialName : sitConfigAdditionalBlocksSection.getKeys(false)) {
						final Material material = Material.getMaterial(materialName);
						if (material != null)
							additionalChairs.put(material, sitConfigAdditionalBlocksSection.getDouble(materialName));
					}
				}
			}

			final ConfigurationSection sitEffectsSection = config.getConfigurationSection(sitEffectsSectionPath);
			if (sitEffectsSection != null) {
				final ConfigurationSection sitEffectsHealSection = sitEffectsSection.getConfigurationSection(sitEffectsHealingSectionPath);
				if (sitEffectsHealSection != null) {
					effectsHealEnabled = sitEffectsHealSection.getBoolean(sitEffectsHealingEnabledPath, effectsHealEnabled);
					effectsHealMaxHealth = sitEffectsHealSection.getInt(sitEffectsHealingMaxPercentPath, effectsHealMaxHealth);
					effectsHealInterval = sitEffectsHealSection.getInt(sitEffectsHealingIntervalPath, effectsHealInterval);
					effectsHealHealthPerInterval = sitEffectsHealSection.getInt(sitEffectsHealingAmountPath, effectsHealHealthPerInterval);
				}

				final ConfigurationSection sitEffectsItempickupSection = sitEffectsSection.getConfigurationSection(sitEffectsItempickupPath);
				if (sitEffectsItempickupSection != null)
					effectsItemPickupEnabled = sitEffectsItempickupSection.getBoolean(sitEffectsItempickupEnabledPath, effectsItemPickupEnabled);
			}

			final ConfigurationSection sitRestirctionsSection = config.getConfigurationSection(sitRestrictionsSectionPath);
			if (sitRestirctionsSection != null) {
				final ConfigurationSection sitRestrictionsCommandsSection = sitRestirctionsSection.getConfigurationSection(sitRestricitonsCommandsSectionPath);

				if (sitRestrictionsCommandsSection != null) {
					restrictionsDisableAllCommands = sitRestrictionsCommandsSection.getBoolean(sitRestrictionsCommandsBlockAllPath, restrictionsDisableAllCommands);
					restrictionsDisabledCommands.clear();
					restrictionsDisabledCommands.addAll(sitRestrictionsCommandsSection.getStringList(sitRestrictionsCommandsBlockListPath));
				}
			}

			final ConfigurationSection msgSection = config.getConfigurationSection(msgSectionPath);
			if (msgSection != null) {
				msgEnabled = msgSection.getBoolean(msgEnabledPath, msgEnabled);
				final ConfigurationSection msgSitSection = msgSection.getConfigurationSection(msgSitSectionPath);
				if (msgSitSection != null) {
					msgSitEnter = msgSitSection.getString(msgSitEnterPath, msgSitEnter);
					msgSitLeave = msgSitSection.getString(msgSitLeavePath, msgSitLeave);
					msgSitEnabled = msgSitSection.getString(msgSitEnabledPath, msgSitEnabled);
					msgSitDisabled = msgSitSection.getString(msgSitDisabledPath, msgSitDisabled);
					msgSitCommandRestricted = msgSitSection.getString(msgSitCommandRestrictedPath, msgSitCommandRestricted);
				}
			}
		}

		{
			final FileConfiguration config = new final YamlConfiguration();

			final ConfigurationSection sitConfigSection = config.createSection(sitConfigSectionPath);
			{
				sitConfigSection.set(sitConfigDisabledWorldsPath, new final ArrayList<>(sitDisabledWorlds));
				sitConfigSection.set(sitConfigRequireEmptyHandPath, sitRequireEmptyHand);

				final ConfigurationSection sitConfigStairsSection = sitConfigSection.createSection(sitConfigStairsSectionPath);
				{
					sitConfigStairsSection.set(sitConfigStairsEnabledPath, stairsEnabled);
					sitConfigStairsSection.set(sitConfigStairsRotatePath, stairsAutoRotate);
					sitConfigStairsSection.set(sitConfigStairsMaxWidthPath, stairsMaxWidth);
					final ConfigurationSection sitConfigStairsSpecialEndSection = sitConfigStairsSection.createSection(sitConfigStairsSpecialEndPath);
					{
						sitConfigStairsSpecialEndSection.set(sitConfigStairsSpecialEndSignPath, stairsSpecialEndSign);
						sitConfigStairsSpecialEndSection.set(sitConfigStairsSpecialEndCornerStairsPath, stairsSpecialEndCornerStairs);
					}
				}

				final ConfigurationSection sitConfigAdditionalBlocksSection = sitConfigSection.createSection(sitConfigAdditionalChairsPath);
				{
					for (final Entry<final Material, final Double> entry : additionalChairs.entrySet()) {
						sitConfigAdditionalBlocksSection.set(entry.getKey().toString(), entry.getValue());
					}
				}
			}

			final ConfigurationSection sitEffectsSection = config.createSection(sitEffectsSectionPath);
			{
				final ConfigurationSection sitEffectsHealSection = sitEffectsSection.createSection(sitEffectsHealingSectionPath);
				{
					sitEffectsHealSection.set(sitEffectsHealingEnabledPath, effectsHealEnabled);
					sitEffectsHealSection.set(sitEffectsHealingMaxPercentPath, effectsHealMaxHealth);
					sitEffectsHealSection.set(sitEffectsHealingIntervalPath, effectsHealInterval);
					sitEffectsHealSection.set(sitEffectsHealingAmountPath, effectsHealHealthPerInterval);
				}

				final ConfigurationSection sitEffectsItempickupSection = sitEffectsSection.createSection(sitEffectsItempickupPath);
				{
					sitEffectsItempickupSection.set(sitEffectsItempickupEnabledPath, effectsItemPickupEnabled);
				}
			}

			final ConfigurationSection sitRestirctionsSection = config.createSection(sitRestrictionsSectionPath);
			{
				final ConfigurationSection sitRestrictionsCommandsSection = sitRestirctionsSection.createSection(sitRestricitonsCommandsSectionPath);
				{
					sitRestrictionsCommandsSection.set(sitRestrictionsCommandsBlockAllPath, restrictionsDisableAllCommands);
					sitRestrictionsCommandsSection.set(sitRestrictionsCommandsBlockListPath, new final ArrayList<>(restrictionsDisabledCommands));
				}
			}

			final ConfigurationSection msgSection = config.createSection(msgSectionPath);
			{
				msgSection.set(msgEnabledPath, msgEnabled);
				final ConfigurationSection msgSitSection = msgSection.createSection(msgSitSectionPath);
				{
					msgSitSection.set(msgSitEnterPath, msgSitEnter);
					msgSitSection.set(msgSitLeavePath, msgSitLeave);
					msgSitSection.set(msgSitEnabledPath, msgSitEnabled);
					msgSitSection.set(msgSitDisabledPath, msgSitDisabled);
					msgSitSection.set(msgSitCommandRestrictedPath, msgSitCommandRestricted);
				}
			}

			try {config.save(file);} catch (IOException e) {}
		}
	}
}
