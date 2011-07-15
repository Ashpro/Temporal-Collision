   package org.ashpro.temporal;

   import java.applet.*;

   public class Sound
   {
   // List of used sounds
      private static char c = java.io.File.separatorChar;
      public static AudioClip attack = getClip("resrc"+c+"sound"+c+"main.wav");
   
      public static AudioClip getClip(String arg)
      {
         try
         {
         
            java.net.URL file = new java.io.File(arg).toURL();
            return Applet.newAudioClip(file);
         }
            catch(Exception e)
            {
               System.out.println("Error-GetClip");
               e.printStackTrace();
               return null;
            }
      }
   
      public static void loop(final AudioClip arg)
      {
         try
         {
            arg.loop();
         }
            catch(Exception e)
            {
               System.out.println("Error-Loop");
               e.printStackTrace();
            }
      }
      public static void play(AudioClip arg)
      {
         try
         {
            arg.play();
         }
            catch(Exception e)
            {
               System.out.println("Error-Play");
               e.printStackTrace();
            }
      }
      public static void stop(AudioClip arg)
      {
         try
         {
            arg.stop();
         }
            catch(Exception e)
            {
               System.out.println("Error-Stop");
               e.printStackTrace();
            }
      }
   
   }
