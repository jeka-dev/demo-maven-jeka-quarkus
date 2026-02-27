import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.api.utils.JkUtilsJdk;
import dev.jeka.core.tool.JkConstants;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInject;
import dev.jeka.core.tool.KBean;
import dev.jeka.core.tool.builtins.tooling.maven.MavenKBean;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Declare here, script dependencies
// @JkInjectClasspath("groupId:moduleId:version")
class Script extends KBean {

    @JkDoc("Maven arguments to pass when using 'mvn' method")
    private String mvnArgs = "";

    @JkInject
    private MavenKBean maven;

    @JkDoc("Build application and copy result in jeka-output in order to be run with '-p' option")
    public void build() {
        mvn("clean package -DskipTests -Pnative");
        copyToJekaOutput();
    }

    public void build2() {
        cleanOutput();
        maven.wrapPackage();
    }

    @JkDoc("Execute Maven with ")
    public void mvn() {
        mvn(mvnArgs);
    }

    private void mvn(String mvnArguments) {
        JkLog.info("Executing mvn " + mvnArguments);
        Path graalvmHome = JkUtilsJdk.getJdk("graalvm", "22");
        String newPath =  graalvmHome.resolve("bin") + File.pathSeparator + System.getenv("PATH");
        JkProcess.ofWinOrUx("mvnw.cmd", "./mvnw")
                .addParamsAsCmdLine(mvnArguments)
                .addParamsIf(System.getProperties().containsKey("jeka.test.skip"), "-Dmaven.test.skip=true")
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