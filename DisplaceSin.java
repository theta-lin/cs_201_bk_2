import javax.vecmath.*;

public class DisplaceSin implements SDF
{
	private final SDF sdf;
	private final double freq;
	private final double amp;

	public DisplaceSin(SDF sdf, double freq, double amp)
	{
		this.sdf = sdf;
		this.freq = freq;
		this.amp = amp;
	}

	private double disp(Vector3d p)
	{
		return Math.sin(freq * p.x) * Math.sin(freq * p.y) * Math.sin(freq * p.z) * amp;
	}

	@Override
	public double dist(Vector3d p)
	{
		return sdf.dist(p) + disp(p);
	}
}
