/* This is an example step groovy to show the proper use of APTool
 * In order to use import these utilities, you have to use the "pluginutilscripts" jar
 * that comes bundled with this plugin example. 
 */
import com.urbancode.air.AirPluginTool
import com.urbancode.air.plugin.DeployArchive

/* This gets us the plugin tool helper. 
 * This assumes that args[0] is input props file and args[1] is output props file.
 * By default, this is true. If your plugin.xml looks like the example. 
 * Any arguments you wish to pass from the plugin.xml to this script that you don't want to 
 * pass through the step properties can be accessed using this argument syntax
 */
def apTool = new AirPluginTool(this.args[0], this.args[1]) 

/* Here we call getStepProperties() to get a Properties object that contains the step properties
 * provided by the user. 
 */
def props = apTool.getStepProperties();

/* This is how you retrieve properties from the object. You provide the "name" attribute of the 
 * <property> element 
 * 
 */
def artifactoryURI = props['artifactoryURI'];
def repository = props['repository'];
def userName = props['userName'];
def password = props['password'];
def fileNames = props['fileNames'];
def dirOffset = props['dirOffset'];

def directoryOffset = new File(dirOffset).getCanonicalPath();

def fileNameList = fileNames.readLines();
List<File> files = new ArrayList<File>();
File tempFile = null;
for(def fileName : fileNameList) {
	tempFile = new File(directoryOffset+File.separator+fileName);
	files.add(tempFile)
	println "Found: " + tempFile.getName()
}
DeployArchive deployer = new DeployArchive();
boolean success = deployer.putArchives(artifactoryURI, repository, userName, password, files);

//Set an output property

apTool.storeOutputProperties();//write the output properties to the file

if(!success)
	System.exit(-1);
else
	System.exit(0);
