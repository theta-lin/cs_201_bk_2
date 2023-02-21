import javax.vecmath.*;

public class Sphere implements SDF
{
	private final double r;

	private final Vector3d color;
	private final double diffuseRatio;
	private final double specularExp;

	public Sphere(double r)
	{
		this.r = r;
		this.color = new Vector3d(1.0, 1.0, 1.0);
		this.diffuseRatio = 1.0;
		this.specularExp = 0.0;
	}

	public Sphere(double r, Vector3d color, double diffuseRatio, double specularExp)
	{
		this.r = r;
		this.color = color;
		this.diffuseRatio = diffuseRatio;
		this.specularExp = specularExp;
	}

	@Override
	public double dist(Vector3d p)
	{
		var d = new Vector3d(p);
		return d.length() - r;
	}

	@Override
	public Vector3d getColor(Vector3d p)
	{
		return new Vector3d(color);
	}

	@Override
	public double getDiffuseRatio(Vector3d p)
	{
		return diffuseRatio;
	}

	@Override
	public double getSpecularExp(Vector3d p)
	{
		return specularExp;
	}

}
