package com.legacy.aether.events;

import com.legacy.aether.AetherConfig;
import com.legacy.aether.entities.util.EntityMountable;
import com.legacy.aether.world.TeleporterAether;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AetherEntityEvents
{
	@SubscribeEvent
	public void onEntityUpdate(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent event)
	{
		if (event.getEntity() instanceof EntityLiving) entityUpdateEvents((EntityLiving) event.getEntity(), event);
	}
	
	private void entityUpdateEvents(EntityLiving entity, Event event)
	{
		if (entity instanceof EntityMountable)
		{
			//EntityMountable mountable = (EntityMountable) entity;
			
			if (entity.dimension == AetherConfig.dimension.aether_dimension_id && !entity.world.isRemote)
			{
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int previousDimension = entity.dimension;
				int transferDimension = previousDimension == AetherConfig.dimension.aether_dimension_id ? 0 : AetherConfig.dimension.aether_dimension_id;
				
				if (entity.posY <= 0)
				{
					for (Entity passenger : entity.getPassengers())
					{
						passenger.dismountRidingEntity();
					}
					
					entity.timeUntilPortal = 300;
					transferEntity(false, entity, server.getWorld(previousDimension), server.getWorld(transferDimension));	
				}
			}
		}
	}
	
	public static void transferEntity(boolean shouldSpawnPortal, Entity entityIn, WorldServer previousWorldIn, WorldServer newWorldIn)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		entityIn.dimension = newWorldIn.provider.getDimension();
		previousWorldIn.removeEntityDangerously(entityIn);
		entityIn.isDead = false;

		server.getPlayerList().transferEntityToWorld(entityIn, previousWorldIn.provider.getDimension(), previousWorldIn, newWorldIn, new TeleporterAether(false, newWorldIn));
	}
}
