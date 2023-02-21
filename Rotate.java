import javax.vecmath.*;


// Rotate around three axes by three angles counterclockwise.
public class Rotate implements SDF
{
	public final SDF sdf;
	private final Matrix3d rot;

	public Rotate(SDF sdf, Vector3d by)
	{
		this.sdf = sdf;

		by.scale(-1); // Reverse the rotation angle, as we are rotating the world coordinates when calculating the distance
		var rotX = new Matrix3d(new double[] {1, 0, 0,
		                                      0, Math.cos(by.x), -Math.sin(by.x),
		                                      0, Math.sin(by.x), Math.cos(by.x)});
		var rotY = new Matrix3d(new double[] {Math.cos(by.y), 0, Math.sin(by.y),
		                                      0, 1, 0,
		                                      -Math.sin(by.y), 0, Math.cos(by.y)});
		var rotZ = new Matrix3d(new double[] {Math.cos(by.z), -Math.sin(by.z), 0,
		                                      Math.sin(by.z), Math.cos(by.z), 0,
		                                      0, 0, 1});
		rot = rotX;
		rot.mul(rotY);
		rot.mul(rotZ);
	}

	public Vector3d getRot(Vector3d p)
	{
		var pRot = new Vector3d(p);
		rot.transform(pRot);
		return pRot;
	}

	@Override
	public double dist(Vector3d p)
	{
		return sdf.dist(getRot(p));
	}

	@Override
	public Vector3d getColor(Vector3d p)
	{
		return sdf.getColor(getRot(p));
	}

	@Override
	public double getDiffuseRatio(Vector3d p)
	{
		return sdf.getDiffuseRatio(getRot(p));
	}

	@Override
	public double getSpecularExp(Vector3d p)
	{
		return sdf.getSpecularExp(getRot(p));
	}
}
