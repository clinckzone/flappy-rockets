class ParticleSystem 		{
	
	ArrayList<Particle> particleSystem;
	Vec2 velocity;
	Vec2 origin;

	ParticleSystem(float x, float y)	{
		particleSystem = new ArrayList<Particle>();
		origin   = new Vec2(x, y);
	}

	void add()	{
		Vec2 velocity = new Vec2(random(-5, -7), 0);
		//In order of occurence - "origin", "velocity", "radius"
		particleSystem.add(new Particle(origin, velocity, 40));  
	}

	void update(Vec2 updatedPosition)	{
		//update the origin point of the particle system
		origin = updatedPosition.clone();
		//Add a new particle each time the function runs in accordance with the health
		float frequency = map(constrain(state.lives, 0, 5), 0, 5, 0.50, 1);
		if(random(1) < frequency)	{
			this.add();  
		}

		Iterator<Particle> iterator = particleSystem.iterator();
		while (iterator.hasNext()) {
			Particle particle = iterator.next();
			if(particle.isDead())	{
				//if particle is dead, remove it
				iterator.remove();
			}
			else {
				//else update the position of the particle
				particle.update();
			}
		}
	}

	void display()	{
		for(Particle particle : particleSystem)		{
			particle.display();
		}
	}
}
