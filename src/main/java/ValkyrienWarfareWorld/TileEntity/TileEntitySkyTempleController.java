package ValkyrienWarfareWorld.TileEntity;

import javax.vecmath.Vector2d;

import ValkyrienWarfareBase.NBTUtils;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.Physics.PhysicsCalculations;
import ValkyrienWarfareBase.Physics.PhysicsCalculationsManualControl;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsObject;
import ValkyrienWarfareControl.TileEntity.ImplPhysicsProcessorNodeTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntitySkyTempleController extends ImplPhysicsProcessorNodeTileEntity {

	private Vector originPos = new Vector();
	private double orbitDistance;

	double yawChangeRate = 8D;
	double yawPathRate = 2D;
	double yPathRate = 2D;

	double totalSecondsExisted = Math.random() * 15D;

	@Override
	public void onPhysicsTick(PhysicsObject object, PhysicsCalculations calculations, double secondsToSimulate) {
		if(calculations instanceof PhysicsCalculationsManualControl) {
			PhysicsCalculationsManualControl manualControl = (PhysicsCalculationsManualControl) calculations;

			((PhysicsCalculationsManualControl) calculations).useLinearMomentumForce = true;

			if(originPos == null || originPos.isZero()) {
				setOriginPos(new Vector(object.wrapper.posX, object.wrapper.posY, object.wrapper.posZ));
			}

			manualControl.yawRate = yawChangeRate;

			Vector2d distanceFromCenter = new Vector2d(object.wrapper.posX - originPos.X, object.wrapper.posZ - originPos.Z);

			double realDist = distanceFromCenter.length();

			double invTan = Math.toDegrees(Math.atan2(distanceFromCenter.getY(), distanceFromCenter.getX()));

			double velocityAngle = invTan + 90D;

			double x = Math.cos(Math.toRadians(velocityAngle)) * yawPathRate;
			double z = Math.sin(Math.toRadians(velocityAngle)) * yawPathRate;

			if(realDist / orbitDistance > 1D) {
				double reductionFactor = (realDist / realDist) - 1D;

				x -= reductionFactor * distanceFromCenter.x * yawPathRate;
				z -= reductionFactor * distanceFromCenter.y * yawPathRate;

//				System.out.println(reductionFactor);
			}

			calculations.linearMomentum.X = x;
			calculations.linearMomentum.Z = z;

			totalSecondsExisted += secondsToSimulate;

			calculations.linearMomentum.Y = Math.sin(Math.toRadians(totalSecondsExisted * 7.5D)) * yPathRate;
		}
	}

	public void setOriginPos(Vector newPos)	{
		originPos = newPos;
		//Minimum orbit of 40, maximum orbit of 100
		double orbitDistance =  40D + (Math.random() * 60D);
		//Assume we are at 0 degrees in our orbit
		originPos.X -= orbitDistance;

		this.markDirty();
	}

	@Override
    public void readFromNBT(NBTTagCompound compound) {
    	super.readFromNBT(compound);
    	originPos = NBTUtils.readVectorFromNBT("originPos", compound);
    	orbitDistance = compound.getDouble("orbitDistance");
    	yawChangeRate = compound.getDouble("yawChangeRate");
    	yawPathRate = compound.getDouble("yawPathRate");
    	yPathRate = compound.getDouble("yPathRate");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    	compound = super.writeToNBT(compound);
    	NBTUtils.writeVectorToNBT("originPos", originPos, compound);
    	compound.setDouble("orbitDistance", orbitDistance);
    	compound.setDouble("yawChangeRate", yawChangeRate);
    	compound.setDouble("yawPathRate", yawPathRate);
    	compound.setDouble("yPathRate", yPathRate);
        return compound;
    }

}
