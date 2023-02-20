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

	private static SDF rayMarch(Vector3d p, Vector3d dir)
	{
		final int maxStepCnt = 50;
		final double minStepSize = 1e-3;

		for (int i = 0; i < maxStepCnt; ++i)
		{
			double d = Double.POSITIVE_INFINITY;
			for (var obj : objs)
			{
				d = Math.min(d, obj.dist(p));
				if (d <= 0) return obj;
			}
			d = Math.max(d, minStepSize);

			var step = new Vector3d(dir);
			step.scale(d);
			p.add(step);
		}
		return null;
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

		//var sphere = new Translate(new Sphere(1), new Vector3d(-0.6, 0.4, 0.3));
		//var box = new Box(new Vector3d(0.4, 0.7, 0.5));
		//objs.add(new Translate(new DisplaceSin(new Union(sphere, box), 12, 0.15), new Vector3d(-2, -2, 4)));
		//objs.add(new Translate(new Intersect(sphere, box), new Vector3d(0, 0, 6)));
		//objs.add(new Translate(new Subtract(sphere, box), new Vector3d(3, 3, 6)));

		var nearBox = new Box(new Vector3d(1.5, 1.5, 1.5));
		var grid = new Scale(new RandSphGrid(0.5), 0.5);
		objs.add(new Subtract(grid, nearBox));

		var lightOrigin = new Vector3d(10, 10, -10);

		for (int i = 0; i < width; ++i)
		{
			for (int j = 0; j < height; ++j)
			{
				double x = ((i + 0.5) / width * 2.0 - 1) * Math.tan(fov / 2.0) * width / height;
				double y = ((j + 0.5) / height * 2.0 - 1) * Math.tan(fov / 2.0);
				var p = new Vector3d(0.0, 0.0, 0.0);
				var dir = new Vector3d(x, y, 1.0);
				dir.normalize();

				SDF hit = rayMarch(p, dir);
				if (hit != null)
				{
					var lightDir = new Vector3d(p);
					lightDir.sub(lightOrigin);
					lightDir.normalize();
					var n = normal(hit, p);

					double intensity = Math.max(-lightDir.dot(n), 0.0);
					StdDraw.setPenColor((int) (255 * intensity), (int) (255 * intensity), (int) (255 * intensity));
					// StdDraw.setPenColor(Math.min(Math.max((int) ((n.x / 2 + 1) * 255), 0), 255), Math.min(Math.max((int) ((n.y / 2 + 1) * 255), 0), 255), 0);
					//StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.point(i, j);
				}
			}
		}

		StdDraw.show();
		StdDraw.save("output.png");
		System.out.println("Done!");
	}
}
