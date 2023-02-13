public class RayMarcher
{
	public static void main(String[] args)
	{
		final int width = 800;
		final int height = 800;

		StdDraw.enableDoubleBuffering();
		StdDraw.setCanvasSize(width, height);
		StdDraw.setXscale(0, width);
		StdDraw.setYscale(0, height);

		for (int i = 0; i < width; ++i)
		{
			int r = 0, g = 0, b = 0;
			if (i / 50 % 3 == 0)
			{
				r = 255;
			}
			else if (i / 50 % 3 == 1)
			{
				g = 255;
			}
			else
			{
				b = 255;
			}
			StdDraw.setPenColor(r, g, b);
			for (int j = 0; j < height; ++j)
			{
				StdDraw.point(i, j);
			}
		}
		StdDraw.show();
	}
}
