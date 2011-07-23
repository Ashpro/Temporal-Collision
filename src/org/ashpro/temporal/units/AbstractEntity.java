   package org.ashpro.temporal.units;
   
   import com.threed.jpct.*;
   import org.ashpro.temporal.TemporalCollision;

   public abstract class AbstractEntity extends Object3D implements Entity {
   
      protected float speed=0;
      protected int health = 20;
   
      public AbstractEntity(Object3D obj) {
         super(obj);
      }
   
      public void addToWorld(World world) {
         world.addObject(this);
      }
   
      public SimpleVector moveForward() {
         SimpleVector a=this.getZAxis();
         a.scalarMul(speed);
         SimpleVector ellipsoid = new SimpleVector(2, 2, 2);
         SimpleVector bump = checkForCollisionEllipsoid(a, ellipsoid, 3);
         if(bump != a)
            hurt(2);
         a = bump;
         this.translate(a);
         return a;
      }
   
      public void moveBackward() {
         SimpleVector a=this.getZAxis();
         a.scalarMul(-speed);
         this.translate(a);
      }
   
      public float getSpeed() {
         return speed;
      }
   
      public void setSpeed(float speed) {
         this.speed=speed;
      }
      public void hurt(int damage) {
         health -= damage;
      }
      public int getHealth() {
         return health;
      }
   }
