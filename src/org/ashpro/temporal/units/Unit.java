   package org.ashpro.temporal.units;

   import com.threed.jpct.*;
   import java.io.*;


   public class Unit extends AbstractEntity {
      private float yRot=0;
      private static char c = File.separatorChar;
      private Object3D leg1 = null, leg2 = null;
      static
      {
         Texture spot=new Texture("resrc"+c+"textures"+c+"skin.jpg");
         TextureManager.getInstance().addTexture("magician", spot);
      }
      public Unit(){
         super(Primitives.getBox(3, 1.25f));
         leg1 = Primitives.getSphere(5, 4);
         leg2 = Primitives.getSphere(5, 4);
         addChild(leg1);
         addChild(leg2);
         setTexture("magician");
         leg1.setTexture("magician");
         leg2.setTexture("magician");
         setEnvmapped(Object3D.ENVMAP_ENABLED);
         leg1.setEnvmapped(Object3D.ENVMAP_ENABLED);
         leg2.setEnvmapped(Object3D.ENVMAP_ENABLED);
         build();
         leg1.build();
         leg2.build();
      }
      public boolean place(Object3D ground) {
         SimpleVector dropDown=new SimpleVector(0, 1, 0);
         Matrix rotMat=getRotationMatrix();
         rotMat.setIdentity();
         setRotationMatrix(rotMat);
      
         rotateY(yRot);
         translate(0, -10, 0);
      
         float rightFrontHeight=ground.calcMinDistance(leg1.getTransformedCenter(), dropDown, 4*30);
         float leftFrontHeight=ground.calcMinDistance(leg2.getTransformedCenter(), dropDown, 4*30);
      
         translate(0, 10, 0);
      
         rotMat=getRotationMatrix();
         rotMat.setIdentity();
         setRotationMatrix(rotMat);
      
         if (rightFrontHeight!=Object3D.COLLISION_NONE&&
          leftFrontHeight!=Object3D.COLLISION_NONE) {
         
            rightFrontHeight-=10;
            leftFrontHeight-=10;
            double angleFront=rightFrontHeight-leftFrontHeight;
            double as=(angleFront/(16d));
            angleFront=Math.atan(as);
         
            float rot=(float) ((angleFront*2)/2d);
            rotateZ(rot);
         
            float down=rightFrontHeight;
            if (leftFrontHeight<down) {
               down=leftFrontHeight;
            }
            dropDown.scalarMul(down-4);
            translate(dropDown);
         
         } 
         else {
            return false;
         }
         rotateY(yRot);
         return true;
      }
   
      public void addToWorld(World world) {
         super.addToWorld(world);
         world.addObject(leg1);
         world.addObject(leg2);
      }
      public void turnLeft() {
         yRot-=0.075f;
      }
      public void turnRight() {
         yRot+=0.075f;
      }
      public float getDirection() {
         return yRot;
      }
   
   }