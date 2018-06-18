package valkyrienwarfare.addon.control.nodenetwork;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import valkyrienwarfare.physics.management.PhysicsObject;

/**
 * The nodes that form the graphs of control elements.
 * @author thebest108
 *
 */
public interface IVWNode {

	public static final String NBT_DATA_KEY = "VWNode_Tile_Data";
	
	/**
	 * This does not return the full graph of connected nodes, just the ones that
	 * are directly connected to this node.
	 * 
	 * @return
	 */
	Iterable<IVWNode> getDirectlyConnectedNodes();

	void makeConnection(IVWNode other);

	void breakConnection(IVWNode other);

	/**
	 * Mark this IVWNode as safe to use.
	 */
	void validate();
	
	/**
	 * Mark this IVWNode as unsafe to use.
	 */
	void invalidate();
	
	/**
	 * Returns true if the node is safe, false if it isn't.
	 * @return
	 */
	boolean isValid();
	
	/**
	 * Returns null if this node doesn't have a blockpos.
	 * @return
	 */
	BlockPos getNodePos();
	
	World getNodeWorld();
	
	Set<BlockPos> getLinkedNodesPos();
	
	void writeToNBT(NBTTagCompound compound);
	
	void readFromNBT(NBTTagCompound compound);
	
	void setIsNodeRelay(boolean isRelay);
	
	boolean isNodeRelay();
	
	default void breakAllConnections() {
		for (IVWNode node : getDirectlyConnectedNodes()) {
			breakConnection(node);
		}
	}

	default boolean canLinkToNode(IVWNode other) {
		return isNodeRelay() || other.isNodeRelay();
	}
	
	void sendNodeUpdates();

	/**
	 * Can only be called while this node is invalid. Otherwise an
	 * IllegalStateException is thrown.
	 * 
	 * @param offset
	 */
	void shiftConnections(BlockPos offset);
	
	/**
	 * Should only be called when after shiftConnections()
	 * @param parent
	 */
	void setParentPhysicsObject(PhysicsObject parent);

	PhysicsObject getPhysicsObject();
}
