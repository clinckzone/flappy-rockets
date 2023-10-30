class Rocket {

  Body body;
  Vec2 position;
  PImage skin;
  float angle;
  float radius;
  ParticleSystem[] particleSystem;

  Rocket(float xPos, float yPos, float angle, float radius) {
    this.position = new Vec2(xPos, yPos);
    this.angle = angle;
    this.radius = radius;
    this.skin = loadImage("./rocket/rocket0.png");
    this.makeRocket(xPos, yPos, angle, radius);

    this.particleSystem = new ParticleSystem[2];
    particleSystem[0] = new ParticleSystem(position.x - random(radius/3), position.y - random(radius/3));
    particleSystem[1] = new ParticleSystem(position.x - random(radius/3), position.y + random(radius/3));
  }

  void makeRocket(float xPos, float yPos, float angle, float radius) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.setType(BodyType.DYNAMIC);
    bodyDef.position.set(box2d.coordPixelsToWorld(xPos, yPos));
    bodyDef.setAngle(angle);
    body = box2d.createBody(bodyDef);

    PolygonShape polygonShape = new PolygonShape();
    //Defining vertices of the spaceship
    Vec2[] vertices = new Vec2[3];
    vertices[0]   = box2d.vectorPixelsToWorld(new Vec2(1.5*radius, 0));
    vertices[1]   = box2d.vectorPixelsToWorld(new Vec2(-radius*cos(PI/3), -radius*sin(PI/3)));
    vertices[2]   = box2d.vectorPixelsToWorld(new Vec2(-radius*cos(PI/3), radius*sin(PI/3)));
    polygonShape.set(vertices, vertices.length);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = polygonShape;
    fixtureDef.friction = 0.5;
    fixtureDef.restitution = 0.3;
    fixtureDef.density = 3;

    body.createFixture(fixtureDef);
    body.setUserData(this);
  }

  void update() {
    //update the position and angle variables of the object
    position = box2d.getBodyPixelCoord(body);
    body.getAngle();

    //Update the origin point of the particle system
    particleSystem[0].update(new Vec2(position.x - random(radius/3), position.y - random(radius/3)));
    particleSystem[1].update(new Vec2(position.x - random(radius/3), position.y + random(radius/3)));
  }

  void display() {
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
      popMatrix();
    popStyle();
  }
}