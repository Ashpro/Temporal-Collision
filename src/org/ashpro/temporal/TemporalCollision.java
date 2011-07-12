   package org.ashpro.temporal;
   import java.io.*;

   import java.awt.*;
   import java.awt.event.*;
   import java.awt.image.*;

   import com.threed.jpct.*;
   import com.threed.jpct.util.*;


   class TemporalCollision {
   
      public static final double VERSION = 0.01;
      private final static int SWITCH_RENDERER=35;
      private boolean fullscreen=false;
      private boolean openGL=false;
      private boolean wireframe=false;
      
      private FrameBuffer buffer=null;
      
      private World theWorld=null;
      private TextureManager texMan=null;
      private Camera camera=null;
      
      private Object3D terrain=null;
      private Texture numbers=null;
      
      private int width=640;
      private int height=480;
   
      private Frame frame=null;
      private Graphics gFrame=null;
      private BufferStrategy bufferStrategy=null;
      private GraphicsDevice device=null;
      private int titleBarHeight=0;
      private int leftBorderWidth=0;
   
      private int switchMode=0;
   
      private int fps;
      private int lastFps;
      private long totalFps;
   
      private int pps;
      private int lastPps;
   
      private boolean isIdle=false;
      private boolean exit=false;
   
      private float speed=0;
   
      public static void main(String[] args) {
         new TemporalCollision(args);
      }
      
      private TemporalCollision(String[] args) {
      
         Config.maxPolysVisible=10000;
      
         for (int i=0; i<args.length; i++) {
            if (args[i].equals("fullscreen")) {
               fullscreen=true;
               Config.glFullscreen=true;
            }
            if (args[i].equals("mipmap")) {
               Config.glMipmap=true;
            }
            if (args[i].equals("trilinear")) {
               Config.glTrilinear=true;
            }
         
            if (args[i].equals("16bit")) {
               Config.glColorDepth=16;
            }
            try {
               if (args[i].startsWith("width=")) {
                  width=Integer.parseInt(args[i].substring(6));
               }
               if (args[i].startsWith("height=")) {
                  height=Integer.parseInt(args[i].substring(7));
               }
               if (args[i].startsWith("refresh=")) {
                  Config.glRefresh=Integer.parseInt(args[i].substring(8));
               }
               if (args[i].startsWith("zbuffer=")) {
                  Config.glZBufferDepth=Integer.parseInt(args[i].substring(8));
                  if (Config.glZBufferDepth==16) {
                     Config.glFixedBlitting=true;
                  }
               }
            
            } 
               catch (Exception e) {
               // We don't care
               }
         }
      
         isIdle=false;
         switchMode=0;
         totalFps=0;
         fps=0;
         lastFps=0;
      
         theWorld=new World();
         texMan=TextureManager.getInstance();
      
         Config.fadeoutLight=false;
         theWorld.getLights().setOverbrightLighting(Lights.OVERBRIGHT_LIGHTING_DISABLED);
         theWorld.getLights().setRGBScale(Lights.RGB_SCALE_2X);
         theWorld.setAmbientLight(25, 30, 30);
      
         theWorld.addLight(new SimpleVector(0, -150, 0), 25, 22, 19);
         theWorld.addLight(new SimpleVector(-1000, -150, 1000), 22, 5, 4);
         theWorld.addLight(new SimpleVector(1000, -150, -1000), 4, 2, 22);
      
         theWorld.setFogging(World.FOGGING_ENABLED);
         theWorld.setFogParameters(4800,255,-255,-255);
         Config.farPlane=1200;
      
         char c=File.separatorChar;
      
         numbers=new Texture("resrc"+c+"textures"+c+"numbers.jpg");
         texMan.addTexture("numbers", numbers);
      
         Texture rocks=new Texture("resrc"+c+"textures"+c+"rocks.png");
         texMan.addTexture("rocks", rocks);
      
         Object3D[] objs=Loader.load3DS("resrc"+c+"models"+c+"terascene.3ds", 400);
         if (objs.length>0) {
            terrain=objs[0];
            terrain.setTexture("rocks");
         }
      
         terrain.enableLazyTransformations();
         theWorld.addObject(terrain);
      
         terrain.build();
      
         SimpleVector pos=terrain.getCenter();
         pos.scalarMul(-1f);
         terrain.translate(pos);
         terrain.rotateX((float)-Math.PI/2f);
         terrain.translateMesh();
         terrain.rotateMesh();
         terrain.setTranslationMatrix(new Matrix());
         terrain.setRotationMatrix(new Matrix());
      
         terrain.createTriangleStrips(2);
      
         OcTree oc=new OcTree(terrain,50,OcTree.MODE_OPTIMIZED);
         terrain.setOcTree(oc);
         oc.setCollisionUse(OcTree.COLLISION_USE);
      
         Config.collideOffset=250;
      
         terrain.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
         terrain.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
         
         camera=theWorld.getCamera();
         camera.setPosition(0,-500,0);
         camera.lookAt(terrain.getTransformedCenter());
      
         Config.tuneForOutdoor();
      
         initializeFrame();
      
         gameLoop();
      }
   
      private void initializeFrame() {
         if (fullscreen) {
            GraphicsEnvironment env=GraphicsEnvironment.getLocalGraphicsEnvironment();
            device=env.getDefaultScreenDevice();
            GraphicsConfiguration gc=device.getDefaultConfiguration();
            frame=new Frame(gc);
            frame.setUndecorated(true);
            frame.setIgnoreRepaint(true);
            device.setFullScreenWindow(frame);
            if (device.isDisplayChangeSupported()) {
               device.setDisplayMode(new DisplayMode(width, height, 32, 0));
            }
            frame.createBufferStrategy(2);
            bufferStrategy=frame.getBufferStrategy();
            Graphics g=bufferStrategy.getDrawGraphics();
            bufferStrategy.show();
            g.dispose();
         } 
         else {
            frame=new Frame();
            frame.setTitle("Temporal Collision "+VERSION);
            frame.pack();
            Insets insets = frame.getInsets();
            titleBarHeight=insets.top;
            leftBorderWidth=insets.left;
            frame.setSize(width+leftBorderWidth+insets.right, height+titleBarHeight+insets.bottom);
            frame.setResizable(false);
            frame.show();
            gFrame=frame.getGraphics();
         }
      
         frame.addWindowListener(new WindowEvents());
      }
   
      private void display() {
      
         blitNumber((int) totalFps, 5, 2);
         blitNumber((int) lastPps, 5, 12);
      
         if (!openGL) {
            if (!fullscreen) {
               buffer.display(gFrame, leftBorderWidth, titleBarHeight);
            } 
            else {
               Graphics g=bufferStrategy.getDrawGraphics();
               g.drawImage(buffer.getOutputBuffer(), 0, 0, null);
               bufferStrategy.show();
               g.dispose();
            }
         } 
         else {
            buffer.displayGLOnly();
         }
      }
   
      private void blitNumber(int number, int x, int y) {
         if (numbers!=null) {
            String sNum=Integer.toString(number);
            for (int i=0; i<sNum.length(); i++) {
               char cNum=sNum.charAt(i);
               int iNum=cNum-48;
               buffer.blit(numbers, iNum*5, 0, x, y, 5, 9, FrameBuffer.TRANSPARENT_BLITTING);
               x+=5;
            }
         }
      }
   
      private void gameLoop() {
         World.setDefaultThread(Thread.currentThread());
      
         buffer=new FrameBuffer(width, height, FrameBuffer.SAMPLINGMODE_NORMAL);
         buffer.enableRenderer(IRenderer.RENDERER_SOFTWARE);
         buffer.setBoundingBoxMode(FrameBuffer.BOUNDINGBOX_NOT_USED);
      
         buffer.optimizeBufferAccess();
      
         Timer timer=new Timer(25);
         timer.start();
      
         Timer fpsTimer=new Timer(1000);
         fpsTimer.start();
      
         while (!exit) {
            if (!isIdle) {
            
               long ticks=timer.getElapsedTicks();
            
               if (switchMode!=0) {
                  switchOptions();
               }
            
               buffer.clear();
               theWorld.renderScene(buffer);
            
               if (!wireframe) {
                  theWorld.draw(buffer);
               }
               else {
                  theWorld.drawWireframe(buffer, Color.white);
               }
               buffer.update();
               display();
            
               fps++;
               pps+=theWorld.getVisibilityList().getSize();
            
               if (fpsTimer.getElapsedTicks()>0) {
                  totalFps=(fps-lastFps);
                  lastFps=fps;
                  lastPps=pps;
                  pps=0;
               }
            
               Thread.yield();
            
            } 
            else {
               try {
                  Thread.sleep(500);
               } 
                  catch (InterruptedException e) {}
            }
         }
      
         if (!openGL && fullscreen) {
            device.setFullScreenWindow(null);
         }
      
         System.exit(0);
      }
   
      private void switchOptions() {
         switch (switchMode) {
            case (SWITCH_RENDERER): 
               {
                  isIdle=true;
                  if (buffer.usesRenderer(IRenderer.RENDERER_OPENGL)) {
                     buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
                     buffer.enableRenderer(IRenderer.RENDERER_SOFTWARE, IRenderer.MODE_OPENGL);
                     openGL=false;
                     if (fullscreen) {
                        device.setFullScreenWindow(null);
                     }
                     frame.hide();
                     frame.dispose();
                     initializeFrame();
                  } 
                  else {
                     frame.hide();
                     buffer.enableRenderer(IRenderer.RENDERER_OPENGL, IRenderer.MODE_OPENGL);
                     buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
                     openGL=true;
                  }
                  isIdle=false;
                  break;
               }
         }
         switchMode=0;
      }
   
   
      private class WindowEvents extends WindowAdapter {
      
         public void windowIconified(WindowEvent e) {
            isIdle=true;
         }
      
         public void windowDeiconified(WindowEvent e) {
            isIdle=false;
         }
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      }
   
   
      private class Timer {
      
         private long ticks=0;
         private long granularity=0;
      
         public Timer(int granularity) {
            this.granularity=granularity;
         }
      
         public void start() {
            ticks=System.currentTimeMillis();
         }
      
         public void reset() {
            start();
         }
      
         public long getElapsedTicks() {
            long cur=System.currentTimeMillis();
            long l=cur-ticks;
         
            if (l>=granularity) {
               ticks=cur-(l%granularity);
               return l/granularity;
            }
            return 0;
         }
      }
   
   }
