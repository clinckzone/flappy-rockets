import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import processing.sound.*; 
import ddf.minim.*; 
import shiffman.box2d.*; 
import java.util.Iterator; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.joints.*; 
import org.jbox2d.collision.shapes.*; 
import org.jbox2d.collision.shapes.Shape; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.*; 
import org.jbox2d.dynamics.contacts.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FlappyRockets extends PApplet {














Box2DProcessing box2d;
Rocket rocket;
Spring spring;
GameState state;
Controller controller;
AudioPlayer player;
Minim minim;

public void setup()	{
	background(0);
	
	//fullScreen(P2D);

	box2d = new Box2DProcessing(this);
	box2d.createWorld();
	box2d.setGravity(0, 0);
	box2d.listenForCollisions();

	state = new GameState(this);
	rocket = new Rocket(width/4, height/2, 0, 20);
	//controller = new Controller(this);
	spring = new Spring(width/4, height/2, box2d.getGroundBody(), rocket.body);

	//background music
	minim = new Minim(this);
	player = minim.loadFile("./music/background.mp3", 2048);
	player.play();
	player.loop();
}

public void draw()	{
	//background image
	background(state.backgroundImg);

	if(state.mode == -1)	{
		//start
		textAlign(CENTER);
		textFont(state.scoreFont);

		textSize(50);
		text("Into Darkness", width/2, height/2);

		textSize(25);
		text("Click anywhere to play", width/2, height - 25);

		if(mousePressed)		{
			state.mode = 0;
		}
	}
	else if(state.mode == 0)	{			
		//Move one time unit forward in Box2D time
		box2d.step();

		if(random(1) < 0.03f)	{
			state.obstacles.add(new Obstacle(width + 75, random(height), 0, 55));
		}
		Iterator<Obstacle> iterator = state.obstacles.iterator();
		while(iterator.hasNext())	{
			Obstacle obstacle = iterator.next();
			if(obstacle.checkForKill())	{
				obstacle.kill();
				iterator.remove();
				state.score++;
			}
			else 	{
				obstacle.update();
				obstacle.display();
			}
		}
	  
	  	//float xvalue = width/4;
	  	//float yvalue = controller.controllerRead();
		spring.updateTarget(width/4, mouseY);
		rocket.update();

		rocket.display();
		state.display();
	}
	else if(state.mode == 1)	{
		//Deleting all the asteroid objects
		Iterator<Obstacle> iterator = state.obstacles.iterator();
		while(iterator.hasNext())	{
			Obstacle obstacle = iterator.next();
			obstacle.kill();
			iterator.remove();
		}
		state.obstacles.clear();
		state.collidedObjects.clear();

		textAlign(CENTER);
		textFont(state.scoreFont);

		textSize(35);
		text("Game Over", width/2, height/2 - 35);
		
		textSize(25);
		text("Score: " + state.score, width/2, height/2);
		
		textSize(50);
		text("Try Again", width/2, height/2 + 50);

		textSize(25);
		text("Click anywhere to restart", width/2, height - 25);

		if(mousePressed)	{
			state = new GameState(this);
			state.mode = 0;
			rocket.skin = loadImage("./rocket/rocket" + 0 + ".png");
		}
	}

}

public void beginContact(Contact contact) 	{
	Fixture fixtureA = contact.getFixtureA();
	Fixture fixtureB = contact.getFixtureB();

	Body bodyA = fixtureA.getBody();
	Body bodyB = fixtureB.getBody();

	Object objectA = bodyA.getUserData();
	Object objectB = bodyB.getUserData();

	if(objectA.getClass() == Rocket.class || objectB.getClass() == Rocket.class)	{
		if(!(state.lives < 0))	{
			state.lives = 5 - state.collidedObjects.size();
			rocket.skin = loadImage("./rocket/rocket" + constrain(5 - state.lives, 0, 5) + ".png");
		}	
		else {
			state.mode = 1;
		}

		if(objectA.getClass() == Rocket.class)		{
			if(!state.collidedObjects.contains(objectB))		{
				state.collidedObjects.add((Obstacle) objectB);
				state.soundFile.play();
			}
		}
		else 	{
			if(!state.collidedObjects.contains(objectA))		{
				state.collidedObjects.add((Obstacle) objectA);
				state.soundFile.play();
			}
		}
	}
}
class Controller 	{
	
	Serial controlPort;
	float val;
  
	Controller(PApplet appletName)	{
		controlPort = new Serial(appletName, "COM5", 9600);
	}

	public int controllerRead()	{
		if(controlPort.available() > 0)	{
			val = controlPort.read();     
			println("val: "+val);
			return (int) val;
		}
		else {
			return (int) val;
		}
	}
}
class GameState		{
  	ArrayList<Obstacle> obstacles;
 	ArrayList<Obstacle> collidedObjects;  //An arraylist to store the asteroids that have collided with the rocket       

  	int mode;
  	int score;
  	int lives;

  	PImage backgroundImg;
	PImage rocketImage;
  	SoundFile soundFile;
	PFont scoreFont;

	GameState(PApplet program)		{
    	obstacles = new ArrayList<Obstacle>();
    	collidedObjects = new ArrayList<Obstacle>();
    	
    	mode  = -1;
    	score = 0;
    	lives = 5;

    	//background
    	backgroundImg = loadImage("background.png");
    	backgroundImg.resize(width, height);
    	 
		rocketImage = loadImage("./rocket/rocketLife.png");
		scoreFont = createFont("font/Moon Light.otf", 50);
		soundFile = new SoundFile(program, "./music/sound.wav"); 
	}

	public void display()	{
		for(int i = 0; i < lives; i++)	{
			imageMode(CENTER);
			image(rocketImage, 50*(i+1), 40, 125, 125);
		}
		textFont(scoreFont);
		text(score, width - 75, height -25);
	}
}
class Obstacle 	{

	Body body;
	Vec2 position;
	PImage skin;
	int sides;
	float angle;
	float radius;

	Obstacle(float x, float y, float angle, float radius)	{
		this.position = new Vec2(x, y);
		this.angle = angle;
		this.radius = radius;
		this.sides = PApplet.parseInt(random(6, 9));
		this.skin =  loadImage("./obstacles/"+sides+"sides.png");
		makeObstacle(x, y, angle, radius);
	}

	public void makeObstacle(float x, float y, float angle, float radius)	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.setAngle(angle);
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.position.set(box2d.coordPixelsToWorld(x, y));
		bodyDef.setLinearVelocity(new Vec2(random(-30, -20), 0));
		bodyDef.setAngularVelocity(random(-2, 2));
		body = box2d.createBody(bodyDef);


		Vec2[] vertices = new Vec2[sides];
		for(int i = 0; i < vertices.length; i++)	{
			float theta = i*(TWO_PI)/(vertices.length);
			radius = radius - (random(3));
			vertices[i] = box2d.vectorPixelsToWorld(radius*cos(theta), radius*sin(theta));  
		}

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.set(vertices, vertices.length); 

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.3f;
		fixtureDef.density = 100;

		body.createFixture(fixtureDef);
		body.setUserData(this);
	}

	public boolean checkForKill()	{
		if(position.x + radius< 0)	return true;
		else return false;
	}

	public void kill()	{
		box2d.destroyBody(body);
	}

	public void update()	{
		position = box2d.getBodyPixelCoord(body);
		angle = body.getAngle();
	}

	public void display()	{
		Fixture fixture = body.getFixtureList();
		PolygonShape polygonShape = (PolygonShape) fixture.getShape();
		pushStyle();
			stroke(0);
			fill(255);

			pushMatrix();
				translate(position.x, position.y);
				rotate(-angle);
				imageMode(CENTER);
				image(skin, 0, 0);
				/*Code for drawing the actual skeleton of the asteroid*/
				// beginShape();
				// for(int i = 0; i < polygonShape.getVertexCount(); i++)	{
				// 	Vec2 vertex = box2d.vectorWorldToPixels(polygonShape.getVertex(i));
				// 	vertex(vertex.x, vertex.y);
				// }
				// endShape(CLOSE);			
			popMatrix();
		popStyle();
	}
}
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
		this.skin     = loadImage("./flame/flame"+PApplet.parseInt(random(5))+".png");
	}

	public void update()	{
		velocity.addLocal(acceleration);
		location.addLocal(velocity);
		lifespan -= 10;
		acceleration.mulLocal(0);
	}

	public boolean isDead() 		{
		if(lifespan < 0)	{
			return true;
		}	
		else {
			return false;
		}
	}

	public void display()		{
		imageMode(CENTER);
		float tintDiff = map(state.lives, 0, 5, 255, 0);
		tint(255, 255 - tintDiff, 0, lifespan);
		image(skin, location.x, location.y, 2.8f*radius, 2.8f*radius);
	}
}
class ParticleSystem 		{
	
	ArrayList<Particle> particleSystem;
	Vec2 velocity;
	Vec2 origin;

	ParticleSystem(float x, float y)	{
		particleSystem = new ArrayList<Particle>();
		origin   = new Vec2(x, y);
	}

	public void add()	{
		Vec2 velocity = new Vec2(random(-5, -7), 0);
		//In order of occurence - "origin", "velocity", "radius"
		particleSystem.add(new Particle(origin, velocity, 40));  
	}

	public void update(Vec2 updatedPosition)	{
		//update the origin point of the particle system
		origin = updatedPosition.clone();
		//Add a new particle each time the function runs in accordance with the health
		float frequency = map(constrain(state.lives, 0, 5), 0, 5, 0.50f, 1);
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

	public void display()	{
		for(Particle particle : particleSystem)		{
			particle.display();
		}
	}
}
class Rocket	{

	Body body;	
	Vec2 position;
	PImage skin;
	float angle;
	float radius;
	ParticleSystem[] particleSystem;

	Rocket(float xPos, float yPos, float angle, float radius)	{
		this.position = new Vec2(xPos, yPos);
		this.angle = angle;
		this.radius = radius;
		this.skin = loadImage("./rocket/rocket0.png");
		this.makeRocket(xPos, yPos, angle, radius);

		this.particleSystem = new ParticleSystem[2];
		particleSystem[0] = new ParticleSystem(position.x - random(radius/3), position.y - random(radius/3));
		particleSystem[1] = new ParticleSystem(position.x - random(radius/3), position.y + random(radius/3));
	}

	public void makeRocket(float xPos, float yPos, float angle, float radius)	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.position.set(box2d.coordPixelsToWorld(xPos, yPos));
		bodyDef.setAngle(angle);
		body = box2d.createBody(bodyDef);

		/*Simple circular shape*/
		// CircleShape circleShape = new CircleShape();
		// circleShape.m_radius = box2d.scalarPixelsToWorld(radius);

		PolygonShape polygonShape = new PolygonShape();
		//Defining vertices of the spaceship
		Vec2[] vertices = new Vec2[3];
		vertices[0]   = box2d.vectorPixelsToWorld(new Vec2(1.5f*radius, 0));
		vertices[1]   = box2d.vectorPixelsToWorld(new Vec2(-radius*cos(PI/3), -radius*sin(PI/3)));
		vertices[2]   = box2d.vectorPixelsToWorld(new Vec2(-radius*cos(PI/3), radius*sin(PI/3)));
		polygonShape.set(vertices, vertices.length);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.3f;
		fixtureDef.density = 3;

		body.createFixture(fixtureDef);
		body.setUserData(this);
	}

	public void update()	{
		//update the position and angle variables of the object
		position = box2d.getBodyPixelCoord(body);
		body.getAngle();

		//Update the origin point of the particle system
		particleSystem[0].update(new Vec2(position.x - random(radius/3), position.y - random(radius/3)));
		particleSystem[1].update(new Vec2(position.x - random(radius/3), position.y + random(radius/3)));
	}

	public void display()	{
		//rendering particle system
		pushStyle();
			blendMode(ADD);
			particleSystem[0].display();
			particleSystem[1].display();
		popStyle();

		pushStyle();
			stroke(0);
			fill(255);
			pushMatrix();
				translate(position.x, position.y);
				rotate(-angle);
				imageMode(CENTER);
				image(skin, 0, 0);
				/*Code snippet for drawing the skeleton of the rocket*/
				// beginShape();
				// vertex(1.5*radius, 0);
				// vertex(-radius*cos(PI/3), -radius*sin(PI/3));
				// vertex(-radius*cos(PI/3), radius*sin(PI/3));
				// endShape(CLOSE);
			popMatrix();
		popStyle();
	}
}
class Spring	{

	MouseJoint mouseJoint;
	Body bodyA;
	Body bodyB;
	Vec2 follow;

	Spring(float xFollow, float yFollow, Body bodyA, Body bodyB)	{
		this.mouseJoint = null;
		this.bodyA = bodyA;
		this.bodyB = bodyB;
		this.follow = new Vec2(xFollow, yFollow);
		this.bind(xFollow, yFollow, bodyA, bodyB);
	}

	public void bind(float xFollow, float yFollow, Body bodyA, Body bodyB)	{
		MouseJointDef mouseJointDef = new MouseJointDef();
		mouseJointDef.bodyA = bodyA;
		mouseJointDef.bodyB = bodyB;
		mouseJointDef.maxForce = 5000*bodyB.m_mass;
		mouseJointDef.frequencyHz = 5;
		mouseJointDef.dampingRatio = 0.5f;

		//Setting target
		Vec2 mousePos = box2d.coordPixelsToWorld(xFollow, yFollow);
		mouseJointDef.target.set(mousePos);
		
		//Creating mouse joint
		mouseJoint = (MouseJoint) box2d.world.createJoint(mouseJointDef);
	}

	public void updateTarget(float xFollow, float yFollow)	{
		//Updating target
		Vec2 mousePos = box2d.coordPixelsToWorld(xFollow, yFollow);
		mouseJoint.setTarget(mousePos);
	}

}
  public void settings() { 	size(1200, 640, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlappyRockets" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
