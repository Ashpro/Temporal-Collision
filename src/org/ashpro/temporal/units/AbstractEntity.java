   package org.ashpro.temporal.units;
   
   import com.threed.jpct.*;
   import org.ashpro.temporal.TemporalCollision;

   public abstract class AbstractEntity extends Object3D implements Entity {
   
      protected float speed=0;
   
      public AbstractEntity(Object3D obj) {
         super(obj);
      }
   
      public void addToWorld(World world) {
         world.addObject(this);
      }
   
      public void moveForward() {
         SimpleVector a=this.getZAxis();
         a.scalarMul(speed);
         this.translate(a);
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
   }
