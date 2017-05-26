public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;

	public Planet(double xP, double yP, double xV, double yV, double m, String img) {
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}
	// why do we have to reassign these values? -- assigning them to instances (?) of Planet.

	public Planet(Planet p) {
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}
	/* what is the copy planet? -- "this" is the copy planet, p is the original
	   would work if code was "this.xxPos = p.xxPos" */

	public double calcDistance(Planet p) {
		double dx = (p.xxPos - xxPos);
		double dy = (p.yyPos - yyPos);
		double distance = Math.sqrt((dx * dx) + (dy * dy));
		return distance;
	}
	// why does this method need double and not the ones above? not even void?

	// force is exerted by the planet p, the planet the method is taking in.
	public double calcForceExertedBy(Planet p) {
		double gravity = 6.67e-11;
		double mass1 = mass;
		double mass2 = p.mass;
		double distance = calcDistance(p);
		double force = (gravity*mass1*mass2)/(distance*distance);
		return force;
	}

	public double calcForceExertedByX(Planet p) {
		double totalForce = calcForceExertedBy(p);
		double dx = p.xxPos - xxPos;
		double r = calcDistance(p);
		return (totalForce*dx)/r;
	}

	public double calcForceExertedByY(Planet p) {
		double totalForce = calcForceExertedBy(p);
		double dy = p.yyPos - yyPos;
		double r = calcDistance(p);
		return (totalForce * dy)/r;
	}

	public double calcNetForceExertedByX(Planet[] planets) {
		double netForce = 0;
		for (Planet planet : planets) {
			if (this.equals(planet)) {
				netForce += 0;
			}
			else {
				double force = calcForceExertedByX(planet);
				netForce += force;
			}
		}
		return netForce; 
	}

	/* if the target planet is in the array planets, the netForce should be 0.
	   but how does it return netForce of 0.0, when the netForce of the other planets are also added up? */

	/* public |what_type_method_returns| |type_of_array|[] |name_of_array| {} 
	   -- type_of_array can be int, double, class name, String, etc. --

	 * to run loop through each element in array:
	   for (|Type_of_element| element : |name_of_array|) {} */

	public double calcNetForceExertedByY(Planet[] planets) {
		double netForce = 0;
		for (Planet planet : planets) {
			if (this.equals(planet)) {
				netForce += 0;
			}
			else {
				double force = calcForceExertedByY(planet);
				netForce += force;
			}
		}
		return netForce; 
	}

	public void update(double dt, double fX, double fY) {
		double netAccelX = fX/mass;
		double netAccelY = fY/mass;
		double newVelX = xxVel + (dt * netAccelX);
		double newVelY = yyVel + (dt * netAccelY);
		xxVel = newVelX;
		yyVel = newVelY;
		xxPos = xxPos + (dt * newVelX);
		yyPos = yyPos + (dt * newVelY);
	}

	//drawing all planets
	public void draw() {
		String image = "images/" + imgFileName;
		StdDraw.picture(xxPos, yyPos, image);
	}
}