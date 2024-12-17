///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17

//REPOS central,jitpack


//DEPS info.picocli:picocli:4.6.3
//DEPS org.json:json:20240303
//DEPS org.jsoup:jsoup:1.18.3
//DEPS org.slf4j:slf4j-simple:2.0.9
//DEPS com.konghq:unirest-java-core:4.4.5
//DEPS com.github.aghasemi:try4j:0.0.8

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import kong.unirest.core.Unirest;

@Command(name = "licenses", mixinStandardHelpOptions = true, version = "licenses 0.1",
        description = "licenses made with jbang")
class licenses implements Callable<Integer> {

   @Option(names= {"-p", "--pom"}, description = "The path to teh POM file")
    private File pomFile;

    public static void main(String... args) {
        
        int exitCode = new CommandLine(new licenses()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { 
        var pom = Files.readString(pomFile.toPath(), Charset.forName("UTF-8"));
        var deps = Jsoup.parse(pom).select("dependency");
        deps.forEach(d ->
        {
            String groupId = d.select("groupId").get(0).text().trim();
            String artifactId = d.select("artifactId").get(0).text().trim();
            System.out.print("%40s %30s ".formatted(groupId,artifactId));
            String license = new JSONObject(Unirest.get("https://cl.xlit.app/for/%s/%s".formatted(groupId,artifactId)).asString().getBody()) .getString("license");
            System.out.println("%s".formatted(license));
           
            //System.out.println("%40s %30s %s".formatted(groupId,artifactId, license));
            
        });
        return 0;
    }
}
