package org.valkyrienskies.addon.control;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.valkyrienskies.addon.control.block.multiblocks.GiantPropellerMultiblockSchematic;
import org.valkyrienskies.addon.control.block.multiblocks.RudderAxleMultiblockSchematic;
import org.valkyrienskies.addon.control.block.multiblocks.TileEntityGiantPropellerPart;
import org.valkyrienskies.addon.control.block.multiblocks.TileEntityRudderPart;
import org.valkyrienskies.addon.control.block.multiblocks.TileEntityValkyriumCompressorPart;
import org.valkyrienskies.addon.control.block.multiblocks.TileEntityValkyriumEnginePart;
import org.valkyrienskies.addon.control.block.multiblocks.ValkyriumCompressorMultiblockSchematic;
import org.valkyrienskies.addon.control.block.multiblocks.ValkyriumEngineMultiblockSchematic;
import org.valkyrienskies.addon.control.block.torque.TileEntityRotationAxle;
import org.valkyrienskies.addon.control.capability.ICapabilityLastRelay;
import org.valkyrienskies.addon.control.capability.ImplCapabilityLastRelay;
import org.valkyrienskies.addon.control.capability.StorageLastRelay;
import org.valkyrienskies.addon.control.item.ItemRelayWire;
import org.valkyrienskies.addon.control.item.ItemWrench;
import org.valkyrienskies.addon.control.network.MessagePlayerStoppedPiloting;
import org.valkyrienskies.addon.control.network.MessagePlayerStoppedPilotingHandler;
import org.valkyrienskies.addon.control.network.MessageStartPiloting;
import org.valkyrienskies.addon.control.network.MessageStartPilotingHandler;
import org.valkyrienskies.addon.control.network.MessageStopPiloting;
import org.valkyrienskies.addon.control.network.MessageStopPilotingHandler;
import org.valkyrienskies.addon.control.piloting.PilotControlsMessage;
import org.valkyrienskies.addon.control.piloting.PilotControlsMessageHandler;
import org.valkyrienskies.addon.control.proxy.CommonProxyControl;
import org.valkyrienskies.addon.control.tileentity.TileEntityCaptainsChair;
import org.valkyrienskies.addon.control.tileentity.TileEntityGearbox;
import org.valkyrienskies.addon.control.tileentity.TileEntityGyroscopeDampener;
import org.valkyrienskies.addon.control.tileentity.TileEntityGyroscopeStabilizer;
import org.valkyrienskies.addon.control.tileentity.TileEntityLiftLever;
import org.valkyrienskies.addon.control.tileentity.TileEntityLiftValve;
import org.valkyrienskies.addon.control.tileentity.TileEntityNetworkDisplay;
import org.valkyrienskies.addon.control.tileentity.TileEntityNetworkRelay;
import org.valkyrienskies.addon.control.tileentity.TileEntityPassengerChair;
import org.valkyrienskies.addon.control.tileentity.TileEntityPropellerEngine;
import org.valkyrienskies.addon.control.tileentity.TileEntityShipHelm;
import org.valkyrienskies.addon.control.tileentity.TileEntitySpeedTelegraph;
import org.valkyrienskies.addon.world.ValkyrienSkiesWorld;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienwarfare.api.addons.Module;

@Mod(
    name = ValkyrienSkiesControl.MOD_NAME,
    modid = ValkyrienSkiesControl.MOD_ID,
    version = ValkyrienSkiesControl.MOD_VERSION,
    dependencies = "required-after:" + ValkyrienSkiesWorld.MOD_ID
)
@Mod.EventBusSubscriber(modid = ValkyrienSkiesControl.MOD_ID)
public class ValkyrienSkiesControl {

    // MOD INFO CONSTANTS
    public static final String MOD_ID = "vs_control";
    public static final String MOD_NAME = "Valkyrien Skies Control";
    public static final String MOD_VERSION = ValkyrienSkiesMod.MOD_VERSION;

    // MOD INSTANCE
    @Instance(MOD_ID)
    public static ValkyrienSkiesControl INSTANCE;

    @SidedProxy(
        clientSide = "org.valkyrienskies.addon.control.proxy.ClientProxyControl",
        serverSide = "org.valkyrienskies.addon.control.proxy.CommonProxyControl")
    private static CommonProxyControl proxy;

    @CapabilityInject(ICapabilityLastRelay.class)
    public static final Capability<ICapabilityLastRelay> lastRelayCapability = null;

    // MOD CLASS MEMBERS
    public static SimpleNetworkWrapper controlNetwork;
    public final BlocksValkyrienSkiesControl vsControlBlocks = new BlocksValkyrienSkiesControl();
    public Item relayWire;
    public Item multiBlockWrench;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        INSTANCE.vsControlBlocks.registerBlocks(event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        INSTANCE.relayWire = new ItemRelayWire().setTranslationKey("relay_wire")
            .setRegistryName(MOD_ID, "relay_wire")
            .setCreativeTab(ValkyrienSkiesMod.VS_CREATIVE_TAB);
        INSTANCE.multiBlockWrench = new ItemWrench().setTranslationKey("vs_wrench")
            .setRegistryName(MOD_ID, "vs_wrench")
            .setCreativeTab(ValkyrienSkiesMod.VS_CREATIVE_TAB);

        event.getRegistry()
            .register(INSTANCE.relayWire);
        event.getRegistry()
            .register(INSTANCE.multiBlockWrench);

        INSTANCE.vsControlBlocks.registerBlockItems(event);
        // This doesn't really belong here, but whatever.
        MultiblockRegistry
            .registerAllPossibleSchematicVariants(ValkyriumEngineMultiblockSchematic.class);
        MultiblockRegistry
            .registerAllPossibleSchematicVariants(ValkyriumCompressorMultiblockSchematic.class);
        MultiblockRegistry
            .registerAllPossibleSchematicVariants(RudderAxleMultiblockSchematic.class);
        MultiblockRegistry
            .registerAllPossibleSchematicVariants(GiantPropellerMultiblockSchematic.class);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Module.registerRecipe(event, "recipe_captains_chair",
            new ItemStack(INSTANCE.vsControlBlocks.captainsChair),
            "SLS",
            "VWV",
            " S ",
            'S', Items.STICK,
            'L', Items.LEATHER,
            'W', Item.getItemFromBlock(Blocks.LOG),
            'V', ValkyrienSkiesWorld.INSTANCE.valkyriumCrystal);

        Module.registerRecipe(event, "recipe_basic_engine",
            new ItemStack(INSTANCE.vsControlBlocks.basicEngine, 4),
            "I##",
            "IPP",
            "I##",
            '#', Item.getItemFromBlock(Blocks.PLANKS),
            'P', Item.getItemFromBlock(Blocks.PISTON),
            'I', Items.IRON_INGOT);

        Module.registerRecipe(event, "recipe_advanced_engine_stone",
            new ItemStack(INSTANCE.vsControlBlocks.advancedEngine, 4),
            "I##",
            "IPP",
            "I##",
            '#', Item.getItemFromBlock(Blocks.STONE),
            'P', Item.getItemFromBlock(Blocks.PISTON),
            'I', Items.IRON_INGOT);
        Module.registerRecipe(event, "recipe_advanced_engine_cobblestone",
            new ItemStack(INSTANCE.vsControlBlocks.advancedEngine, 2), "I##", "IPP", "I##", '#',
            Item.getItemFromBlock(Blocks.COBBLESTONE), 'P', Item.getItemFromBlock(Blocks.PISTON),
            'I', Items.IRON_INGOT);
        Module.registerRecipe(event, "recipe_elite_engine",
            new ItemStack(INSTANCE.vsControlBlocks.eliteEngine, 4), "III", "IPP", "III", 'P',
            Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT);
        Module.registerRecipe(event, "recipe_ultimate_engine",
            new ItemStack(INSTANCE.vsControlBlocks.ultimateEngine, 4), "I##", "IPP", "I##", '#',
            Item.getItemFromBlock(Blocks.OBSIDIAN), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I',
            Items.IRON_INGOT);

        Item relayWireIngot = Items.IRON_INGOT;
        // TODO: Code to check for copper and set relayWireIngot

        Module.registerRecipe(event, "recipe_relay_wire",
            new ItemStack(INSTANCE.relayWire, 4), // 1 per copper/iron ingot
            " I ",
            "ISI",
            " I ",
            'I', relayWireIngot,
            'S', Items.STICK);

        Module.registerRecipe(event, "recipe_compacted_valkyrium",
            new ItemStack(INSTANCE.vsControlBlocks.compactedValkyrium, 1),
            "VVV",
            "VVV",
            "VVV",
            'V', ValkyrienSkiesWorld.INSTANCE.valkyriumCrystal);

        Module.registerRecipe(event, "recipe_valkyrium_engine_part",
            new ItemStack(INSTANCE.vsControlBlocks.valkyriumEnginePart, 1),
            "IVI",
            "VFV",
            "IVI",
            'I', Items.IRON_INGOT,
            'F', Item.getItemFromBlock(Blocks.FURNACE),
            'V', ValkyrienSkiesWorld.INSTANCE.valkyriumCrystal);

        Module.registerRecipe(event, "recipe_valkyrium_compressor_part",
            new ItemStack(INSTANCE.vsControlBlocks.valkyriumCompressorPart, 1),
            "GVG",
            "VFV",
            "GVG",
            'G', Items.GOLD_INGOT,
            'F', Item.getItemFromBlock(Blocks.PISTON),
            'V', ValkyrienSkiesWorld.INSTANCE.valkyriumCrystal);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        registerTileEntities();
        registerNetworks();
        registerCapabilities();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    private void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityCaptainsChair.class,
            new ResourceLocation(MOD_ID, "tile_captains_chair"));
        GameRegistry.registerTileEntity(TileEntityNetworkRelay.class,
            new ResourceLocation(MOD_ID, "tile_network_relay"));
        GameRegistry.registerTileEntity(TileEntityShipHelm.class,
            new ResourceLocation(MOD_ID, "tile_ship_helm"));
        GameRegistry.registerTileEntity(TileEntitySpeedTelegraph.class,
            new ResourceLocation(MOD_ID, "tile_speed_telegraph"));
        GameRegistry.registerTileEntity(TileEntityPropellerEngine.class,
            new ResourceLocation(MOD_ID, "tile_propeller_engine"));
        GameRegistry.registerTileEntity(TileEntityGyroscopeStabilizer.class,
            new ResourceLocation(MOD_ID, "tile_gyroscope_stabilizer"));
        GameRegistry.registerTileEntity(TileEntityLiftValve.class,
            new ResourceLocation(MOD_ID, "tile_lift_valve"));
        GameRegistry.registerTileEntity(TileEntityNetworkDisplay.class,
            new ResourceLocation(MOD_ID, "tile_network_display"));
        GameRegistry.registerTileEntity(TileEntityLiftLever.class,
            new ResourceLocation(MOD_ID, "tile_lift_lever"));

        GameRegistry.registerTileEntity(TileEntityGyroscopeDampener.class,
            new ResourceLocation(MOD_ID, "tile_gyroscope_dampener"));
        GameRegistry.registerTileEntity(TileEntityValkyriumEnginePart.class,
            new ResourceLocation(MOD_ID, "tile_valkyrium_engine_part"));
        GameRegistry.registerTileEntity(TileEntityGearbox.class,
            new ResourceLocation(MOD_ID, "tile_gearbox"));
        GameRegistry.registerTileEntity(TileEntityValkyriumCompressorPart.class,
            new ResourceLocation(MOD_ID, "tile_valkyrium_compressor_part"));
        GameRegistry.registerTileEntity(TileEntityRudderPart.class,
            new ResourceLocation(MOD_ID, "tile_rudder_axle_part"));
        GameRegistry.registerTileEntity(TileEntityGiantPropellerPart.class,
            new ResourceLocation(MOD_ID, "tile_giant_propeller_part"));
        GameRegistry.registerTileEntity(TileEntityRotationAxle.class,
            new ResourceLocation(MOD_ID, "tile_rotation_axle"));

        GameRegistry.registerTileEntity(TileEntityPassengerChair.class,
            new ResourceLocation(MOD_ID, "tile_passenger_chair"));
    }

    private void registerNetworks() {
        controlNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("control_network");
        controlNetwork
            .registerMessage(PilotControlsMessageHandler.class, PilotControlsMessage.class, 2,
                Side.SERVER);
        controlNetwork
            .registerMessage(MessageStartPilotingHandler.class, MessageStartPiloting.class, 3,
                Side.CLIENT);
        controlNetwork
            .registerMessage(MessageStopPilotingHandler.class, MessageStopPiloting.class, 4,
                Side.CLIENT);
        controlNetwork.registerMessage(MessagePlayerStoppedPilotingHandler.class,
            MessagePlayerStoppedPiloting.class, 5, Side.SERVER);
    }

    private void registerCapabilities() {
        CapabilityManager.INSTANCE.register(ICapabilityLastRelay.class, new StorageLastRelay(),
            ImplCapabilityLastRelay::new);
    }
}
