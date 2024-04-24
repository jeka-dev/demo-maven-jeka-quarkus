import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.tool.JkConstants;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.KBean;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Declare here, script dependencies
// @JkInjectClasspath("groupId:moduleId:version")
class Script extends KBean {

    @JkDoc("Maven arguments to pass when using 'mvn' method")
    private String mvnArgs = "";

    @JkDoc("Build application and copy result in jeka-output in order to be run with '-p' option")
    public void build() {
        mvn("clean package -DskipTests -Pnative");
        copyToJekaOutput();
    }

    @JkDoc("Execute Maven with ")
    public void mvn() {
        mvn(mvnArgs);
    }

    private void mvn(String mvnArguments) {
        JkLog.info("Executing mvn " + mvnArguments);
        String distrib = getRunbase().getProperties().get("jeka.java.distrib", "graalvm");
        String javaVersion = getRunbase().getProperties().get("jeka.java.version", "22");
        String distribFolder = distrib + "-" + javaVersion;
        Path graalvmHome = JkLocator.getCacheDir().resolve("jdks").resolve(distribFolder);
        String newPath =  graalvmHome.resolve("bin") + File.pathSeparator + System.getenv("PATH");
        JkProcess.ofWinOrUx("mvnw.cmd", "./mvnw")
                .addParamsAsCmdLine(mvnArguments)
                .setWorkingDir(getBaseDir())
                .setEnv("JAVA_HOME", graalvmHome.toString())
                .setEnv("GRAALVM_HOME", graalvmHome.toString())
                .setEnv("PATH", newPath)
                .setInheritIO(true)
                .exec();
    }

    private void copyToJekaOutput() {
        JkPathTree.of(getBaseDir().resolve("target")).andMatching("*.jar", "*-runner")
                .copyTo(getBaseDir().resolve(JkConstants.OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
    }

}