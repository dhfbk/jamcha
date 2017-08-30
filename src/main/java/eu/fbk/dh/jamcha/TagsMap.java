package eu.fbk.dh.jamcha;

import com.google.common.collect.Multimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Class that represents a map of all feature possible tags.
 *
 * @author dan92d
 */
public final class TagsMap
{

   private HashMap<Integer, String> tagsMap = new HashMap<>();

   /**
    * Constructor
    *
    * @param tagsList list of all tags
    */
   public TagsMap(@Nonnull Iterable<String> tagsList)
   {
      createMapFromTagsList(tagsList);
   }
   
   public TagsMap(@Nonnull Multimap<Integer, FeatureInfo> featuresPerRow)
   {
      ArrayList<String> tagsList=new ArrayList<>(featuresPerRow.keys().size());
      for(FeatureInfo feature:featuresPerRow.values())
      {
         if(feature.isTag())
         {
            tagsList.add(feature.getFeatureValue());
         }
      }
      createMapFromTagsList(tagsList);
   }
   
   private void createMapFromTagsList(@Nonnull Iterable<String> tagsList)
   {
      for (String tag : tagsList)
      {
         tagsMap.put(tag.hashCode(), tag);
      }
   }

   @Nonnull
   public Map getTagsMap()
   {
      if (tagsMap == null)
      {
         tagsMap = new HashMap<>();
      }

      return tagsMap;
   }
}
