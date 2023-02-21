import javax.vecmath.*;

// As each SDF object is centered at the origin, it would necessary to translate their SDF to move them around.
// This is actually done by moving the point inputted into the SDF towards the opposite direction.
public class Translate implements SDF
{
	private final SDF sdf;
	private final Vector3d trans;

	public Translate(SDF sdf, Vector3d trans)
	{
		this.sdf = sdf;
		this.trans = trans;
	}

	public Vector3d getTrans(Vector3d p)
	{
		var pTrans = new Vector3d(p);
		pTrans.sub(trans);
		return pTrans;
	}

	@Override
	public double dist(Vector3d p)
	{
		return sdf.dist(getTrans(p));
	}

	@Override
	public Vector3d getColor(Vector3d p)
	{
		return sdf.getColor(getTrans(p));
	}

	@Override
	public double getDiffuseRatio(Vector3d p)
	{
		return sdf.getDiffuseRatio(getTrans(p));
	}

	@Override
	public double getSpecularExp(Vector3d p)
	{
		return sdf.getSpecularExp(getTrans(p));
	}
}
