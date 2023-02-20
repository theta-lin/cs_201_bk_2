import javax.vecmath.*;

public class Scale implements SDF
{
	private final SDF sdf;
	final double scale;

	public Scale(SDF sdf, double scale)
	{
		this.sdf = sdf;
		this.scale = scale;
	}

	@Override
	public double dist(Vector3d p)
	{
		var pScale = new Vector3d(p);
		pScale.scale(1 / scale);
		return sdf.dist(pScale) * scale;
	}
}
