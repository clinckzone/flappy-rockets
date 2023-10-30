class Obstacle {

  Body body;
  Vec2 position;
  PImage skin;
  int sides;
  float angle;
  float radius;

  Obstacle(float x, float y, float angle, float radius) {
    this.position = new Vec2(x, y);
    this.angle = angle;
    this.radius = radius;
    this.sides = int(random(6, 9));
    this.skin =  loadImage("./obstacles/"+sides+"sides.png");
    makeObstacle(x, y, angle, radius);
  }

  void makeObstacle(float x, float y, float angle, float radius) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.setAngle(angle);
    bodyDef.setType(BodyType.DYNAMIC);
    bodyDef.position.set(box2d.coordPixelsToWorld(x, y));
    bodyDef.setLinearVelocity(new Vec2(random(-30, -20), 0));
    bodyDef.setAngularVelocity(random(-2, 2));
    body = box2d.createBody(bodyDef);


    Vec2[] vertices = new Vec2[sides];
    for(int i = 0; i < vertices.length; i++) {
      float theta = i*(TWO_PI)/(vertices.length);
      radius = radius - (random(3));
      vertices[i] = box2d.vectorPixelsToWorld(radius*cos(theta), radius*sin(theta));  
    }

    PolygonShape polygonShape = new PolygonShape();
    polygonShape.set(vertices, vertices.length); 

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = polygonShape;
    fixtureDef.friction = 0.5;
    fixtureDef.restitution = 0.3;
    fixtureDef.density = 100;

    body.createFixture(fixtureDef);
    body.setUserData(this);
  }

  boolean checkForKill() {
    if(position.x + radius< 0)	return true;
    else return false;
  }

  void kill() {
    box2d.destroyBody(body);
  }

  void update() {
    position = box2d.getBodyPixelCoord(body);
    angle = body.getAngle();
  }

  void display() {
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
      popMatrix();
    popStyle();
  }
}