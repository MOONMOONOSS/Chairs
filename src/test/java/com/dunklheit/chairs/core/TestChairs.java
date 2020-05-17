package com.dunklheit.chairs.core;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import com.cnaude.chairs.core.Chairs;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestChairs {
  private ServerMock server;
  private Chairs plugin;

  @Before
  public void setUp() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(Chairs.class);
  }

  @After
  public void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  public void spawnsArrow() {
    final WorldMock world = new WorldMock();
    final Location spawn = new Location(world, 69.0, 64.0, 420.0);
    final Entity arrow = Chairs.spawnChairsArrow(spawn);

    assertEquals(arrow.getLocation(), spawn);
  }
}
