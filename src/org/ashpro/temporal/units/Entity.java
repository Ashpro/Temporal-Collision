   package org.ashpro.temporal.units;
   
   import com.threed.jpct.*;

   public interface Entity {
   
      void addToWorld(World world);
   
      SimpleVector moveForward();
   
      void moveBackward();
   
      float getSpeed();
   
      void setSpeed(float speed);
   }
