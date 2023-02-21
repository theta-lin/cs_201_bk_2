import javax.vecmath.*;

// Only the exterior of the union is a true SDF
public class Union implements SDF
{
	private final SDF a;
	private final SDF b;

	public Union(SDF a, SDF b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public double dist(Vector3d p)
	{
		return Math.min(a.dist(p), b.dist(p));
	}

	@Override
	public Vector3d getColor(Vector3d p)
	{
		if (a.dist(p) < b.dist(p))
		{
			return a.getColor(p);
		}
		else
		{
			return b.getColor(p);
		}
	}

	@Override
	public double getDiffuseRatio(Vector3d p)
	{
		if (a.dist(p) < b.dist(p))
		{
			return a.getDiffuseRatio(p);
		}
		else
		{
			return b.getDiffuseRatio(p);
		}
	}

	@Override
	public double getSpecularExp(Vector3d p)
	{
		if (a.dist(p) < b.dist(p))
		{
			return a.getSpecularExp(p);
		}
		else
		{
			return b.getSpecularExp(p);
		}
	}
}
