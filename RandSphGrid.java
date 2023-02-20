import javax.vecmath.*;

// Infinite 1x1x1 grid, with each cell filled with a sphere of random radii.
public class RandSphGrid implements SDF
{
	final double maxSize;

	public RandSphGrid(double maxSize)
	{
		this.maxSize = maxSize;
	}

	@Override
	public double dist(Vector3d p)
	{
		final int precision = 100;

		var pi = new Vector3d(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
		var pf = new Vector3d(p.x - pi.x, p.y - pi.y, p.z - pi.z);

		double d = Double.POSITIVE_INFINITY;
		for (int x = 0; x <= 1; ++x)
		for (int y = 0; y <= 1; ++y)
		for (int z = 0; z <= 1; ++z)
		{
			var shift = new Vector3d(x, y, z);
			var cell = new Vector3d(pi);
			cell.add(shift);

			// Use the hash code of the integer part of the position for a random sphere radius.
			// Precision is the range that the integer hash code is mapped into,
			// which is then scaled into the range from 0 to maxSize.
			double r = ((cell.hashCode() % precision) + precision) % precision * maxSize / precision;

			// By extracting the fractional part of a point, it is mapping all points in the space into a unit cube.
			// Each corner of that unit cube is in fact one-eighth of a sphere.
			d = Math.min(d, new Translate(new Sphere(r), shift).dist(pf));
		}

		return d;
	}
}
