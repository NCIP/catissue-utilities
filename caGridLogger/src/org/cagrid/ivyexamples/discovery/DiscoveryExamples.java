package org.cagrid.ivyexamples.discovery;

import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.cagrid.metadata.common.PointOfContact;
import gov.nih.nci.cagrid.metadata.common.UMLClass;

import org.apache.axis.message.addressing.EndpointReferenceType;


public class DiscoveryExamples {

    @SuppressWarnings("null")
    public static void main(String[] args) {
        DiscoveryClient client = null;
        try {
            if (args.length == 1) {
                client = new DiscoveryClient(args[1]);
            } else {
                client = new DiscoveryClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String searchString = "Scott";
        String center = "OSU";
        PointOfContact poc = new PointOfContact();
        poc.setFirstName("Scott");
        poc.setLastName("Oster");
        String servName = "CaDSRService";
        String operName = "findAllProjects";
        UMLClass umlClass = new UMLClass();
        umlClass.setClassName("Project");

        try {
            EndpointReferenceType[] services = null;

            printHeader("All Services");
            services = client.getAllServices(false);
            printResults(services);

            printHeader("Search String [" + searchString + "]");
            services = client.discoverServicesBySearchString(searchString);
            printResults(services);

            printHeader("Research Center Name [" + center + "]");
            services = client.discoverServicesByResearchCenter(center);
            printResults(services);

            printHeader("POC [" + poc + "]");
            services = client.discoverServicesByPointOfContact(poc);
            printResults(services);

            printHeader("Service name [" + servName + "]");
            services = client.discoverServicesByName(servName);
            printResults(services);

            printHeader("Operation name [" + operName + "]");
            services = client.discoverServicesByOperationName(operName);
            printResults(services);

            printHeader("Operation input [" + umlClass + "]");
            services = client.discoverServicesByOperationInput(umlClass);
            printResults(services);

            printHeader("Operation output [" + umlClass + "]");
            services = client.discoverServicesByOperationOutput(umlClass);
            printResults(services);

            printHeader("Operation class [" + umlClass + "]");
            services = client.discoverServicesByOperationClass(umlClass);
            printResults(services);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    private static void printHeader(String title) {
        System.out.println("==================================================");
        System.out.println("Querying by " + title);
        System.out.println("==================================================");
    }


    private static void printResults(EndpointReferenceType[] types) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                System.out.println("\t" + i + ")  " + types[i].toString().trim());
            }
        } else {
            System.out.println("no results.");
        }
        System.out.println("--------------------------------------------------\n\n");
    }
}
