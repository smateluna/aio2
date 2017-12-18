import java.util.HashSet;
import java.util.Map;
 
public class ADTest {
 
    public static void main (String[] args) {
 
        ADAuthenticator adAuthenticator = new ADAuthenticator();
         
        // Test bad password
        //System.out.println("Testing bad password...");
        //Map<String,Object> attrs = adAuthenticator.authenticate("SOMEUSER","badpassword");
         
        // Test good password
        System.out.println("Testing good password...");
        Map<String,Object> attrs = adAuthenticator.authenticate("evillar","0458");
         
        if (attrs != null) {
            for (String attrKey : attrs.keySet()) {
                if (attrs.get(attrKey) instanceof String) {
                    System.out.println(attrKey +": "+attrs.get(attrKey));
                } else {
                    System.out.println(attrKey +": (Multiple Values)");
                    for (Object o : (HashSet)attrs.get(attrKey)) {
                        System.out.println("\t value: " +o);
                    }
                }
            }
        } else {
            System.out.println("Attributes are null!");
        }
  
    }
     
}