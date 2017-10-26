package eu.fbk.dh.jamcha.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represent a features line
 */
public class Line
{
   private int line;
   private String tag;
   private ArrayList<String> features;
   private int sequenceIndex;

   /**
    * Constructor
    *
    * @param line          line number, zero or greater
    * @param sequenceIndex the number of the sentence to which this co belongs. Value must be zero or greater
    * @param tag           tag of line. Can be null
    * @param lineFeatures  list of words to add to co features
    */
   public Line(@Nonnegative int line, @Nonnegative int sequenceIndex, @Nullable String tag, @Nonnull Collection<String> lineFeatures)
   {
      if (line < 0 || sequenceIndex < 0)
      {
         throw new IllegalArgumentException("Line or sequenceIndex < 0");
      }
      this.line = line;
      this.sequenceIndex = sequenceIndex;
      this.tag = tag;
      this.features = new ArrayList<>(lineFeatures);
   }

   /**
    * Constructor
    *
    * @param line          line number, zero or greater
    * @param sequenceIndex the number of the sentence to which this co belongs. Value must be zero or greater
    * @param lineFeatures  list of words to add to co features
    */
   public Line(@Nonnegative int line, @Nonnegative int sequenceIndex, @Nonnull Collection<String> lineFeatures)
   {
      this(line, sequenceIndex, null, lineFeatures);
   }

   @Nonnegative
   public int getLine()
   {
      return this.line;
   }

   public String getTag()
   {
      return this.tag;
   }

   @Nonnegative
   public int getSequence()
   {
      return this.sequenceIndex;
   }

   @Nonnull
   public List<String> getWords()
   {
      return this.features;
   }

   public void setTag(@Nonnull String tag)
   {
      this.tag = tag;
   }

   @Override
   public boolean equals(Object o)
   {
      boolean retval = false;
      if (o != null && o instanceof Line)
      {
         Line co = (Line) o;
         boolean tagsFlag = co.tag == null ? this.tag == null : co.tag.equals(this.tag);
         retval = tagsFlag && co.features.equals(this.features) && co.line == this.line && co.sequenceIndex == this.sequenceIndex;
      }
      return retval;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 97 * hash + this.line;
      hash = 97 * hash + Objects.hashCode(this.tag);
      hash = 97 * hash + Objects.hashCode(this.features);
      hash = 97 * hash + this.sequenceIndex;
      return hash;
   }
}
