import javax.vecmath.*;

// Signed distance field
public interface SDF
{
	// The distance function could return the actual Euclidean distance for some SDFs.
	// But for others, it might only guarantee that the points outside of the object would be mapped to a positive value, and the points inside negative.
	public double dist(Vector3d p);
}
