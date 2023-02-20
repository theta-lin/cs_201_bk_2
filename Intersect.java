import javax.vecmath.*;

// Only returns bounding SDF
public class Intersect implements SDF
{
	private final SDF a;
	private final SDF b;

	public Intersect(SDF a, SDF b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public double dist(Vector3d p)
	{
		return Math.max(a.dist(p), b.dist(p));
	}
}
