   package org.ashpro.temporal.units;
   
   import com.threed.jpct.*;

   public interface Entity {
   
      void addToWorld(World world);
   
      void moveForward();
   
      void moveBackward();
   
      float getSpeed();
   
      void setSpeed(float speed);
   }
