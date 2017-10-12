package eu.fbk.dh.jamcha.parametersReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public abstract class ParametersReaderTest
{
   protected final Path CORPUS_PATH = Paths.get("CORPUSTestPath");
   protected final Path MODEL_PATH = Paths.get("MODELTestPath");

   @Test
   public abstract void testReadParameters();
}
