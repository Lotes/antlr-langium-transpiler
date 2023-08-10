package org.langium.antlr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

public class AppTest 
{
    @Test
    public void shouldGenerateLangiumGrammars() throws IOException
    {
        File resourcesDirectory = new File("src/test/resources");
        File grammarsDirectory = new File(resourcesDirectory, "grammars");
        for (File grammarDirectory : grammarsDirectory.listFiles(f -> f.isDirectory())) {
            var entryFile = new File(grammarDirectory, "entry.txt");
            var entry = Files.readString(entryFile.toPath());
            File entryGrammar = new File(grammarDirectory, entry);
            File targetFolder = new File(grammarDirectory, "target");
            targetFolder.mkdir();
            App.main(new String[] {
                entryGrammar.getAbsolutePath(),
                targetFolder.getAbsolutePath()
            });
        }
    }
}
