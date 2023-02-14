import javax.vecmath.*;

public class Box implements SDF
{
	private final Vector3d a;

	public Box(Vector3d a)
	{
		this.a = a;
	}

	@Override
	public double dist(Vector3d p)
	{
		var pDiff = new Vector3d(p);
		pDiff.absolute();
		pDiff.sub(a);
		var pOut = new Vector3d(pDiff);
		pOut.clampMin(0);

		return pOut.length() + Math.min(Math.max(Math.max(pDiff.x, pDiff.y), pDiff.z), 0);
	}

	@Override
	public double distMin(Vector3d p)
	{
		return dist(p);
	}
}
