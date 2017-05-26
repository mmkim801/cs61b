public class NBody {

	public static double readRadius(String file) {
		In text = new In(file);
		text.readDouble();
		double r = text.readDouble();
			return r;
	}

	public static Planet[] readPlanets(String file) {
		In text = new In(file);
		int index = 0;
		int num = text.readInt();
		Planet[] listPlanet = new Planet[num];
		text.readDouble();
		while(index<num) {
			Planet p1 = new Planet(text.readDouble(), text.readDouble(), text.readDouble(), text.readDouble(), text.readDouble(), text.readString());
			listPlanet[index] = p1;
			index += 1;
		}
		return listPlanet;
	}

	public static void main(String args[]) {
		//background
		Double T = new Double(args[0]);
		Double dt = new Double (args[1]);
		String filename = args[2];
		double radius = readRadius(filename);
		StdDraw.setScale(-radius, radius);
		StdDraw.picture(0,0,"images/starfield.jpg");

		//drawing 1 planet
		int index = 0;
		Planet[] listPlanet = readPlanets(filename);
		while (index < listPlanet.length) {
			listPlanet[index].draw();
			index += 1;
		}

		//animation
		double time = 0;
		while (time < T) {
			double[] xForces = new double[listPlanet.length];
			double[] yForces = new double[listPlanet.length];

			// calculate net x and y forces for each planet
			int i = 0;
			for (Planet planet : listPlanet) {
				xForces[i] = planet.calcNetForceExertedByX(listPlanet);
				yForces[i] = planet.calcNetForceExertedByY(listPlanet);
				i += 1;
			}

			// call update on each of the planets
			int item = 0;
			for (Planet planet : listPlanet) {
				planet.update(dt, xForces[item], yForces[item]);
				item += 1;
			}

			StdDraw.picture(0,0,"images/starfield.jpg");
			for (Planet planet : listPlanet) {
				planet.draw();
			}
			StdDraw.show(10);
			time += dt;
		}

		//print
		StdOut.printf("%d\n", listPlanet.length);
		StdOut.printf("%.2e\n", radius);
		for (int i = 0; i < listPlanet.length; i++) {
			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   				listPlanet[i].xxPos, listPlanet[i].yyPos, listPlanet[i].xxVel, listPlanet[i].yyVel, listPlanet[i].mass, listPlanet[i].imgFileName);	
		}
	}

}