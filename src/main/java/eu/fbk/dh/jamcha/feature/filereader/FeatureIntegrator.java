package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.SortedSetMultimap;
import javax.annotation.Nonnull;

/**
 * This class adds feature of other lines to the current line using features parameters.
 *
 * @author dan92d
 */
public class FeatureIntegrator
{

   private SortedSetMultimap featuresParameters = null;

   private FeatureIntegrator(@Nonnull SortedSetMultimap featuresParameters)
   {
      this.featuresParameters = featuresParameters;
   }
}
