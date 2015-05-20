import com.amazonaws.services.route53.infima.OneDimensionalLattice;
import com.amazonaws.services.route53.infima.RubberTree;
import com.amazonaws.services.route53.infima.SingleCellLattice;
import com.amazonaws.services.route53.infima.util.HealthCheckedResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

import java.util.Arrays;
import java.util.List;

/**
 * @author Josh Padnick
 */

public class Main {

    /**
     * This main method is meant to be executed from Maven.  See README.md for more info.
     */
    public static void main(String[] args) {
        System.out.println("**********");

        // Define a set of Route53 Resource Records based on the endpoint they refer to, and the HealthCheck name associated with them
        HealthCheckedResourceRecord endpoint1 = new HealthCheckedResourceRecord("HaProxy-1", "10.0.0.1");
        HealthCheckedResourceRecord endpoint2 = new HealthCheckedResourceRecord("HaProxy-2", "10.0.0.2");
        HealthCheckedResourceRecord endpoint3 = new HealthCheckedResourceRecord("HaProxy-3", "10.0.0.3");
        HealthCheckedResourceRecord endpoint4 = new HealthCheckedResourceRecord("HaProxy-4", "10.0.0.4");
        HealthCheckedResourceRecord endpoint5 = new HealthCheckedResourceRecord("HaProxy-5", "10.0.0.5");
        HealthCheckedResourceRecord endpoint6 = new HealthCheckedResourceRecord("HaProxy-6", "10.0.0.6");

        // Create a 1-dimensional "Lattice" of HAProxy endpoints with "Availability Zone" as the endpoint.
        OneDimensionalLattice<HealthCheckedResourceRecord> lattice = new OneDimensionalLattice<HealthCheckedResourceRecord>("AvailabilityZone");
        lattice.addEndpoint("us-west-2a", endpoint1);
//        lattice.addEndpoint("us-west-2a", endpoint2);
        lattice.addEndpoint("us-west-2b", endpoint3);
//        lattice.addEndpoint("us-west-2b", endpoint4);
        lattice.addEndpoint("us-west-2c", endpoint5);
//        lattice.addEndpoint("us-west-2c", endpoint6);

        // Now create a "RubberTree" (or set of Route53 Resource Records) based on the Lattice defined above.
        List<ResourceRecordSet> rrs = RubberTree.vulcanize("Z31FOR5YI16P0H", "www.phoenixdevops.io", "A", 30L, lattice, 2);

        for ( ResourceRecordSet r : rrs ) {
            System.out.println( r.toString() );
        }

        System.out.println("**********");
    }

}
