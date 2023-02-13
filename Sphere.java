import javax.vecmath.*;

public class Sphere implements SDF
{
	private final double r;

	public Sphere(double r)
	{
		this.r = r;
	}

	@Override
	public double dist(Vector3d p)
	{
		var d = new Vector3d(p);
		return d.length() - r;
	}

	@Override
	public double distMin(Vector3d p)
	{
		return dist(p);
	}
}
