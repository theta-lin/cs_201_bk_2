import javax.vecmath.*;
import java.util.Random;

public class FBM implements SDF
{
	static private final SDF noise = new RandSphGrid(0.5);

	private final SDF sdf;
	private SDF layers;

	private final double[] solid;
	private final double[] trans;
	private final Vector3d[] colors;

	public FBM(SDF sdf, int n, double s0, double sFactor, double inflation, double kMax, double kMin, long rotSeed, double[] solid, double[] trans, Vector3d[] colors)
	{
		this.sdf = sdf;
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

		this.solid = solid;
		this.trans = trans;
		this.colors = colors;
		assert(solid.length == trans.length && trans.length == colors.length);
	}

	@Override
	public double dist(Vector3d p)
	{
		return layers.dist(p);
	}

	@Override
	public Vector3d getColor(Vector3d p)
	{
		double height = sdf.dist(p);
		double sum = 0.0;

		for (int i = 0; i < colors.length - 1; ++i)
		{
			sum += solid[i];
			if (height <= sum) return new Vector3d(colors[i]);

			sum += trans[i];
			if (height <= sum)
			{
				var c = new Vector3d(colors[i]);
				var cd = new Vector3d(colors[i + 1]);
				cd.sub(c);
				cd.scale((height - (sum - trans[i])) / trans[i]);
				c.add(cd);
				return c;
			}
		}

		return new Vector3d(colors[colors.length - 1]);
	}
}
