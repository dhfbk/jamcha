package eu.fbk.dh.jamcha;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Class that represents a map of all feature possible tags.
 * @author dan92d
 */
public final class TagsMap
{
   private HashMap<Integer,String> tagsMap= new HashMap<>();
   /**
    * Calculate 
    * @param tagsList list of all tags
    * @return map of all possible tags
    */  
   @Nonnull
   public Map mapTags(@Nonnull Iterable<String> tagsList)
   {
      for(String tag:tagsList)
      {
         tagsMap.put(tag.hashCode(), tag);
      }
      return getTagsMap();
   }
   
   @Nonnull
   public Map getTagsMap()
   {
      if(tagsMap==null)
      {
         tagsMap= new HashMap<>();
      }
      
      return tagsMap;
   }
}
