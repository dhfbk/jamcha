package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.SortedSetMultimap;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * This class adds feature of other lines to the current line using features parameters.
 *
 * @author dan92d
 */
public class FeatureIntegrator
{

   private SortedSetMultimap featuresParameters = null;
   private static FeatureIntegrator integrator = null;

   private FeatureIntegrator(@Nonnull SortedSetMultimap featuresParameters)
   {
//      if(featuresParameters==null)
//      {
//         throw new IllegalArgumentException("Parameter cannot be null");
//      }
      this.featuresParameters = featuresParameters;
   }
}
