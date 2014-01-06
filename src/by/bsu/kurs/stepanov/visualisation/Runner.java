package by.bsu.kurs.stepanov.visualisation;

import jade.BootProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.05.13
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class Runner {

    private Properties properties = System.getProperties();
    private String CLASS_PATH = "C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\alt-rt.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\rt.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\sunpkcs11.jar;C:\\Program Files\\Java\\jdk1.7.0\\jre\\lib\\ext\\zipfs.jar;C:\\Users\\Dmitriy\\Documents\\Java\\Intelig.Idea\\VisualJade\\out\\production\\VisualJade;C:\\Users\\Dmitriy\\Documents\\Java\\Курсовая\\JADE-bin-4.2.0\\jade\\lib\\commons-codec\\commons-codec-1.3.jar;C:\\Users\\Dmitriy\\Documents\\Java\\Курсовая\\JADE-bin-4.2.0\\jade\\lib\\jade.jar;C:\\Program Files\\Oracle\\JavaFX 2.2 SDK\\lib\\ant-javafx.jar;C:\\Program Files\\Oracle\\JavaFX 2.2 SDK\\lib\\javafx-doclet.jar;C:\\Program Files\\Oracle\\JavaFX 2.2 SDK\\lib\\javafx-mx.jar;C:\\Program Files\\Oracle\\JavaFX 2.2 SDK\\rt\\lib\\jfxrt.jar;C:\\Program Files\\Oracle\\JavaFX 2.2 SDK\\rt\\bin;C:\\Program Files\\Oracle\\JavaFX 2.2 SDK\\bin;C:\\Program Files\\Oracle\\JavaFX 2.2 Runtime;C:\\Program Files\\JetBrains\\IntelliJ IDEA 12.0\\lib\\idea_rt.jar";

    public static void main(String[] args) {
        // new Runner().launch(args);
        new Runner().run(null);
    }

    public void run(MapFX mapFX) {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        BootProfileImpl bootProfile = new BootProfileImpl();
        bootProfile.setParameter("-accept-foreign-agents","true");
        bootProfile.setParameter("-platform-id","note");
        bootProfile.setParameter("-host","note");
        AgentContainer cont = runtime.createMainContainer(new BootProfileImpl());
        runtime.setCloseVM(true);
        try {
            //runtime.startUp(new BootProfileImpl());
            AgentController agentController1 = createMapAgent("Minsk",cont,new Object[]{mapFX});
            agentController1.start();
            //agentController1 = cont.createNewAgent("RMA","jade.tools.rma.rma",null);
            //agentController1.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Object arguments:
     * AID - road AID in current node
     */
    public static AgentController createNodeAgent(String name, AgentContainer cont, Object[] args) throws StaleProxyException {
        return cont.createNewAgent(name, "by.bsu.kurs.stepanov.agents.control.NodeAgent", args);
    }

    /**
     * Object arguments:
     * int - road length
     * AID - firstRoadEnd
     * AID - secondRoadEnd
     * int - roadMotionMode
     */
    public static AgentController createRoadAgent(String name, AgentContainer cont, Object[] args) throws StaleProxyException {
        return cont.createNewAgent(name, "by.bsu.kurs.stepanov.agents.control.RoadAgent", args);
    }

    /**
     * Object arguments:
     * AID - situated
     * AID - destination
     */
    public static AgentController createTransportAgent(String name, AgentContainer cont, Object[] args) throws StaleProxyException {
        return cont.createNewAgent(name, "by.bsu.kurs.stepanov.agents.movable.TransportAgent", args);
    }

    /**
     * Object arguments:
     * AID - roads with current speed
     */
    public static AgentController createAtomicMotionAgent(String name, AgentContainer cont, Object[] args) throws StaleProxyException {
        return cont.createNewAgent(name, "by.bsu.kurs.stepanov.agents.control.AtomicMotionAgent", args);
    }

    public static AgentController createMapAgent(String name, AgentContainer cont, Object[] args) throws StaleProxyException {
        return cont.createNewAgent(name, "by.bsu.kurs.stepanov.agents.control.MapControlAgent", args);
    }

    private void launch(String[] arguments) {
        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-cp");
        args.add(CLASS_PATH);
        args.add("jade.Boot3" );
        args.add("-accept-foreign-agents");
        args.add("true");
        args.add("-gui");
        args.add("jade.Boot");
        args.add("agentA:by.bsu.kurs.stepanov.agents.movable.TrafficTemplate");
        args.add("agentB:by.bsu.kurs.stepanov.agents.control.PointTemplate");
        ProcessBuilder pb = new ProcessBuilder(args);
        // Print out the command being executed
        String inputCommand = "";
        for (String arg : args) {
            if (inputCommand.isEmpty()) {
                inputCommand = arg;
            } else {
                inputCommand = inputCommand + " " + arg;
            }
        }
        System.out.println(inputCommand);
        pb.redirectErrorStream(true);
        Map<String, String> env = pb.environment();

        setEnvironmentProperties(env, properties);
        setEnvironmentVariable(env, "CLASSPATH", CLASS_PATH);
        try {
            Process ret = pb.start();
            writeProcessOutput(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setEnvironmentVariable(Map<String, String> env, String name, String value) {
        env.put(name, value);
    }

    private void setEnvironmentProperties(Map<String, String> env, Properties prop) {
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
            setEnvironmentVariable(env, (String) entry.getKey(), (String) entry.getValue());
        }
    }

    protected void writeProcessOutput(Process process) throws IOException {
        InputStreamReader tempReader = new InputStreamReader(
                new BufferedInputStream(process.getInputStream()));
        BufferedReader reader = new BufferedReader(tempReader);
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
        }
        tempReader = new InputStreamReader(new BufferedInputStream(process.getErrorStream()));
        reader = new BufferedReader(tempReader);
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
        }
    }
}
