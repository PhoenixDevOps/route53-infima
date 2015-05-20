import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.infima.OneDimensionalLattice;
import com.amazonaws.services.route53.infima.RubberTree;
import com.amazonaws.services.route53.infima.SingleCellLattice;
import com.amazonaws.services.route53.infima.util.HealthCheckedResourceRecord;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

import java.util.ArrayList;
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
        HealthCheckedResourceRecord endpoint1 = new HealthCheckedResourceRecord("8473184f-5e61-4700-8c07-55c72eefd77a", "52.25.24.6");
        HealthCheckedResourceRecord endpoint2 = new HealthCheckedResourceRecord("Nginx 1", "10.0.0.2");
        HealthCheckedResourceRecord endpoint3 = new HealthCheckedResourceRecord("981b6402-c9a7-49dc-bc78-ce6d3bcd5213", "52.24.172.161");
        HealthCheckedResourceRecord endpoint4 = new HealthCheckedResourceRecord("Nginx 2", "10.0.0.4");
        HealthCheckedResourceRecord endpoint5 = new HealthCheckedResourceRecord("59cf92a0-ff4d-4a5b-8da5-68c64cd1c694", "52.25.39.55");
        HealthCheckedResourceRecord endpoint6 = new HealthCheckedResourceRecord("Nginx 3", "10.0.0.6");

        // Create a 1-dimensional "Lattice" of HAProxy endpoints with "Availability Zone" as the endpoint.
        OneDimensionalLattice<HealthCheckedResourceRecord> lattice = new OneDimensionalLattice<HealthCheckedResourceRecord>("AvailabilityZone");
        lattice.addEndpoint("us-west-2a", endpoint1);
        //lattice.addEndpoint("us-west-2a", endpoint2);
        lattice.addEndpoint("us-west-2b", endpoint3);
        //lattice.addEndpoint("us-west-2b", endpoint4);
        //lattice.addEndpoint("us-west-2c", endpoint5);
        //lattice.addEndpoint("us-west-2c", endpoint6);

        // Now create a "RubberTree" (or set of Route53 Resource Records) based on the Lattice defined above.
        List<ResourceRecordSet> rrs = RubberTree.vulcanize("Z31FOR5YI16P0H", "www.phoenixdevops.io", "A", 30L, lattice, 2);

        // Let's use the AWS Java SDK to add the Rubbertree records to Route53.
        for ( ResourceRecordSet r : rrs ) {
            System.out.println("-------------------------");
            System.out.println("Name = " + r.getName());
            System.out.println("Set Identifier = " + r.getSetIdentifier());
            System.out.println("Alias Target = " + r.getAliasTarget());
            System.out.println("Weight = " + r.getWeight());
            System.out.println("Health Check Id = " + r.getHealthCheckId());
            System.out.println("Failover = " + r.getFailover());
            System.out.println("Type = " + r.getType());
            System.out.println("TTL = " + r.getTTL());
            System.out.println(r.getResourceRecords().size() + " Resource Records");
            System.out.println("~ ~ ~ ~ ~ ~ ~ ~");
            System.out.println(r.toString());
            System.out.println("-------------------------");

            List<Change> changes = new ArrayList<Change>();

            changes.add(
                    new Change(ChangeAction.UPSERT,
                            new ResourceRecordSet()
                                    .withName(r.getName())
                                    .withType(r.getType())
                                    .withTTL(r.getTTL())
                                    .withSetIdentifier(r.getSetIdentifier())
                                    .withHealthCheckId(r.getHealthCheckId())
                                    .withWeight(r.getWeight())
                                    .withFailover(r.getFailover())
                                    .withAliasTarget(r.getAliasTarget())
                                    .withResourceRecords(r.getResourceRecords())
                    ));

            ChangeBatch changeBatch = new ChangeBatch(changes);

            ChangeResourceRecordSetsRequest changeRequest = new ChangeResourceRecordSetsRequest()
                    .withHostedZoneId("Z31FOR5YI16P0H")
                    .withChangeBatch(changeBatch);

            AmazonRoute53Client route53Client = new AmazonRoute53Client();
            route53Client.changeResourceRecordSets(changeRequest);
            route53Client.shutdown();
        }

        System.out.println("**********");
    }

}
