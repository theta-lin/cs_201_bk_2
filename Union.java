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
}
