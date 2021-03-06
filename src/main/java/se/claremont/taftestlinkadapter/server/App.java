package se.claremont.taftestlinkadapter.server;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Command line enabled start class for 'TAF Testlink Adapter Server'. This
 * server start up a gateway listening for posted test run results from a
 * TAF test run - and reporting the test results to Testlink.
 *
 * Created by jordam on 2017-03-18.
 */
public class App {
    public static ObjectMapper mapper = new ObjectMapper();

    /**
     * Help text for command line.
     *
     * @return The text
     */
    private static String helpText(){
        return System.lineSeparator() +
                "TAF Testlink Adapter Server" + System.lineSeparator() +
                "===============================" + System.lineSeparator() +
                "Usage example:" + System.lineSeparator() + System.lineSeparator() +
                "   java -jar TafTestlinkAdapterServer.jar testlinkaddress=http://mytestlinkserver.mycompany.com:80/testlink/lib/api/xmlrpc/v1/xmlrpc.php devkey=2a861343a3dca60b876ca5b6567568de username=taftestlinkuser port=2221" + System.lineSeparator() + System.lineSeparator() +
                "where port number is the TAF Testlink Adapter Server port number for TAF to connect to, also stated as the TCP port in the URL given as a run settings parameter in the TAF test run (e.g. http://localhost:8080/taftestlinkadapter)." + System.lineSeparator() +
                "Default TCP port is 2221 - chosen not to collide with potential Testlink installation on default http port 80 the same machine. The important part is that it should not be a port in use already, by Testlink or other web server." + System.lineSeparator() +
                System.lineSeparator() +
                "If you want to understand how TAF Testlink Adapter Server works, and should be applied, and what other switches exist, use the switch:" + System.lineSeparator() + System.lineSeparator() +
                "   java -jar TafTestlinkAdapterServer.jar -help" + System.lineSeparator();
    }

    /**
     * Printing information text describing the workings of this program if the argument 'info' is found.
     *
     * @param args The run time argument this jar file is started with
     */
    private static void printHelpTextIfSwitchIsFound(String[] args){
        for(String arg : args){
            if(arg.toLowerCase().equals("info") || arg.equals("-help") || arg.equals("man") || arg.equals("--help") || arg.equals("-h")){
                System.out.println(System.lineSeparator() + "This server is used as a proxy between a TAF test automation and a Testlink server. It produces test run results to Testlink for TAF test runs. " + System.lineSeparator() + System.lineSeparator() +
                        "Getting the TAF Testlink Adapter Server started" + System.lineSeparator() +
                        "--------------------------------------------------" + System.lineSeparator() +
                        "A few different command line parameters can be used at TAF Testlink Adapter Server startup:" + System.lineSeparator() + System.lineSeparator() +
                        "Required parameters:" + System.lineSeparator() +
                        "     testlinkaddress=http://testlink.mycompany.com/testlink/lib/api/xmlrpc/v1/xmlrpc.php" + System.lineSeparator() +
                        "                                              Make sure the full adress to the Testlink" + System.lineSeparator() +
                        "                                              API endpoint is there." + System.lineSeparator() + System.lineSeparator() +
                        "     username=taftestlinkuser                 Used to create test cases in Testlink, so" + System.lineSeparator() +
                        "                                              make sure you use a valid user name for" + System.lineSeparator() +
                        "                                              Testlink." + System.lineSeparator() + System.lineSeparator() +
                        "     devkey=2a861343a3dca60b876ca5b6567568de  You can find the Testlink API DevKey on the" + System.lineSeparator() +
                        "                                              user page in Testlink, called 'API interface" + System.lineSeparator() +
                        "                                              Personal API access key'." + System.lineSeparator() + System.lineSeparator() +
                        "Optional parameters that change behaviour of this server:" + System.lineSeparator() +
                        "     port=2221                                Port number is of your own choice. Make sure " + System.lineSeparator() +
                        "                                              it is not the same as the Testlink server uses." + System.lineSeparator() + System.lineSeparator() +
                        "     defaultTestProject=AutoTestProject       Makes test results for test cases that cannot " + System.lineSeparator() +
                        "                                              be identified in Testlink be reported into this" + System.lineSeparator() +
                        "                                              project." + System.lineSeparator() + System.lineSeparator() +
                        "     defaultTestSuite=AutoTestOrphanTestCases Makes test results for test cases that cannot be" + System.lineSeparator() +
                        "                                              identified in Testlink be reported to test cases" + System.lineSeparator() +
                        "                                              in this test suite." + System.lineSeparator() + System.lineSeparator() +
                        "     defaultTestPlan=AutoTestPlan             Makes test results for test cases that cannot be" + System.lineSeparator() +
                        "                                              identified in Testlink be reported to test cases," + System.lineSeparator() +
                        "                                              and build, within this test plan." + System.lineSeparator() + System.lineSeparator() +
                        "     defaultBuild=AutotestBuild               If no suitable build is found in Testlink a build" + System.lineSeparator() +
                        "                                              to report results to is created with this name.." + System.lineSeparator() + System.lineSeparator() +
                        "     connectionTimeout=15                     Sets the connection timeout for Testlink server" + System.lineSeparator() +
                        "                                              connection to 15 seconds." + System.lineSeparator() + System.lineSeparator() +
                        "All of these run time parameters are case insensitive and the order of them are irrelevant." + System.lineSeparator() +
                        System.lineSeparator() +
                        "How this adapter works" + System.lineSeparator() +
                        "-----------------------------" + System.lineSeparator() +
                        "If the TAF Settings parameter for 'TAF Testlink Adapter Server' address is set the TAF test runner automatically tries to send the test run results to this server." +
                        "The settings parameter in TAF is called 'URL_TO_TESTLINK_ADAPTER' and is set thus (example):" + System.lineSeparator() +
                        System.lineSeparator() +
                        "    TestRun.setSettingsValue(Settings.SettingParameters.URL_TO_TESTLINK_ADAPTER, " +
                        "\"http://127.0.0.1:2221/taftestlinkadapter\");" + System.lineSeparator() +
                        System.lineSeparator() +
                        "After a TAF test run is complete it checks if this parameter is changed from its original value." + System.lineSeparator() +
                        "If it is actively set to something other than its default value an attempt to push the test result to the TAF Testlink Adapter Server will be performed. " + System.lineSeparator() + System.lineSeparator() +
                        "Interfaces" + System.lineSeparator() +
                        "-------------" + System.lineSeparator() +
                        "This TAF Testlink Adapter Server has a few REST interfaces, and can display a few web pages. " + System.lineSeparator() +
                        System.lineSeparator() +
                        "The TAF test execution run results are formatted to JSON data format and POSTed to the TAF Testlink Adapter Server REST API." + System.lineSeparator() +
                        "The TAF Testlink Adapter Server then connects to the Testlink server and tries to identify the test cases to report results to. The identification sequence is as follows: " + System.lineSeparator() +
                        "  1). If a test case with a name corresponding to the TAF test case in the test " + System.lineSeparator() +
                        "      run is found, and either the Testlink test suite name or the Testlink test " + System.lineSeparator() +
                        "      plan name for that test case has a name containing the TAF test case test " + System.lineSeparator() +
                        "      set name, reporting will be performed to that test case." + System.lineSeparator() +
                        System.lineSeparator() +
                        "  2). If only one Testlink test case with a name corresponding to the TAF test " + System.lineSeparator() +
                        "      case is found reporting will be performed to that Testlink test case." + System.lineSeparator() +
                        System.lineSeparator() +
                        "  3). If several test cases with corresponding names are found reporting " + System.lineSeparator() +
                        "      will be performed to the first test case encountered." + System.lineSeparator() +
                        System.lineSeparator() +
                        "  4). If no corresponding test case is found at all in Testlink a new one will " + System.lineSeparator() +
                        "      be created, but in a Testlink test project called 'Test automation project'" + System.lineSeparator() +
                        "      and a test plan named 'Automated regression tests' and a Testlink test "  + System.lineSeparator() +
                        "      suite named after the TAF test case test set name." + System.lineSeparator() + System.lineSeparator() +
                        "Test case reporting is always performed againt the latest Testlink build for the Testlink test plan." + System.lineSeparator() +
                        System.lineSeparator() +
                        "The TAF Testlink Adapter Server also has a built in cache for Testlink resources, since lookup of these are quite slow. " +
                        "If it seem to behave badly, try restarting the server. The server will continuously present output while performing its tasks." + System.lineSeparator() +
                        System.lineSeparator() +
                        "Modifications on the TAF tests" + System.lineSeparator() +
                        "----------------------------------" + System.lineSeparator() +
                        "You need to set the setting called 'URL_TO_TESTLINK_ADAPTER' on your test execution. One way of doing this is from the command line when starting your TAF test run:" + System.lineSeparator() +
                        "   java -jar TafFull.jar MyTestClasses URL_TO_TESTLINK_ADAPTER=http://localhost:2222/taftestlinkadapter" + System.lineSeparator() +
                        System.lineSeparator() +
                        "Another way is programatically by adding for example:" +System.lineSeparator() +
                        "    @BeforeClass" + System.lineSeparator() +
                        "    public static void setup(){" + System.lineSeparator() +
                        "        TestRun.setSettingsValue(Settings.SettingParameters.URL_TO_TESTLINK_ADAPTER, \"http://127.0.0.1:2221/taftestlinkadapter\");" + System.lineSeparator() +
                        "    }" + System.lineSeparator() +
                        System.lineSeparator() +
                        "Doing this will engage the reporting to this adapter." + System.lineSeparator() +
                        System.lineSeparator() +
                        "gl:hf");
                System.exit(0);
            }
        }
    }

    /**
     * Setting the address to the Testlink API endpoint.
     *
     * @param args Runtime arguments for this program.
     */
    private static void setAddress(String[] args){
        for(String arg : args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("testlinkaddress")){
                    String address = arg.split("=")[1];
                    System.out.println("Setting testlink server testlinkServerAddress to " + address + ".");
                    Settings.testlinkServerAddress = address;
                }
            }
        }
    }

    private static void setDevKey(String[] args){
        for(String arg : args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("devkey")){
                    String testlinkDevKey = arg.split("=")[1];
                    System.out.println("Setting Testlink devkey to " + testlinkDevKey + ".");
                    Settings.testlinkDevKey = testlinkDevKey;
                }
            }
        }
    }

    /**
     *  Sets the user name used for communication with Testlink API. A username is used in
     *  combination with a Developer key (DevKey). The DevKey is generated from the
     *  Testlink GUI user account details page.
     *
     * @param args The runtime arguments for this program
     */
    private static void setUserName(String[] args){
        for(String arg : args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("username")){
                    String testlinkUserName= arg.split("=")[1];
                    System.out.println("Setting Testlink user name to " + testlinkUserName + ".");
                    Settings.testlinkUserName = testlinkUserName;
                }
            }
        }
    }

    private static void setPortIfStatedAsParameter(String[] args){
        for(String arg : args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("port")){
                    Integer port = Integer.valueOf(arg.split("=")[1]);
                    System.out.println("Setting server port to " + port + ".");
                    Settings.port = port;
                }
            }
        }
    }

    /**
     * When registering a TAF test run to Testlink sometimes no corresponding test case
     * can be identified in Testlink. This utilitiy then creates a test case in Testlink.
     * This method is used to set the name of the default test project in Testlink
     * where this test case is created.
     *
     * @param args The runtime arguments of this program.
     */
    private static void setDefaultTestProjectIfStatedAsAParameter(String[] args){
        for(String arg: args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("defaulttestproject") && arg.split("=").length > 1 && arg.split("=")[1].trim().length() > 0){
                    System.out.println("Setting default Testlink test project to " + arg.split("=")[1]);
                    Settings.defaultTestProjectNameForNewTestCases = arg.split("=")[1];
                }
            }
        }
    }

    /**
     * When registering a TAF test run to Testlink sometimes no corresponding test case
     * can be identified in Testlink. This utilitiy then creates a test case in Testlink.
     * This method is used to set the name of the default test suite in Testlink
     * where this test case is created.
     *
     * @param args The runtime arguments of this program.
     */
    private static void setDefaultTestSuiteIfStatedAsAParameter(String[] args){
        for(String arg: args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("defaulttestsuite") && arg.split("=").length > 1 && arg.split("=")[1].trim().length() > 0){
                    System.out.println("Setting default Testlink test suite to " + arg.split("=")[1]);
                    Settings.defaultTestSuiteNameForNewTestCases= arg.split("=")[1];
                }
            }
        }
    }

    /**
     * When registering a TAF test run to Testlink sometimes no corresponding test case
     * can be identified in Testlink. This utilitiy then creates a test case in Testlink.
     * This method is used to set the name of the default build in Testlink
     * where this test case is created.
     *
     * @param args The runtime arguments of this program.
     */
    private static void setDefaultBuildNameIfStatedAsAParameter(String[] args){
        for(String arg: args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("defaultbuild") && arg.split("=").length > 1 && arg.split("=")[1].trim().length() > 0){
                    System.out.println("Setting default Testlink build name to " + arg.split("=")[1]);
                    Settings.defaultBuildNameForNewTestCases = arg.split("=")[1];
                }
            }
        }
    }

    /**
     * Testlink connection attempts has a timeout for establishing a successful connection.
     * This method sets the timeout from runtime parameters.
     *
     * @param args The runtime arguments of this program
     */
    private static void setConnectionTimeoutIfStatedAsAParameter(String[] args){
        for(String arg: args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("connectiontimeout") && arg.split("=").length > 1 && arg.split("=")[1].trim().length() > 0){
                    System.out.println("Setting Testlink server connection timeout to " + arg.split("=")[1]);
                    Settings.testlinkServerConnectionTimeoutInSeconds= arg.split("=")[1];
                }
            }
        }
    }

    /**
     * When registering a TAF test run to Testlink sometimes no corresponding test case
     * can be identified in Testlink. This utilitiy then creates a test case in Testlink.
     * This method is used to set the name of the default test plan in Testlink
     * where this test case is created.
     *
     * @param args The runtime arguments of this program.
     */
    private static void setDefaultTestPlanIfStatedAsAParameter(String[] args){
        for(String arg: args){
            if(arg.contains("=")){
                if(arg.split("=")[0].toLowerCase().equals("defaulttestplan") && arg.split("=").length > 1 && arg.split("=")[1].trim().length() > 0){
                    System.out.println("Setting default Testlink test plan to " + arg.split("=")[1]);
                    Settings.defaultTestPlanName = arg.split("=")[1];
                }
            }
        }
    }

    /**
     * This is the main program executor
     *
     * @param args Runtime arguments
     */
    public static void main(String[] args){
        //originalOutputChannel = System.out;
        if(args.length == 0){
            System.out.println(helpText());
            System.exit(0);
        }
        printHelpTextIfSwitchIsFound(args);
        System.out.println("Processing " + args.length + " runtime arguments.");
        setAddress(args);
        setDevKey(args);
        setUserName(args);
        setPortIfStatedAsParameter(args);
        setDefaultTestProjectIfStatedAsAParameter(args);
        setDefaultTestPlanIfStatedAsAParameter(args);
        setDefaultTestSuiteIfStatedAsAParameter(args);
        setDefaultBuildNameIfStatedAsAParameter(args);
        setConnectionTimeoutIfStatedAsAParameter(args);
        if(Settings.testlinkDevKey == null || Settings.testlinkServerAddress == null || Settings.testlinkUserName == null){
            System.out.println("Cannot start server. Required parameter missing.");
            return;
        }
        HttpServer server = new HttpServer();
        server.start();
        if(!server.isStarted()) {
            try {
                server.server.stop();
            } catch (Exception ignored) {
            }
            server.server.destroy();
            return;
        }

        try {
            server.server.join();
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        } finally {
            server.stop();
        }
    }
}
