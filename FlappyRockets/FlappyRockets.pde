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

Box2DProcessing box2d;
Rocket rocket;
Spring spring;
GameState state;
Controller controller;
AudioPlayer player;
Minim minim;

void setup() {
  background(0);
  size(1200, 640, P2D);
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

void draw() {
  //background image
  background(state.backgroundImg);

  if(state.mode == -1) {
    //start
    textAlign(CENTER);
    textFont(state.scoreFont);

    textSize(50);
    text("Into Darkness", width/2, height/2);

    textSize(25);
    text("Click anywhere to play", width/2, height - 25);

    if(mousePressed) {
      state.mode = 0;
    }
  }
  else if(state.mode == 0) {
    //Move one time unit forward in Box2D time
    box2d.step();

    if(random(1) < 0.03) {
      state.obstacles.add(new Obstacle(width + 75, random(height), 0, 55));
    }
    Iterator<Obstacle> iterator = state.obstacles.iterator();
    while(iterator.hasNext()) {
      Obstacle obstacle = iterator.next();
      if(obstacle.checkForKill()) {
        obstacle.kill();
        iterator.remove();
        state.score++;
      }
      else {
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
  else if(state.mode == 1) {
    //Deleting all the asteroid objects
    Iterator<Obstacle> iterator = state.obstacles.iterator();
    while(iterator.hasNext()) {
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

    if(mousePressed) {
      state = new GameState(this);
      state.mode = 0;
      rocket.skin = loadImage("./rocket/rocket" + 0 + ".png");
    }
  }

}

void beginContact(Contact contact) {
  Fixture fixtureA = contact.getFixtureA();
  Fixture fixtureB = contact.getFixtureB();

  Body bodyA = fixtureA.getBody();
  Body bodyB = fixtureB.getBody();

  Object objectA = bodyA.getUserData();
  Object objectB = bodyB.getUserData();

  if(objectA.getClass() == Rocket.class || objectB.getClass() == Rocket.class) {
    if(!(state.lives < 0)) {
      state.lives = 5 - state.collidedObjects.size();
      rocket.skin = loadImage("./rocket/rocket" + constrain(5 - state.lives, 0, 5) + ".png");
    }
    else {
      state.mode = 1;
    }

    if(objectA.getClass() == Rocket.class) {
      if(!state.collidedObjects.contains(objectB)) {
        state.collidedObjects.add((Obstacle) objectB);
        state.soundFile.play();
      }
    }
    else {
      if(!state.collidedObjects.contains(objectA)) {
        state.collidedObjects.add((Obstacle) objectA);
        state.soundFile.play();
      }
    }
  }
}
