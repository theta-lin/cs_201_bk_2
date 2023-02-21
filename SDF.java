import javax.vecmath.*;

// Signed distance field
public interface SDF
{
	// The distance function could return the actual Euclidean distance for some SDFs.
	// But for others, it might only guarantee that the points outside of the object would be mapped to a positive value, and the points inside negative.
	public double dist(Vector3d p);

	default public Vector3d getColor(Vector3d p)
	{
		return new Vector3d(1.0, 1.0, 1.0);
	}

	default public double getDiffuseRatio(Vector3d p)
	{
		return 1.0;
	}

	default public double getSpecularExp(Vector3d p)
	{
		return 0.0;
	}
}
