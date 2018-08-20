class Particle 		{
	
	Vec2 location;
	Vec2 velocity;
	Vec2 acceleration;
	float radius;
	float mass;
	int lifespan;
	PImage skin;

	Particle(Vec2 location, Vec2 velocity, float radius)	{
		this.location = location;
		this.velocity = velocity;
		this.acceleration = new Vec2(0, 0);
		this.radius   = radius;
		this.mass 	  = 1;
		this.lifespan = 255;
		this.skin     = loadImage("./flame/flame"+int(random(5))+".png");
	}

	void update()	{
		velocity.addLocal(acceleration);
		location.addLocal(velocity);
		lifespan -= 10;
		acceleration.mulLocal(0);
	}

	boolean isDead() 		{
		if(lifespan < 0)	{
			return true;
		}	
		else {
			return false;
		}
	}

	void display()		{
		imageMode(CENTER);
		float tintDiff = map(state.lives, 0, 5, 255, 0);
		tint(255, 255 - tintDiff, 0, lifespan);
		image(skin, location.x, location.y, 2.8*radius, 2.8*radius);
	}
}
