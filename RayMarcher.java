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

	private static class RayRes
	{
		Vector3d p;
		public SDF obj;
		public double shadow;

		public RayRes(Vector3d p, SDF obj, double shadow)
		{
			this.p = p;
			this.obj = obj;
			this.shadow = shadow;
		}
	}

	private static RayRes ray(Vector3d orig, Vector3d dir, double maxDist)
	{
		final int maxStepCnt = 60;
		final double minStepSize = 1e-3;

		Vector3d p = new Vector3d(orig);
		double dSum = 0;
		double shadow = Double.POSITIVE_INFINITY; // Scaled minimum distance to objects for soft shadow
		for (int i = 0; dSum < maxDist && i < maxStepCnt; ++i)
		{
			double d = Double.POSITIVE_INFINITY;
			for (var obj : objs)
			{
				d = Math.min(d, obj.dist(p));
				if (d <= 0) return new RayRes(p, obj, 0);
			}
			shadow = Math.min(shadow, d / dSum);

			d = Math.max(d, minStepSize);
			dSum += d;

			var step = new Vector3d(dir);
			step.scale(d);
			p.add(step);
		}

		return new RayRes(p, null, shadow);
	}

	public static void main(String[] args)
	{
		final int width = 512;
		final int height = 512;
		final double fov = Math.PI / 2;

		StdDraw.enableDoubleBuffering();
		StdDraw.setCanvasSize(width, height);
		StdDraw.setXscale(0, width);
		StdDraw.setYscale(0, height);
		StdDraw.clear(StdDraw.BLACK);

		var sphere = new Sphere(1);
		var fbm = new FBM(sphere, 5, 1.0, 0.5,
				          0.05, 0.1, 0.4,
				          666);
		objs.add(new Translate(fbm, new Vector3d(0, 0, 1.8)));

		//var box = new Translate(new Box(new Vector3d(0.4, 0.7, 0.5)), new Vector3d(0.6, -0.4, -0.3));

		//objs.add(new Translate(new Rotate(box, new Vector3d(Math.toRadians(30), Math.toRadians(0), Math.toRadians(30))), new Vector3d(0, 0, 3)));
		//objs.add(new Translate(new DisplaceSin(new SUnion(sphere, box, 0.5), 12, 0.15), new Vector3d(0, 0, 3)));
		//objs.add(new Translate(new Intersect(sphere, box), new Vector3d(0, 0, 6)));
		//objs.add(new Translate(new Subtract(sphere, box), new Vector3d(3, 3, 6)));

		//var nearBox = new Box(new Vector3d(4, 4, 4));
		//var grid = new Rotate(new RandSphGrid(0.5), new Vector3d(0, Math.toRadians(45), Math.toRadians(30)));
		//objs.add(new Subtract(grid, nearBox));

		//objs.add(new Translate(new SUnion(sphere, box, 0.5), new Vector3d(0, 0, 3)));

		var lightOrigin = new Vector3d(3, 3, -3);

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
				if (hit.obj != null)
				{
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
					double intensity = 0.0;
					if (lightHit.obj == null) intensity = Math.max(lightDir.dot(n), 0.0);
					intensity *= Math.min(lightHit.shadow * shadowHardness, 1.0);

					StdDraw.setPenColor((int) (255 * intensity), (int) (255 * intensity), (int) (255 * intensity));
					// StdDraw.setPenColor(Math.min(Math.max((int) ((n.x / 2 + 1) * 255), 0), 255), Math.min(Math.max((int) ((n.y / 2 + 1) * 255), 0), 255), 0);
					//StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.point(i, j);
				}
			}
		}

		StdDraw.show();
		StdDraw.save("output.png");

		long endTime = System.currentTimeMillis();
		System.out.println("Rendering done in: " + (endTime - startTime) / 1000.0 +" s");
	}
}
