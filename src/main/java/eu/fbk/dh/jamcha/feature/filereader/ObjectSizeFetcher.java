package eu.fbk.dh.jamcha.feature.filereader;

import java.lang.instrument.Instrumentation;

/**
 *
 * @author dan92d
 */
public class ObjectSizeFetcher
{
   private static Instrumentation instr;
   
   public static void premain(String args, Instrumentation inst)
   {
      instr=inst;
   }
   
   public static long getObjectSize(Object o)
   {
      return instr.getObjectSize(o);
   }
}
