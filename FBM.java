import javax.vecmath.*;
import java.util.Random;

public class FBM implements SDF
{
	static private final SDF noise = new RandSphGrid(0.5);
	private SDF layers;

	public FBM(SDF sdf, int n, double s0, double sFactor, double inflation, double kMax, double kMin, long rotSeed)
	{
		layers = sdf;

		var rand = new Random(rotSeed);
		final double a = Math.PI / 4;

		double s = s0;
		for (int i = 0; i < n; ++i)
		{
			SDF ns = new Scale(noise, s);
			ns = new SIntersect(ns, new Scale(layers, 1 + inflation * s), kMax * s);
			layers = new SUnion(ns, layers, kMin * s);
			layers = new Rotate(layers, new Vector3d(a * rand.nextDouble(), a * rand.nextDouble(), a * rand.nextDouble()));
			s *= sFactor;
		}
	}

	@Override
	public double dist(Vector3d p)
	{
		return layers.dist(p);
	}
}
