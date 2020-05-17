package com.cnaude.chairs.core;

import java.text.MessageFormat;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.entity.Player;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.data.type.Stairs.Shape;

public class SitUtils {
	protected final Chairs plugin;
	protected final ChairsConfig config;
	protected final PlayerSitData sitdata;
	public SitUtils(final Chairs plugin) {
		this.plugin = plugin;
		this.config = plugin.getChairsConfig();
		this.sitdata = plugin.getPlayerSitData();
	}

	protected boolean canSitGeneric(final Player player, final Block block) {
		if (player.isSneaking())
			return false;
		if (!player.hasPermission("chairs.sit"))
			return false;

		if (config.sitDisabledWorlds.contains(player.getWorld().getName()))
			return false;
		if ((config.sitMaxDistance > 0) && (player.getLocation().distance(block.getLocation().add(0.5, 0, 0.5)) > config.sitMaxDistance))
			return false;
		if (config.sitRequireEmptyHand && (player.getInventory().getItemInMainHand().getType() != Material.AIR))
			return false;

		if (sitdata.isSittingDisabled(player.getUniqueId()))
			return false;
		if (sitdata.isSitting(player))
			return false;
		if (sitdata.isBlockOccupied(block))
			return false;

		return true;
	}

	public Location calculateSitLocation(final Player player, final Block block) {
		if (!canSitGeneric(player, block))
			return null;

		final BlockData blockdata = block.getBlockData();
		float yaw = player.getLocation().getYaw();
		Double sitHeight = null;

		if ((blockdata instanceof Stairs) && config.stairsEnabled) {
			sitHeight = 0.5;
			final Stairs stairs = (Stairs) blockdata;
			if (!isStairsSittable(stairs))
				return null;

			final BlockFace ascendingFacing = stairs.getFacing();
			if (config.stairsAutoRotate) {
				switch (ascendingFacing.getOppositeFace()) {
					case NORTH: {
						yaw = 180;
						break;
					}
					case EAST: {
						yaw = -90;
						break;
					}
					case SOUTH: {
						yaw = 0;
						break;
					}
					case WEST: {
						yaw = 90;
						break;
					}
					default: {}
				}
			}
			if (config.stairsMaxWidth > 0) {
				final BlockFace facingLeft = rotL(ascendingFacing);
				final BlockFace facingRight = rotR(ascendingFacing);
				final int widthLeft = calculateStairsWidth(ascendingFacing, block, facingLeft, config.stairsMaxWidth);
				final int widthRight = calculateStairsWidth(ascendingFacing, block, facingRight, config.stairsMaxWidth);
				if ((widthLeft + widthRight + 1) > config.stairsMaxWidth)
					return null;
				if (config.stairsSpecialEndEnabled) {
					boolean specialEndCheckSuccess = false;
					final Block blockLeft = block.getRelative(facingLeft, widthLeft + 1);
					final Block blockRight = block.getRelative(facingRight, widthRight + 1);
					if (
						config.stairsSpecialEndSign &&
						isStairsEndingSign(facingLeft, blockLeft) &&
						isStairsEndingSign(facingRight, blockRight)
					) {
						specialEndCheckSuccess = true;
					}
					if (
						config.stairsSpecialEndCornerStairs && (
							isStairsEndingCornerStairs(facingLeft, Stairs.Shape.INNER_RIGHT, blockLeft) ||
							isStairsEndingCornerStairs(ascendingFacing, Stairs.Shape.INNER_LEFT, blockLeft)
						) && (
							isStairsEndingCornerStairs(facingRight, Stairs.Shape.INNER_LEFT, blockRight) ||
							isStairsEndingCornerStairs(ascendingFacing, Stairs.Shape.INNER_RIGHT, blockRight)
						)
					) {
						specialEndCheckSuccess = true;
					}
					if (!specialEndCheckSuccess)
						return null;
				}
			}
		}

		if (sitHeight == null) {
			sitHeight = config.additionalChairs.get(blockdata.getMaterial());
			if (sitHeight == null)
				return null;
		}

		final Location plocation = block.getLocation();
		plocation.setYaw(yaw);
		plocation.add(0.5D, (sitHeight - 0.5D), 0.5D);
		return plocation;
	}

	protected static final boolean isStairsSittable(final Stairs stairs) {
		return (stairs.getHalf() == Half.BOTTOM) && (stairs.getShape() == Shape.STRAIGHT);
	}

	protected static boolean isStairsEndingSign(final BlockFace expectedFacing, final Block block) {
		final BlockData blockdata = block.getBlockData();
		if (blockdata instanceof WallSign)
			return expectedFacing == ((WallSign) blockdata).getFacing();
		return false;
	}

	protected static boolean isStairsEndingCornerStairs(final BlockFace expectedFacing, final Stairs.Shape expectedShape, final Block block) {
		final BlockData blockdata = block.getBlockData();
		if (blockdata instanceof Stairs) {
			final Stairs stairs = (Stairs) blockdata;
			return (stairs.getHalf() == Half.BOTTOM) && (stairs.getFacing() == expectedFacing) && (stairs.getShape() == expectedShape);
		}
		return false;
	}

	protected int calculateStairsWidth(final BlockFace expectedFace, Block block, final BlockFace searchFace, final int limit) {
		for (int i = 0; i < limit; i++) {
			block = block.getRelative(searchFace);
			BlockData blockdata = block.getBlockData();
			if (!(blockdata instanceof Stairs))
				return i;
			final Stairs stairs = (Stairs) blockdata;
			if (!isStairsSittable(stairs) || (stairs.getFacing() != expectedFace)) {
				return i;
			}
		}
		return limit;
	}

	protected static BlockFace rotL(final BlockFace face) {
		switch (face) {
			case NORTH: {
				return BlockFace.WEST;
			}
			case WEST: {
				return BlockFace.SOUTH;
			}
			case SOUTH: {
				return BlockFace.EAST;
			}
			case EAST: {
				return BlockFace.NORTH;
			}
			default: {
				throw new IllegalArgumentException(MessageFormat.format("Cant rotate blockface {0}", face));
			}
		}
	}

	protected static BlockFace rotR(final BlockFace face) {
		switch (face) {
			case NORTH: {
				return BlockFace.EAST;
			}
			case EAST: {
				return BlockFace.SOUTH;
			}
			case SOUTH: {
				return BlockFace.WEST;
			}
			case WEST: {
				return BlockFace.NORTH;
			}
			default: {
				throw new IllegalArgumentException(MessageFormat.format("Cant rotate blockface {0}", face));
			}
		}
	}
}