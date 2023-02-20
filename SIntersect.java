import javax.vecmath.*;

public class SIntersect implements SDF
{
	private final SDF a;
	private final SDF b;
	private double k;

	public SIntersect(SDF a, SDF b, double k)
	{
		this.a = a;
		this.b = b;
		this.k = k;
	}

	@Override
	public double dist(Vector3d p)
	{
		double ad = a.dist(p), bd = b.dist(p);
		double h = Math.max(k - Math.abs(ad - bd), 0.0) / k;
		return Math.max(ad, bd) + h * h * h * k / 6.0;
	}
}
