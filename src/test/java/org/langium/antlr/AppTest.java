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
            if(!grammarDirectory.getAbsolutePath().contains("debug")) {
               // continue;
            }
            var entryFile = new File(grammarDirectory, "entry.txt");
            var entry = Files.readString(entryFile.toPath());
            File entryGrammar = new File(grammarDirectory, entry);
            File targetFolder = new File(grammarDirectory, "target");
            clearDirectory(targetFolder);
            targetFolder.mkdir();
            App.main(new String[] {
                entryGrammar.getAbsolutePath(),
                targetFolder.getAbsolutePath()
            });
        }
    }

    private void clearDirectory(File grammarDirectory) {
        if(!grammarDirectory.exists()) {
            return;
        }
        for (File file : grammarDirectory.listFiles()) {
            if (file.isDirectory()) {
                clearDirectory(file);
            }
            file.delete();
        }
    }
}
