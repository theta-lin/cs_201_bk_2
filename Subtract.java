import javax.vecmath.*;

// Only returns bounding SDF
public class Subtract implements SDF
{
	private final SDF a;
	private final SDF b;

	public Subtract(SDF a, SDF b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public double dist(Vector3d p)
	{
		return Math.max(a.dist(p), -b.dist(p));
	}
}
