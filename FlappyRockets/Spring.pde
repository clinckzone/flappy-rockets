class Spring {

  MouseJoint mouseJoint;
  Body bodyA;
  Body bodyB;
  Vec2 follow;

  Spring(float xFollow, float yFollow, Body bodyA, Body bodyB) {
    this.mouseJoint = null;
    this.bodyA = bodyA;
    this.bodyB = bodyB;
    this.follow = new Vec2(xFollow, yFollow);
    this.bind(xFollow, yFollow, bodyA, bodyB);
  }

  void bind(float xFollow, float yFollow, Body bodyA, Body bodyB) {
    MouseJointDef mouseJointDef = new MouseJointDef();
    mouseJointDef.bodyA = bodyA;
    mouseJointDef.bodyB = bodyB;
    mouseJointDef.maxForce = 5000*bodyB.m_mass;
    mouseJointDef.frequencyHz = 5;
    mouseJointDef.dampingRatio = 0.5;

    //Setting target
    Vec2 mousePos = box2d.coordPixelsToWorld(xFollow, yFollow);
    mouseJointDef.target.set(mousePos);
    
    //Creating mouse joint
    mouseJoint = (MouseJoint) box2d.world.createJoint(mouseJointDef);
  }

  void updateTarget(float xFollow, float yFollow) {
    //Updating target
    Vec2 mousePos = box2d.coordPixelsToWorld(xFollow, yFollow);
    mouseJoint.setTarget(mousePos);
  }

}