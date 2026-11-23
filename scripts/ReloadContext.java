import com.sun.tools.attach.VirtualMachine;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

class ReloadContext {
    public static void main(String[] args) throws Exception {
        var vm = VirtualMachine.attach(args[0]);
        String address;
        try { address = vm.startLocalManagementAgent(); } finally { vm.detach(); }
        try (var connection = JMXConnectorFactory.connect(new JMXServiceURL(address))) {
            var server = connection.getMBeanServerConnection();
            for (var context : server.queryNames(new ObjectName("Catalina:j2eeType=WebModule,*"), null)) {
                if (("//localhost" + args[1]).equals(context.getKeyProperty("name"))) {
                    server.invoke(context, "reload", new Object[0], new String[0]);
                    System.out.println("Reloaded " + args[1] + " on the existing Tomcat.");
                    return;
                }
            }
            throw new IllegalStateException("Requested context was not found on this Tomcat.");
        }
    }
}
