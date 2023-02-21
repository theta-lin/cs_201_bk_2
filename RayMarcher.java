import java.util.*;
import javax.vecmath.*;

public class RayMarcher
{
	private static ArrayList<SDF> objs = new ArrayList<SDF>();

	// Get the normal vector by finite difference
	private static Vector3d normal(SDF obj, Vector3d p)
	{
		final double eps = 1e-5;

		double d = obj.dist(p);
		var xMove = new Vector3d(p);
		var yMove = new Vector3d(p);
		var zMove = new Vector3d(p);
		xMove.x += eps;
		yMove.y += eps;
		zMove.z += eps;

		var normal = new Vector3d(obj.dist(xMove) - d, obj.dist(yMove) - d, obj.dist(zMove) - d);
		normal.normalize();
		return normal;
	}

	private static Vector3d reflect(Vector3d v, Vector3d n)
	{
		var p = new Vector3d(n);
		p.scale(-2 * v.dot(n));
		var o = new Vector3d(v);
		o.add(p);
		return o;
	}

	private static class RayRes
	{
		Vector3d p;
		public SDF obj;
		public double dist;
		public double height;
		public double shadow;

		public RayRes(Vector3d p, SDF obj, double dist, double height, double shadow)
		{
			this.p = p;
			this.obj = obj;
			this.dist = dist;
			this.height = height;
			this.shadow = shadow;
		}
	}

	private static RayRes ray(Vector3d orig, Vector3d dir, double maxDist)
	{
		final int maxStepCnt = 80;
		final double minStepSize = 1e-3;

		Vector3d p = new Vector3d(orig);
		double dTravel = 0.0;
		double height = Double.POSITIVE_INFINITY;
		double shadow = Double.POSITIVE_INFINITY; // Scaled minimum distance to objects for soft shadow

		for (int i = 0; dTravel < maxDist && i < maxStepCnt; ++i)
		{
			double d = Double.POSITIVE_INFINITY;
			for (var obj : objs)
			{
				d = Math.min(d, obj.dist(p));
				if (d <= 0.0) return new RayRes(p, obj, dTravel, 0.0, 0.0);
			}
			height = Math.min(height, d);
			shadow = Math.min(shadow, d / dTravel);

			d = Math.max(d, minStepSize);
			dTravel += d;

			var step = new Vector3d(dir);
			step.scale(d);
			p.add(step);
		}

		return new RayRes(p, null, Double.POSITIVE_INFINITY, height, shadow);
	}

	private static Vector3d applyFog(Vector3d color, double dist, double height)
	{
		final Vector3d fogColor = new Vector3d(0.44, 0.50, 0.56);
		final double strength = 0.32;
		final double decayDist = 0.4;
		final double decayHeight = 5.3;

		double amount = strength * (1.0 - Math.exp(-decayDist * dist)) * Math.exp(-decayHeight * height);
		Vector3d t0 = new Vector3d(color);
		t0.scale(1.0 - amount);
		Vector3d t1 = new Vector3d(fogColor);
		t1.scale(amount);
		t0.add(t1);
		return t0;
	}

	public static void main(String[] args)
	{
		final int width = 1024;
		final int height = 1024;
		final double fov = Math.PI / 2;

		StdDraw.enableDoubleBuffering();
		StdDraw.setCanvasSize(width, height);
		StdDraw.setXscale(0, width);
		StdDraw.setYscale(0, height);
		StdDraw.clear(StdDraw.BLACK);

		var sphere = new Sphere(1);
		var fbm = new FBM(sphere, 8, 1.0, 0.5,
				          0.07, 0.07, 0.28,
				          666,
						  new double[] {0.02, 0.01, 0.03, 0.02, 0.0},
						  new double[] {0.01, 0.04, 0.01, 0.01, 0.0},
						  new Vector3d[] {new Vector3d(0.91, 0.59, 0.48), new Vector3d(0.49, 0.99, 0), new Vector3d(0.13, 0.54, 0.13), new Vector3d(0.41, 0.41, 0.41), new Vector3d(1.0, 0.98, 0.98)});
		var water = new Sphere(1.01, new Vector3d(0.15, 0.56, 1.0), 0.2, 3);
		objs.add(new Translate(new Rotate(new Union(fbm, water), new Vector3d(Math.toRadians(60), 0, Math.toRadians(-45))), new Vector3d(0, 0, 1.8)));

		//var box = new Translate(new Box(new Vector3d(0.4, 0.7, 0.5)), new Vector3d(0.6, -0.4, -0.3));

		//objs.add(new Translate(new Rotate(box, new Vector3d(Math.toRadians(30), Math.toRadians(0), Math.toRadians(30))), new Vector3d(0, 0, 3)));
		//objs.add(new Translate(new DisplaceSin(new SUnion(sphere, box, 0.5), 12, 0.15), new Vector3d(0, 0, 3)));
		//objs.add(new Translate(new Intersect(sphere, box), new Vector3d(0, 0, 6)));
		//objs.add(new Translate(new Subtract(sphere, box), new Vector3d(3, 3, 6)));

		//var nearBox = new Box(new Vector3d(4, 4, 4));
		//var grid = new Rotate(new RandSphGrid(0.5), new Vector3d(0, Math.toRadians(45), Math.toRadians(30)));
		//objs.add(new Subtract(grid, nearBox));

		//objs.add(new Translate(new SUnion(sphere, box, 0.5), new Vector3d(0, 0, 3)));

		var lightOrigin = new Vector3d(-3, 3, -3);

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < width; ++i)
		{
			for (int j = 0; j < height; ++j)
			{
				double x = -((i + 0.5) / width * 2.0 - 1) * Math.tan(fov / 2.0) * width / height;
				double y = ((j + 0.5) / height * 2.0 - 1) * Math.tan(fov / 2.0);
				var p = new Vector3d(0.0, 0.0, 0.0);
				var dir = new Vector3d(x, y, 1.0);
				dir.normalize();

				var hit = ray(p, dir, Double.POSITIVE_INFINITY);
				var color = new Vector3d(0.0, 0.0, 0.0);
				if (hit.obj != null)
				{
					color = hit.obj.getColor(hit.p);
					var lightDir = new Vector3d(lightOrigin);
					lightDir.sub(hit.p);
					double lightDist = lightDir.length();
					lightDir.normalize();
					var n = normal(hit.obj, hit.p);

					// Amount of offset to be applied at the intersection point,
					// so that the shadow ray will not be stuck in the object.
					final double hitOffsetScale = 1e-3;
					var hitOffset = new Vector3d(n);
					hitOffset.scale(hitOffsetScale);
					hit.p.add(hitOffset);
					var lightHit = ray(hit.p, lightDir, lightDist);

					// Soft shadow by dimming the light according to the shortest distance to the light
					// when travelling along the shadow ray.
					final double shadowHardness = 10.0;
					double diffuseInt = 0.0;
					if (lightHit.obj == null) diffuseInt = Math.max(lightDir.dot(n), 0.0);
					diffuseInt *= Math.min(lightHit.shadow * shadowHardness, 1.0);
					diffuseInt *= hit.obj.getDiffuseRatio(hit.p);

					double specularInt = 0.0;
					if (lightHit.obj == null) specularInt = Math.max(reflect(dir, n).dot(lightDir), 0.0);
					specularInt = Math.pow(specularInt, hit.obj.getSpecularExp(hit.p));
					specularInt *= 1.0 - hit.obj.getDiffuseRatio(hit.p);

					color.scale(diffuseInt + specularInt);
				}

				color = applyFog(color, hit.dist, hit.height);
				color.clampMin(0.0);
				color.clampMax(1.0);
				StdDraw.setPenColor((int) (255 * color.x), (int) (255 * color.y), (int) (255 * color.z));
				// StdDraw.setPenColor(Math.min(Math.max((int) ((n.x / 2 + 1) * 255), 0), 255), Math.min(Math.max((int) ((n.y / 2 + 1) * 255), 0), 255), 0);
				//StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.point(i, j);
			}
		}

		StdDraw.show();
		StdDraw.save("output.png");

		long endTime = System.currentTimeMillis();
		System.out.println("Rendering done in: " + (endTime - startTime) / 1000.0 +" s");
	}
}
